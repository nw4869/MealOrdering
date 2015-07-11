package com.nightwind.mealordering.view;

import com.nightwind.mealordering.controller.DishesController;
import com.nightwind.mealordering.service.Dish;
import com.nightwind.mealordering.service.DishManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nightwind on 15/7/9.
 */
public class DishesForm implements Dish.Listener<Dish> {
    private JFrame frame;
    private DishesController controller;
    private JPanel panel;
    private JTable table1;
    private JTextField costField;
    private JButton insertButton;
    private JButton deleteButton;
    private JTextField nameField;
    private JTextField infoField;
    private DishesController.MyTableModel tableModel;

//    DishesForm() {
//        frame = createAndShowGUI();
//    }

    DishesForm(DishesController controller) {
        this.controller = controller;
    }

    private JFrame createAndShowGUI() {
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.insert();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                controller.removeRow();
                controller.delete();
            }
        });
        JFrame frame;
        frame = new JFrame("Dishes Mange");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    public void setController(DishesController controller) {
        this.controller = controller;
    }

    public String getNameField() {
        return nameField.getText();
    }


    public String getCostField() {
        return costField.getText();
    }

    public String getInfoField() {
        return infoField.getText();
    }

    public static void main(String[] args) {
        DishesController controller = new DishesController();
        DishManager model = DishManager.getInstance();
        controller.setModel(model);
        DishesForm view = new DishesForm(controller);
        controller.setView(view);
        view.setController(controller);
        model.attach(view);

        view.createAndShowGUI();
    }

    @Override
    public void update(Dish dish) {
    }

    @Override
    public void insert(Dish dish) {
//        ((DefaultTableModel)table1.getModel()).addRow(new Object[] {dish.getId(), dish.getName(), dish.getCost(), dish.getInfo(), dish.getStatus()});
        tableModel.addRow(dish);
    }



    @Override
    public void delete(Dish dish) {
        tableModel.removeRow(dish);
    }

//    public JTable getTable() {
//        return table1;
//    }

    public int[] getSelectionRows() {
        return table1.getSelectedRows();
    }

    private void createUIComponents() {
        tableModel = (DishesController.MyTableModel) controller.getTableModel();
        table1 = new JTable(tableModel);
    }
}
