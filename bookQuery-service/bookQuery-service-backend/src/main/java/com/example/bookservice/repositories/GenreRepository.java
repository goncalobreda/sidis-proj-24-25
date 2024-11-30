package com.example.bookservice.repositories;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAll();

    Genre findByInterest(String interest);

    Genre save(Genre genre);
}