package com.nightwind.mealordering.service;

/**
 * Created by nightwind on 15/7/10.
 */
public interface Subject<Model> {

    interface Listener<Model> {
        void update(Model model);
        void insert(Model model);
        void delete(Model model);
    }

    void attach(Listener<Model> listener);

    void detach(Listener<Model> listener);

}
