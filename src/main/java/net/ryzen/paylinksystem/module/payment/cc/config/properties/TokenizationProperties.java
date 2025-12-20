package net.ryzen.paylinksystem.module.payment.cc.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "paylink.tokenization")
@Configuration
public class TokenizationProperties {
    private String deleteTokenUrl;
    private String getTokenUrl;
}
