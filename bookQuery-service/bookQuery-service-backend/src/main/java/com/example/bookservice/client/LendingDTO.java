package com.example.bookservice.client;

import java.time.LocalDate;

public class LendingDTO {

    private String lendingID;
    private Long bookID;


    public String getLendingID() {
        return lendingID;
    }

    public void setLendingID(String lendingID) {
        this.lendingID = lendingID;
    }

    public Long getBookID() {
        return bookID;
    }

    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }

}
