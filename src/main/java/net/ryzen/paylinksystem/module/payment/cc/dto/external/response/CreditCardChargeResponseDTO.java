package net.ryzen.paylinksystem.module.payment.cc.dto.external.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardChargeResponseDTO {
    private String clientId;
    private String requestId;
    private String invoiceNumber;
    private BigInteger amount;
    private String currency;
    private String status;
    private String uuid;
    private String requestTimestamp;
    private Map<String, Object> additionalInfo;
    private String url;
}
