package com.example.readerserviceCommand.infrastructure.repositories.impl;

import com.example.readerserviceCommand.model.Reader;
import com.example.readerserviceCommand.service.Page;
import com.example.readerserviceCommand.service.SearchReadersQuery;

import java.util.List;

public interface ReaderRepoCustom {
    List<Reader> searchReaders(Page page, SearchReadersQuery query);
}
