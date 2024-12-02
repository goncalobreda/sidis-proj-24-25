package com.example.readerserviceQuery.infrastructure.repositories.impl;

import com.example.readerserviceQuery.model.Reader;
import com.example.readerserviceQuery.service.Page;
import com.example.readerserviceQuery.service.SearchReadersQuery;

import java.util.List;

public interface ReaderRepoCustom {
    List<Reader> searchReaders(Page page, SearchReadersQuery query);
}
