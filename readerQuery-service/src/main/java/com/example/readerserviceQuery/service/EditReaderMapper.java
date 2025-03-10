package com.example.readerserviceQuery.service;

import com.example.readerserviceQuery.model.Reader;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class EditReaderMapper {

    @Mapping(source = "fullName", target = "fullName") // Mapear fullName para name
    @Mapping(source = "interests", target = "interests", qualifiedByName = "stringToInterests")
    @Mapping(target = "readerID", ignore = true)
    public abstract Reader create(CreateReaderRequest request);

    @Mapping(source = "readerID", target = "readerID")
    @Mapping(target = "enabled", ignore = true)
    @Mapping(source = "fullName", target = "fullName") // Mapear fullName para name
    @Mapping(source = "interests", target = "interests", qualifiedByName = "stringToInterests")
    public abstract void update(EditReaderRequest request, @MappingTarget Reader reader);

    @Named("stringToInterests")
    protected Set<String> stringToInterests(final Set<String> interests) {
        return interests != null ? new HashSet<>(interests) : new HashSet<>();
    }
}
