package com.gorokhov.repositories;

import com.gorokhov.models.Address;
import com.gorokhov.models.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoragesRepository extends JpaRepository<Storage, Long> {
    Optional<Storage> findByAddress(Address address);
}