package com.nightwind.mealordering.view;

import com.nightwind.mealordering.controller.OrdersController;
import com.nightwind.mealordering.model.Order;
import com.nightwind.mealordering.model.OrderImpl;

import java.awt.print.PrinterException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;

/**
 * Created by nightwind on 15/7/13.
 */
public class OrderManageForm implements ActionListener{
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
    private JTable overviewTable;
    private JButton printButton;
    private JLabel ordersNumberLabel;
    private JLabel totalMoneyLabel;
    private OrderImpl.OrdersTableModel ordersTableModel;
    private OrdersController controller;
    TableRowSorter<OrderImpl.OrdersTableModel> sorter;
    private boolean admin = false;
    private java.util.Timer timer;
    private OrderImpl.OverviewTableModel overviewTableModel;
    private List<Order> orders;

    public OrderManageForm() {
        initComponents();
    }

    public OrderManageForm(boolean admin) {
        this.admin = admin;

        initComponents();
    }

    public void showOrderMenuItemDetail(Order order) {
        OrderImpl.OrderDetailModel orderModel = new OrderImpl.OrderDetailModel(order);
        order.removeActionListener(this);
        order.addActionListener(this);
        detailTable.setModel(orderModel);
    }

    private void createUIComponents() {

        if (this.admin) {
            ordersTableModel = new OrderImpl.OrdersTableModel();
        } else {
            ordersTableModel = new OrderImpl.ChefOrdersTableModel();
        }
        this.orders = ordersTableModel.getOrders();
        table1 = new JTable(ordersTableModel);
        table1.setDefaultRenderer(OrderImpl.Status.class, new StatusRender());
        table1.setDefaultEditor(OrderImpl.Status.class, new StatusEditor(OrderImpl.Status.LIST));
        table1.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(ordersTableModel);
        List <RowSorter.SortKey> sortKey = new ArrayList<>();
        // sort key default: time desc
        sortKey.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKey);
        table1.setRowSorter(sorter);
        table1.getSelectionModel().addListSelectionListener(e -> {
            // when selected show the menu items
            if (e.getValueIsAdjusting())
                return;
            int row = table1.getSelectedRow();
            if (0 <= row && row < ordersTableModel.getRowCount()) {
                row = table1.convertRowIndexToModel(row);
                controller.rowSelected(row);
            }
        });

        final boolean forAllOrders = this.admin;
        overviewTableModel = new OrderImpl.OverviewTableModel(ordersTableModel.getOrders(), forAllOrders);
        overviewTable = new JTable(overviewTableModel);
        overviewTable.setRowSorter(new TableRowSorter<>(overviewTableModel));
        overviewTable.getSelectionModel().addListSelectionListener( e -> {
            int row = overviewTable.getSelectedRow();
            if (0 <= row && row < overviewTableModel.getRowCount()) {
                row = overviewTable.convertRowIndexToModel(row);
                String dish = (String) overviewTableModel.getValueAt(row, 0);
                comboBox2.setSelectedIndex(1);
                filterField2.setText(dish);
            }
        });

        detailTable = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                //TODO gray background
//                boolean ok = (Boolean)getValueAt(row, 3);
//                if (!ok) {
//                    c.setBackground(Color.LIGHT_GRAY);
//                }
                return c;
            }
        };

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

    private void initComponents() {
        setupOrdersInfo(orders);
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
        printButton.addActionListener(e -> {
            try {
                table1.print();
            } catch (PrinterException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void setupOrdersInfo(List<Order> orders) {
        ordersNumberLabel.setText(String.valueOf(orders.size()));
        totalMoneyLabel.setText(String.valueOf(OrderImpl.getTotalMoney(orders, true)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == ActionEvent.ACTION_PERFORMED) {
            String cmd = e.getActionCommand();
            if (cmd.equals(OrderImpl.COMMIT)) {
                controller.refresh();
            } else if (cmd.equals(OrderImpl.ACTION_COMPLETED) || cmd.equals(OrderImpl.ACTION_CANCEL) ||
                    cmd.equals(OrderImpl.ACTION_NORMAL)) {
                controller.refresh();
                ((OrderImpl.OrderDetailModel) detailTable.getModel()).fireAllUpdate();
            }
        }
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
//        System.out.println("row count = " + table1.getRowCount());
        List<Order> filteredOrders = new ArrayList<>();
        for (int i = 0; i < table1.getRowCount(); i++) {
            final int modelRow = table1.convertRowIndexToModel(i);
            filteredOrders.add(orders.get(modelRow));
        }
        setupOrdersInfo(filteredOrders);
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

    public void show() {
        if (frame != null) {
            frame.dispose();
        }
        frame = new JFrame("订单管理");
//        OrderManageForm view = new OrderManageForm();
        OrderManageForm view = this;
        view.controller = new OrdersController();
        view.controller.setTableModel(view.ordersTableModel);
        view.controller.setView(view);

        frame.setContentPane(view.panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        // auto refresh
//        if (!admin) {
            view.refreshPeriod(30000);
//        }
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
//        show(true);
    }
}
