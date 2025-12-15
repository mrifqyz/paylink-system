package net.ryzen.paylinksystem.module.payment.bt.jpy.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.base.request.ServiceRequest;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GenerateBankTransferNumberResponseDTO {
    private String currency;
    private String accountNumber;
    private BankDetails bankDetails;
    private List<String> howToPay;

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BankDetails {
        private String bankName;
        private String bankBranch;
        private String accountType;
        private String recipientName;
    }
}
