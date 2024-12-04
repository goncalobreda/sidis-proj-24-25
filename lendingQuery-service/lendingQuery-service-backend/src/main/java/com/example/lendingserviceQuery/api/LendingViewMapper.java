package com.example.lendingserviceQuery.api;

import com.example.lendingserviceQuery.model.Lending;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LendingViewMapper {

    @Mapping(source = "lendingID", target = "id")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "expectedReturnDate", target = "expectedReturnDate")
    @Mapping(source = "returnDate", target = "returnDate")
    @Mapping(source = "overdue", target = "overdue")
    @Mapping(source = "fine", target = "fine")
    @Mapping(source = "readerID", target = "readerID")  // Usar ID diretamente
    @Mapping(source = "bookID", target = "bookID")  // Usar ID diretamente
    @Mapping(target = "numberOfDaysInOverdue", expression = "java(calculateDaysInOverdue(lending))")
    @Mapping(source = "notes", target = "notes")
    LendingView toLendingView(Lending lending);

    Iterable<LendingView> toLendingView(Iterable<Lending> lendings);

    List<LendingView> toLendingView(List<Lending> lendings);

    default long calculateDaysInOverdue(Lending lending) {
        if (lending.getReturnDate() != null && lending.getReturnDate().isAfter(lending.getExpectedReturnDate())) {
            return java.time.temporal.ChronoUnit.DAYS.between(lending.getExpectedReturnDate(), lending.getReturnDate());
        } else if (lending.getReturnDate() == null && java.time.LocalDate.now().isAfter(lending.getExpectedReturnDate())) {
            return java.time.temporal.ChronoUnit.DAYS.between(lending.getExpectedReturnDate(), java.time.LocalDate.now());
        }
        return 0;
    }
}
