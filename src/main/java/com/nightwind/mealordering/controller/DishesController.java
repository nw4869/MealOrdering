package com.nightwind.mealordering.controller;

import com.nightwind.mealordering.service.Dish;
import com.nightwind.mealordering.service.DishManager;
import com.nightwind.mealordering.view.DishesForm;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nightwind on 15/7/10.
 */
public class DishesController{
    private DishesForm view;
    private DishManager model;
    private MyTableModel tableModel;

    public void setView(DishesForm view) {
        this.view = view;
    }

    public void setModel(DishManager model) {
        this.model = model;
    }

    public List<Dish> getDishes() {
        return model.getDishes();
    }

    public class MyTableModel extends AbstractTableModel{

            private List<Dish> dishes = model.getDishes();

            public void addRow(Dish dish) {
                dishes.add(dish);
                fireTableRowsInserted(dishes.size()-1, dishes.size()-1);
            }

            @Override
            public String getColumnName(int column) {
                String col = "";
                switch (column) {
                    case 0:
                        col = "id";
                        break;
                    case 1:
                        col = "name";
                        break;
                    case 2:
                        col = "cost";
                        break;
                    case 3:
                        col = "info";
                        break;
                    case 4:
                        col = "status";
                        break;
                }
                return col;
            }

            @Override
            public int getRowCount() {
                return dishes.size();
            }

            @Override
            public int getColumnCount() {
                return 5;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object object = null;
                Dish dish = dishes.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        object = dish.getId();
                        break;
                    case 1:
                        object = dish.getName();
                        break;
                    case 2:
                        object = dish.getCost();
                        break;
                    case 3:
                        object = dish.getInfo();
                        break;
                    case 4:
                        object = dish.getStatus().equals("normal");
                        break;
                }
                return object;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                Object obj = getValueAt(0, columnIndex);
                if (obj != null) {
                    return obj.getClass();
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex > 0;
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                Dish dish = dishes.get(rowIndex);
                switch (columnIndex) {
                    case 1:
                        //name
                        dish.setName((String) aValue);
                        break;
                    case 2:
                        //cost
                        dish.setCost((Double) aValue);
                        break;
                    case 3:
                        //info
                        dish.setInfo((String) aValue);
                        break;
                    case 4:
                        //status
                        boolean enable = (boolean) aValue;
                        if (enable) {
                            dish.enable();
                        } else {
                            dish.disable();
                        }
                        break;
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            }

        public void removeRow(Dish dish) {
            int index = dishes.indexOf(dish);
            fireTableRowsDeleted(index, index);
            dishes.remove(dish);
        }

        public Dish get(int index) {
            return dishes.get(index);
        }
    }

    public TableModel getTableModel() {
        if (tableModel == null) {
            tableModel = new MyTableModel();
//            tableModel.addTableModelListener(new TableModelListener() {
//                @Override
//                public void tableChanged(TableModelEvent e) {
//
//                    int row = e.getFirstRow();
//                    int column = e.getColumn();
//                    TableModel model = (TableModel)e.getSource();
//                    String columnName = model.getColumnName(column);
//                    Object data = model.getValueAt(row, column);
//
//                    if (e.getType() == TableModelEvent.UPDATE) {
////                        System.out.print("UPDATE EVENT: ");
////                        System.out.println("data = " + data);
//                    }
//                    switch (e.getType()) {
//                        case TableModelEvent.DELETE:
//                            break;
//                        case TableModelEvent.INSERT:
//                            break;
//                    }
//                }
//            });
        }
        return tableModel;
    }

    public void insert() {
        double cost;
        try {
            cost = Double.valueOf(view.getCostField());
            String name = view.getNameField();
            String info = view.getInfoField();
            model.insert(name, cost, info);
        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Cost must be a digital");
        }
    }

    public void delete() {
        List<Dish> dishes = new ArrayList<>();
        for (int i: view.getSelectionRows()) {
            dishes.add(((MyTableModel)getTableModel()).get(i));
        }
        for (Dish dish: dishes) {
            model.delete(dish);
        }
    }
}
