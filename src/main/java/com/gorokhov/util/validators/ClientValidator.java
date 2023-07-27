package com.gorokhov.util.validators;

import com.gorokhov.models.Client;
import com.gorokhov.services.ClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ClientValidator implements Validator {

    private final ClientsService clientsService;

    @Autowired
    public ClientValidator(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Client.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Client client = (Client) target;
        if (clientsService.findOne(client.getEmail()).isPresent())
            errors.rejectValue("email", "", "Клиент с таким email уже существует");
    }
}