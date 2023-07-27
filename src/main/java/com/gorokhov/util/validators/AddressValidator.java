package com.gorokhov.util.validators;

import com.gorokhov.models.Address;
import com.gorokhov.services.AddressesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AddressValidator implements Validator {

    private final AddressesService addressesService;

    @Autowired
    public AddressValidator(AddressesService addressesService) {
        this.addressesService = addressesService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Address.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Address address = (Address) target;
        if (addressesService.findOne(address.getCity(), address.getStreet(), address.getHouse()).isPresent())
            errors.rejectValue("house", "", "Такой адрес уже существует");
    }
}