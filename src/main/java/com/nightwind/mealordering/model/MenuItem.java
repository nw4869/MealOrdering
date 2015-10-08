package com.nightwind.mealordering.model;

/**
 * Created by nightwind on 15/7/6.
 */
public interface MenuItem {

    Dish getDish();

    void setDish(Dish dish);

    Integer getNumber();

    void setNumber(Integer number);

    Integer getStatus();

    void setStatus(Integer status);
}
