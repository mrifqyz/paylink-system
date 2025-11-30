package net.ryzen.paylinksystem.module.auth.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "paylink.dashboard.auth")
@Configuration
public class DashboardAuthProperties {
    private String clientIdPrefix = "PMU-001-";
    private String sharedKeyPrefix = "SK-";
}
