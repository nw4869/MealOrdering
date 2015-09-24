package com.nightwind.mealordering.view;

import com.nightwind.mealordering.model.User;
import com.nightwind.mealordering.model.UserManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Created by nightwind on 15/7/9.
 */
public class UserAdminForm {
    private static final int TABLE_COL_COUNT = 4;
    private List<User> users;
    private JFrame frame;
    private JPanel panel;
    private JTable table1;

    private void setupTable() {

    }

    public void show() {
        if (frame == null) {
            frame = new JFrame("用户管理");
            frame.setContentPane(new UserAdminForm().panel);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            setupTable();
            frame.pack();
        }
        frame.setVisible(true);
    }

    public void dispose() {
        frame.dispose();
    }

    public static void main(String[] args) {
    }

    private void createUIComponents() {
        users = UserManager.getInstance().getUsers();

        table1 = new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return users.size();
            }

            @Override
            public int getColumnCount() {
                return TABLE_COL_COUNT;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                User user = users.get(rowIndex);
                Object obj = null;
                switch (columnIndex) {
                    case 0:
                        obj = user.getUsername();
                        break;
                    case 1:
                        obj = user.getName();
                        break;
                    case 2:
                        obj = user.isEnable();
                        break;
                    case 3:
                        obj = user.isAdmin();
                        break;
                }
                return obj;
            }

            @Override
            public String getColumnName(int column) {
                String name = super.getColumnName(column);
                switch (column) {
                    case 0:
                        name = "username";
                        break;
                    case 1:
                        name = "name";
                        break;
                    case 2:
                        name = "enable";
                        break;
                    case 3:
                        name = "isAdmin";
                        break;
                }
                return name;
            }
        });
    }
}
