package com.gorokhov.services;

import com.gorokhov.models.Address;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.repositories.StoragesRepository;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoragesServiceTest {

    @Mock
    private StoragesRepository storagesRepository;

    @InjectMocks
    private StoragesService storagesService;

    @Test
    public void givenStorage_whenGetById_thenReturnStorage() {
        long id = 1L;
        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 18;
        Address address = new Address(city, street, house);
        address.setId(id);
        Storage storage = new Storage(address);
        storage.setId(id);

        given(storagesRepository.findById(id)).willReturn(Optional.of(storage));

        Storage found = storagesService.findOne(id).orElseThrow(StorageNotFoundException::new);

        assertEquals(storage, found);
        verify(storagesRepository, times(1)).findById(id);
        reset(storagesRepository);
    }

    @Test
    public void givenStorage_whenGetByAddress_thenReturnStorage() {
        long id = 2L;
        City city = City.NIZHNY_NOVGOROD;
        String street = "Yamskay";
        int house = 73;
        Address address = new Address(city, street, house);
        address.setId(id);
        Storage storage = new Storage(address);
        storage.setId(id);

        given(storagesRepository.findByAddress(address)).willReturn(Optional.of(storage));

        Storage found = storagesService.findOne(address).orElseThrow(StorageNotFoundException::new);

        assertEquals(storage, found);
        verify(storagesRepository, times(1)).findByAddress(address);
        reset(storagesRepository);
    }

    @Test
    public void givenStorages_whenGetAllStorages_thenReturnStoragesList() {
        long id1 = 3L;
        City city1 = City.NOVOSIBIRSK;
        String street1 = "Severnaya";
        int house1 = 78;
        Address address1 = new Address(city1, street1, house1);
        address1.setId(id1);
        Storage storage1 = new Storage(address1);
        storage1.setId(id1);

        long id2 = 5L;
        City city2 = City.KRASNODAR;
        String street2 = "Severnaya";
        int house2 = 89;
        Address address2 = new Address(city2, street2, house2);
        address2.setId(id2);
        Storage storage2 = new Storage(address2);
        storage2.setId(id2);

        given(storagesRepository.findAll()).willReturn(List.of(storage1, storage2));

        Set<Storage> found = storagesService.findAll();

        assertNotNull(found);
        assertEquals(2, found.size());
        verify(storagesRepository, times(1)).findAll();
        reset(storagesRepository);
    }
}