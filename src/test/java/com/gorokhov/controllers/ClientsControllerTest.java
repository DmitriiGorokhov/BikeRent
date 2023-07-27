package com.gorokhov.controllers;

import com.gorokhov.models.Address;
import com.gorokhov.models.Client;
import com.gorokhov.models.Comment;
import com.gorokhov.models.enums.City;
import com.gorokhov.services.AddressesService;
import com.gorokhov.services.ClientsService;
import com.gorokhov.util.JsonUtil;
import com.gorokhov.util.exceptions.*;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientsController.class)
public class ClientsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientsService clientsService;

    @MockBean
    private Comment comment;

    @Test
    public void givenClient_whenPostClient_thenReturnStatusCreated() {
        long id = 1L;
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsService.save(Mockito.any())).willReturn(client);

        try {
            mockMvc.perform(post("/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(client)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(clientsService, times(1)).save(Mockito.any());
        reset(clientsService);
    }

    @Test
    public void givenClient_whenPostClient_thenThrowClientNotCreatedException() {
        long id = 2L;
        String email = "bob.email.com";
        String name = "Bob";
        Client client = new Client(email, name);
        client.setId(id);

        String errorMessage = "email - Email должен быть корректным; ";

        try {
            mockMvc.perform(post("/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(client)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof ClientNotCreatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(clientsService);
    }

    @Test
    public void givenClient_whenGetClient_thenReturnJson() {
        long id = 3L;
        String email = "mike@email.com";
        String name = "Mike";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsService.findOne(id)).willReturn(Optional.of(client));

        try {
            mockMvc.perform(get("/clients/3")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$.name", is(client.getName()), String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(clientsService, times(1)).findOne(id);
        reset(clientsService);
    }

    @Test
    public void givenClients_whenGetClients_thenReturnJsonArray() {
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

        Set<Client> clients = new LinkedHashSet<>();
        Collections.addAll(clients, client1, client2);

        given(clientsService.findAll()).willReturn(clients);

        try {
            mockMvc.perform(get("/clients")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(client1.getId()), long.class))
                    .andExpect(jsonPath("$[0].email", is(client1.getEmail()), String.class))
                    .andExpect(jsonPath("$[0].name", is(client1.getName()), String.class))
                    .andExpect(jsonPath("$[1].id", is(client2.getId()), long.class))
                    .andExpect(jsonPath("$[1].email", is(client2.getEmail()), String.class))
                    .andExpect(jsonPath("$[1].name", is(client2.getName()), String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(clientsService, times(1)).findAll();
        reset(clientsService);
    }

    @Test
    public void givenClient_whenGetNonExistentClient_thenThrowClientNotFoundException() {
        long id = 6L;
        String errorMessage = "Клиент не был найден";

        given(clientsService.findOne(id)).willThrow(new ClientNotFoundException());

        try {
            mockMvc.perform(get("/clients/6")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof ClientNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(clientsService);
    }

    @Test
    public void givenClients_whenGetClientsByNameContaining_thenReturnJsonArray() {
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

        Set<Client> clients = new LinkedHashSet<>();
        Collections.addAll(clients, client1, client2);
        String searchName = "ake";

        given(clientsService.findAllByNameContaining(searchName)).willReturn(clients);

        try {
            mockMvc.perform(get("/clients/search?name=ake")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(client1.getId()), long.class))
                    .andExpect(jsonPath("$[0].email", is(client1.getEmail()), String.class))
                    .andExpect(jsonPath("$[0].name", is(client1.getName()), String.class))
                    .andExpect(jsonPath("$[1].id", is(client2.getId()), long.class))
                    .andExpect(jsonPath("$[1].email", is(client2.getEmail()), String.class))
                    .andExpect(jsonPath("$[1].name", is(client2.getName()), String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(clientsService, times(1)).findAllByNameContaining(searchName);
        reset(clientsService);
    }

    @Test
    public void givenClient_whenPostUpdatedClient_thenReturnStatusAccepted() {
        long id = 9L;
        String email = "uggy@email.com";
        String name = "Uggy";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsService.update(id, client)).willReturn(client);

        try {
            mockMvc.perform(post("/clients/9/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(client)))
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(clientsService, times(1)).update(id, client);
        reset(clientsService);
    }

    @Test
    public void givenClient_whenPostUpdatedClient_thenThrowClientNotUpdatedException() {
        long id = 10L;
        String email = "daddy@email.com";
        String name = "Daddy";
        Client client = new Client(email, name);
        client.setId(id);
        String errorMessage = "Клиент с таким email уже существует";

        given(clientsService.update(id, client))
                .willThrow(new ClientNotUpdatedException(errorMessage));

        try {
            mockMvc.perform(post("/clients/10/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(client)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof ClientNotUpdatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                            Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(clientsService);
    }

    @Test
    public void givenClient_whenPostIncorrectUpdatedClient_thenThrowClientNotUpdatedException() {
        long id = 11L;
        String email = "homer@email.com";
        String name = "H";
        Client client = new Client(email, name);
        client.setId(id);
        String errorMessage = "name - Имя должно содержать от 2 до 30 символов; ";

        try {
            mockMvc.perform(post("/clients/11/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(client)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof ClientNotUpdatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(clientsService);
    }

    @Test
    public void givenClient_whenGetClient_thenThrowLazyInitializationException() {
        long id = 12L;
        String email = "bart@email.com";
        String name = "Bart";
        Client client = new Client(email, name);
        client.setId(id);
        client.setComments(Collections.singleton(comment));
        String errorMessage = "LAZY INIT EXCEPTION: ";


        given(clientsService.findOne(id)).willReturn(Optional.of(client));
        given(comment.getDescription()).willThrow(new LazyInitializationException(errorMessage));

        try {
            mockMvc.perform(get("/clients/12/lazy")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof LazyInitializationException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(clientsService);
    }

    @Test
    public void givenClient_whenGetClient_thenReturnJsonWithLazyComment() {
        long id = 13L;
        String email = "steve@email.com";
        String name = "Steve";
        Client client = new Client(email, name);
        client.setId(id);
        client.setComments(Collections.singleton(new Comment()));

        given(clientsService.findOne(id)).willReturn(Optional.of(client));

        try {
            mockMvc.perform(get("/clients/13/lazy")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$.name", is(client.getName()), String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(clientsService);
    }

    @Test
    public void givenClient_whenPostClientWithHibernate_thenReturnStatusCreated() {
        long id = 14L;
        String email = "hiber@email.com";
        String name = "Hiber";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsService.saveWithHibernate(Mockito.any())).willReturn(client);

        try {
            mockMvc.perform(post("/clients/hibernate/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(client)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(clientsService, times(1)).saveWithHibernate(Mockito.any());
        reset(clientsService);
    }

    @Test
    public void givenClient_whenPostClientWithHibernate_thenThrowClientNotCreatedException() {
        long id = 15L;
        String email = "hiber.email.com";
        String name = "Hiber";
        Client client = new Client(email, name);
        client.setId(id);

        String errorMessage = "email - Email должен быть корректным; ";

        try {
            mockMvc.perform(post("/clients/hibernate/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(client)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof ClientNotCreatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(clientsService);
    }

    @Test
    public void givenClient_whenGetClientWithHibernate_thenReturnJson() {
        long id = 16L;
        String email = "hib@email.com";
        String name = "Hib";
        Client client = new Client(email, name);
        client.setId(id);

        given(clientsService.findOneWithHibernate(id)).willReturn(client);

        try {
            mockMvc.perform(get("/clients/hibernate/16")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$.name", is(client.getName()), String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(clientsService, times(1)).findOneWithHibernate(id);
        reset(clientsService);
    }
}