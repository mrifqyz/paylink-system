package net.ryzen.paylinksystem.module.payment.bt.jpy.dto.external.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GenerateBankTransferCoreResponseDTO {
    private Transaction transaction;
    private String currency;
    private String accountNumber;
    private BankDetails bankDetails;
    private List<String> howToPay;
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

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BankDetails {
        private String bankName;
        private String bankBranch;
        private String accountType;
        private String recipientName;
    }
}
