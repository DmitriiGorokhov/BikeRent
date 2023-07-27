package com.gorokhov.controllers;

import com.gorokhov.models.Address;
import com.gorokhov.models.Bike;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import com.gorokhov.services.BikesService;
import com.gorokhov.util.JsonUtil;
import com.gorokhov.util.exceptions.BikeNotCreatedException;
import com.gorokhov.util.exceptions.BikeNotFoundException;
import com.gorokhov.util.exceptions.BikeNotUpdatedException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BikesController.class)
public class BikesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BikesService bikesService;

    @Test
    public void givenBike_whenPostBike_thenReturnStatusCreated() {
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

        given(bikesService.save(Mockito.any())).willReturn(bike);

        try {
            mockMvc.perform(post("/bikes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(bike)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(bikesService, times(1)).save(Mockito.any());
        reset(bikesService);
    }

    @Test
    public void givenBike_whenPostBike_thenThrowBikeNotCreatedException() {
        long addressId = 2L;
        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 82;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 2L;
        Color color = null;
        Size size = Size.XS;
        Label label = Label.FORWARD;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        String errorMessage = "color - Необходимо указать цвет; ";

        try {
            mockMvc.perform(post("/bikes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(bike)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof BikeNotCreatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(bikesService);
    }

    @Test
    public void givenBike_whenGetBike_thenReturnJson() {
        long addressId = 3L;
        City city = City.NOVOSIBIRSK;
        String street = "Lenina";
        int house = 47;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 3L;
        Color color = Color.BLUE;
        Size size = Size.S;
        Label label = Label.SALSA;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        given(bikesService.findOne(bikeId)).willReturn(Optional.of(bike));

        try {
            mockMvc.perform(get("/bikes/3")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(bike.getId()), long.class))
                    .andExpect(jsonPath("$.color", is(bike.getColor().name()), String.class))
                    .andExpect(jsonPath("$.size", is(bike.getSize().name()), String.class))
                    .andExpect(jsonPath("$.label", is(bike.getLabel().name()), String.class))
                    .andExpect(jsonPath("$.available", is(bike.isAvailable()), boolean.class))
                    .andExpect(jsonPath("$.storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$.storage.address.id", is(address.getId()), long.class))
                    .andExpect(jsonPath("$.storage.address.city", is(address.getCity().name()), String.class))
                    .andExpect(jsonPath("$.storage.address.street", is(address.getStreet()), String.class))
                    .andExpect(jsonPath("$.storage.address.house", is(address.getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(bikesService, times(1)).findOne(bikeId);
        reset(bikesService);
    }

    @Test
    public void givenClients_whenGetClients_thenReturnJsonArray() {
        long addressId = 4L;
        City city = City.KRASNODAR;
        String street = "Letnaya";
        int house = 24;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId1 = 4L;
        Color color1 = Color.GREY;
        Size size1 = Size.XS;
        Label label1 = Label.TREK_BICYCLE;
        Bike bike1 = new Bike(color1, size1, label1, storage);
        bike1.setId(bikeId1);

        long bikeId2 = 5L;
        Color color2 = Color.WHITE;
        Size size2 = Size.M;
        Label label2 = Label.MONGOOSE;
        Bike bike2 = new Bike(color2, size2, label2, storage);
        bike2.setId(bikeId2);

        Set<Bike> bikes = new LinkedHashSet<>();
        Collections.addAll(bikes, bike1, bike2);

        given(bikesService.findAll()).willReturn(bikes);

        try {
            mockMvc.perform(get("/bikes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(bike1.getId()), long.class))
                    .andExpect(jsonPath("$[0].color", is(bike1.getColor().name()), String.class))
                    .andExpect(jsonPath("$[0].size", is(bike1.getSize().name()), String.class))
                    .andExpect(jsonPath("$[0].label", is(bike1.getLabel().name()), String.class))
                    .andExpect(jsonPath("$[0].available", is(bike1.isAvailable()), boolean.class))
                    .andExpect(jsonPath("$[0].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$[0].storage.address.id", is(address.getId()), long.class))
                    .andExpect(jsonPath("$[0].storage.address.city", is(address.getCity().name()), String.class))
                    .andExpect(jsonPath("$[0].storage.address.street", is(address.getStreet()), String.class))
                    .andExpect(jsonPath("$[0].storage.address.house", is(address.getHouse()), int.class))
                    .andExpect(jsonPath("$[1].id", is(bike2.getId()), long.class))
                    .andExpect(jsonPath("$[1].color", is(bike2.getColor().name()), String.class))
                    .andExpect(jsonPath("$[1].size", is(bike2.getSize().name()), String.class))
                    .andExpect(jsonPath("$[1].label", is(bike2.getLabel().name()), String.class))
                    .andExpect(jsonPath("$[1].available", is(bike2.isAvailable()), boolean.class))
                    .andExpect(jsonPath("$[1].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$[1].storage.address.id", is(address.getId()), long.class))
                    .andExpect(jsonPath("$[1].storage.address.city", is(address.getCity().name()), String.class))
                    .andExpect(jsonPath("$[1].storage.address.street", is(address.getStreet()), String.class))
                    .andExpect(jsonPath("$[1].storage.address.house", is(address.getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(bikesService, times(1)).findAll();
        reset(bikesService);
    }

    @Test
    public void givenBike_whenGetNonExistentBike_thenThrowBikeNotFoundException() {
        long id = 6L;
        String errorMessage = "Велосипед не был найден";

        given(bikesService.findOne(id)).willThrow(new BikeNotFoundException());

        try {
            mockMvc.perform(get("/bikes/6")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof BikeNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(bikesService);
    }

    @Test
    public void givenBike_whenPostUpdatedBike_thenReturnStatusAccepted() {
        long addressId = 7L;
        City city = City.EKATERINBURG;
        String street = "Centralnaya";
        int house = 70;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 7L;
        Color color = Color.BLACK;
        Size size = Size.XXL;
        Label label = Label.SCOTT;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        given(bikesService.update(bikeId, bike)).willReturn(bike);

        try {
            mockMvc.perform(post("/bikes/7/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(bike)))
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(bikesService, times(1)).update(bikeId, bike);
        reset(bikesService);
    }

    @Test
    public void givenBike_whenPostIncorrectUpdatedBike_thenThrowBikeNotUpdatedException() {
        long addressId = 8L;
        City city = City.SAINT_PETERSBURG;
        String street = "Nevskii";
        int house = 6;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 8L;
        Color color = Color.GREY;
        Size size = null;
        Label label = Label.CANNONDALE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);
        String errorMessage = "size - Необходимо указать размер; ";

        given(bikesService.update(bikeId, bike))
                .willThrow(new BikeNotUpdatedException(errorMessage));

        try {
            mockMvc.perform(post("/bikes/8/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(bike)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof BikeNotUpdatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                            Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(bikesService);
    }
}