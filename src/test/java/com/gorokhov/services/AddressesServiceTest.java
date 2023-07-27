package com.gorokhov.services;

import com.gorokhov.models.Address;
import com.gorokhov.models.enums.City;
import com.gorokhov.repositories.AddressesRepository;
import com.gorokhov.util.exceptions.AddressNotFoundException;
import com.gorokhov.util.exceptions.AddressNotUpdatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressesServiceTest {

    @Mock
    private AddressesRepository addressesRepository;

    @InjectMocks
    private AddressesService addressesService;

    @Test
    public void givenAddress_whenSaveAddress_thenReturnAddress() {
        long id = 1L;
        City city = City.SAINT_PETERSBURG;
        String street = "Levaya";
        int house = 143;
        Address address = new Address(city, street, house);
        address.setId(id);

        given(addressesRepository.save(address)).willReturn(address);

        Address saved = addressesService.save(address);

        assertNotNull(saved);
        assertEquals(address, saved);
        verify(addressesRepository, times(1)).save(address);
        reset(addressesRepository);
    }

    @Test
    public void givenAddress_whenGetById_thenReturnAddress() {
        long id = 2L;
        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 18;
        Address address = new Address(city, street, house);
        address.setId(id);

        given(addressesRepository.findById(id)).willReturn(Optional.of(address));

        Address found = addressesService.findOne(id).orElseThrow(AddressNotFoundException::new);

        assertEquals(address, found);
        verify(addressesRepository, times(1)).findById(id);
        reset(addressesRepository);
    }

    @Test
    public void givenAddress_whenGetByCityAndStreetAndHouse_thenReturnAddress() {
        long id = 3L;
        City city = City.NIZHNY_NOVGOROD;
        String street = "Yamskay";
        int house = 73;
        Address address = new Address(city, street, house);
        address.setId(id);

        given(addressesRepository.findByCityAndStreetAndHouse(city, street, house)).willReturn(Optional.of(address));

        Address found = addressesService.findOne(city, street, house).orElseThrow(AddressNotFoundException::new);

        assertEquals(address, found);
        verify(addressesRepository, times(1)).findByCityAndStreetAndHouse(city, street, house);
        reset(addressesRepository);
    }

    @Test
    public void givenAddresses_whenGetAllAddresses_thenReturnAddressesList() {
        long id1 = 4L;
        City city1 = City.NOVOSIBIRSK;
        String street1 = "Severnaya";
        int house1 = 78;
        Address address1 = new Address(city1, street1, house1);
        address1.setId(id1);

        long id2 = 5L;
        City city2 = City.KRASNODAR;
        String street2 = "Severnaya";
        int house2 = 89;
        Address address2 = new Address(city2, street2, house2);
        address2.setId(id2);

        given(addressesRepository.findAll()).willReturn(List.of(address1, address2));

        Set<Address> found = addressesService.findAll();

        assertNotNull(found);
        assertEquals(2, found.size());
        verify(addressesRepository, times(1)).findAll();
        reset(addressesRepository);
    }

    @Test
    public void givenAddress_whenUpdateAddress_thenReturnUpdatedAddress() {
        long id = 6L;
        City oldCity = City.EKATERINBURG;
        String oldStreet = "Hramovaya";
        int oldHouse = 92;
        Address address = new Address(oldCity, oldStreet, oldHouse);
        address.setId(id);

        given(addressesRepository.findById(id)).willReturn(Optional.of(address));

        City newCity = City.NIZHNY_NOVGOROD;
        String newStreet = "Vaneeva";
        int newHouse = 98;
        address.setCity(newCity);
        address.setStreet(newStreet);
        address.setHouse(newHouse);

        Address updated = addressesService.update(id, address);

        assertEquals(newCity, updated.getCity());
        assertEquals(newStreet, updated.getStreet());
        assertEquals(newHouse, updated.getHouse());
        verify(addressesRepository, times(1)).findById(id);
        reset(addressesRepository);
    }

    @Test
    public void givenAddress_whenUpdateAddress_thenThrowAddressNotUpdatedException() {
        long id = 7L;
        City city = City.MOSCOW;
        String street = "Krasnaya";
        int house = 1;
        Address address = new Address(city, street, house);
        address.setId(id);

        given(addressesRepository.findByCityAndStreetAndHouse(city, street, house))
                .willReturn(Optional.of(address));

        assertThrows(AddressNotUpdatedException.class, () -> addressesService.update(id, address));
        verify(addressesRepository, times(1)).findByCityAndStreetAndHouse(city, street, house);
        reset(addressesRepository);
    }
}