package net.ryzen.paylinksystem.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseMessageEnum {
    WRONG_USER_CREDENTIALS("Invalid email or password", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS("Email already registered and active", HttpStatus.BAD_REQUEST),
    REGISTER_PASSWORD_NOT_MATCH("Password not match", HttpStatus.BAD_REQUEST),
    UNREGISTERED_USER("User is not registered in database. Please contact administrator", HttpStatus.FORBIDDEN),
    INVALID_DATA("Invalid data input: %s", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("Data not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpCode;

    ResponseMessageEnum(String message, HttpStatus httpCode) {
        this.message = message;
        this.httpCode = httpCode;
    }

}
