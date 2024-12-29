package com.example.bookservice.dto;

import java.io.Serializable;

public class BookCreationResponseDTO implements Serializable {

    private String isbn;
    private boolean success;
    private String errorReason;

    public BookCreationResponseDTO() {
    }

    public BookCreationResponseDTO(String isbn, boolean success, String errorReason) {
        this.isbn = isbn;
        this.success = success;
        this.errorReason = errorReason;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    @Override
    public String toString() {
        return "BookCreationResponseDTO{" +
                "isbn='" + isbn + '\'' +
                ", success=" + success +
                ", errorReason='" + errorReason + '\'' +
                '}';
    }
}
