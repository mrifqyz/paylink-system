package net.ryzen.paylinksystem.module.dashboard.settings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentMethodDTO;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClientConfigGetResponseDTO {
    private String clientId;
    private String sharedKey;
    private Boolean isActive;
    private String createdDate;
    private String name;
    private String clientRedirectUrl;
    private Integer transactionDueDate;
    private Boolean notifyEmail;
    private Boolean notifySms;
    private Boolean notifyHttp;
    private String notifyHttpUrl;
    private String currency;
    private List<PaymentMethodDTO> paymentMethods;
}
