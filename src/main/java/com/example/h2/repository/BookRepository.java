package com.example.h2.repository;

import com.example.h2.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT DISTINCT b FROM Book b ORDER BY b.isbn, b.title, b.author")
    List<Book> findDistinctBooks();

    List<Book> findByIsbn(String isbn);
}
