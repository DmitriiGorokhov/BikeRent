package com.gorokhov.services;

import com.gorokhov.models.Address;
import com.gorokhov.models.Client;
import com.gorokhov.models.Storage;
import com.gorokhov.models.enums.City;
import com.gorokhov.repositories.ClientsRepository;
import com.gorokhov.repositories.StoragesRepository;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import com.gorokhov.util.exceptions.ClientNotUpdatedException;
import com.gorokhov.util.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientsServiceTest {

    @Mock
    private ClientsRepository clientsRepository;

    @InjectMocks
    private ClientsService clientsService;

    @Test
    public void givenClient_whenSaveClient_thenReturnClient() {
        long id = 1L;
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsRepository.save(client)).willReturn(client);

        Client saved = clientsService.save(client);

        assertNotNull(saved);
        assertEquals(client, saved);
        verify(clientsRepository, times(1)).save(client);
        reset(clientsRepository);
    }

    @Test
    public void givenClient_whenGetById_thenReturnClient() {
        long id = 2L;
        String email = "bob.email.com";
        String name = "Bob";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsRepository.findById(id)).willReturn(Optional.of(client));

        Client found = clientsService.findOne(id).orElseThrow(ClientNotFoundException::new);

        assertEquals(client, found);
        verify(clientsRepository, times(1)).findById(id);
        reset(clientsRepository);
    }

    @Test
    public void givenClient_whenGetByEmail_thenReturnClient() {
        long id = 3L;
        String email = "mike@email.com";
        String name = "Mike";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsRepository.findByEmail(email)).willReturn(Optional.of(client));

        Client found = clientsService.findOne(email).orElseThrow(ClientNotFoundException::new);

        assertEquals(client, found);
        verify(clientsRepository, times(1)).findByEmail(email);
        reset(clientsRepository);
    }

    @Test
    public void givenClients_whenGetAllClients_thenReturnClientsList() {
        long id1 = 4L;
        String email1 = "jerry@email.com";
        String name1 = "Jerry";
        Client client1 = new Client(email1, name1);
        client1.setId(id1);

        long id2 = 5L;
        String email2 = "maggy@email.com";
        String name2 = "Maggy";
        Client client2 = new Client(email2, name2);
        client2.setId(id2);

        given(clientsRepository.findAll()).willReturn(List.of(client1, client2));

        Set<Client> found = clientsService.findAll();

        assertNotNull(found);
        assertEquals(2, found.size());
        verify(clientsRepository, times(1)).findAll();
        reset(clientsRepository);
    }

    @Test
    public void givenClients_whenGetAllClientsByNameContaining_thenReturnClientsList() {
        long id1 = 7L;
        String email1 = "jake@email.com";
        String name1 = "Jake";
        Client client1 = new Client(email1, name1);
        client1.setId(id1);

        long id2 = 8L;
        String email2 = "flake@email.com";
        String name2 = "Flake";
        Client client2 = new Client(email2, name2);
        client2.setId(id2);

        String searchName = "ake";

        given(clientsRepository.findAllByNameContaining(searchName)).willReturn(Set.of(client1, client2));

        Set<Client> found = clientsService.findAllByNameContaining(searchName);

        assertNotNull(found);
        assertEquals(2, found.size());
        verify(clientsRepository, times(1)).findAllByNameContaining(searchName);
        reset(clientsRepository);
    }

    @Test
    public void givenClient_whenUpdateClient_thenReturnUpdatedClient() {
        long id = 9L;
        String email = "uggy@email.com";
        String name = "Uggy";
        Client client = new Client(email, name);
        client.setId(id);

        String newEmail = "muggy@email.com";
        String newName = "Muggy";

        given(clientsRepository.findByEmail(newEmail)).willReturn(Optional.empty());
        given(clientsRepository.findById(id)).willReturn(Optional.of(client));

        client.setEmail(newEmail);
        client.setName(newName);
        Client updated = clientsService.update(id, client);

        assertEquals(newEmail, updated.getEmail());
        assertEquals(newName, updated.getName());
        verify(clientsRepository, times(1)).findByEmail(newEmail);
        verify(clientsRepository, times(1)).findById(id);
        reset(clientsRepository);
    }

    @Test
    public void givenClient_whenUpdateClient_thenThrowClientNotUpdatedException() {
        long id = 10L;
        String email = "luffy@email.com";
        String name = "Luffy";
        Client client = new Client(email, name);
        client.setId(id);

        String newEmail = "luffy@email.com";

        long id2 = 11L;
        String name2 = "Sanji";
        Client existingClient = new Client(newEmail, name2);
        existingClient.setId(id2);

        given(clientsRepository.findByEmail(newEmail)).willReturn(Optional.of(existingClient));

        assertThrows(ClientNotUpdatedException.class, () -> clientsService.update(id, client));
        verify(clientsRepository, times(1)).findByEmail(newEmail);
        reset(clientsRepository);
    }
}