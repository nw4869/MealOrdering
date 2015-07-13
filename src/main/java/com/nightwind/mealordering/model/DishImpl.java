package com.nightwind.mealordering.model;

import com.nightwind.mealordering.Entity.DishEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nightwind on 15/7/9.
 */
public class DishImpl implements Dish{

    private DishEntity entity = new DishEntity();

    private List<Listener> listeners = new ArrayList<>();

    @Override
    public void attach(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void detach(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public void setName(String name) {
        entity.setName(name);
        for (Listener listener: listeners) {
            listener.update(this);
        }
    }

    @Override
    public Double getCost() {
        return null;
    }

    @Override
    public void setCost(double cost) {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void setInfo(String info) {

    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void remove() {

    }
}
