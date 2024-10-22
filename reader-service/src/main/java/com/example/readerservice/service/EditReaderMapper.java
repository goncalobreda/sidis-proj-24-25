package com.example.readerservice.service;

import com.example.readerservice.model.Reader;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class EditReaderMapper {

    @Mapping(source = "interests", target = "interests", qualifiedByName = "stringToInterests")
    public abstract Reader create(CreateReaderRequest request);

    @Mapping(target = "readerID", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(source = "interests", target = "interests", qualifiedByName = "stringToInterests")
    public abstract void update(com.example.readerservice.service.EditReaderRequest request, @MappingTarget Reader reader);

    @Named("stringToInterests")
    protected Set<String> stringToInterests(final Set<String> interests) {
        if (interests != null) {
            return new HashSet<>(interests);
        }
        return new HashSet<>();
    }
}
