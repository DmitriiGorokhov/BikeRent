package com.gorokhov.repositories;

import com.gorokhov.models.Address;
import com.gorokhov.models.enums.City;
import com.gorokhov.util.exceptions.AddressNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Set;

@DataJpaTest
public class AddressesRepositoryTest {

    private final AddressesRepository addressesRepository;

    @Autowired
    public AddressesRepositoryTest(AddressesRepository addressesRepository) {
        this.addressesRepository = addressesRepository;
    }

    @Test
    public void givenNewAddress_whenFindById_thenReturnAddressWithCorrectId() {
        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 18;
        Address address = new Address(city, street, house);
        long expectedId = addressesRepository.save(address).getId();

        Address found = addressesRepository.findById(expectedId)
                .orElseThrow(AddressNotFoundException::new);

        Assertions.assertEquals(expectedId, found.getId());
    }

    @Test
    public void givenNewAddress_whenFindByCityStreetHouse_thenReturnAddressWithCorrectCity() {
        City city = City.KRASNODAR;
        String street = "Svoboda";
        int house = 27;
        Address address = new Address(city, street, house);
        addressesRepository.save(address);

        Address found = addressesRepository.findByCityAndStreetAndHouse(city, street, house)
                .orElseThrow(AddressNotFoundException::new);

        Assertions.assertEquals(city, found.getCity());
    }

    @Test
    public void givenNewAddress_whenFindByCityStreetHouse_thenReturnAddressWithCorrectStreet() {
        City city = City.NOVOSIBIRSK;
        String street = "Novaya";
        int house = 30;
        Address address = new Address(city, street, house);
        addressesRepository.save(address);

        Address found = addressesRepository.findByCityAndStreetAndHouse(city, street, house)
                .orElseThrow(AddressNotFoundException::new);

        Assertions.assertEquals(street, found.getStreet());
    }

    @Test
    public void givenNewAddress_whenFindByCityStreetHouse_thenReturnAddressWithCorrectHouse() {
        City city = City.EKATERINBURG;
        String street = "Staraya";
        int house = 186;
        Address address = new Address(city, street, house);
        addressesRepository.save(address);

        Address found = addressesRepository.findByCityAndStreetAndHouse(city, street, house)
                .orElseThrow(AddressNotFoundException::new);

        Assertions.assertEquals(house, found.getHouse());
    }

    @Test
    public void givenNewAddress_whenFindAllAddresses_thenReturnMoreThanZero() {
        City city = City.NIZHNY_NOVGOROD;
        String street = "Vaneeva";
        int house = 72;
        Address address = new Address(city, street, house);
        addressesRepository.save(address);

        Set<Address> foundAddresses = new HashSet<>(addressesRepository.findAll());

        Assertions.assertTrue(foundAddresses.size() > 0);
    }
}