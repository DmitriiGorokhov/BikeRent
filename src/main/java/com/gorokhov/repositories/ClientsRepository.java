package com.gorokhov.repositories;

import com.gorokhov.models.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

//    @EntityGraph(attributePaths = "comments")
    @EntityGraph(value = "client-entity-graph")
    Set<Client> findAllByNameContaining(String name);
}