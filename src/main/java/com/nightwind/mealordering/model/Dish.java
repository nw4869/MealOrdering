package com.nightwind.mealordering.model;

/**
 * Created by nightwind on 15/7/6.
 */
public interface Dish extends Subject<Dish> {

    Integer getId();

    String getName();

    void setName(String name);

    Double getCost();

    void setCost(double cost);

    String getInfo();

    void setInfo(String info);

    String getStatus();

    void disable();

    void enable();

    void remove();

}

