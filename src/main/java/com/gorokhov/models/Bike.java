package com.gorokhov.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gorokhov.models.enums.Color;
import com.gorokhov.models.enums.Label;
import com.gorokhov.models.enums.Size;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cascade;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Bike")
public class Bike {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    @NotNull(message = "Необходимо указать цвет")
    private Color color;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    @NotNull(message = "Необходимо указать размер")
    private Size size;

    @Enumerated(EnumType.STRING)
    @Column(name = "label")
    @NotNull(message = "Необходимо указать марку")
    private Label label;

    @Column(name = "available")
    @NotNull(message = "Необходимо указать свободен или нет")
    private boolean available;

    @ManyToMany()
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(name = "Bike_Orders",
                joinColumns = @JoinColumn(name = "id"),
                inverseJoinColumns = @JoinColumn(name = "order_id"))
    @JsonIgnore
    private Set<Order> orders;

    @ManyToOne
    @NotNull(message = "Необходимо указать хранилище")
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private Storage storage;

    public Bike() {
        available = true;
    }

    public Bike(Color color, Size size, Label label, Storage storage) {
        this();
        this.color = color;
        this.size = size;
        this.label = label;
        this.storage = storage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
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
        Bike bike = (Bike) o;
        return id == bike.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Bike{" +
                "id=" + id +
                ", color=" + color +
                ", size=" + size +
                ", label=" + label +
                ", available=" + available +
                '}';
    }
}