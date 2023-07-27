package com.gorokhov.services;

import com.gorokhov.models.Address;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.repositories.AddressesRepository;
import com.gorokhov.util.exceptions.AddressNotFoundException;
import com.gorokhov.util.exceptions.AddressNotUpdatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AddressesService {

    private final AddressesRepository addressesRepository;

    @Autowired
    public AddressesService(AddressesRepository addressesRepository) {
        this.addressesRepository = addressesRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Address> findOne(long id) {
        return addressesRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Address> findOne(City city, String street, int house) {
        return addressesRepository.findByCityAndStreetAndHouse(city, street, house);
    }

    @Transactional(readOnly = true)
    public Set<Address> findAll() {
        return new HashSet<>(addressesRepository.findAll());
    }

    @Transactional
    public Address save(Address address) {
        address = addressesRepository.save(address);
        Storage storage = new Storage(address);
        address.setStorage(storage);
        return address;
    }

    @Transactional
    public Address update(long id, Address address) {
        City newCity = address.getCity();
        String newStreet = address.getStreet();
        int newHouse = address.getHouse();

        if (addressesRepository.findByCityAndStreetAndHouse(newCity, newStreet, newHouse).isPresent())
            throw new AddressNotUpdatedException("Адрес с такими данными уже существует");
        Address updatedAddress = addressesRepository.findById(id).orElseThrow(AddressNotFoundException::new);
        updatedAddress.setCity(newCity);
        updatedAddress.setStreet(newStreet);
        updatedAddress.setHouse(newHouse);
        return updatedAddress;
    }
}