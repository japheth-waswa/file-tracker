package com.elijahwaswa.filetracker.config;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }
    @Bean
    public QrDataFactory qrDataFactory() {
        return new QrDataFactory(HashingAlgorithm.SHA256, 6, 30);
    }
    @Bean
    public QrGenerator qrGenerator() {
        return new ZxingPngQrGenerator();
    }
    @Bean
    public CodeVerifier codeVerifier() {
        return new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());
    }
}
