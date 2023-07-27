package com.gorokhov.repositories;

import com.gorokhov.models.Address;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.util.exceptions.AddressNotFoundException;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Set;

@DataJpaTest
public class StoragesRepositoryTest {

    private final AddressesRepository addressesRepository;
    private final StoragesRepository storagesRepository;

    @Autowired
    public StoragesRepositoryTest(AddressesRepository addressesRepository, StoragesRepository storagesRepository) {
        this.addressesRepository = addressesRepository;
        this.storagesRepository = storagesRepository;
    }

    @Test
    public void givenNewAddress_whenFindById_thenReturnStorageWithCorrectId() {
        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 18;
        Address address = new Address(city, street, house);
        Address expectedAddress = addressesRepository.save(address);
        long expectedId = expectedAddress.getId();

        storagesRepository.save(new Storage(expectedAddress));
        Storage foundStorage = storagesRepository.findById(expectedId)
                .orElseThrow(StorageNotFoundException::new);

        Assertions.assertEquals(expectedAddress.getId(), foundStorage.getId());
    }

    @Test
    public void givenNewAddress_whenFindByAddress_thenReturnStorageWithCorrectAddress() {
        City city = City.KRASNODAR;
        String street = "Svoboda";
        int house = 27;
        Address address = new Address(city, street, house);
        Address expectedAddress = addressesRepository.save(address);

        storagesRepository.save(new Storage(expectedAddress));
        Storage foundStorage = storagesRepository.findByAddress(expectedAddress)
                .orElseThrow(StorageNotFoundException::new);

        Assertions.assertEquals(expectedAddress, foundStorage.getAddress());
    }

    @Test
    public void givenNewAddress_whenFindAllStorages_thenReturnMoreThanZero() {
        City city = City.NIZHNY_NOVGOROD;
        String street = "Vaneeva";
        int house = 72;
        Address address = new Address(city, street, house);
        Address expectedAddress = addressesRepository.save(address);

        storagesRepository.save(new Storage(expectedAddress));
        Set<Storage> foundStorages = new HashSet<>(storagesRepository.findAll());

        Assertions.assertTrue(foundStorages.size() > 0);
    }
}