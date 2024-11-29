package com.example.readerserviceCommand.model;

public class ReaderCountDTO {

    private String readerID;

    private Long readerCount;

    public ReaderCountDTO(String readerID, Long readerCount) {
        this.readerCount = readerCount;
        this.readerID = readerID;
    }

    public ReaderCountDTO(){}


    public String getReaderID() {
        return readerID;
    }

    public void setReaderID(String readerID) {
        this.readerID = readerID;
    }

    public Long getReaderCount() {
        return readerCount;
    }

    public void setReaderCount(Long readerCount) {
        this.readerCount = readerCount;
    }
}
