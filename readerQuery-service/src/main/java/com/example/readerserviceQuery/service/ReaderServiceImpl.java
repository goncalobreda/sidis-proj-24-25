package com.example.readerserviceQuery.service;

import com.example.readerserviceQuery.dto.PartialUpdateDTO;
import com.example.readerserviceQuery.dto.UserSyncDTO;
import com.example.readerserviceQuery.model.Reader;
import com.example.readerserviceQuery.dto.ReaderCountDTO;
import com.example.readerserviceQuery.repositories.ReaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReaderServiceImpl.class);


    public ReaderServiceImpl(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Override
    public List<Reader> findAll() {
        return readerRepository.findAll();
    }

    @Override
    public Optional<Reader> getReaderByID(String readerID) {
        return readerRepository.findByReaderID(readerID);
    }

    @Override
    public Optional<Reader> getReaderByEmail(String email) {
        return readerRepository.findByEmail(email);
    }

    @Override
    public List<Reader> getReaderByName(String fullName) {
        return readerRepository.findByName(fullName);
    }

    @Override
    public List<ReaderCountDTO> findTop5Readers() {
        return readerRepository.findTop5Readers();
    }

    @Override
    public Set<String> getInterestsByReader(Reader reader) {
        return reader.getInterests();
    }

    public Reader createFromUserSyncDTO(UserSyncDTO userSyncDTO) {
        logger.info("Criando Reader a partir de UserSyncDTO: {}", userSyncDTO);

        if (readerRepository.existsByEmail(userSyncDTO.getUsername())) {
            return readerRepository.findByEmail(userSyncDTO.getUsername()).get();
        }

        Reader reader = new Reader();
        reader.setEmail(userSyncDTO.getUsername());
        reader.setFullName(userSyncDTO.getFullName());
        reader.setPassword(userSyncDTO.getPassword());
        reader.setEnabled(userSyncDTO.isEnabled());
        reader.setPhoneNumber(userSyncDTO.getPhoneNumber());

        logger.info("Criando Reader: username={}, phoneNumber={}", reader.getEmail(), reader.getPhoneNumber());

        // Definir o readerID
        reader.setUniqueReaderID();
        reader.setBirthdate(String.valueOf(LocalDate.of(2000, 1, 1)));

        return readerRepository.save(reader);
    }


    public void applyPartialUpdate(PartialUpdateDTO partialUpdateDTO) {
        logger.info("Aplicando partial update no Query: readerID={}, phoneNumber={}",
                partialUpdateDTO.getReaderID(), partialUpdateDTO.getPhoneNumber());


        Reader reader = readerRepository.findByReaderID(partialUpdateDTO.getReaderID())
                .orElseGet(() -> {
                    // Se desejar criar caso não exista
                    Reader newReader = new Reader();
                    newReader.setUniqueReaderID(); // ou setReaderID(partialUpdateDTO.getReaderID())
                    newReader.setReaderID(partialUpdateDTO.getReaderID());
                    logger.info("Reader não encontrado no Query - criando um novo com ID={}", partialUpdateDTO.getReaderID());
                    return newReader;
                });

        reader.setPhoneNumber(partialUpdateDTO.getPhoneNumber());

        readerRepository.save(reader);

        logger.info("Reader (Query) {} atualizado com phoneNumber={}",
                partialUpdateDTO.getReaderID(), partialUpdateDTO.getPhoneNumber());
    }



    // Implementação do metodo searchReaders para consulta
    @Override
    public List<Reader> searchReaders(Page page, SearchReadersQuery query) {
        // Verifica se a página e a query são nulas e define valores padrão
        if (page == null) {
            page = new Page(1, 10);
        }
        if (query == null) {
            query = new SearchReadersQuery("", "", "");
        }

        // Chama o repositório para realizar a busca com paginação e filtros
        return readerRepository.searchReaders(page, query);
    }
}
