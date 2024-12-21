package com.example.readerserviceQuery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartialUpdateDTO {

    private String readerID;
    private String phoneNumber;
    private String originInstanceId;
    private String messageId;

    public PartialUpdateDTO() {
    }

    public PartialUpdateDTO(String readerID, String phoneNumber, String originInstanceId) {
        this.readerID = readerID;
        this.phoneNumber = phoneNumber;
        this.originInstanceId = originInstanceId;
    }

    @Override
    public String toString() {
        return "PartialUpdateDTO{" +
                "readerId='" + readerID + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", originInstanceId='" + originInstanceId + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
