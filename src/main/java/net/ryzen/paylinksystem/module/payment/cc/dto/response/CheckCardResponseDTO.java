package net.ryzen.paylinksystem.module.payment.cc.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckCardResponseDTO {
    private Boolean isValid;
    private Boolean isUse3ds;
    private Boolean allowInstallment;
    private List<String> installmentOptions;
}
