package com.example.bookservice.service;

import com.example.bookservice.model.*;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    Author create(CreateAuthorRequest request);

    Author partialUpdate(String authorID, EditAuthorRequest request, long parseLong);

}
