package com.gorokhov.util.validators;

import com.gorokhov.models.Storage;
import com.gorokhov.services.StoragesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class StorageValidator implements Validator {

    private final StoragesService storagesService;

    @Autowired
    public StorageValidator(StoragesService storagesService) {
        this.storagesService = storagesService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Storage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Storage storage = (Storage) target;
        if (storagesService.findOne(storage.getAddress()).isPresent())
            errors.rejectValue("address", "", "Хранилище по такому адресу уже сущетвует");
    }
}