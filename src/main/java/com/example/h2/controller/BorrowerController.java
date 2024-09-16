package com.example.h2.controller;

import com.example.h2.response.ApiResponse;
import com.example.h2.exception.NotFoundException;
import com.example.h2.model.Borrower;
import com.example.h2.model.EntityType;
import com.example.h2.service.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/borrower")
public class BorrowerController {

    @Autowired
    private BorrowerService borrowerService;

    @PostMapping
    public CompletableFuture<ResponseEntity<ApiResponse<Borrower>>> registerBorrower(@RequestBody Borrower borrower) {
        return borrowerService.registerBorrower(borrower)
                .thenApply(regBorrower ->
                        new ApiResponse<>(HttpStatus.OK.value(),HttpStatus.OK.getReasonPhrase(), regBorrower))
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> removeBorrower(@PathVariable Long id) {
        return borrowerService.removeBorrower(id)
                .thenApply(deletedId ->
                        new ApiResponse<>(HttpStatus.OK.value(),String.format("Successfully deleted Borrower with id %d", id), null) )
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Borrower>>> getBorrowerById(@PathVariable Long id) {
        return borrowerService.getBorrowerById(id)
                .thenApply(borrowerOpt -> borrowerOpt.map(borrower -> new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), borrower))
                        .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                        .orElseThrow(() -> new NotFoundException(EntityType.BORROWER, id)));
    }
}