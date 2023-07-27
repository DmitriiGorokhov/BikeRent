package com.gorokhov.services;

import com.gorokhov.models.Bike;
import com.gorokhov.models.Storage;
import com.gorokhov.repositories.BikesRepository;
import com.gorokhov.repositories.StoragesRepository;
import com.gorokhov.util.exceptions.BikeNotFoundException;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BikesService {

    private final BikesRepository bikesRepository;
    private final StoragesRepository storagesRepository;

    @Autowired
    public BikesService(BikesRepository bikesRepository, StoragesRepository storagesRepository) {
        this.bikesRepository = bikesRepository;
        this.storagesRepository = storagesRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Bike> findOne(long id) {
        return bikesRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Set<Bike> findAll() {
        return new HashSet<>(bikesRepository.findAll());
    }

    @Transactional
    public Bike save(Bike bike) {
        return bikesRepository.save(bike);
    }

    @Transactional
    public Bike update(long id, Bike bike) {
        Bike updatedBike = bikesRepository.findById(id).orElseThrow(BikeNotFoundException::new);
        Storage newStorage = storagesRepository.findById(bike.getStorage().getId())
                                                .orElseThrow(StorageNotFoundException::new);

        updatedBike.getStorage().getBikes().remove(updatedBike);
        newStorage.getBikes().add(updatedBike);

        updatedBike.setColor(bike.getColor());
        updatedBike.setSize(bike.getSize());
        updatedBike.setLabel(bike.getLabel());
        updatedBike.setStorage(newStorage);

        return updatedBike;
    }
}