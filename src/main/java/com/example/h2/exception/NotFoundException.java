package com.example.h2.exception;

import com.example.h2.model.EntityType;

public class NotFoundException extends RuntimeException {
    public static final String BOOK_MESSAGE = "Book with ID %d not found.";
    public static final String BORROWER_MESSAGE = "Borrower with ID %d not found.";

    public NotFoundException(EntityType entityType, Long id) {
        super(getMessage(entityType, id));
    }

    private static String getMessage(EntityType entityType, Long id) {
        return switch (entityType) {
            case BOOK -> String.format(BOOK_MESSAGE, id);
            case BORROWER -> String.format(BORROWER_MESSAGE, id);
        };
    }
}

