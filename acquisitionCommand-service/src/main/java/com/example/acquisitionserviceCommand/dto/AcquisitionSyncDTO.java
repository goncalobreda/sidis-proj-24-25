package com.example.acquisitionserviceCommand.dto;

import java.io.Serializable;
import java.util.List;

public class AcquisitionSyncDTO implements Serializable {

    private String acquisitionId;
    private String status;
    private String originInstanceId;
    private String readerID;
    private String isbn;
    private String title;
    private String description;
    private String reason;
    private List<String> authorIds;
    private String genre;

    public AcquisitionSyncDTO() {}

    public AcquisitionSyncDTO(
            String acquisitionId,
            String status,
            String originInstanceId,
            String readerID,
            String isbn,
            String title,
            String description,
            String reason,
            List<String> authorIds,
            String genre) {
        this.acquisitionId = acquisitionId;
        this.status = status;
        this.originInstanceId = originInstanceId;
        this.readerID = readerID;
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.reason = reason;
        this.authorIds = authorIds;
        this.genre = genre;
    }

    // Getters e Setters
    public String getAcquisitionId() {
        return acquisitionId;
    }

    public void setAcquisitionId(String acquisitionId) {
        this.acquisitionId = acquisitionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriginInstanceId() {
        return originInstanceId;
    }

    public void setOriginInstanceId(String originInstanceId) {
        this.originInstanceId = originInstanceId;
    }

    public String getReaderID() {
        return readerID;
    }

    public void setReaderID(String readerID) {
        this.readerID = readerID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<String> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<String> authorIds) {
        this.authorIds = authorIds;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "AcquisitionSyncDTO{" +
                "acquisitionId='" + acquisitionId + '\'' +
                ", status='" + status + '\'' +
                ", originInstanceId='" + originInstanceId + '\'' +
                ", readerID='" + readerID + '\'' +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", reason='" + reason + '\'' +
                ", authorIds=" + authorIds +
                ", genre='" + genre + '\'' +
                '}';
    }
}
