package com.example.h2.controller;

import com.example.h2.response.ApiResponse;
import com.example.h2.response.BookResponse;
import com.example.h2.exception.NotFoundException;
import com.example.h2.model.Book;
import com.example.h2.model.EntityType;
import com.example.h2.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public CompletableFuture<ResponseEntity<ApiResponse<Book>>> addBook(@RequestBody Book book) {
        return bookService.saveBook(book)
                .thenApply(savedBook ->
                        new ApiResponse<>(HttpStatus.CREATED.value(),HttpStatus.CREATED.getReasonPhrase(), savedBook))
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Book>>> getBook(@PathVariable Long id) {
        return bookService.getBook(id)
                .thenApply(bookOpt -> bookOpt
                        .map(book -> new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), book))
                        .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                        .orElseThrow(() -> new NotFoundException(EntityType.BOOK, id)));
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id)
                .thenApply(deletedId ->
                        new ApiResponse<>(HttpStatus.OK.value(),String.format("Successfully deleted Book with id %d", id), null) )
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping("/{bookId}/borrow/{borrowerId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> borrowBook(@PathVariable Long bookId, @PathVariable Long borrowerId) {
        return bookService.borrowBook(bookId, borrowerId)
                .thenApply(message -> new ApiResponse<>(HttpStatus.OK.value(),message, null))
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping("/{bookId}/return/{borrowerId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> returnBook(@PathVariable Long bookId, @PathVariable Long borrowerId) {
        return bookService.returnBook(bookId, borrowerId)
                .thenApply(message -> new ApiResponse<>(HttpStatus.OK.value(),message, null))
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<ApiResponse<BookResponse>>> getAllUniqueBooks() {
        return bookService.getAllUniqueBooks()
                .thenApply(books ->
                        new ApiResponse<>(HttpStatus.OK.value(),HttpStatus.OK.getReasonPhrase(), books))
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }
}

