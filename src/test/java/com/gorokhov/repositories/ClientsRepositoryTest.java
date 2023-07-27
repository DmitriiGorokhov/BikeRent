package com.gorokhov.repositories;

import com.gorokhov.models.Client;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@DataJpaTest
public class ClientsRepositoryTest {

    private final ClientsRepository clientsRepository;

    @Autowired
    public ClientsRepositoryTest(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    @Test
    public void givenNewClient_whenFindById_thenReturnClientWithCorrectId() {
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        long expectedId = clientsRepository.save(client).getId();

        Client found = clientsRepository.findById(expectedId)
                .orElseThrow(ClientNotFoundException::new);

        Assertions.assertEquals(expectedId, found.getId());
    }

    @Test
    public void givenNewClient_whenFindByEmail_thenReturnClientWithCorrectEmail() {
        String email = "bob@email.com";
        String name = "Bob";
        Client client = new Client(email, name);
        clientsRepository.save(client);

        Client found = clientsRepository.findByEmail(email)
                .orElseThrow(ClientNotFoundException::new);

        Assertions.assertEquals(email, found.getEmail());
    }

    @Test
    public void givenNewClient_whenFindByEmail_thenReturnClientWithCorrectName() {
        String email = "mike@email.com";
        String name = "Mike";
        Client client = new Client(email, name);
        clientsRepository.save(client);

        Client found = clientsRepository.findByEmail(email)
                .orElseThrow(ClientNotFoundException::new);

        Assertions.assertEquals(name, found.getName());
    }

    @Test
    public void givenNewClients_whenFindByNameContaining_thenReturnCorrectClients() {
        String baddyEmail = "baddy@email.com";
        String baddyName = "Baddy";
        Client baddy = new Client(baddyEmail, baddyName);
        clientsRepository.save(baddy);

        String faddyEmail = "faddy@email.com";
        String faddyName = "Faddy";
        Client faddy = new Client(faddyEmail, faddyName);
        clientsRepository.save(faddy);

        String maggEmail = "magg@email.com";
        String maggName = "Magg";
        Client magg = new Client(maggEmail, maggName);
        clientsRepository.save(magg);

        Set<Client> expected = new HashSet<>();
        Collections.addAll(expected, baddy, faddy);

        Set<Client> found = clientsRepository.findAllByNameContaining("addy");

        Assertions.assertTrue(found.containsAll(expected) && found.size() == expected.size());
    }

    @Test
    public void givenNewClient_whenFindAllClients_thenReturnMoreThanZero() {
        String email = "jerry@email.com";
        String name = "Jerry";
        Client client = new Client(email, name);
        clientsRepository.save(client);

        Set<Client> found = new HashSet<>(clientsRepository.findAll());

        Assertions.assertTrue(found.size() > 0);
    }
}