package com.gorokhov.services;

import com.gorokhov.models.*;
import com.gorokhov.models.enums.City;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import com.gorokhov.repositories.BikesRepository;
import com.gorokhov.repositories.ClientsRepository;
import com.gorokhov.repositories.OrdersRepository;
import com.gorokhov.repositories.StoragesRepository;
import com.gorokhov.util.exceptions.BikeNotFoundException;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import com.gorokhov.util.exceptions.OrderNotFoundException;
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
public class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private BikesRepository bikesRepository;

    @Mock
    private ClientsRepository clientsRepository;

    @Mock
    private StoragesRepository storagesRepository;

    @InjectMocks
    private OrdersService ordersService;

    @Test
    public void givenOrder_whenSaveOrder_thenReturnOrder() {
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

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);

        long orderId = 1L;
        Order order = new Order(client, bikes, storage);
        order.setId(orderId);

        given(ordersRepository.save(order)).willReturn(order);
        given(bikesRepository.findById(bikeId)).willReturn(Optional.of(bike));

        bike.setOrders(new HashSet<>());

        Order saved = ordersService.save(order);

        assertNotNull(saved);
        assertEquals(order, saved);
        verify(ordersRepository, times(1)).save(order);
        verify(bikesRepository, times(1)).findById(bikeId);
        reset(ordersRepository);
        reset(bikesRepository);
    }

    @Test
    public void givenOrder_whenSaveOrder_thenThrowBikeNotFoundException() {
        long clientId = 2L;
        String email = "Bob@email.com";
        String name = "Bob";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 2L;
        City city = City.SAINT_PETERSBURG;
        String street = "Novaya";
        int house = 13;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 2L;
        Color color = Color.GREY;
        Size size = Size.S;
        Label label = Label.FORWARD;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);

        long orderId = 2L;
        Order order = new Order(client, bikes, storage);
        order.setId(orderId);

        given(ordersRepository.save(order)).willReturn(order);
        given(bikesRepository.findById(bikeId)).willReturn(Optional.empty());

        assertThrows(BikeNotFoundException.class, () -> ordersService.save(order));
        verify(ordersRepository, times(1)).save(order);
        verify(bikesRepository, times(1)).findById(bikeId);
        reset(ordersRepository);
        reset(bikesRepository);
    }

    @Test
    public void givenOrder_whenGetById_thenReturnOrder() {
        long clientId = 3L;
        String email = "jerry@email.com";
        String name = "Jerry";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 3L;
        City city = City.KRASNODAR;
        String street = "Letnii";
        int house = 193;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 3L;
        Color color = Color.RED;
        Size size = Size.M;
        Label label = Label.SALSA;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);

        long orderId = 3L;
        Order order = new Order(client, bikes, storage);
        order.setId(orderId);

        given(ordersRepository.findById(orderId)).willReturn(Optional.of(order));

        Order found = ordersService.findOne(orderId).orElseThrow(OrderNotFoundException::new);

        assertNotNull(found);
        assertEquals(order, found);
        verify(ordersRepository, times(1)).findById(orderId);
        reset(ordersRepository);
    }

    @Test
    public void givenOrders_whenGetAllOrders_thenReturnOrdersList() {
        long clientId = 4L;
        String email = "marry@email.com";
        String name = "Marry";
        Client client = new Client(email, name);
        client.setId(clientId);

        long addressId = 4L;
        City city = City.NOVOSIBIRSK;
        String street = "Severnii";
        int house = 243;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId1 = 4L;
        Color color1 = Color.BLUE;
        Size size1 = Size.M;
        Label label1 = Label.FUJI;
        Bike bike1 = new Bike(color1, size1, label1, storage);
        bike1.setId(bikeId1);

        Set<Bike> bikes1 = new HashSet<>();
        Collections.addAll(bikes1, bike1);

        long orderId1 = 4L;
        Order order1 = new Order(client, bikes1, storage);
        order1.setId(orderId1);

        long bikeId2 = 5L;
        Color color2 = Color.BLACK;
        Size size2 = Size.S;
        Label label2 = Label.TREK_BICYCLE;
        Bike bike2 = new Bike(color2, size2, label2, storage);
        bike2.setId(bikeId2);

        Set<Bike> bikes2 = new HashSet<>();
        Collections.addAll(bikes2, bike2);

        long orderId2 = 5L;
        Order order2 = new Order(client, bikes2, storage);
        order2.setId(orderId2);

        given(ordersRepository.findAll()).willReturn(List.of(order1, order2));

        Set<Order> found = ordersService.findAll();

        assertNotNull(found);
        assertEquals(2, found.size());
        verify(ordersRepository, times(1)).findAll();
        reset(ordersRepository);
    }

    @Test
    public void givenOrder_whenUpdateOrder_thenReturnUpdatedOrder() {
        long clientId1 = 6L;
        String email1 = "homer@email.com";
        String name1 = "Homer";
        Client client1 = new Client(email1, name1);
        client1.setId(clientId1);

        long addressId = 6L;
        City city = City.EKATERINBURG;
        String street = "Kudryvaya";
        int house = 2;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 6L;
        Color color = Color.GREY;
        Size size = Size.XXL;
        Label label = Label.TREK_BICYCLE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);

        long orderId = 6L;
        Order order = new Order(client1, bikes, storage);
        order.setId(orderId);

        Set<Order> orders = new HashSet<>();
        Collections.addAll(orders, order);

        client1.setOrders(orders);
        storage.setOrders(orders);
        bike.setOrders(orders);

        long clientId2 = 7L;
        String email2 = "bart@email.com";
        String name2 = "Bart";
        Client client2 = new Client(email2, name2);
        client2.setId(clientId2);

        given(clientsRepository.findById(clientId2)).willReturn(Optional.of(client2));
        given(storagesRepository.findById(addressId)).willReturn(Optional.of(storage));
        given(bikesRepository.findById(bikeId)).willReturn(Optional.of(bike));
        given(ordersRepository.findById(orderId)).willReturn(Optional.of(order));

        order.setClient(client2);
        client2.setOrders(orders);

        Order updated = ordersService.update(orderId, order);

        assertEquals(client2, updated.getClient());
        verify(clientsRepository, times(1)).findById(clientId2);
        verify(storagesRepository, times(1)).findById(addressId);
        verify(bikesRepository, times(2)).findById(bikeId);
        verify(ordersRepository, times(1)).findById(orderId);
        reset(clientsRepository);
        reset(storagesRepository);
        reset(bikesRepository);
        reset(ordersRepository);
    }

    @Test
    public void givenOrder_whenUpdateOrder_thenThrowClientNotFoundException() {
        long addressId = 8L;
        City city = City.NIZHNY_NOVGOROD;
        String street = "Naberezhnaya";
        int house = 22;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 8L;
        Color color = Color.GREY;
        Size size = Size.XXL;
        Label label = Label.TREK_BICYCLE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);

        long orderId = 8L;

        long clientId2 = 9L;
        String email2 = "bart@email.com";
        String name2 = "Bart";
        Client client2 = new Client(email2, name2);
        client2.setId(clientId2);

        Order order2 = new Order(client2, bikes, storage);

        given(clientsRepository.findById(clientId2)).willThrow(ClientNotFoundException.class);

        assertThrows(ClientNotFoundException.class, () -> ordersService.update(orderId, order2));
        verify(clientsRepository, times(1)).findById(clientId2);
        reset(clientsRepository);
    }

    @Test
    public void givenOrder_whenUpdateOrder_thenThrowStorageNotFoundException() {
        long clientId1 = 10L;
        String email1 = "bibo@email.com";
        String name1 = "Bibo";
        Client client1 = new Client(email1, name1);
        client1.setId(clientId1);

        long addressId = 10L;
        City city = City.NOVOSIBIRSK;
        String street = "Novaya";
        int house = 227;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 10L;
        Color color = Color.BLUE;
        Size size = Size.XL;
        Label label = Label.GIANT_BICYCLE;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);

        long orderId = 10L;

        long addressId2 = 11L;
        City city2 = City.KRASNODAR;
        String street2 = "Staraya";
        int house2 = 36;
        Address address2 = new Address(city2, street2, house2);
        address.setId(addressId);
        Storage storage2 = new Storage(address2);
        storage2.setId(addressId2);

        Order order2 = new Order(client1, bikes, storage2);

        given(clientsRepository.findById(clientId1)).willReturn(Optional.of(client1));
        given(storagesRepository.findById(addressId2)).willThrow(StorageNotFoundException.class);

        assertThrows(StorageNotFoundException.class, () -> ordersService.update(orderId, order2));
        verify(clientsRepository, times(1)).findById(clientId1);
        verify(storagesRepository, times(1)).findById(addressId2);
        reset(clientsRepository);
        reset(storagesRepository);
    }

    @Test
    public void givenOrder_whenUpdateOrder_thenThrowBikeNotFoundException() {
        long clientId1 = 12L;
        String email1 = "bobo@email.com";
        String name1 = "Bobo";
        Client client1 = new Client(email1, name1);
        client1.setId(clientId1);

        long addressId = 12L;
        City city = City.SAINT_PETERSBURG;
        String street = "Novaya";
        int house = 257;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long orderId = 12L;

        long bikeId2 = 13L;
        Color color2 = Color.YELLOW;
        Size size2 = Size.XS;
        Label label2 = Label.MONGOOSE;
        Bike bike2 = new Bike(color2, size2, label2, storage);
        bike2.setId(bikeId2);

        Set<Bike> bikes2 = new HashSet<>();
        Collections.addAll(bikes2, bike2);

        Order order2 = new Order(client1, bikes2, storage);

        given(clientsRepository.findById(clientId1)).willReturn(Optional.of(client1));
        given(storagesRepository.findById(addressId)).willReturn(Optional.of(storage));
        given(bikesRepository.findById(bikeId2)).willThrow(BikeNotFoundException.class);

        assertThrows(BikeNotFoundException.class, () -> ordersService.update(orderId, order2));
        verify(clientsRepository, times(1)).findById(clientId1);
        verify(storagesRepository, times(1)).findById(addressId);
        verify(bikesRepository, times(1)).findById(bikeId2);
        reset(clientsRepository);
        reset(storagesRepository);
        reset(bikesRepository);
    }

    @Test
    public void givenOrder_whenUpdateOrder_thenThrowOrderNotFoundException() {
        long clientId1 = 14L;
        String email1 = "boby@email.com";
        String name1 = "Boby";
        Client client1 = new Client(email1, name1);
        client1.setId(clientId1);

        long addressId = 14L;
        City city = City.NOVOSIBIRSK;
        String street = "Tunnelinaya";
        int house = 87;
        Address address = new Address(city, street, house);
        address.setId(addressId);
        Storage storage = new Storage(address);
        storage.setId(addressId);

        long bikeId = 14L;
        Color color = Color.GREY;
        Size size = Size.L;
        Label label = Label.FUJI;
        Bike bike = new Bike(color, size, label, storage);
        bike.setId(bikeId);

        Set<Bike> bikes = new HashSet<>();
        Collections.addAll(bikes, bike);

        long orderId = 14L;
        Order order = new Order(client1, bikes, storage);
        order.setId(orderId);

        long orderId2 = 15L;

        given(clientsRepository.findById(clientId1)).willReturn(Optional.of(client1));
        given(storagesRepository.findById(addressId)).willReturn(Optional.of(storage));
        given(bikesRepository.findById(bikeId)).willReturn(Optional.of(bike));
        given(ordersRepository.findById(orderId2)).willThrow(OrderNotFoundException.class);

        assertThrows(OrderNotFoundException.class, () -> ordersService.update(orderId2, order));
        verify(clientsRepository, times(1)).findById(clientId1);
        verify(storagesRepository, times(1)).findById(addressId);
        verify(bikesRepository, times(1)).findById(bikeId);
        verify(ordersRepository, times(1)).findById(orderId2);
        reset(clientsRepository);
        reset(storagesRepository);
        reset(bikesRepository);
        reset(ordersRepository);
    }
}