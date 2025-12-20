package net.ryzen.paylinksystem.module.payment.bt.jpy.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "paylink.bank-transfer")
@Configuration
public class BankTransferProperties {
    private String generateBankNumberUrl;
}
