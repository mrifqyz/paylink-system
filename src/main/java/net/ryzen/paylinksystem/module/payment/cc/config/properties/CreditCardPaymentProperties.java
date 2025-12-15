package net.ryzen.paylinksystem.module.payment.cc.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "paylink.credit-card")
@Configuration
public class CreditCardPaymentProperties {
    private String checkBinUrl;
    private String threeDoSecureUrl;
    private String creditCardChargeUrl;
    private String creditCard3dsCoreUrl;
    private String callbackUrl;
    private String frontendRedirectUrlSuccess;
    private String frontendRedirectUrlFailed;
}
