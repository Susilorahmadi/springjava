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
public class RegistrationService {
    @Value("${expired.ott.used}")
    private long expiryOTTUsed;
    @Value("${url.check.blok.card}")
    private String urlCheckBlokCard;
    @Value("${url.activate.card}")
    private String urlActivateCard;
    @Value("${url.decrypt.card}")
    private String urlDecryptCard;
    @Value("${prefix.bni.map}")
    private String[] validPrefixesArray;
    @Value("${url.callback.page.bind}")
    private String urlCallbackPageBind;
    @Value("${cardlink.blokcode.aktif}")
    private String[] blokCodeAktif;
    @Value("${cardlink.blokcode.tidak.aktif}")
    private String[] blokCodeTidakAktif;
    @Value("${url.encrypt.card}")
    private String urlEncryptCard;
    @Value("${prefix.bni.list.channel}")
    private String[] listChannels;
    @Value("${file.path}")
    private String privateKeyLocation;

    private final RedisService redisService;
    private final GeneralUtil generalUtil;
    private final RestService restService;
    private final DatabaseService databaseService;
    private final ServiceUtil serviceUtil;
    private List<String> prefixBin;
    private final OTPService otpService;
    private List<String> aktifBlokCode;
    private List<String> nonAktifBlokCode;
    private List<String> channelListOn;

    @PostConstruct
    private void init() {
        prefixBin = Arrays.asList(validPrefixesArray);
    }

    @PostConstruct
    private void initBlokcode(){
        aktifBlokCode = Arrays.asList(blokCodeAktif);
    }

    @PostConstruct
    private void initNonBlokcode(){
        nonAktifBlokCode = Arrays.asList(blokCodeTidakAktif);
    }

    @PostConstruct
    private void setListChannels() {
        channelListOn = Arrays.asList(listChannels);
    }

    public List<String> getValidPrefixes() {
        return prefixBin;
    }
    public List<String> blokCodeAktif(){
        return aktifBlokCode;
    }
    public List<String> blokCodeNonAktif(){
        return nonAktifBlokCode;
    }
    public List<String> channelListOn(){
        return channelListOn;
    }

    public RegistrationService(RedisService redisService, GeneralUtil generalUtil, RestService restService, OTPService otpService, DatabaseService databaseService, ServiceUtil serviceUtil) {
        this.redisService = redisService;
        this.generalUtil = generalUtil;
        this.restService = restService;
        this.otpService = otpService;
        this.databaseService = databaseService;
        this.serviceUtil = serviceUtil;
    }

    ObjectMapper mapper = new ObjectMapper();

    public ResponseEntity<Object> register(RequestRegistration body) {
        try {
            log.info("Register request: {}", body);
            ResponseRegistration responseRegistration = new ResponseRegistration();
            ResponseRegistration.AdditionalInfo additionalInfo = new ResponseRegistration.AdditionalInfo();
            responseRegistration.setAdditionalInfo(additionalInfo);
            if(!generalUtil.iaValidChannel(body.getAdditionalInfo().getChannel(),channelListOn().toString())){
                return HandlerUtil.externalAPIBadRequest(null);
            }

            // select biding_trx_card_bind where phone number and binding_status
            String phoneNum = body.getPhoneNo();
            BindingCard respDB = databaseService.finOnePhone(phoneNum, MicroserviceEnum.BINDING_STATUS.getMessage());
            if(respDB != null) {
                return HandlerUtil.alreadyUse();
            }

            String randomString = GeneralUtil.generateRandomString(15);
            String randomCombinedWithDate = GeneralUtil.generateRandomNumberWithDate();
            String formattedOffsetDateTime = GeneralUtil.formatOffsetDateTime();
            String state = GeneralUtil.generateRandomString(10);

            // check authCode by phoneNo
            String authcode= "";
            String respRedis = redisService.get("MAP-"+ body.getPhoneNo());
            if (respRedis == null) {
                authcode = generalUtil.generateAuthcode(state);
                redisService.set("MAP-"+ body.getPhoneNo(),authcode,expiryOTTUsed);
            } else{
                authcode = respRedis;
            }

            String convertBody = mapper.writeValueAsString(body);
            redisService.set(authcode,convertBody,expiryOTTUsed);

            String url = urlCallbackPageBind+"?authCode="+authcode+"&state="+state;

            //------  Ecrypt data with RSA --------------//
            PrivateKey privateKey =  RSAUtil.getPrivateKeyFromPEM(privateKeyLocation);
            String encryptedAuthCode = RSAUtil.encryptWithPrivateKey(authcode,privateKey);
            String encryptedUrl = RSAUtil.encryptWithPrivateKey(url,privateKey);
            //------------------------------------------//
            responseRegistration.setResponseCode(MicroserviceEnum.RDS_SUCCESS_SAVE.getResponseCode());
            responseRegistration.setResponseMessage(MicroserviceEnum.RDS_SUCCESS_SAVE.getMessage());
            responseRegistration.setReferenceNo(randomCombinedWithDate);
            responseRegistration.setPartnerReferenceNo(body.getPartnerReferenceNo());
            responseRegistration.setRandomString(randomString);
            responseRegistration.setTokenExpiryTime(formattedOffsetDateTime);
            additionalInfo.setDeviceId(body.getAdditionalInfo().getDeviceId());
            additionalInfo.setChannel(body.getAdditionalInfo().getChannel());
            additionalInfo.setAuthCode(encryptedAuthCode);
            additionalInfo.setState(state);
            additionalInfo.setUrlBindingPage(encryptedUrl);

            log.info("url page : {}", url);

            return HandlerUtil.success(responseRegistration);
        } catch (DatabaseException e){
            return HandlerUtil.databaseError(e.getMessage());
        } catch (RedisException e){
            ResponseRegistration responseRegistration = new ResponseRegistration();
            responseRegistration.setResponseCode(MicroserviceEnum.RDS_MESS_UNABLE_TO_CONN.getResponseCode());
            responseRegistration.setResponseMessage(MicroserviceEnum.RDS_MESS_UNABLE_TO_CONN.getMessage());
            responseRegistration.setPartnerReferenceNo(body.getPartnerReferenceNo());

            return HandlerUtil.redisError(responseRegistration);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }

    public ResponseEntity<Object> getAuthcode(String authCode, String state) {
        try {
            log.info("get authcode request: {}", authCode + state);
            boolean isValid = generalUtil.constain(authCode, state);
            if (!isValid){
                return HandlerUtil.unAuthorized(MicroserviceEnum.UNAUTHORIZED.getMessage(),null);
            }

            ResponseExpiredRedis responseExpiredRedis = new ResponseExpiredRedis();
            String resp = redisService.get(authCode);

            if (resp == null) {
                responseExpiredRedis.setResponseCode(MicroserviceEnum.RDS_EXPITED_GET.getResponseCode());
                responseExpiredRedis.setResponseMessage(MicroserviceEnum.RDS_EXPITED_GET.getMessage());
                responseExpiredRedis.setAuthCode(authCode);
                responseExpiredRedis.setState(state);
                return HandlerUtil.notFound(responseExpiredRedis);
            }

            RequestRegistration response = mapper.readValue(resp, RequestRegistration.class);

            return HandlerUtil.success(response);
        } catch (RedisException e){
            return HandlerUtil.redisError(e.getMessage());
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }

    public ResponseEntity<Object> confirmBind(RequestCardlink body){
        try {
            log.info("confirm request: {}", body);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String respRedis = redisService.get(body.getAdditionalInfo().getAuthCode());
            if (respRedis == null) {
                ResponseExpiredRedis responseExpiredRedis = new ResponseExpiredRedis();
                responseExpiredRedis.setResponseCode(MicroserviceEnum.RDS_EXPITED_GET.getResponseCode());
                responseExpiredRedis.setResponseMessage(MicroserviceEnum.RDS_EXPITED_GET.getMessage());
                responseExpiredRedis.setAuthCode(body.getAdditionalInfo().getAuthCode());
                return HandlerUtil.notFound(responseExpiredRedis);
            }

            // check BIN MAP
            String cardNumDec = serviceUtil.decryptData(body.getCardNumber());
            if(!generalUtil.isValidPrefix(cardNumDec,getValidPrefixes().toString())){
                return HandlerUtil.unAuthorizedBin(null);
            }

            ReqEncryptCard reqEncryptCard = new ReqEncryptCard();
            reqEncryptCard.setText(cardNumDec);
            String maskAccountNumber = GeneralUtil.maskAccountNumber(cardNumDec);
            log.info("data : "+maskAccountNumber);

            restService.startRequest("POST",urlEncryptCard,maskAccountNumber);
            ResponseModel respDecp = restService.callAPI(urlEncryptCard, "POST", reqEncryptCard);
            ResEncryptCard resEncryptCard = mapper.convertValue(respDecp.getPayload(), ResEncryptCard.class);
            log.info("data Enkrip: "+StringUtil.toString(resEncryptCard.getData()));

            //request check card
            RequestRegistration redisResp = mapper.readValue(respRedis, RequestRegistration.class);
            CheckBlokCard checkBlokCard = new CheckBlokCard();
            checkBlokCard.setCardNumber(resEncryptCard.getData());
            checkBlokCard.setExpiryDate(body.getAdditionalInfo().getExpiryDate());
            checkBlokCard.setChannelID(redisResp.getAdditionalInfo().getChannel());

            restService.startRequest("POST",urlCheckBlokCard,checkBlokCard);
            ResponseModel resp = restService.callAPI(urlCheckBlokCard, "POST", checkBlokCard);
            log.info(StringUtil.toString(resp.getPayload()));
            //check BlockCode
            ResCheckBlok resCheckBlok = mapper.convertValue(resp.getPayload(), ResCheckBlok.class);

            if(!blokCodeAktif().toString().contains(resCheckBlok.getData().getBlockCode())){
                if(!blokCodeNonAktif().toString().contains(resCheckBlok.getData().getBlockCode())){
                    return HandlerUtil.externalAPIError("Tidak dapat binding, harap hubungi BNI Call",resCheckBlok);
                }
                return HandlerUtil.success(resCheckBlok);
            }
            otpService.requestOtp(body);
            return HandlerUtil.success(resCheckBlok);
        } catch (RedisException e){
            return HandlerUtil.redisError(e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage()+ e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }

    public ResponseEntity<Object> activateCard(RequestCardlink body){
        try {
            log.info("activate request: {}", body);
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
            ResponseModel respDecp = restService.callAPI(urlEncryptCard, "POST", reqEncryptCard);
            ResEncryptCard resEncryptCard = mapper.convertValue(respDecp.getPayload(), ResEncryptCard.class);
            log.info("data Enkrip: "+StringUtil.toString(resEncryptCard.getData()));

            RequestRegistration redisResp = mapper.readValue(respRedis, RequestRegistration.class);
            ReqActivateCard reqActivateCard = new ReqActivateCard();
            reqActivateCard.setCardNumber(resEncryptCard.getData());
            reqActivateCard.setDob(body.getAdditionalInfo().getDob());
            reqActivateCard.setChannelID(redisResp.getAdditionalInfo().getChannel());

            RequestRegistration response = mapper.readValue(respRedis, RequestRegistration.class);
            reqActivateCard.setPhone(response.getPhoneNo());

            restService.startRequest("POST",urlActivateCard,reqActivateCard);
            ResponseModel resp = restService.callAPI(urlActivateCard, "POST", reqActivateCard);
            log.info(StringUtil.toString(resp.getPayload()));
            ResActivateCard resActivateCard = mapper.convertValue(resp.getPayload(), ResActivateCard.class);
            if (resActivateCard.getIsoResponse() != null){
                otpService.requestOtp(body);
                return HandlerUtil.success(resActivateCard);
            }

            return HandlerUtil.externalAPIError("Gagal Aktivasi Kartu",resActivateCard);

        } catch (RedisException e){
            return HandlerUtil.redisError(e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage()+ e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }
}
