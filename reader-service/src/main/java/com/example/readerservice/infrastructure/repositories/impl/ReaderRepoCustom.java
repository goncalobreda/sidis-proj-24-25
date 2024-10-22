package com.example.readerservice.infrastructure.repositories.impl;

import com.example.readerservice.model.Reader;
import com.example.readerservice.service.Page;
import com.example.readerservice.service.SearchReadersQuery;

import java.util.List;

public interface ReaderRepoCustom {
    List<Reader> searchReaders(Page page, SearchReadersQuery query);
}
