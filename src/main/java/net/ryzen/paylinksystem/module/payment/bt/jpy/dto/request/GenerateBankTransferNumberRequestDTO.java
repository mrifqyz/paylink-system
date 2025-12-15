package net.ryzen.paylinksystem.module.payment.bt.jpy.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.base.request.ServiceRequest;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GenerateBankTransferNumberRequestDTO implements ServiceRequest {
    @JsonIgnore
    private String tokenId;
    @JsonIgnore
    private String clientId;
    @JsonIgnore
    private String requestId;

    private String bankCode;
}
