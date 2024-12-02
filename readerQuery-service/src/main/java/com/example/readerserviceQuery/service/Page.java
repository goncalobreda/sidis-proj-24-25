package com.example.readerserviceQuery.service;

public class Page {
    private int number;
    private int limit;

    public Page(int number, int limit) {
        this.number = number;
        this.limit = limit;
    }

    public int getNumber() {
        return number;
    }

    public int getLimit() {
        return limit;
    }
}
