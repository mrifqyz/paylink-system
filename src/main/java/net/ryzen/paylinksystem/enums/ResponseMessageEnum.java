package net.ryzen.paylinksystem.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseMessageEnum {
    WRONG_USER_CREDENTIALS("Invalid email or password"),
    USER_ALREADY_EXISTS("Email already registered and active"),
    REGISTER_PASSWORD_NOT_MATCH("Password not match"),
    UNREGISTERED_USER("User is not registered in database. Please contact administrator"),
    INVALID_DATA("Invalid data input: %s"),
    DATA_NOT_FOUND("Data not found"),
    AMOUNT_NOT_MATCH("Total amount and item data not match"),
    TRX_UNPROCESSABLE("Transactions conflict"),
    TRX_ALREADY_SUCCESS("Transaction already success"),
    PAYMENT_CHANNEL_INACTIVE("Payment Channel Inactive. Please contact your payment initiator (merchant)");

    private final String message;

    ResponseMessageEnum(String message) {
        this.message = message;
    }

}
