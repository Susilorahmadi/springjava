package id.co.bni.cardbinding.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bni.cardbinding.configs.MicroserviceEnum;
import id.co.bni.cardbinding.entity.BindingCard;
import id.co.bni.cardbinding.exception.APIException;
import id.co.bni.cardbinding.exception.DatabaseException;
import id.co.bni.cardbinding.exception.RedisException;
import id.co.bni.cardbinding.models.cardlink.*;
import id.co.bni.cardbinding.models.microservice.*;
import id.co.bni.cardbinding.utils.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class PINservice {
    private final StringUtil stringUtil;
    @Value("${url.decrypt.card}")
    private String urlDecryptCard;
    @Value("${url.validate.pin}")
    private String urlValidatePin;
    @Value("${url.callback.bind.api}")
    private String urlCallbackBindApi;
    @Value("${url.encrypt.card}")
    private String urlEncryptCard;
    @Value("${prefix.bni.map}")
    private String[] validPrefixesArray;
    @Value("${file.path}")
    private String privateKeyLocation;

    private final RestService restService;
    private final RedisService redisService;
    private final DatabaseService databaseService;
    private final ServiceUtil serviceUtil;
    private final GeneralUtil generalUtil;
    private List<String> prefixBin;

    public PINservice(RestService restService, RedisService redisService, DatabaseService databaseService, StringUtil stringUtil, ServiceUtil serviceUtil, GeneralUtil generalUtil) {
        this.restService = restService;
        this.redisService = redisService;
        this.databaseService = databaseService;
        this.stringUtil = stringUtil;
        this.serviceUtil = serviceUtil;
        this.generalUtil = generalUtil;
    }

    @PostConstruct
    private void init() {
        prefixBin = Arrays.asList(validPrefixesArray);
    }
    public List<String> getValidPrefixes() {
        return prefixBin;
    }

    ObjectMapper mapper = new ObjectMapper();

    public ResponseEntity<Object> validatePin(RequestCardlink body) {
        try {
            log.info("validate pin request {}", body);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String respRedis = redisService.get(body.getAdditionalInfo().getAuthCode());
            if (respRedis == null) {
                ResponseExpiredRedis responseExpiredRedis = new ResponseExpiredRedis();
                responseExpiredRedis.setResponseCode(MicroserviceEnum.RDS_EXPITED_GET.getResponseCode());
                responseExpiredRedis.setResponseMessage(MicroserviceEnum.RDS_EXPITED_GET.getMessage());
                responseExpiredRedis.setAuthCode(body.getAdditionalInfo().getAuthCode());
                return HandlerUtil.notFound(responseExpiredRedis);
            }

            //enkrip cardNum
            String cardNumDec = serviceUtil.decryptData(body.getCardNumber());
            if(!generalUtil.isValidPrefix(cardNumDec,getValidPrefixes().toString())){
                return HandlerUtil.unAuthorizedBin(null);
            }

            ReqEncryptCard reqEncryptCard = new ReqEncryptCard();
            reqEncryptCard.setText(cardNumDec);
            String maskAccountNumber = GeneralUtil.maskAccountNumber(cardNumDec);
            log.info("data : "+maskAccountNumber);

            restService.startRequest("POST",urlEncryptCard,maskAccountNumber);
            ResponseModel respEncrypt = restService.callAPI(urlEncryptCard, "POST", reqEncryptCard);
            ResEncryptCard resEncryptCard = mapper.convertValue(respEncrypt.getPayload(), ResEncryptCard.class);
            String cardNumber = resEncryptCard.getData();
            log.info("data Enkrip: "+ cardNumber);

            //enkrip PIN
            String pinDec = serviceUtil.decryptData(body.getAdditionalInfo().getPin());
            reqEncryptCard.setText(pinDec);
            restService.startRequest("POST",urlEncryptCard,pinDec);
            ResponseModel respEncPIN= restService.callAPI(urlEncryptCard, "POST", reqEncryptCard);
            ResEncryptCard mapResPin = mapper.convertValue(respEncPIN.getPayload(), ResEncryptCard.class);
            String pinEncrypt = mapResPin.getData();
            log.info("data Enkrip: "+pinEncrypt);

            //validate PIN
            RequestRegistration response = mapper.readValue(respRedis, RequestRegistration.class);
            ReqValidatePIN reqValidatePIN = new ReqValidatePIN();
            reqValidatePIN.setCardNumber(cardNumber);
            reqValidatePIN.setExpiryDate(body.getAdditionalInfo().getExpiryDate());
            reqValidatePIN.setPin(pinEncrypt);
            reqValidatePIN.setDob(body.getAdditionalInfo().getDob());
            reqValidatePIN.setChannelID(response.getAdditionalInfo().getChannel());

            restService.startRequest("POST",urlValidatePin,reqValidatePIN);
            ResponseModel resp = restService.callAPI(urlValidatePin,"POST",reqValidatePIN);
            log.info(StringUtil.toString(resp.getPayload()));
            ResValidatePIN resValidatePIN = mapper.convertValue(resp.getPayload(), ResValidatePIN.class);
            if(!resValidatePIN.getResponseCode().equals("00")){
                return HandlerUtil.externalAPIError("Gagal Validasi PIN",resValidatePIN);
            }

            //save to DB
            String bankCardToken = stringUtil.generateToken()+stringUtil.generateToken();
            BindingCard bindingCard = new BindingCard();
            bindingCard.setBindingTrxId(StringUtil.setUuid());
            bindingCard.setCreateAt(StringUtil.getAsTimestamp());
            bindingCard.setAuthCode(body.getAdditionalInfo().getAuthCode());
            bindingCard.setState("");
            bindingCard.setPatnerRefNum(response.getPartnerReferenceNo());
            bindingCard.setAccountName(response.getAccountName());
            bindingCard.setCardData(response.getCardData());
            bindingCard.setCusId(response.getCustIdMerchant());
            bindingCard.setIsBindInPay(response.getIsBindAndPay());
            bindingCard.setMerchantId(response.getMerchantId());
            bindingCard.setTerminalId(response.getTerminalId());
            bindingCard.setJourneyId(response.getJourneyId());
            bindingCard.setSubMercahntId(response.getSubMerchantId());
            bindingCard.setExternalStoreId(response.getExternalStoreId());
            bindingCard.setLimit(response.getLimit());
            bindingCard.setMerchantLogoUrl(response.getMerchantLogoUrl());
            bindingCard.setPhoneNumber(response.getPhoneNo());
            bindingCard.setSendOtpFlag(response.getSendOtpFlag());
            bindingCard.setType(response.getType());
            bindingCard.setDeviceId(response.getAdditionalInfo().getDeviceId());
            bindingCard.setChannelId(response.getAdditionalInfo().getChannel());
            bindingCard.setUrlCallback(response.getAdditionalInfo().getUrlCallbackSuccess());
            bindingCard.setBlockCode("");
            bindingCard.setCardNumber(cardNumber);
            bindingCard.setBankCardToken(bankCardToken);
            bindingCard.setStatusBinding(MicroserviceEnum.BINDING_STATUS.getMessage());
            bindingCard.setCredentialNumAccount(maskAccountNumber);

            int accepted = databaseService.save(bindingCard);
            if(accepted == 1){
                log.info("1 - Accepted");
            }

            //------  Ecrypt data with RSA --------------//
            PrivateKey privateKey =  RSAUtil.getPrivateKeyFromPEM(privateKeyLocation);
            String encryptedPhoneNo = RSAUtil.encryptWithPrivateKey(response.getPhoneNo(),privateKey);
            //------------------------------------------//

            //calback API
            ReqCallbackApi reqCallbackApi = new ReqCallbackApi();
            reqCallbackApi.setBankCardToken(bankCardToken);
            reqCallbackApi.setAuthCode(body.getAdditionalInfo().getAuthCode());
            reqCallbackApi.setClientId(body.getAdditionalInfo().getChannel());
            reqCallbackApi.setUrl(response.getAdditionalInfo().getUrlCallbackSuccess());
            reqCallbackApi.setPhoneNo(encryptedPhoneNo);

            restService.startRequest("POST",urlCallbackBindApi,reqCallbackApi);
            ResponseModel respCallback = restService.callAPI(urlCallbackBindApi,"POST",reqCallbackApi);
            log.info(StringUtil.toString(respCallback.getPayload()));

            //return resp
            resValidatePIN.setBankCardToken(bindingCard.getBankCardToken());
            resValidatePIN.setUrlCallbackSuccess(response.getAdditionalInfo().getUrlCallbackSuccess()+"?bankCardToken="+bindingCard.getBankCardToken()+"&changeToken="+stringUtil.generateToken()+"&authCode="+bindingCard.getAuthCode());
            redisService.delete(body.getAdditionalInfo().getAuthCode());

            return HandlerUtil.success(resValidatePIN);
        } catch (DatabaseException e){
            return HandlerUtil.databaseError( "0 - Accepted");
        } catch (RedisException e){
            return HandlerUtil.redisError(e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError("Error! ThridParty exception ->"+ e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }
}
