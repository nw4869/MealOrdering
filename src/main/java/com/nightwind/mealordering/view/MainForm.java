package com.nightwind.mealordering.view;

import com.nightwind.mealordering.service.UserManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nightwind on 15/7/8.
 */
public class MainForm {
    private JFrame frame;
    private JPanel panel;
    private JTable table1;

    private void initFrame() {
        frame = new JFrame("MainForm");
        frame.setContentPane(panel);

        setupMenu();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
    }

    private void setupTable() {
        table1.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return 0;
            }

            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return null;
            }
        });
    }

    private void setupMenu() {
        JMenuBar menubar = new JMenuBar();
        JMenu userMenu = new JMenu("User");
        // user info item
        JMenuItem userInfoItem = new JMenuItem("User Info");
        userInfoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserInfoForm().show();
            }
        });
        userMenu.add(userInfoItem);

        // set up admin menus
        if (UserManager.getInstance().getCurrentUser().isAdmin()) {
            // user admin
            JMenuItem userAdminItem = new JMenuItem("User Admin");
            userAdminItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new UserAdminForm().show();
                }
            });
            userMenu.add(userAdminItem);

            // dishes admin
            JMenuItem dishesMangeItem = new JMenuItem("Dish Manage");
            dishesMangeItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    new DishesForm();
                    DishesForm.getInstance();
                }
            });
            JMenu dishMenu = new JMenu("Dish");
            dishMenu.add(dishesMangeItem);
            menubar.add(dishMenu);
        }


        JMenuItem logoutMen = new JMenuItem("Logout");
        userMenu.add(logoutMen);
        logoutMen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                LoginForm.show();
            }
        });

        menubar.add(userMenu);

        frame.setJMenuBar(menubar);
    }

    public void show() {
        if (frame == null) {
            initFrame();
        }
        frame.setVisible(true);
    }

    public static void main(String[] args) {
    }
}
