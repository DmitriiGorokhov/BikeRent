package com.gorokhov.repositories;

import com.gorokhov.models.*;
import com.gorokhov.models.enums.City;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import com.gorokhov.util.exceptions.AddressNotFoundException;
import com.gorokhov.util.exceptions.OrderNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@DataJpaTest
public class OrdersRepositoryTest {

    private final OrdersRepository ordersRepository;
    private final BikesRepository bikesRepository;
    private final AddressesRepository addressesRepository;
    private final StoragesRepository storagesRepository;
    private final ClientsRepository clientsRepository;

    @Autowired
    public OrdersRepositoryTest(OrdersRepository ordersRepository, BikesRepository bikesRepository,
                                AddressesRepository addressesRepository, StoragesRepository storagesRepository,
                                ClientsRepository clientsRepository) {
        this.ordersRepository = ordersRepository;
        this.bikesRepository = bikesRepository;
        this.addressesRepository = addressesRepository;
        this.storagesRepository = storagesRepository;
        this.clientsRepository = clientsRepository;
    }

    @Test
    public void givenNewOrder_whenFindById_thenReturnOrderWithCorrectId() {
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        client = clientsRepository.save(client);

        City city = City.MOSCOW;
        String street = "Lenina";
        int house = 18;
        Address address = new Address(city, street, house);
        Address savedAddress = addressesRepository.save(address);

        Storage storage = storagesRepository.save(new Storage(savedAddress));

        Color color = Color.RED;
        Size size = Size.L;
        Label label = Label.STELS;
        Bike bike = new Bike(color, size, label, storage);
        bike = bikesRepository.save(bike);

        Order order = new Order(client, Collections.singleton(bike), storage);
        long expectedId = ordersRepository.save(order).getId();

        Order found = ordersRepository.findById(expectedId)
                .orElseThrow(OrderNotFoundException::new);

        Assertions.assertEquals(expectedId, found.getId());
    }

    @Test
    public void givenNewOrder_whenFindAllOrders_thenReturnMoreThanZero() {
        String email = "bob@email.com";
        String name = "Bob";
        Client client = new Client(email, name);
        client = clientsRepository.save(client);

        City city = City.NOVOSIBIRSK;
        String street = "Novaya";
        int house = 193;
        Address address = new Address(city, street, house);
        Address savedAddress = addressesRepository.save(address);

        Storage storage = storagesRepository.save(new Storage(savedAddress));

        Color color1 = Color.BLUE;
        Size size1 = Size.M;
        Label label1 = Label.SCOTT;
        Bike bike1 = new Bike(color1, size1, label1, storage);
        bike1 = bikesRepository.save(bike1);

        Color color2 = Color.GREY;
        Size size2 = Size.S;
        Label label2 = Label.STELS;
        Bike bike2 = new Bike(color2, size2, label2, storage);
        bike2 = bikesRepository.save(bike2);

        Order order1 = new Order(client, Collections.singleton(bike1), storage);
        ordersRepository.save(order1);

        Order order2 = new Order(client, Collections.singleton(bike2), storage);
        ordersRepository.save(order2);

        Set<Order> found = new HashSet<>(ordersRepository.findAll());

        Assertions.assertTrue(found.size() > 0);
    }
}