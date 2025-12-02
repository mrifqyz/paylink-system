package net.ryzen.paylinksystem.module.checkout.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "paylink.payment.generate")
@Configuration
public class PaymentGenerateProperties {
    private String baseFrontendUrl;
}
