package com.gorokhov.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gorokhov.models.enums.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "Address")
public class Address {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "city")
    @NotNull(message = "Необходимо указать город")
    private City city;

    @Column(name = "street")
    @NotNull(message = "Необходимо указать улицу")
    private String street;

    @Column(name = "house")
    @NotNull(message = "Необходимо указать номер дома")
    private int house;

    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private Storage storage;

    public Address() {}

    public Address(City city, String street, int house) {
        this.city = city;
        this.street = street;
        this.house = house;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouse() {
        return house;
    }

    public void setHouse(int house) {
        this.house = house;
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
        Address address = (Address) o;
        return id == address.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", city=" + city +
                ", street='" + street + '\'' +
                ", house=" + house +
                '}';
    }
}