package com.example.lendingserviceCommand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@ActiveProfiles("bootstrap")
public class LendingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LendingServiceApplication.class, args);
    }
}
