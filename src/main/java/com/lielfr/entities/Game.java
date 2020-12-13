package com.lielfr.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private double price;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "game", targetEntity = CustomerGame.class)
    private Set<CustomerGame> customers;

    public Game() {
        customers = new HashSet<>();
    }

    public Game(String name, double price) {
        customers = new HashSet<>();
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public Set<CustomerGame> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<CustomerGame> customers) {
        this.customers = customers;
    }
}
