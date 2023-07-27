package com.gorokhov.repositories;

import com.gorokhov.models.Address;
import com.gorokhov.models.Bike;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import com.gorokhov.util.exceptions.BikeNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Set;

@DataJpaTest
public class BikesRepositoryTest {

    private final BikesRepository bikesRepository;
    private final AddressesRepository addressesRepository;
    private final StoragesRepository storagesRepository;

    @Autowired
    public BikesRepositoryTest(BikesRepository bikesRepository, AddressesRepository addressesRepository, StoragesRepository storagesRepository) {
        this.bikesRepository = bikesRepository;
        this.addressesRepository = addressesRepository;
        this.storagesRepository = storagesRepository;
    }

    @Test
    public void givenNewBike_whenFindById_thenReturnBikeWithCorrectId() {
        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 18;
        Address address = new Address(city, street, house);
        Address savedAddress = addressesRepository.save(address);

        Storage savedStorage = storagesRepository.save(new Storage(savedAddress));

        Color color = Color.RED;
        Size size = Size.L;
        Label label = Label.STELS;
        Bike bike = new Bike(color, size, label, savedStorage);
        long expectedId = bikesRepository.save(bike).getId();

        Bike found = bikesRepository.findById(expectedId)
                .orElseThrow(BikeNotFoundException::new);

        Assertions.assertEquals(expectedId, found.getId());
    }

    @Test
    public void givenNewBike_whenFindAllBikes_thenReturnMoreThanZero() {
        City city = City.KRASNODAR;
        String street = "Svoboda";
        int house = 424;
        Address address = new Address(city, street, house);
        Address savedAddress = addressesRepository.save(address);

        Storage savedStorage = storagesRepository.save(new Storage(savedAddress));

        Color color1 = Color.GREEN;
        Size size1 = Size.XS;
        Label label1 = Label.FORWARD;
        Bike bike1 = new Bike(color1, size1, label1, savedStorage);
        bikesRepository.save(bike1);

        Color color2 = Color.BLUE;
        Size size2 = Size.M;
        Label label2 = Label.TREK_BICYCLE;
        Bike bike2 = new Bike(color2, size2, label2, savedStorage);
        bikesRepository.save(bike2);

        Set<Bike> found = new HashSet<>(bikesRepository.findAll());

        Assertions.assertTrue(found.size() > 0);
    }
}