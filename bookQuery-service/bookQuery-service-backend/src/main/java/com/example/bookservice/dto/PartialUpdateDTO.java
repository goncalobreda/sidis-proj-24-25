package com.example.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartialUpdateDTO {
    private Long bookID;
    private String title;
    private String description;
    private String originInstanceId;
}
