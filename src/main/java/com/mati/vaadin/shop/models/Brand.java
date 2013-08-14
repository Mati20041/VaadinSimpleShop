package com.mati.vaadin.shop.models;

import javax.persistence.*;
import java.util.Set;

/**
 * User: Mati
 * Date: 09.07.13
 * Time: 13:46
 */
@Entity
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;


    @OneToMany(mappedBy = "brand")
    private Set<Car> cars;

    public Brand(){}

    public Brand(String name){
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }
}
