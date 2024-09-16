package com.example.h2.service;

import com.example.h2.response.BookResponse;
import com.example.h2.exception.BadRequestException;
import com.example.h2.exception.BookValidationException;
import com.example.h2.exception.NotFoundException;
import com.example.h2.model.Book;
import com.example.h2.model.Borrower;
import com.example.h2.model.EntityType;
import com.example.h2.repository.BorrowerRepository;
import com.example.h2.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

import static com.example.h2.exception.NotFoundException.BOOK_MESSAGE;

@Service
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;

    private static final String BOOK_VALIDATION_EXCEPTION_MESSAGE = "A book with ISBN %s must have the same title and author. Existing: %s by %s, Provided: %s by %s";

    @Transactional
    @Async
    public CompletableFuture<Book> saveBook(Book book) {
        return CompletableFuture.supplyAsync(() -> {
            List<Book> existingBooks = bookRepository.findByIsbn(book.getIsbn());

            if (!existingBooks.isEmpty()) {
                Book existingBook = existingBooks.get(0);
                if (!existingBook.getTitle().equals(book.getTitle()) ||
                        !existingBook.getAuthor().equals(book.getAuthor())) {
                    String errorMessage = String.format(BOOK_VALIDATION_EXCEPTION_MESSAGE, book.getIsbn(), existingBook.getTitle(), existingBook.getAuthor(), book.getTitle(), book.getAuthor());
                    logger.error(errorMessage);
                    throw new BookValidationException(errorMessage);
                }
            }

            return bookRepository.save(book);
        });
    }

    @Async
    public CompletableFuture<Optional<Book>> getBook(Long id) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findById(id));
    }

    @Transactional
    @Async
    public CompletableFuture<Long> deleteBook(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            if (!bookRepository.existsById(id)) {
                logger.error(String.format(BOOK_MESSAGE, id));
                throw new NotFoundException(EntityType.BOOK, id);
            }
            try {
                bookRepository.deleteById(id);
            } catch (Exception e) {
                logger.error("Failed to delete Book with id " + id);
                throw new RuntimeException("Failed to delete Book with id " + id, e);
            }
            return id;
        });
    }

    @Transactional
    @Async
    public CompletableFuture<String> borrowBook(Long bookId, Long borrowerId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            Optional<Borrower> borrowerOpt = borrowerRepository.findById(borrowerId);

            Book book = bookOpt.orElseThrow(() -> new NotFoundException(EntityType.BOOK, bookId));
            Borrower borrower = borrowerOpt.orElseThrow(() -> new NotFoundException(EntityType.BORROWER, borrowerId));

            if (borrower.getBorrowedBookIds().contains(bookId)) {
                logger.error("Book already borrowed by this member, borrowerId : {}, bookId: {}",borrowerId,bookId);
                throw new BadRequestException("Book already borrowed by this member.");
            }

            borrower.getBorrowedBookIds().add(bookId);
            borrowerRepository.save(borrower);
            return "Book borrowed successfully.";
        });
    }

    @Async
    public CompletableFuture<BookResponse> getAllUniqueBooks() {
        return CompletableFuture.supplyAsync(() -> {
            List<Book> allBooks = bookRepository.findAll();

            Set<String> seen = new HashSet<>();
            List<Book> uniqueBooks = allBooks.stream()
                    .filter(book -> seen.add(book.getIsbn() + "|" + book.getTitle() + "|" + book.getAuthor()))
                    .collect(Collectors.toList());

            return new BookResponse(uniqueBooks, uniqueBooks.size());
        });
    }

    @Transactional
    @Async
    public CompletableFuture<String> returnBook(Long bookId, Long borrowerId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Borrower> borrowerOpt = borrowerRepository.findById(borrowerId);

            if (borrowerOpt.isPresent()) {
                Borrower borrower = borrowerOpt.get();

                if (borrower.getBorrowedBookIds().remove(bookId)) {
                    borrowerRepository.save(borrower);
                    return "Book returned successfully.";
                }
                logger.error("This book was not borrowed by the member, borrowerId : {}, bookId: {}",borrowerId,bookId);
                throw new BadRequestException("This book was not borrowed by the member.");
            }
            throw new NotFoundException(EntityType.BOOK, bookId);
        });
    }
}
