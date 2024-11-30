package com.example.bookservice.model;

public class TopAuthorLendingDTO {
    private String authorID;
    private String authorName;
    private Long lendingCount;

    public TopAuthorLendingDTO(String authorID, String authorName, Long lendingCount) {
        this.authorID = authorID;
        this.authorName = authorName;
        this.lendingCount = lendingCount;
    }

    // Getters e Setters
    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getLendingCount() {
        return lendingCount;
    }

    public void setLendingCount(Long lendingCount) {
        this.lendingCount = lendingCount;
    }
}
