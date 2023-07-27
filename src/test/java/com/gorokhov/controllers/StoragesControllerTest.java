package com.gorokhov.controllers;

import com.gorokhov.models.Address;
import com.gorokhov.models.Bike;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import com.gorokhov.services.StoragesService;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoragesController.class)
public class StoragesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoragesService storagesService;

    @Test
    public void givenStorage_whenGetStorage_thenReturnJson() {
        long id = 1L;
        City city = City.NIZHNY_NOVGOROD;
        String street = "Vaneeva";
        int house = 138;
        Address address = new Address(city, street, house);
        address.setId(id);
        Storage storage = new Storage(address);
        storage.setId(id);

        given(storagesService.findOne(id)).willReturn(Optional.of(storage));

        try {
            mockMvc.perform(get("/storages/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$.address.house", is(storage.getAddress().getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(storagesService, times(1)).findOne(id);
        reset(storagesService);
    }

    @Test
    public void givenStorages_whenGetStorages_thenReturnJsonArray() {
        long id1 = 2L;
        City city1 = City.KRASNODAR;
        String street1 = "Novaya";
        int house1 = 29;
        Address address1 = new Address(city1, street1, house1);
        address1.setId(id1);
        Storage storage1 = new Storage(address1);
        storage1.setId(id1);

        long id2 = 3L;
        City city2 = City.SAINT_PETERSBURG;
        String street2 = "Nevskii";
        int house2 = 52;
        Address address2 = new Address(city2, street2, house2);
        address2.setId(id2);
        Storage storage2 = new Storage(address2);
        storage2.setId(id2);

        Set<Storage> storages = new LinkedHashSet<>();
        Collections.addAll(storages, storage1, storage2);

        given(storagesService.findAll()).willReturn(storages);

        try {
            mockMvc.perform(get("/storages")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(storage1.getId()), long.class))
                    .andExpect(jsonPath("$[0].address.id", is(storage1.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$[0].address.city", is(storage1.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$[0].address.street", is(storage1.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$[0].address.house", is(storage1.getAddress().getHouse()), int.class))
                    .andExpect(jsonPath("$[1].id", is(storage2.getId()), long.class))
                    .andExpect(jsonPath("$[1].address.id", is(storage2.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$[1].address.city", is(storage2.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$[1].address.street", is(storage2.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$[1].address.house", is(storage2.getAddress().getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(storagesService, times(1)).findAll();
        reset(storagesService);
    }

    @Test
    public void givenStorage_whenGetNonExistentStorage_thenThrowStorageNotFoundException() {
        long id = 4L;
        String errorMessage = "Хранилище не было найдено";

        given(storagesService.findOne(id)).willThrow(new StorageNotFoundException());

        try {
            mockMvc.perform(get("/storages/4")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof StorageNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(storagesService);
    }

    @Test
    public void givenStorage_whenGetStorageWithRedBikes_thenReturnJsonArray() {
        long id = 5L;
        City city = City.NOVOSIBIRSK;
        String street = "Holodnaya";
        int house = 2;
        Address address = new Address(city, street, house);
        address.setId(id);
        Storage storage = new Storage(address);
        storage.setId(id);

        long bikeId1 = 11L;
        Color color1 = Color.RED;
        Size size1 = Size.M;
        Label label1 = Label.STELS;
        Bike redBike = new Bike(color1, size1, label1, storage);
        redBike.setId(bikeId1);

        long bikeId2 = 12L;
        Color color2 = Color.GREEN;
        Size size2 = Size.XS;
        Label label2 = Label.FORWARD;
        Bike greenBike = new Bike(color2, size2, label2, storage);
        greenBike.setId(bikeId2);

        Set<Bike> bikes = new LinkedHashSet<>();
        Collections.addAll(bikes, redBike, greenBike);
        storage.setBikes(bikes);

        given(storagesService.findOne(id)).willReturn(Optional.of(storage));

        try {
            mockMvc.perform(get("/storages/5/lazy")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(redBike.getId()), long.class))
                    .andExpect(jsonPath("$[0].color", is(redBike.getColor().name()), String.class))
                    .andExpect(jsonPath("$[0].size", is(redBike.getSize().name()), String.class))
                    .andExpect(jsonPath("$[0].label", is(redBike.getLabel().name()), String.class))
                    .andExpect(jsonPath("$[0].available", is(redBike.isAvailable()), boolean.class))
                    .andExpect(jsonPath("$[0].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$[0].storage.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$[0].storage.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$[0].storage.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$[0].storage.address.house", is(storage.getAddress().getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(storagesService, times(1)).findOne(id);
        reset(storagesService);
    }
}