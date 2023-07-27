package com.gorokhov.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gorokhov.models.Address;
import com.gorokhov.models.enums.City;
import com.gorokhov.services.AddressesService;
import com.gorokhov.util.JsonUtil;
import com.gorokhov.util.exceptions.AddressNotCreatedException;
import com.gorokhov.util.exceptions.AddressNotFoundException;
import com.gorokhov.util.exceptions.AddressNotUpdatedException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

@WebMvcTest(AddressesController.class)
public class AddressesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressesService addressesService;

    @Test
    public void givenAddress_whenPostAddress_thenReturnStatusCreated() {
        long id = 1L;
        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 18;
        Address address = new Address(city, street, house);
        address.setId(id);

        given(addressesService.save(Mockito.any())).willReturn(address);

        try {
            mockMvc.perform(post("/addresses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(address)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(addressesService, times(1)).save(Mockito.any());
        reset(addressesService);
    }

    @Test
    public void givenAddress_whenPostAddress_thenThrowAddressNotCreatedException() {
        long id = 2L;
        City city = City.EKATERINBURG;
        String street = null;
        int house = 879;
        Address address = new Address(city, street, house);
        address.setId(id);
        String errorMessage = "street - Необходимо указать улицу; ";

        try {
            mockMvc.perform(post("/addresses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(address)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof AddressNotCreatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(addressesService);
    }

    @Test
    public void givenAddress_whenGetAddress_thenReturnJson() {
        long id = 3L;
        City city = City.NIZHNY_NOVGOROD;
        String street = "Vaneeva";
        int house = 138;
        Address address = new Address(city, street, house);
        address.setId(id);

        given(addressesService.findOne(id)).willReturn(Optional.of(address));

        try {
            mockMvc.perform(get("/addresses/3")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(address.getId()), long.class))
                    .andExpect(jsonPath("$.city", is(address.getCity().name()), String.class))
                    .andExpect(jsonPath("$.street", is(address.getStreet()), String.class))
                    .andExpect(jsonPath("$.house", is(address.getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(addressesService, times(1)).findOne(id);
        reset(addressesService);
    }

    @Test
    public void givenAddresses_whenGetAddresses_thenReturnJsonArray() {
        long id1 = 4L;
        City city1 = City.KRASNODAR;
        String street1 = "Novaya";
        int house1 = 29;
        Address address1 = new Address(city1, street1, house1);
        address1.setId(id1);

        long id2 = 5L;
        City city2 = City.SAINT_PETERSBURG;
        String street2 = "Nevskii";
        int house2 = 52;
        Address address2 = new Address(city2, street2, house2);
        address2.setId(id2);

        Set<Address> addresses = new LinkedHashSet<>();
        Collections.addAll(addresses, address1, address2);

        given(addressesService.findAll()).willReturn(addresses);

        try {
            mockMvc.perform(get("/addresses")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(address1.getId()), long.class))
                    .andExpect(jsonPath("$[0].city", is(address1.getCity().name()), String.class))
                    .andExpect(jsonPath("$[0].street", is(address1.getStreet()), String.class))
                    .andExpect(jsonPath("$[0].house", is(address1.getHouse()), int.class))
                    .andExpect(jsonPath("$[1].id", is(address2.getId()), long.class))
                    .andExpect(jsonPath("$[1].city", is(address2.getCity().name()), String.class))
                    .andExpect(jsonPath("$[1].street", is(address2.getStreet()), String.class))
                    .andExpect(jsonPath("$[1].house", is(address2.getHouse()), int.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(addressesService, times(1)).findAll();
        reset(addressesService);
    }

    @Test
    public void givenAddress_whenGetNonExistentAddress_thenThrowAddressNotFoundException() {
        long id = 6L;
        String errorMessage = "Адрес не был найден";

        given(addressesService.findOne(id)).willThrow(new AddressNotFoundException());

        try {
            mockMvc.perform(get("/addresses/6")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof AddressNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(addressesService);
    }

    @Test
    public void givenAddress_whenPostUpdatedAddress_thenReturnStatusAccepted() {
        long id = 7L;

        City newCity = City.NOVOSIBIRSK;
        String newStreet = "Lapteva";
        int newHouse = 40;
        Address newAddress = new Address(newCity, newStreet, newHouse);
        newAddress.setId(id);

        given(addressesService.update(id, newAddress)).willReturn(newAddress);

        try {
            mockMvc.perform(post("/addresses/7/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(newAddress)))
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(addressesService, times(1)).update(id, newAddress);
        reset(addressesService);
    }

    @Test
    public void givenAddress_whenPostUpdatedAddress_thenThrowAddressNotUpdatedException() {
        long id = 8L;
        City city = City.EKATERINBURG;
        String street = "Minina";
        int house = 26;
        Address address = new Address(city, street, house);
        address.setId(id);
        String errorMessage = "Адрес с такими данными уже существует";

        given(addressesService.update(id, address))
                .willThrow(new AddressNotUpdatedException(errorMessage));

        try {
            mockMvc.perform(post("/addresses/8/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(address)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof AddressNotUpdatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                            Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(addressesService);
    }

    @Test
    public void givenAddress_whenPostIncorrectUpdatedAddress_thenThrowAddressNotUpdatedException() {
        long id = 9L;
        City city = null;
        String street = "Minina";
        int house = 26;
        Address address = new Address(city, street, house);
        address.setId(id);
        String errorMessage = "city - Необходимо указать город; ";

        try {
            mockMvc.perform(post("/addresses/9/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.toJson(address)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof AddressNotUpdatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(addressesService);
    }
}