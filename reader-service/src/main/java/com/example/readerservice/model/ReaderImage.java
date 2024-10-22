package com.example.readerservice.model;

import jakarta.persistence.*;

@Entity
public class ReaderImage {
    @Id
    @GeneratedValue
    private Long readerImageID;


    // in this case we are storing the image in the database, but it would be
    // "better" to store it in a server file system
    @Lob
    private byte[] image;

    @ManyToOne
    private Reader reader;

    private String contentType;

    public ReaderImage() {}

    public ReaderImage(Reader reader, byte[] image, String contentType) {
        this.reader = reader;
        this.image = image;
        this.contentType = contentType;
    }

    public Long getReaderImageID() {
        return readerImageID;
    }

    public void setReaderImageID(Long readerImageID) {
        this.readerImageID = readerImageID;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Reader getReader() {
        return reader;
    }
    public void setReader(Reader reader) {
        this.reader = reader;
    }
}
