package com.gorokhov.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull(message = "Необходимо указать клиента")
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @ManyToMany(mappedBy = "orders")
    @NotNull(message = "Необходимо указать хотя бы 1 велосипед")
    private Set<Bike> bikes;

    @ManyToOne
    @NotNull(message = "Необходимо указать хранилище")
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private Storage storage;

    public Order() {}

    public Order(Client client, Set<Bike> bikes, Storage storage) {
        this.client = client;
        this.bikes = bikes;
        this.storage = storage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Bike> getBikes() {
        return bikes;
    }

    public void setBikes(Set<Bike> bikes) {
        this.bikes = bikes;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", client=" + client +
                ", bikes=" + bikes +
                ", storage=" + storage +
                '}';
    }
}