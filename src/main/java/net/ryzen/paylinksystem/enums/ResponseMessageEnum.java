package net.ryzen.paylinksystem.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseMessageEnum {
    WRONG_USER_CREDENTIALS("Invalid username or password", HttpStatus.BAD_REQUEST),
    UNREGISTERED_USER("User is not registered in database. Please contact administrator", HttpStatus.FORBIDDEN),
    INVALID_DATA("Invalid data input", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("Data not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpCode;

    ResponseMessageEnum(String message, HttpStatus httpCode) {
        this.message = message;
        this.httpCode = httpCode;
    }

}
