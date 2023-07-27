package com.gorokhov.controllers;

import com.gorokhov.models.Bike;
import com.gorokhov.services.BikesService;
import com.gorokhov.util.ErrorResponse;
import com.gorokhov.util.exceptions.BikeNotCreatedException;
import com.gorokhov.util.exceptions.BikeNotFoundException;
import com.gorokhov.util.exceptions.BikeNotUpdatedException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/bikes")
public class BikesController {

    private final BikesService bikesService;

    @Autowired
    public BikesController(BikesService bikesService) {
        this.bikesService = bikesService;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Bike bike, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                    .append(" - ")
                    .append(e.getDefaultMessage())
                    .append("; "));
            throw new BikeNotCreatedException(errorMessage.toString());
        }
        bikesService.save(bike);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    @GetMapping()
    public Set<Bike> getAll() {
        return bikesService.findAll();
    }

    @GetMapping("/{id}")
    public Bike getBike(@PathVariable("id") long id) {
        return bikesService.findOne(id).orElseThrow(BikeNotFoundException::new);
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") long id,
                                             @RequestBody @Valid Bike bike,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                                            .append(" - ")
                                            .append(e.getDefaultMessage())
                                            .append("; "));
            throw new BikeNotUpdatedException(errorMessage.toString());
        }
        bikesService.update(id, bike);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpStatus.ACCEPTED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(BikeNotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(BikeNotCreatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(BikeNotUpdatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}