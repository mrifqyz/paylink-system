package net.ryzen.paylinksystem.config;

import net.ryzen.paylinksystem.config.interceptor.AuthInterceptor;
import net.ryzen.paylinksystem.config.interceptor.SignatureVerificationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private SignatureVerificationInterceptor signatureVerificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(signatureVerificationInterceptor).order(1);
        registry.addInterceptor(authInterceptor).order(2);
    }
}
