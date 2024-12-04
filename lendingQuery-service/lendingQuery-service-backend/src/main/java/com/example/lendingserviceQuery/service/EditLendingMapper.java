package com.example.lendingserviceQuery.service;

import com.example.lendingserviceQuery.model.Lending;
import com.example.lendingserviceQuery.repositories.LendingRepository;
import jakarta.validation.ValidationException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class EditLendingMapper {

    @Autowired
    private LendingRepository lendingRepository;

    @Mapping(source = "returnDate", target = "returnDate")
    @Mapping(source = "notes", target = "notes")
    @Mapping(target = "lendingID", ignore = true) // Ignora o ID pois este não muda
    @Mapping(target = "version", ignore = true) // Ignora a versão pois ela será tratada automaticamente
    @Mapping(target = "bookID", ignore = true) // Ignora o ID do livro, pois ele não será atualizado neste caso
    @Mapping(target = "readerID", ignore = true) // Ignora o ID do leitor
    @Mapping(target = "startDate", ignore = true) // Data de início também não muda
    @Mapping(target = "expectedReturnDate", ignore = true) // A data esperada de devolução não será atualizada aqui
    @Mapping(target = "fine", ignore = true) // A multa será calculada com base no retorno do livro
    @Mapping(target = "overdue", ignore = true) // O status de atraso será atualizado de acordo com o retorno
    public abstract void update(EditLendingRequest request, @MappingTarget Lending lending);

    // Método para obter um lending pelo ID
    public Lending toLending(final String lendingID) {
        return lendingRepository.findByLendingID(lendingID)
                .orElseThrow(() -> new ValidationException("Select an existing lending"));
    }
}
