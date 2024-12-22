package com.example.readerserviceQuery.dto;

public class ReaderCountDTO {

    private String readerID;
    private String fullName; // Adicionado o fullName
    private Long readerCount;

    public ReaderCountDTO(String readerID, String fullName, Long readerCount) {
        this.readerID = readerID;
        this.fullName = fullName; // Novo campo
        this.readerCount = readerCount;
    }

    public ReaderCountDTO() {}

    public String getReaderID() {
        return readerID;
    }

    public void setReaderID(String readerID) {
        this.readerID = readerID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getReaderCount() {
        return readerCount;
    }

    public void setReaderCount(Long readerCount) {
        this.readerCount = readerCount;
    }
}
