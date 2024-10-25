package com.example.authservice.usermanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> items;
}
