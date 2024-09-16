package com.example.h2.service;

import com.example.h2.exception.NotFoundException;
import com.example.h2.model.Borrower;
import com.example.h2.model.EntityType;
import com.example.h2.repository.BorrowerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.example.h2.exception.NotFoundException.BOOK_MESSAGE;


@Service
public class BorrowerService {

    private static final Logger logger = LoggerFactory.getLogger(BorrowerService.class);

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Transactional
    @Async
    public CompletableFuture<Borrower> registerBorrower(Borrower borrower) {
        return CompletableFuture.supplyAsync(() -> borrowerRepository.save(borrower));
    }

    @Transactional
    @Async
    public CompletableFuture<Long> removeBorrower(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            if (!borrowerRepository.existsById(id)) {
                logger.error(String.format(BOOK_MESSAGE,id));
                throw new NotFoundException(EntityType.BORROWER,id);
            }
            try {
                borrowerRepository.deleteById(id);
            } catch (Exception e) {
                String errorMessage = "Failed to remove Borrower with id "+ id;
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
            return id;
        });
    }

    @Async
    public CompletableFuture<Optional<Borrower>> getBorrowerById(Long id) {
        return CompletableFuture.supplyAsync(() -> borrowerRepository.findById(id));
    }
}