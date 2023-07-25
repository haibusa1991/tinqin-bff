package com.tinqin.bff.rest.controller;

import com.tinqin.bff.core.exception.ServiceUnavailableException;
import com.tinqin.bff.core.exception.StorageItemNotFoundException;
import com.tinqin.bff.core.exception.StoreItemNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class ItemControllerAdvise {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<String> handleValidatorException(MethodArgumentNotValidException e) {

        String errors = Arrays.stream(e.getDetailMessageArguments()).toList()
                .stream()
                .flatMap(listError -> Stream.of(listError.toString()))
                .toList()
                .stream()
                .map(error -> error.replace("[", ""))
                .map(error -> error.replace("]", ""))
                .filter(error -> error.length() > 0)
                .collect(Collectors.joining(System.lineSeparator()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        String referencedUuid = e.getConstraintViolations()
                .stream()
                .map(d -> d.getInvalidValue().toString())
                .collect(Collectors.joining(System.lineSeparator()));
        return new ResponseEntity<>(String.format("UUID '%s' is not valid.",referencedUuid), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseBody
    public ResponseEntity<String> handleServiceUnavailableException(ServiceUnavailableException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({StorageItemNotFoundException.class, StoreItemNotFoundException.class})
    @ResponseBody
    public ResponseEntity<String> handleServiceUnavailableException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
