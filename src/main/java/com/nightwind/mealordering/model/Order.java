package com.nightwind.mealordering.model;

import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by nightwind on 15/7/6.
 */
public interface Order {
    
    void commit(User user);

    void cancel();

    void enable();

    List<MenuItem> getMenuItems();

    void addMenuItem(MenuItem menuItem);

    void removeMenuItem(MenuItem menuItem);

    void saveOrUpdateMenuItem(MenuItem menuItem);

    void clear();

    void refresh();

    User getUser();

    Timestamp getTime();

    List<Order> getAllOrder();

    String getStatus();

    int getId();

    double getTotalCost();

    void addActionListener(ActionListener listener);

    void removeActionListener(ActionListener listener);
}
