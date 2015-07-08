package com.nightwind.mealordering.service;

/**
 * Created by nightwind on 15/7/6.
 */
public interface Dish {
    
    String getName();

    Double getCost();

    String getInfo();

    String getStatus();

    Dish save(Dish dish);

    void disable();

    void enable();

    void remove();

}

