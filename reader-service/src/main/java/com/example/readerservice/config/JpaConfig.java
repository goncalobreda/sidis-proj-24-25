package com.example.readerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class JpaConfig {


    @Bean("auditorProvider")
    public AuditorAware<String> auditorProvider() {
        // TODO for public anonymous access, it should not be "SYSTEM" but "PUBLIC"
        return () -> Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication)
                .map(Authentication::getName).or(() -> Optional.of("PUBLIC"));
    }
}
