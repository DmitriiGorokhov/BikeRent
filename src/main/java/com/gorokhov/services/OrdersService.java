package com.gorokhov.services;

import com.gorokhov.models.Bike;
import com.gorokhov.models.Client;
import com.gorokhov.models.Order;
import com.gorokhov.models.Storage;
import com.gorokhov.repositories.BikesRepository;
import com.gorokhov.repositories.ClientsRepository;
import com.gorokhov.repositories.OrdersRepository;
import com.gorokhov.repositories.StoragesRepository;
import com.gorokhov.util.exceptions.BikeNotFoundException;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import com.gorokhov.util.exceptions.OrderNotFoundException;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final BikesRepository bikesRepository;
    private final ClientsRepository clientsRepository;
    private final StoragesRepository storagesRepository;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository, BikesRepository bikesRepository, ClientsRepository clientsRepository, StoragesRepository storagesRepository) {
        this.ordersRepository = ordersRepository;
        this.bikesRepository = bikesRepository;
        this.clientsRepository = clientsRepository;
        this.storagesRepository = storagesRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOne(long id) {
        return ordersRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Set<Order> findAll() {
        return new HashSet<>(ordersRepository.findAll());
    }

    @Transactional
    public Order save(Order order) {
        Set<Bike> bikes = order.getBikes();
        order = ordersRepository.save(order);
        for (Bike b : bikes) {
            Optional<Bike> bikeOpt = bikesRepository.findById(b.getId());
            if (bikeOpt.isPresent()) {
                Bike bike = bikeOpt.get();
                bike.getOrders().add(order);
                bike.setAvailable(false);
            } else
                throw new BikeNotFoundException();
        }
        return order;
    }

    @Transactional
    public Order update(long id, Order order) {
        Client newClient = clientsRepository.findById(order.getClient().getId())
                                            .orElseThrow(ClientNotFoundException::new);
        Storage newStorage = storagesRepository.findById(order.getStorage().getId())
                                            .orElseThrow(StorageNotFoundException::new);
        Set<Bike> newBikes = new HashSet<>();
        Set<Bike> tempBikes = order.getBikes();
        for (Bike b : tempBikes) {
            Bike newBike = bikesRepository.findById(b.getId()).orElseThrow(BikeNotFoundException::new);
            newBikes.add(newBike);
        }

        Order updatedOrder = ordersRepository.findById(id).orElseThrow(OrderNotFoundException::new);
        Client oldClient = updatedOrder.getClient();
        Storage oldStorage = updatedOrder.getStorage();
        Set<Bike> oldBikes = updatedOrder.getBikes();

        oldClient.getOrders().remove(updatedOrder);
        oldStorage.getOrders().remove(updatedOrder);
        for (Bike b : oldBikes) {
            Bike oldBike = bikesRepository.findById(b.getId()).orElseThrow(BikeNotFoundException::new);
            oldBike.getOrders().remove(updatedOrder);
        }

        updatedOrder.setClient(newClient);
        newClient.getOrders().add(updatedOrder);
        updatedOrder.setStorage(newStorage);
        newStorage.getOrders().add(updatedOrder);
        updatedOrder.setBikes(newBikes);
        for (Bike b : newBikes) {
            b.getOrders().add(updatedOrder);
        }

        return updatedOrder;
    }
}