package id.co.bni.cardbinding.exception;

import org.springframework.http.HttpStatusCode;

public class APIException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final HttpStatusCode errorCode;
    private final String message;
    private final Object payload;

    public APIException(HttpStatusCode errorCode, String message, Object payload) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.payload = payload;
    }

    public HttpStatusCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }
}
