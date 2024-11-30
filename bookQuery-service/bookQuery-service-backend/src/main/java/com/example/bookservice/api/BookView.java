package com.example.bookservice.api;

import com.example.bookservice.model.Author;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "A Book")
public class BookView {
    @Schema(description = "The ISBN of the book")
    private String isbn;

    @Schema(description = "The title of the book")
    private String title;

    @Schema(description = "The genre of the book")
    private String genre;

    @Schema(description = "The description of the book")
    private String description;

    @Schema(description = "The author(s) of the book")
    private List<Author> author;

    @Schema(description = "The image URL of the book")
    private String imageUrl;

    @Schema(description = "The ID of the book")
    private Long bookID;

    private Long bookCount;

    public Long getBookCount() {
        return bookCount;
    }

    public void setBookCount(Long bookCount) {
        this.bookCount = bookCount;
    }
}
