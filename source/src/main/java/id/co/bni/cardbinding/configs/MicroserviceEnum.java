package id.co.bni.cardbinding.configs;

import lombok.Getter;

@Getter
public enum MicroserviceEnum {
    SIGN_OFF("Sign off is maintenance", "9080"),
    BAD_REQUEST("(Microservice) Bad request", "9009"),
    NOT_FOUND("(Microservice) Data not found", "9007"),
    GENERAL_ERROR("(Microservice) General error", "9008"),
    EXTERNAL_API_ERROR("(Microservice) Undefined", "9003"),
    VALIDATEBIN("Unauthorized CardNumber", "9006"),
    SUCCESS("Success", "9000"),
    UNAUTHORIZED("Unauthorized", "7000"),
    DATABASE_ERROR("(Microservice) Database error", "9005"),

    RDS_EXPITED_GET("Transaction expired", "4040100"),
    RDS_SUCCESS_SAVE("Request has been processed successfully", "2000100"),
    RDS_MESS_UNABLE_TO_CONN("Unable to connect Redis", "2000400"),
    RDS_MESS_ERR("error redis", "9004"),
    RDS_MESS_FAIL("failed connect to redis", "9004"),

    OTP_COUNTRY_CODE("62","Indonesia"),
    BINDING_STATUS("binding", "00"),
    UN_BINDING_STATUS("unbinding","99"),
    INVALID_TOKEN("InValid backCardToken","9004"),
    ALREADY_USE("Card / Account Already In Use","9004"),
    ERROR_THRIDPARTY("Error! ThridParty exception -> ","9004");

    private final String message;
    private final String responseCode;

    MicroserviceEnum(String message, String responseCode) {
        this.message = message;
        this.responseCode = responseCode;
    }
}
