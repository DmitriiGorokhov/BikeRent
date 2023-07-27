package com.gorokhov.controllers;

import com.gorokhov.models.Bike;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.Color;
import com.gorokhov.services.StoragesService;
import com.gorokhov.util.ErrorResponse;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/storages")
public class StoragesController {

    private final StoragesService storagesService;

    @Autowired
    public StoragesController(StoragesService storagesService) {
        this.storagesService = storagesService;
    }

    @GetMapping()
    public Set<Storage> getAll() {
        return storagesService.findAll();
    }

    @GetMapping("/{id}")
    public Storage get(@PathVariable("id") long id) {
        return storagesService.findOne(id).orElseThrow(StorageNotFoundException::new);
    }

    @GetMapping("/{id}/lazy")
    public Set<Bike> getRedBikes(@PathVariable("id") long id) {
        Storage storage = storagesService.findOne(id).orElseThrow(StorageNotFoundException::new);

        Set<Bike> bikes = storage.getBikes();
        Set<Bike> result = new HashSet<>();
        for (Bike bike : bikes) {
            if (bike.getColor().equals(Color.RED))
                result.add(bike);
        }
        return result;
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(StorageNotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}