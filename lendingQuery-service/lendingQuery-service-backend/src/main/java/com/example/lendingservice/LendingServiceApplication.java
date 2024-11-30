package com.example.lendingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@ActiveProfiles("bootstrap")
public class LendingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LendingServiceApplication.class, args);
    }
}
