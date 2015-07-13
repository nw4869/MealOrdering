package com.nightwind.mealordering.controller;

import com.nightwind.mealordering.model.OrderImpl;
import com.nightwind.mealordering.view.OrderManageForm;

/**
 * Created by nightwind on 15/7/13.
 */
public class OrdersController {

    private OrderManageForm view;
    private OrderImpl.OrdersTableModel tableModel;

    public void setView(OrderManageForm view) {
        this.view = view;
    }

    public void setTableModel(OrderImpl.OrdersTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void refresh() {
        tableModel.refresh();
    }

//    public void cancel() {
//        tableModel.cancel();
//    }
}
