package com.example.h2;

import com.example.h2.model.Book;
import com.example.h2.model.Borrower;
import com.example.h2.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class BookControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void testAddBookWithValidation() {
        // Add a book
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Author A");
        book.setIsbn("1234567890");

        restTemplate.postForEntity("http://localhost:" + port + "/books", book, Book.class);

        // Add another book with the same ISBN but different title/author
        Book invalidBook = new Book();
        invalidBook.setTitle("Different Title");
        invalidBook.setAuthor("Different Author");
        invalidBook.setIsbn("1234567890");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/books",
                invalidBook,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("A book with ISBN 1234567890 must have the same title and author.");
    }


    @Test
    public void testBorrowAndReturnBook() throws IOException {
        // Create a book
        Book book = new Book();
        book.setTitle("Integration Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("9876543210");

        ResponseEntity<ApiResponse<Book>> response = restTemplate.exchange(
                "http://localhost:" + port + "/books",
                HttpMethod.POST,
                new HttpEntity<>(book, new HttpHeaders()),
                new ParameterizedTypeReference<ApiResponse<Book>>() {}
        );

        assertThat(response.getBody()).isNotNull();
        Long bookId = response.getBody().getData().getId();

        Borrower borrower = new Borrower();
        borrower.setName("Azhar");
        borrower.setEmailAddress("azharusim95@gmail.com");

        ResponseEntity<ApiResponse<Borrower>> postBorrowerResponse = restTemplate.exchange(
                "http://localhost:" + port + "/borrower",
                HttpMethod.POST,
                new HttpEntity<>(borrower, new HttpHeaders()),
                new ParameterizedTypeReference<ApiResponse<Borrower>>() {}
        );

        assertThat(postBorrowerResponse.getBody()).isNotNull();
        Long borrowerId = postBorrowerResponse.getBody().getData().getId();

        // Borrow the book
        ResponseEntity<String> borrowResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/books/" + bookId + "/borrow/" + borrowerId,
                null,
                String.class
        );

        assertThat(borrowResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(borrowResponse.getBody()).contains("Book borrowed successfully.");

        // Try borrowing the book again
        ResponseEntity<String> duplicateBorrowResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/books/" + bookId + "/borrow/" + borrowerId,
                null,
                String.class
        );


        assertThat(duplicateBorrowResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(duplicateBorrowResponse.getBody()).contains("Book already borrowed by this member.");

        // Return the book
        ResponseEntity<String> returnResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/books/" + bookId + "/return/" + borrowerId,
                null,
                String.class
        );

        assertThat(returnResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(returnResponse.getBody()).contains("Book returned successfully.");

        // Try returning the book again
        ResponseEntity<String> duplicateReturnResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/books/" + bookId + "/return/" + borrowerId,
                null,
                String.class
        );

        assertThat(duplicateReturnResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(duplicateReturnResponse.getBody()).contains("This book was not borrowed by the member.");
    }


    @Test
    public void testBorrowNonExistentBook() {
        Borrower borrower = new Borrower();
        borrower.setName("Azhar");
        borrower.setEmailAddress("azharusim95@gmail.com");

        ResponseEntity<ApiResponse<Borrower>> postBorrowerResponse = restTemplate.exchange(
                "http://localhost:" + port + "/borrower",
                HttpMethod.POST,
                new HttpEntity<>(borrower, new HttpHeaders()),
                new ParameterizedTypeReference<ApiResponse<Borrower>>() {}
        );
        Long borrowerId = postBorrowerResponse.getBody().getData().getId();

        ResponseEntity<String> borrowResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/books/999999/borrow/" + borrowerId,
                null,
                String.class
        );

        assertThat(borrowResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(borrowResponse.getBody()).contains("Book with ID 999999 not found.");
    }
}
