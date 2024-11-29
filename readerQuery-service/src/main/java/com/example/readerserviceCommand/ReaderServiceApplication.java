package com.example.readerserviceCommand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ReaderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReaderServiceApplication.class, args);
    }

}
