package com.example.readerserviceQuery.api;

import com.example.readerserviceQuery.model.Reader;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ReaderViewMapper {

    @Mapping(source = "readerImage.readerImageID", target = "imageUrl", qualifiedByName = "mapImageIdToUrl")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "readerID", target = "readerID") // Adiciona o mapeamento explícito do campo fullName
    public abstract ReaderView toReaderView(Reader reader);

    public abstract List<ReaderView> toReaderView(List<Reader> reader);

    public abstract Iterable<ReaderView> toReaderView(Iterable<Reader> reader);

    Set<String> map(Set<String> interests) {
        return new HashSet<>(interests);
    }

    @Named("mapImageIdToUrl")
    String mapImageIdToUrl(Long readerImageID) {
        return readerImageID != null ? "/api/readers/images/" + readerImageID : null;
    }
}

