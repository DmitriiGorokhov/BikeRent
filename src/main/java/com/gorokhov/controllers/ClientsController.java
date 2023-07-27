package com.gorokhov.controllers;

import com.gorokhov.models.Client;
import com.gorokhov.models.Comment;
import com.gorokhov.services.ClientsService;
import com.gorokhov.util.ErrorResponse;
import com.gorokhov.util.exceptions.ClientNotCreatedException;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import com.gorokhov.util.exceptions.ClientNotUpdatedException;
import jakarta.validation.Valid;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/clients")
public class ClientsController {

    private final ClientsService clientsService;

    @Autowired
    public ClientsController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Client client, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                                            .append(" - ")
                                            .append(e.getDefaultMessage())
                                            .append("; "));
            throw new ClientNotCreatedException(errorMessage.toString());
        }
        clientsService.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    // For call LazyInitializationException
    @GetMapping("/{id}/lazy")
    public Client getLazy(@PathVariable("id") long id) {
        Client client = clientsService.findOne(id).orElseThrow(ClientNotFoundException::new);

        for (Comment c : client.getComments())
            System.out.println("Description: " + c.getDescription());

        return client;
    }

    @GetMapping()
    public Set<Client> getAll() {
        return clientsService.findAll();
    }


    // For EntityGraph
    @GetMapping("/search")
    public Set<Client> getAllByNameContaining(@RequestParam String name) {
        Set<Client> clients = clientsService.findAllByNameContaining(name);

        System.out.println("BEFORE CALL");
        for (Client c : clients)
            System.out.println("Name: " + c.getName() + ". " + c.getComments());

        return clients;
    }

    @GetMapping("/{id}")
    public Client get(@PathVariable("id") long id) {
        return clientsService.findOne(id).orElseThrow(ClientNotFoundException::new);
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") long id,
                                             @RequestBody @Valid Client client,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                                            .append(" - ")
                                            .append(e.getDefaultMessage())
                                            .append("; "));
            throw new ClientNotUpdatedException(errorMessage.toString());
        }
        clientsService.update(id, client);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpStatus.ACCEPTED);
    }

    @PostMapping("/hibernate/create")
    public ResponseEntity<HttpStatus> createWithHibernate(@RequestBody @Valid Client client, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                    .append(" - ")
                    .append(e.getDefaultMessage())
                    .append("; "));
            throw new ClientNotCreatedException(errorMessage.toString());
        }
        clientsService.saveWithHibernate(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    @GetMapping("/hibernate/{id}")
    public Client getWithHibernate(@PathVariable("id") long id) {
        return clientsService.findOneWithHibernate(id);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ClientNotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ClientNotCreatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ClientNotUpdatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(LazyInitializationException e) {
        ErrorResponse response = new ErrorResponse("LAZY INIT EXCEPTION: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}