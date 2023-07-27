package com.gorokhov.controllers;

import com.gorokhov.models.*;
import com.gorokhov.models.enums.City;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import com.gorokhov.services.BikesService;
import com.gorokhov.services.OrdersService;
import com.gorokhov.util.JsonUtil;
import com.gorokhov.util.exceptions.*;
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

@WebMvcTest(OrdersController.class)
public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdersService ordersService;

    @Test
    public void givenOrder_whenPostOrder_thenReturnStatusCreated() {
        long clientId = 1L;
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        client.setId(clientId);

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

        long orderId = 1L;
        Order order = new Order(client, Collections.singleton(bike), storage);
        order.setId(orderId);

        given(ordersService.save(Mockito.any())).willReturn(order);

        try {
            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(order)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(ordersService, times(1)).save(Mockito.any());
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenPostOrder_thenThrowOrderNotCreatedException() {
        Client client = null;

        long addressId = 2L;
        City city = City.MOSCOW;
        String street = "Lomonosova";
        int house = 27;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 2L;
        Color color = Color.GREY;
        Size size = Size.XS;
        Label label = Label.FORWARD;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        long orderId = 2L;
        Order order = new Order(client, Collections.singleton(bike), storage);
        order.setId(orderId);

        String errorMessage = "client - Необходимо указать клиента; ";

        try {
            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(order)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof OrderNotCreatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenGetOrder_thenReturnJson() {
        long clientId = 3L;
        String email = "jerry@email.com";
        String name = "Jerry";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 3L;
        City city = City.KRASNODAR;
        String street = "Severnaya";
        int house = 82;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 3L;
        Color color = Color.BLUE;
        Size size = Size.XXL;
        Label label = Label.CANNONDALE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        long orderId = 3L;
        Order order = new Order(client, Collections.singleton(bike), storage);
        order.setId(orderId);

        given(ordersService.findOne(orderId)).willReturn(Optional.of(order));

        try {
            mockMvc.perform(get("/orders/3")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(order.getId()), long.class))
                    .andExpect(jsonPath("$.client.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$.client.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$.client.name", is(client.getName()), String.class))
                    .andExpect(jsonPath("$.bikes[0].id", is(bike.getId()), long.class))
                    .andExpect(jsonPath("$.bikes[0].color", is(bike.getColor().name()), String.class))
                    .andExpect(jsonPath("$.bikes[0].size", is(bike.getSize().name()), String.class))
                    .andExpect(jsonPath("$.bikes[0].label", is(bike.getLabel().name()), String.class))
                    .andExpect(jsonPath("$.bikes[0].available", is(bike.isAvailable()), boolean.class))
                    .andExpect(jsonPath("$.bikes[0].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$.bikes[0].storage.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$.bikes[0].storage.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$.bikes[0].storage.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$.bikes[0].storage.address.house", is(storage.getAddress().getHouse()), int.class))
                    .andExpect(jsonPath("$.storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$.storage.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$.storage.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$.storage.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$.storage.address.house", is(storage.getAddress().getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(ordersService, times(1)).findOne(orderId);
        reset(ordersService);
    }

    @Test
    public void givenOrders_whenGetOrders_thenReturnJsonArray() {
        long clientId = 4L;
        String email = "marry@email.com";
        String name = "Marry";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 4L;
        City city = City.EKATERINBURG;
        String street = "Centralnaya";
        int house = 42;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId1 = 4L;
        Color color1 = Color.WHITE;
        Size size1 = Size.S;
        Label label1 = Label.SALSA;
        Bike bike1 = new Bike(color1, size1, label1, storage);
        bike1.setId(bikeId1);

        long bikeId2 = 5L;
        Color color2 = Color.YELLOW;
        Size size2 = Size.L;
        Label label2 = Label.MONGOOSE;
        Bike bike2 = new Bike(color2, size2, label2, storage);
        bike2.setId(bikeId2);

        long orderId1 = 4L;
        Order order1 = new Order(client, Collections.singleton(bike1), storage);
        order1.setId(orderId1);

        long orderId2 = 5L;
        Order order2 = new Order(client, Collections.singleton(bike2), storage);
        order2.setId(orderId2);

        Set<Order> orders = new LinkedHashSet<>();
        Collections.addAll(orders, order1, order2);

        given(ordersService.findAll()).willReturn(orders);

        try {
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(order1.getId()), long.class))
                    .andExpect(jsonPath("$[0].client.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$[0].client.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$[0].client.name", is(client.getName()), String.class))
                    .andExpect(jsonPath("$[0].bikes[0].id", is(bike1.getId()), long.class))
                    .andExpect(jsonPath("$[0].bikes[0].color", is(bike1.getColor().name()), String.class))
                    .andExpect(jsonPath("$[0].bikes[0].size", is(bike1.getSize().name()), String.class))
                    .andExpect(jsonPath("$[0].bikes[0].label", is(bike1.getLabel().name()), String.class))
                    .andExpect(jsonPath("$[0].bikes[0].available", is(bike1.isAvailable()), boolean.class))
                    .andExpect(jsonPath("$[0].bikes[0].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$[0].bikes[0].storage.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$[0].bikes[0].storage.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$[0].bikes[0].storage.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$[0].bikes[0].storage.address.house", is(storage.getAddress().getHouse()), int.class))
                    .andExpect(jsonPath("$[0].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$[0].storage.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$[0].storage.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$[0].storage.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$[0].storage.address.house", is(storage.getAddress().getHouse()), int.class))
                    .andExpect(jsonPath("$[1].id", is(order2.getId()), long.class))
                    .andExpect(jsonPath("$[1].client.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$[1].client.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$[1].client.name", is(client.getName()), String.class))
                    .andExpect(jsonPath("$[1].bikes[0].id", is(bike2.getId()), long.class))
                    .andExpect(jsonPath("$[1].bikes[0].color", is(bike2.getColor().name()), String.class))
                    .andExpect(jsonPath("$[1].bikes[0].size", is(bike2.getSize().name()), String.class))
                    .andExpect(jsonPath("$[1].bikes[0].label", is(bike2.getLabel().name()), String.class))
                    .andExpect(jsonPath("$[1].bikes[0].available", is(bike2.isAvailable()), boolean.class))
                    .andExpect(jsonPath("$[1].bikes[0].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$[1].bikes[0].storage.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$[1].bikes[0].storage.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$[1].bikes[0].storage.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$[1].bikes[0].storage.address.house", is(storage.getAddress().getHouse()), int.class))
                    .andExpect(jsonPath("$[1].storage.id", is(storage.getId()), long.class))
                    .andExpect(jsonPath("$[1].storage.address.id", is(storage.getAddress().getId()), long.class))
                    .andExpect(jsonPath("$[1].storage.address.city", is(storage.getAddress().getCity().name()), String.class))
                    .andExpect(jsonPath("$[1].storage.address.street", is(storage.getAddress().getStreet()), String.class))
                    .andExpect(jsonPath("$[1].storage.address.house", is(storage.getAddress().getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(ordersService, times(1)).findAll();
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenGetNonExistentOrder_thenThrowOrderNotFoundException() {
        long id = 6L;
        String errorMessage = "Заказ не был найден";

        given(ordersService.findOne(id)).willThrow(new OrderNotFoundException());

        try {
            mockMvc.perform(get("/orders/6")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof OrderNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenPostUpdatedOrder_thenReturnStatusAccepted() {
        long clientId = 7L;
        String email = "john@email.com";
        String name = "John";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 7L;
        City city = City.SAINT_PETERSBURG;
        String street = "Nevskii";
        int house = 4;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 7L;
        Color color = Color.BLUE;
        Size size = Size.XXL;
        Label label = Label.GIANT_BICYCLE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        long orderId = 7L;
        Order order = new Order(client, Collections.singleton(bike), storage);
        order.setId(orderId);

        given(ordersService.update(orderId, order)).willReturn(order);

        try {
            mockMvc.perform(post("/orders/7/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(order)))
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(ordersService, times(1)).update(orderId, order);
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenPostUpdatedOrder_thenThrowOrderNotUpdatedException() {
        long clientId = 8L;
        String email = "moo@email.com";
        String name = "Moo";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 8L;
        City city = City.NIZHNY_NOVGOROD;
        String street = "Varvarskaya";
        int house = 17;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long orderId = 8L;
        Order order = new Order(client, null, storage);
        order.setId(orderId);
        String errorMessage = "bikes - Необходимо указать хотя бы 1 велосипед; ";

        try {
            mockMvc.perform(post("/orders/8/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(order)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof OrderNotUpdatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                            Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenPostUpdatedOrder_thenThrowBikeNotFoundException() {
        long clientId = 9L;
        String email = "Bibo@email.com";
        String name = "Bibo";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 9L;
        City city = City.KRASNODAR;
        String street = "Yuzhnaya";
        int house = 84;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 9L;
        Color color = Color.BLACK;
        Size size = Size.S;
        Label label = Label.TREK_BICYCLE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        long orderId = 9L;
        Order order = new Order(client, Collections.singleton(bike), storage);
        order.setId(orderId);

        String errorMessage = "Велосипед не был найден";

        given(ordersService.update(orderId, order))
                .willThrow(new BikeNotFoundException());

        try {
            mockMvc.perform(post("/orders/9/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(order)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof BikeNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenPostUpdatedOrder_thenThrowClientNotFoundException() {
        long clientId = 10L;
        String email = "Boba@email.com";
        String name = "Boba";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 10L;
        City city = City.NOVOSIBIRSK;
        String street = "Holodnaya";
        int house = 174;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 10L;
        Color color = Color.GREEN;
        Size size = Size.M;
        Label label = Label.MONGOOSE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        long orderId = 10L;
        Order order = new Order(client, Collections.singleton(bike), storage);
        order.setId(orderId);

        String errorMessage = "Клиент не был найден";

        given(ordersService.update(orderId, order))
                .willThrow(new ClientNotFoundException());

        try {
            mockMvc.perform(post("/orders/10/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.toJson(order)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof ClientNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(ordersService);
    }

    @Test
    public void givenOrder_whenPostUpdatedOrder_thenThrowStorageNotFoundException() {
        long clientId = 11L;
        String email = "pig@email.com";
        String name = "Pig";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 11L;
        City city = City.EKATERINBURG;
        String street = "Kolonova";
        int house = 196;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 11L;
        Color color = Color.BLUE;
        Size size = Size.L;
        Label label = Label.GIANT_BICYCLE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        long orderId = 11L;
        Order order = new Order(client, Collections.singleton(bike), storage);
        order.setId(orderId);

        String errorMessage = "Хранилище не было найдено";

        given(ordersService.update(orderId, order))
                .willThrow(new StorageNotFoundException());

        try {
            mockMvc.perform(post("/orders/11/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.toJson(order)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof StorageNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(ordersService);
    }
}