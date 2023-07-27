package com.gorokhov.services;

import com.gorokhov.models.Address;
import com.gorokhov.models.Storage;
import com.gorokhov.repositories.StoragesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class StoragesService {

    private final StoragesRepository storagesRepository;

    @Autowired
    public StoragesService(StoragesRepository storagesRepository) {
        this.storagesRepository = storagesRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Storage> findOne(long id) {
        return storagesRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Storage> findOne(Address address) {
        return storagesRepository.findByAddress(address);
    }

    @Transactional(readOnly = true)
    public Set<Storage> findAll() {
        return new HashSet<>(storagesRepository.findAll());
    }
}