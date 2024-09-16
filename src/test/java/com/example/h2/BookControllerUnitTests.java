package com.example.h2;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.example.h2.controller.BookController;
import com.example.h2.exception.BadRequestException;
import com.example.h2.exception.NotFoundException;
import com.example.h2.model.Book;
import com.example.h2.model.EntityType;
import com.example.h2.response.ApiResponse;
import com.example.h2.service.BookService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
@RunWith(SpringRunner.class)
@SpringBootTest
public class BookControllerUnitTests {

    @Autowired
    private BookController controller;

    @MockBean
    private BookService service;

    @Test
    public void addBook_ShouldReturnCreatedBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Sample Book");
        book.setAuthor("Sample Author");
        book.setIsbn("1234567890");

        ApiResponse<Book> apiResponse = new ApiResponse<>(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), book);
        when(service.saveBook(any(Book.class))).thenReturn(CompletableFuture.completedFuture(book));

        ResponseEntity<ApiResponse<Book>> response = controller.addBook(book).join(); // Use join() to block until CompletableFuture completes

        verify(service, times(1)).saveBook(any(Book.class));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void getBook_ShouldReturnBookWhenExists() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Sample Book");

        ApiResponse<Book> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), book);
        when(service.getBook(anyLong())).thenReturn(CompletableFuture.completedFuture(Optional.of(book)));

        ResponseEntity<ApiResponse<Book>> response = controller.getBook(1L).join(); // Use join() to block until CompletableFuture completes

        verify(service, times(1)).getBook(anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteBook_ShouldReturnNoContent() {
        ApiResponse<Object> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Successfully deleted Book with id 1", null);
        when(service.deleteBook(anyLong())).thenReturn(CompletableFuture.completedFuture(1L));

        ResponseEntity<ApiResponse<Object>> response = controller.deleteBook(1L).join(); // Use join() to block until CompletableFuture completes

        verify(service, times(1)).deleteBook(anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void borrowBook_ShouldReturnSuccessMessage() {
        ApiResponse<Object> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Book borrowed successfully.", null);
        when(service.borrowBook(anyLong(), anyLong())).thenReturn(CompletableFuture.completedFuture("Book borrowed successfully."));

        ResponseEntity<ApiResponse<Object>> response = controller.borrowBook(1L, 1L).join(); // Use join() to block until CompletableFuture completes

        verify(service, times(1)).borrowBook(anyLong(), anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void borrowBook_ShouldReturnBadRequestWhenAlreadyBorrowed() {
        when(service.borrowBook(anyLong(), anyLong()))
                .thenReturn(CompletableFuture.failedFuture(new BadRequestException("Book already borrowed by this member.")));

        ResponseEntity<ApiResponse<Object>> response = null;
        try {
            response = controller.borrowBook(1L, 1L).join(); // Use join() to block until CompletableFuture completes
        } catch (CompletionException e) {
            if (e.getCause() instanceof BadRequestException) {
                response = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getCause().getMessage(), null));
            } else {
                throw e;
            }
        }

        verify(service, times(1)).borrowBook(anyLong(), anyLong());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Book already borrowed by this member.", response.getBody().getStatusMessage());
    }

    @Test
    public void returnBook_ShouldReturnSuccessMessage() {
        ApiResponse<Object> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Book returned successfully.", null);
        when(service.returnBook(anyLong(), anyLong())).thenReturn(CompletableFuture.completedFuture("Book returned successfully."));

        ResponseEntity<ApiResponse<Object>> response = controller.returnBook(1L, 1L).join(); // Use join() to block until CompletableFuture completes

        verify(service, times(1)).returnBook(anyLong(), anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void returnBook_ShouldReturnNotFoundWhenBookDoesNotExist() {
        when(service.returnBook(anyLong(), anyLong())).thenReturn(CompletableFuture.failedFuture(new NotFoundException(EntityType.BOOK, 1L)));

        ResponseEntity<ApiResponse<Object>> response = null;
        try {
            response = controller.returnBook(1L, 1L).join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof NotFoundException) {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getCause().getMessage(), null));
            } else {
                throw e;
            }
        }

        verify(service, times(1)).returnBook(anyLong(), anyLong());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ID 1 not found.", response.getBody().getStatusMessage()); // Adjust based on your implementation
    }
}
