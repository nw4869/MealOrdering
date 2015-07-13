package com.nightwind.mealordering.view;

import com.nightwind.mealordering.controller.OrdersController;
import com.nightwind.mealordering.model.OrderImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nightwind on 15/7/13.
 */
public class OrderManageForm {
    private static JFrame frame;
    private JPanel panel;
    private JTable table1;
    private JButton refreshButton;
    private OrderImpl.OrdersTableModel ordersTableModel;
    private OrdersController controller;

    private void createUIComponents() {
        ordersTableModel = new OrderImpl.OrdersTableModel();
        table1 = new JTable(ordersTableModel);

        refreshButton = new JButton();
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.refresh();
                }
            }
        });
    }

    public static void show() {
        if (frame != null) {
            frame.dispose();
        }
        frame = new JFrame("OrderManageForm");
        OrderManageForm view = new OrderManageForm();
        view.controller = new OrdersController();
        view.controller.setTableModel(view.ordersTableModel);

        frame.setContentPane(view.panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public static void dispose() {
        frame.dispose();
    }

    public static void main(String[] args) {
        show();
    }
}
