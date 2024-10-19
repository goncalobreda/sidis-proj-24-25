package com.example.bookservice.service;

import com.example.bookservice.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class EditAuthorMapper {

    public abstract Author create(CreateAuthorRequest request);

    @Mapping(target = "authorID", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    public abstract void  update(EditAuthorRequest request, @MappingTarget Author author);

}