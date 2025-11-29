package net.ryzen.paylinksystem.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "auth.core.jwt")
@Configuration
public class JwtTokenProperties {
    private Long accessTokenExpiry;
    private Long refreshTokenExpiry;
    private String secretKey;
}
