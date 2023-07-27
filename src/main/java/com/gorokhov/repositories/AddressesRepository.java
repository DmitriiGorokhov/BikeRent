package com.gorokhov.repositories;

import com.gorokhov.models.Address;
import com.gorokhov.models.enums.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressesRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByCityAndStreetAndHouse(City city, String street, int house);
}
