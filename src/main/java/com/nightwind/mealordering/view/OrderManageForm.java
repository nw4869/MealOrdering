package com.nightwind.mealordering.view;

import com.nightwind.mealordering.controller.OrdersController;
import com.nightwind.mealordering.model.Order;
import com.nightwind.mealordering.model.OrderImpl;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;

/**
 * Created by nightwind on 15/7/13.
 */
public class OrderManageForm {
    private static JFrame frame;
    private JPanel panel;
    private JTable table1;
    private JButton refreshButton;
    private JTextField filterField;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JTextField filterField2;
    private JButton ResetButton;
    private JButton ResetButton2;
    private JTable detailTable;
    private OrderImpl.OrdersTableModel ordersTableModel;
    private OrdersController controller;
    TableRowSorter<OrderImpl.OrdersTableModel> sorter;
    private static boolean sAdmin = false;
    private java.util.Timer timer;

    public OrderManageForm() {
        comboBox1.addActionListener(e -> {
            newFilter();
        });
        comboBox2.addActionListener(e -> {
            newFilter();
        });
        ResetButton.addActionListener(e -> {
            filterField.setText("");
        });
        ResetButton2.addActionListener(e -> {
            filterField2.setText("");
        });
    }

    public void showOrderMenuItemDetail(Order order) {
        OrderImpl.OrderDetailModel orderModel = new OrderImpl.OrderDetailModel(order);
        detailTable.setModel(orderModel);
    }

    private void createUIComponents() {
        if (OrderManageForm.sAdmin) {
            ordersTableModel = new OrderImpl.OrdersTableModel();
        } else {
            ordersTableModel = new OrderImpl.ChefOrdersTableModel();
        }
        table1 = new JTable(ordersTableModel);
        table1.setDefaultRenderer(OrderImpl.Status.class, new StatusRender());
        table1.setDefaultEditor(OrderImpl.Status.class, new StatusEditor(OrderImpl.Status.LIST));
        table1.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(ordersTableModel);
        table1.setRowSorter(sorter);
        table1.getSelectionModel().addListSelectionListener(e -> {
            // when selected show the menu items
            controller.rowSelected(table1.getSelectedRow());
        });

        filterField = new JTextField();
        filterField.getDocument().addDocumentListener( new FilterDocumentListener());

        filterField2 = new JTextField();
        filterField2.getDocument().addDocumentListener(new FilterDocumentListener());

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

    private class FilterDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            newFilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            newFilter();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            newFilter();
        }

    }


    private void newFilter() {
        int filterIndex1 = comboBox1.getSelectedIndex();
        int filterIndex2 = comboBox2.getSelectedIndex();
        List<RowFilter<OrderImpl.OrdersTableModel, Object>> filters = new ArrayList<>();
        RowFilter<OrderImpl.OrdersTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            filters.add(RowFilter.regexFilter(filterField.getText(), filterIndex1));
            filters.add(RowFilter.regexFilter(filterField2.getText(), filterIndex2));
            rf = RowFilter.andFilter(filters);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

    private class StatusRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof OrderImpl.Status) {
                setText(((OrderImpl.Status) value).value);
            }

            return this;
        }
    }

    public class StatusEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private final List<OrderImpl.Status> list;
        private OrderImpl.Status value;

        public StatusEditor(List<OrderImpl.Status> list) {
            this.list = list;
        }

        @Override
        public Object getCellEditorValue() {
            return this.value;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof OrderImpl.Status) {
                this.value = (OrderImpl.Status) value;
            }

            JComboBox<OrderImpl.Status> comboBox = new JComboBox<>();

            for (OrderImpl.Status status: list) {
                comboBox.addItem(status);
            }

            comboBox.setSelectedItem(value);
            comboBox.addActionListener(this);

            return comboBox;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.value = (OrderImpl.Status) ((JComboBox< OrderImpl.Status >)e.getSource()).getSelectedItem();
        }
    }

    public static void show(boolean admin) {
        OrderManageForm.sAdmin = admin;
        if (frame != null) {
            frame.dispose();
        }
        frame = new JFrame("订单管理");
        OrderManageForm view = new OrderManageForm();
        view.controller = new OrdersController();
        view.controller.setTableModel(view.ordersTableModel);
        view.controller.setView(view);

        frame.setContentPane(view.panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        if (!admin) {
            view.refreshPeriod(1000);
        }
    }

    public void refreshPeriod(int periodMs) {
        if (this.timer == null) {
            this.timer = new Timer();
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                OrderManageForm.this.controller.refresh();
            }
        }, periodMs, periodMs);
    }

    public static void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        show(true);
    }
}
