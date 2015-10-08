package com.nightwind.mealordering.model;

import com.nightwind.mealordering.Entity.DishEntity;
import com.nightwind.mealordering.Entity.MenuEntity;

/**
 * Created by nightwind on 15/7/11.
 */
public class MenuItemImpl implements MenuItem {

    private int id;
    private int number;
    private Dish dish;
    private int status;

    public MenuItemImpl() {
    }

    public MenuItemImpl(MenuEntity entity) {
        id = entity.getId();
        number = entity.getDishNumber();
        dish = new DefaultDish(entity.getDishId());
        status = entity.getStatus();
    }

    public MenuItemImpl(Dish dish, int number) {
        this.number = number;
        this.dish = dish;
    }

    @Override
    public Dish getDish() {
        return dish;
    }

    @Override
    public void setDish(Dish dish) {
        this.dish = dish;
    }

    @Override
    public Integer getNumber() {
        return number;
    }

    @Override
    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public void setStatus(Integer status) {
        // TODO: update database
        this.status = status;
    }
}
