package com.gorokhov.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Storage")
public class Storage {

    @Id
    @Column(name = "id")
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private Address address;

    @OneToMany(mappedBy = "storage")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JsonIgnore
    private Set<Bike> bikes;

    @OneToMany(mappedBy = "storage")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JsonIgnore
    private Set<Order> orders;

    public Storage() {}

    public Storage(Address address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Bike> getBikes() {
        return bikes;
    }

    public void setBikes(Set<Bike> bikes) {
        this.bikes = bikes;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Storage storage = (Storage) o;
        return id == storage.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Storage{" +
                "id=" + id +
                ", address=" + address +
                '}';
    }
}