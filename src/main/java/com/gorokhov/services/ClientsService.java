package com.gorokhov.services;

import com.gorokhov.models.*;
import com.gorokhov.repositories.ClientsRepository;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import com.gorokhov.util.exceptions.ClientNotUpdatedException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ClientsService {

    private final ClientsRepository clientsRepository;

    @Autowired
    public ClientsService(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Client> findOne(long id) {
        return clientsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Client> findOne(String email) {
        return clientsRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Set<Client> findAll() {
        return new HashSet<>(clientsRepository.findAll());
    }

    @Transactional
    public Set<Client> findAllByNameContaining(String name) {
        return clientsRepository.findAllByNameContaining(name);
    }

    @Transactional
    public Client save(Client client) {
        return clientsRepository.save(client);
    }

    @Transactional
    public Client update(long id, Client client) {
        String newEmail = client.getEmail();
        String newName = client.getName();
        Optional<Client> clientOpt = clientsRepository.findByEmail(newEmail);

        if (clientOpt.isPresent() && clientOpt.get().getId() != id)
            throw new ClientNotUpdatedException("Клиент с таким email уже существует");
        Client updatedClient = clientsRepository.findById(id).orElseThrow(ClientNotFoundException::new);
        updatedClient.setEmail(newEmail);
        updatedClient.setName(newName);
        return updatedClient;
    }

    @Transactional
    public Client saveWithHibernate(Client client) {
        try (Session session = getHibernateSession()) {
            session.beginTransaction();
            session.persist(client);
            session.getTransaction().commit();
        }
        return client;
    }

    @Transactional
    public Client findOneWithHibernate(long id) {
        Client client;
        try (Session session = getHibernateSession()) {
            session.beginTransaction();
            client = session.get(Client.class, id);
            session.getTransaction().commit();
        }
        return client;
    }

    private Session getHibernateSession() {
        ServiceRegistry registry;
        Session session;
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.config.xml");

            registry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            MetadataSources metadataSources = new MetadataSources(registry);
            metadataSources.addAnnotatedClasses(
                    Address.class,
                    Storage.class,
                    Client.class,
                    Comment.class,
                    Bike.class,
                    Order.class);
            Metadata metadata = metadataSources.buildMetadata();

            SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
            session = sessionFactory.getCurrentSession();
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
        return session;
    }
}