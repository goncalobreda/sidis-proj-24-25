package com.example.lendingserviceCommand.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lending")
public class LendingConfiguration {

    private int maxDaysWithoutFine;
    private int finePerDay;

    // Getters and Setters
    public int getMaxDaysWithoutFine() {
        return maxDaysWithoutFine;
    }

    public void setMaxDaysWithoutFine(int maxDaysWithoutFine) {
        this.maxDaysWithoutFine = maxDaysWithoutFine;
    }

    public int getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(int finePerDay) {
        this.finePerDay = finePerDay;
    }
}
