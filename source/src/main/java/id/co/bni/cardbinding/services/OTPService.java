package id.co.bni.cardbinding.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bni.cardbinding.configs.MicroserviceEnum;
import id.co.bni.cardbinding.exception.APIException;
import id.co.bni.cardbinding.exception.RedisException;
import id.co.bni.cardbinding.models.cardlink.RequestSendOTP;
import id.co.bni.cardbinding.models.cardlink.RequestValidateOTP;
import id.co.bni.cardbinding.models.cardlink.ResponseSendOTP;
import id.co.bni.cardbinding.models.cardlink.ResponseValidateOTP;
import id.co.bni.cardbinding.models.microservice.RequestCardlink;
import id.co.bni.cardbinding.models.microservice.RequestRegistration;
import id.co.bni.cardbinding.models.microservice.ResponseExpiredRedis;
import id.co.bni.cardbinding.models.microservice.ResponseModel;
import id.co.bni.cardbinding.utils.GeneralUtil;
import id.co.bni.cardbinding.utils.HandlerUtil;
import id.co.bni.cardbinding.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OTPService {

    @Value("${url.request.otp}")
    private String urlRequestOtp;
    @Value("${url.validate.otp}")
    private String urlValidateOtp;

    private final RestService restService;
    private final RedisService redisService;

    public OTPService(RestService restService,RedisService redisService) {
        this.restService = restService;
        this.redisService = redisService;
    }

    ObjectMapper mapper = new ObjectMapper();

    public ResponseEntity<Object> requestOtp(RequestCardlink body){
        try {
            log.info("otp request {}",body);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if(body.getAdditionalInfo().getOtpSender() == null) {
                return HandlerUtil.externalAPIBadRequest("field 'otp_sender' value is mandatory");
            }

            String respRedis = redisService.get(body.getAdditionalInfo().getAuthCode());
            if (respRedis == null) {
                ResponseExpiredRedis responseExpiredRedis = new ResponseExpiredRedis();
                responseExpiredRedis.setResponseCode(MicroserviceEnum.RDS_EXPITED_GET.getResponseCode());
                responseExpiredRedis.setResponseMessage(MicroserviceEnum.RDS_EXPITED_GET.getMessage());
                responseExpiredRedis.setAuthCode(body.getAdditionalInfo().getAuthCode());
                return HandlerUtil.notFound(responseExpiredRedis);
            }

            RequestRegistration response = mapper.readValue(respRedis, RequestRegistration.class);
            String phoneNumber = GeneralUtil.convertPhoneNumber(response.getPhoneNo());
            RequestSendOTP requestOTP = new RequestSendOTP();
            requestOTP.setChannel(response.getAdditionalInfo().getChannel());
            requestOTP.setPhoneNumber(phoneNumber);
            requestOTP.setCountryCode(MicroserviceEnum.OTP_COUNTRY_CODE.getMessage());
            requestOTP.setOtpSender(body.getAdditionalInfo().getOtpSender());
            requestOTP.setProductCode("");

            restService.startRequest("POST",urlRequestOtp,requestOTP);
            ResponseModel resp = restService.callAPI(urlRequestOtp, "POST", requestOTP);
            log.info(StringUtil.toString(resp.getPayload()));
            ResponseSendOTP responseOTP = mapper.convertValue(resp.getPayload(), ResponseSendOTP.class);
            if(responseOTP.getStatus().equals("SUCCESS")){
                return HandlerUtil.success(responseOTP);
            }

            return HandlerUtil.externalAPIError("Gagal Kirim OTP",responseOTP);
        } catch (RedisException e){
            return HandlerUtil.redisError(e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage()+ e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }

    public ResponseEntity<Object> validateOtp(RequestCardlink body){
       try {
           log.info("validate otp request{}", body);
           mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
           String respRedis = redisService.get(body.getAdditionalInfo().getAuthCode());
           if (respRedis == null) {
               ResponseExpiredRedis responseExpiredRedis = new ResponseExpiredRedis();
               responseExpiredRedis.setResponseCode(MicroserviceEnum.RDS_EXPITED_GET.getResponseCode());
               responseExpiredRedis.setResponseMessage(MicroserviceEnum.RDS_EXPITED_GET.getMessage());
               responseExpiredRedis.setAuthCode(body.getAdditionalInfo().getAuthCode());
               return HandlerUtil.notFound(responseExpiredRedis);
           }

           RequestRegistration response = mapper.readValue(respRedis, RequestRegistration.class);
           String phoneNumber = GeneralUtil.convertPhoneNumber(response.getPhoneNo());
           RequestValidateOTP reqValidateOTP = new RequestValidateOTP();
           reqValidateOTP.setChannel(response.getAdditionalInfo().getChannel());
           reqValidateOTP.setPhoneNumber(phoneNumber);
           reqValidateOTP.setCountryCode(MicroserviceEnum.OTP_COUNTRY_CODE.getMessage());
           reqValidateOTP.setOtpCode(body.getAdditionalInfo().getOtpCode());
           reqValidateOTP.setProductCode("");

           restService.startRequest("POST",urlValidateOtp,reqValidateOTP);
           ResponseModel resp = restService.callAPI(urlValidateOtp, "POST", reqValidateOTP);
           log.info(StringUtil.toString(resp.getPayload()));
           ResponseValidateOTP responseOTP = mapper.convertValue(resp.getPayload(), ResponseValidateOTP.class);
           if(responseOTP.getStatus().equals("SUCCESS")){
               return HandlerUtil.success(responseOTP);
           }

           return HandlerUtil.externalAPIError("Gagal Validasi OTP",responseOTP);
       } catch (RedisException e){
           return HandlerUtil.redisError(e.getMessage());
       } catch (APIException e){
           return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage()+ e.getPayload(),null);
       } catch (Exception ex){
           return HandlerUtil.generalError(ex.getMessage());
       }

    }
}
