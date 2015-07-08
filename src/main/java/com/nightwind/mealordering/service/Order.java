package com.nightwind.mealordering.service;

/**
 * Created by nightwind on 15/7/6.
 */
public interface Order {
    
    Order commit(User user, MenuItem[] menuItems);

    void cancel();

    MenuItem[] getMenuItems();

    void addMenuItem(MenuItem menuItem);

    void removeMenuItem(MenuItem menuItem);
}
