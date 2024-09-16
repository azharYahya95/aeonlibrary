package com.example.h2.response;

import com.example.h2.model.Book;

import java.util.List;

public class BookResponse {

    private List<Book> books;
    private long totalBooks;

    public BookResponse() {}

    public BookResponse(List<Book> books, long totalBooks) {
        this.books = books;
        this.totalBooks = totalBooks;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }
}
