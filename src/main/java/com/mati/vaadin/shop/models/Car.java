package com.mati.vaadin.shop.models;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Mati
 * Date: 09.07.13
 * Time: 13:46
 */
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name="";
    @Temporal(value = TemporalType.DATE)
    private Date yearOfProduction;
    private double price;
    private int sold;


    @ManyToOne
    private Brand brand;

    public Car(){}

    public Car(String name, Date yearOfProduction, double price, Brand brand) {
        this.name = name;
        this.yearOfProduction = yearOfProduction;
        this.price = price;
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(Date year) {
        this.yearOfProduction = year;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
