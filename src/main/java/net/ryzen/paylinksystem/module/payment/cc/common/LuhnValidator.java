package net.ryzen.paylinksystem.module.payment.cc.common;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.InvalidDataException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LuhnValidator {
    public static void isValid(String cardNumber) {
        int sum = 0;
        int cardLength = cardNumber.length();
        int parity = cardLength % 2;

        if (cardNumber.startsWith("1")) return;

        for (int i = cardLength - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (i % 2 == parity) {
                digit *= 2;
            }

            if (digit > 9) {
                digit -= 9;
            }

            sum += digit;
        }

        if (sum % 10 != 0) {
            throw new InvalidDataException(ResponseMessageEnum.INVALID_CARD_NUMBER.getMessage());
        }
    }
}
