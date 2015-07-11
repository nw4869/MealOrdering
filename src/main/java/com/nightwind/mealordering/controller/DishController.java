package com.nightwind.mealordering.controller;

import com.nightwind.mealordering.service.Dish;
import com.nightwind.mealordering.view.DishesForm;

/**
 * Created by nightwind on 15/7/9.
 */
public class DishController {

    private DishesForm view;
    private Dish model;

    public void setView(DishesForm view) {
        this.view = view;
    }

    public void setModel(Dish model) {
        this.model = model;
    }


}
