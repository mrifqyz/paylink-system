package net.ryzen.paylinksystem.module.payment.cc.dto.external.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.module.payment.cc.dto.PaymentDataDTO;

import java.math.BigInteger;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreeDoSecurePaymentRequestDTO {
    private CreditCardChargeRequestDTO.Transaction transaction;
    private PaymentDataDTO cardData;
    private String uuid;
    private String requestTimestamp;
    private Map<String, Object> additionalInfo;

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Transaction {
        private String invoiceNumber;
        private BigInteger amount;
        private String currency;
    }
}
