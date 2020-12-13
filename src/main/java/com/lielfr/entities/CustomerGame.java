package com.lielfr.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class CustomerGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Customer.class)
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Game.class)
    @JoinColumn(name="game_id", nullable = false)
    private Game game;

    private Instant purchaseTime;
    private double paidPrice;

    @Column(nullable = true, name="game_rating")
    private Integer rating;

    public CustomerGame() {
    }

    public CustomerGame(Customer customer, Game game, Instant purchaseTime, double paidPrice) {
        this.customer = customer;
        this.game = game;
        this.purchaseTime = purchaseTime;
        this.paidPrice = paidPrice;
        // Notice these two lines! We MUST make sure we add the item to the other side of the connection.
        customer.getGames().add(this);
        game.getCustomers().add(this);
    }

    public long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Instant getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Instant purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(double paidPrice) {
        this.paidPrice = paidPrice;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
