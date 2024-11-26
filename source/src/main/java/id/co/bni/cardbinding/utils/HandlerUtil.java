package id.co.bni.cardbinding.utils;

import id.co.bni.cardbinding.configs.MicroserviceEnum;
import id.co.bni.cardbinding.models.microservice.ResponseModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HandlerUtil {
    public static ResponseEntity<Object> success(Object payload) {
        log.info(StringUtil.toString(ResponseModel.builder()
                .isSuccess(true)
                .payload(payload)
                .responseCode(MicroserviceEnum.SUCCESS.getResponseCode())
                .responseMessage(MicroserviceEnum.SUCCESS.getMessage())
                .build()));
        return ResponseEntity.ok(ResponseModel.builder()
                .isSuccess(true)
                .payload(payload)
                .responseCode(MicroserviceEnum.SUCCESS.getResponseCode())
                .responseMessage(MicroserviceEnum.SUCCESS.getMessage())
                .build());
    }

    public static ResponseEntity<Object> alreadyUse() {
        log.info(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false)
                .payload(null)
                .responseCode(MicroserviceEnum.ALREADY_USE.getResponseCode())
                .responseMessage(MicroserviceEnum.ALREADY_USE.getMessage())
                .build()));
        return ResponseEntity.ok(ResponseModel.builder()
                .isSuccess(false)
                .payload(null)
                .responseCode(MicroserviceEnum.ALREADY_USE.getResponseCode())
                .responseMessage(MicroserviceEnum.ALREADY_USE.getMessage())
                .build());
    }

    public static ResponseEntity<Object> signOff(Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.SIGN_OFF.getResponseCode())
                .responseMessage(MicroserviceEnum.SIGN_OFF.getMessage())
                .build()), HttpStatus.SERVICE_UNAVAILABLE);
        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.SIGN_OFF.getResponseCode())
                .responseMessage(MicroserviceEnum.SIGN_OFF.getMessage())
                .build(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    public static ResponseEntity<Object> externalAPIError(String message,Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.EXTERNAL_API_ERROR.getResponseCode())
                .responseMessage(message)
                .build()), HttpStatus.BAD_GATEWAY);
        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.EXTERNAL_API_ERROR.getResponseCode())
                .responseMessage(message)
                .build(), HttpStatus.BAD_GATEWAY);
    }

    public static ResponseEntity<Object> externalAPIBadRequest(Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.BAD_REQUEST.getResponseCode())
                .responseMessage(MicroserviceEnum.BAD_REQUEST.getMessage())
                .build()), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.BAD_REQUEST.getResponseCode())
                .responseMessage(MicroserviceEnum.BAD_REQUEST.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> databaseError(Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.DATABASE_ERROR.getResponseCode())
                .responseMessage(MicroserviceEnum.DATABASE_ERROR.getMessage())
                .build()), HttpStatus.BAD_GATEWAY);
        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.DATABASE_ERROR.getResponseCode())
                .responseMessage(MicroserviceEnum.DATABASE_ERROR.getMessage())
                .build(), HttpStatus.BAD_GATEWAY);
    }

    public static ResponseEntity<Object> redisError(Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.RDS_MESS_ERR.getResponseCode())
                .responseMessage(MicroserviceEnum.RDS_MESS_ERR.getMessage())
                .build()), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.RDS_MESS_ERR.getResponseCode())
                .responseMessage(MicroserviceEnum.RDS_MESS_ERR.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }


    public static ResponseEntity<Object> notFound(Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.NOT_FOUND.getResponseCode())
                .responseMessage(MicroserviceEnum.NOT_FOUND.getMessage())
                .build()), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.NOT_FOUND.getResponseCode())
                .responseMessage(MicroserviceEnum.NOT_FOUND.getMessage())
                .build(), HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<Object> generalError(Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false).payload(payload)
                .responseCode(MicroserviceEnum.GENERAL_ERROR.getResponseCode())
                .responseMessage(MicroserviceEnum.GENERAL_ERROR.getMessage())
                .build()), HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.GENERAL_ERROR.getResponseCode())
                .responseMessage(MicroserviceEnum.GENERAL_ERROR.getMessage())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<Object> unAuthorized(String message,Object payload) {
        log.warn(StringUtil.toString(ResponseModel.builder()
                .isSuccess(false).payload(payload)
                .responseCode(MicroserviceEnum.UNAUTHORIZED.getResponseCode())
                .responseMessage(message)
                .build()), HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(ResponseModel.builder()
                .isSuccess(false)
                .payload(payload)
                .responseCode(MicroserviceEnum.UNAUTHORIZED.getResponseCode())
                .responseMessage(message)
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<Object> unAuthorizedBin(Object payload) {
        log.info(StringUtil.toString(ResponseModel.builder()
                .isSuccess(true)
                .payload(payload)
                .responseCode(MicroserviceEnum.VALIDATEBIN.getResponseCode())
                .responseMessage(MicroserviceEnum.VALIDATEBIN.getMessage())
                .build()));
        return ResponseEntity.ok(ResponseModel.builder()
                .isSuccess(true)
                .payload(payload)
                .responseCode(MicroserviceEnum.VALIDATEBIN.getResponseCode())
                .responseMessage(MicroserviceEnum.VALIDATEBIN.getMessage())
                .build());
    }
}
