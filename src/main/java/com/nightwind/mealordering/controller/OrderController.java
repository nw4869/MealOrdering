package com.nightwind.mealordering.controller;

import com.nightwind.mealordering.model.MenuItem;
import com.nightwind.mealordering.model.Order;
import com.nightwind.mealordering.model.UserManager;
import com.nightwind.mealordering.view.MainForm;

import javax.swing.*;

/**
 * Created by nightwind on 15/7/12.
 */
public class OrderController {
    private MainForm view;
    private Order model;

    public void setView(MainForm view) {
        this.view = view;
    }

    public void setModel(Order model) {
        this.model = model;
    }

    public void inc(MenuItem menuItem) {
        menuItem.setNumber(menuItem.getNumber() + 1);
        model.saveOrUpdateMenuItem(menuItem);
    }

    public void dec(MenuItem menuItem) {
        int number = menuItem.getNumber() - 1;
        if (number >= 0) {
            menuItem.setNumber(number);
            model.saveOrUpdateMenuItem(menuItem);
        }
    }

    public boolean commit() {
        if (model.getMenuItems().size() > 0) {
            model.commit(UserManager.getInstance().getCurrentUser());
            JOptionPane.showMessageDialog(null, "提交成功");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "请选择菜品");
            return false;
        }
    }

    public void reset() {
        model.clear();
    }

    public void refresh() {
//        view.initList();
        model.refresh();
    }
}
