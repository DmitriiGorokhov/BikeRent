package com.gorokhov.services;

import com.gorokhov.models.Address;
import com.gorokhov.models.Bike;
import com.gorokhov.models.Client;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import com.gorokhov.repositories.BikesRepository;
import com.gorokhov.repositories.ClientsRepository;
import com.gorokhov.repositories.StoragesRepository;
import com.gorokhov.util.exceptions.BikeNotFoundException;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import com.gorokhov.util.exceptions.ClientNotUpdatedException;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BikesServiceTest {

    @Mock
    private BikesRepository bikesRepository;

    @Mock
    private StoragesRepository storagesRepository;

    @InjectMocks
    private BikesService bikesService;

    @Test
    public void givenBike_whenSaveBike_thenReturnBike() {
        long addressId = 1L;
        City city = City.NIZHNY_NOVGOROD;
        String street = "Vaneeva";
        int house = 138;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 1L;
        Color color = Color.RED;
        Size size = Size.L;
        Label label = Label.STELS;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        given(bikesRepository.save(bike)).willReturn(bike);

        Bike saved = bikesService.save(bike);

        assertNotNull(saved);
        assertEquals(bike, saved);
        verify(bikesRepository, times(1)).save(bike);
        reset(bikesRepository);
    }

    @Test
    public void givenBike_whenGetById_thenReturnBike() {
        long addressId = 2L;
        City city = City.NOVOSIBIRSK;
        String street = "Lenina";
        int house = 47;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 2L;
        Color color = Color.BLUE;
        Size size = Size.S;
        Label label = Label.SALSA;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        given(bikesRepository.findById(bikeId)).willReturn(Optional.of(bike));

        Bike found = bikesService.findOne(bikeId).orElseThrow(BikeNotFoundException::new);

        assertEquals(bike, found);
        verify(bikesRepository, times(1)).findById(bikeId);
        reset(bikesRepository);
    }

    @Test
    public void givenCBikes_whenGetAllBikes_thenReturnBikesList() {
        long addressId = 3L;
        City city = City.KRASNODAR;
        String street = "Letnaya";
        int house = 24;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId1 = 3L;
        Color color1 = Color.GREY;
        Size size1 = Size.XS;
        Label label1 = Label.TREK_BICYCLE;
        Bike bike1 = new Bike(color1, size1, label1, storage);
        bike1.setId(bikeId1);

        long bikeId2 = 4L;
        Color color2 = Color.WHITE;
        Size size2 = Size.M;
        Label label2 = Label.MONGOOSE;
        Bike bike2 = new Bike(color2, size2, label2, storage);
        bike2.setId(bikeId2);

        given(bikesRepository.findAll()).willReturn(List.of(bike1, bike2));

        Set<Bike> found = bikesService.findAll();

        assertNotNull(found);
        assertEquals(2, found.size());
        verify(bikesRepository, times(1)).findAll();
        reset(bikesRepository);
    }

    @Test
    public void givenBike_whenUpdateBike_thenReturnUpdatedBike() {
        long addressId = 5L;
        City city = City.EKATERINBURG;
        String street = "Centralnaya";
        int house = 70;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 5L;
        Color color = Color.BLACK;
        Size size = Size.XXL;
        Label label = Label.SCOTT;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);
        storage.setBikes(bikes);

        Color newColor = Color.WHITE;
        Size newSize = Size.L;

        given(bikesRepository.findById(bikeId)).willReturn(Optional.of(bike));
        given(storagesRepository.findById(addressId)).willReturn(Optional.of(storage));

        bike.setColor(newColor);
        bike.setSize(newSize);
        Bike updated = bikesService.update(bikeId, bike);

        assertEquals(newColor, updated.getColor());
        assertEquals(newSize, updated.getSize());
        verify(bikesRepository, times(1)).findById(bikeId);
        verify(storagesRepository, times(1)).findById(addressId);
        reset(bikesRepository);
        reset(storagesRepository);
    }

    @Test
    public void givenBike_whenUpdateBike_thenThrowBikeNotFoundException() {
        long addressId = 6L;
        City city = City.SAINT_PETERSBURG;
        String street = "Novaya";
        int house = 82;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 6L;
        Color color = Color.BLUE;
        Size size = Size.L;
        Label label = Label.CANNONDALE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        given(bikesRepository.findById(bikeId)).willThrow(BikeNotFoundException.class);

        assertThrows(BikeNotFoundException.class, () -> bikesService.update(bikeId, bike));
        verify(bikesRepository, times(1)).findById(bikeId);
        reset(bikesRepository);
    }

    @Test
    public void givenBike_whenUpdateBike_thenThrowStorageNotFoundException() {
        long addressId = 7L;
        City city = City.NOVOSIBIRSK;
        String street = "Letnii";
        int house = 70;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 7L;
        Color color = Color.YELLOW;
        Size size = Size.XXL;
        Label label = Label.TREK_BICYCLE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        given(bikesRepository.findById(bikeId)).willReturn(Optional.of(bike));
        given(storagesRepository.findById(addressId)).willThrow(StorageNotFoundException.class);

        assertThrows(StorageNotFoundException.class, () -> bikesService.update(bikeId, bike));
        verify(bikesRepository, times(1)).findById(bikeId);
        verify(storagesRepository, times(1)).findById(addressId);
        reset(bikesRepository);
        reset(storagesRepository);
    }
}