package com.gorokhov.controllers;

import com.gorokhov.models.Address;
import com.gorokhov.services.AddressesService;
import com.gorokhov.util.ErrorResponse;
import com.gorokhov.util.exceptions.AddressNotCreatedException;
import com.gorokhov.util.exceptions.AddressNotFoundException;
import com.gorokhov.util.exceptions.AddressNotUpdatedException;
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
@RequestMapping("/addresses")
public class AddressesController {

    private final AddressesService addressesService;

    @Autowired
    public AddressesController(AddressesService addressesService) {
        this.addressesService = addressesService;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Address address, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                                            .append(" - ")
                                            .append(e.getDefaultMessage())
                                            .append("; "));
            throw new AddressNotCreatedException(errorMessage.toString());
        }
        addressesService.save(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    @GetMapping()
    public Set<Address> getAll() {
        return addressesService.findAll();
    }

    @GetMapping("/{id}")
    public Address get(@PathVariable("id") long id) {
        return addressesService.findOne(id).orElseThrow(AddressNotFoundException::new);
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") long id,
                          @RequestBody @Valid Address address,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                                            .append(" - ")
                                            .append(e.getDefaultMessage())
                                            .append("; "));
            throw new AddressNotUpdatedException(errorMessage.toString());
        }
        addressesService.update(id, address);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpStatus.ACCEPTED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AddressNotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AddressNotCreatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AddressNotUpdatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}