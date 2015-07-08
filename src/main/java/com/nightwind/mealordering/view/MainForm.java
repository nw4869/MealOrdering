package com.nightwind.mealordering.view;

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
        JMenuItem userInfoItem = new JMenuItem("User Info");
        userInfoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserInfoForm().show();
            }
        });
        userMenu.add(userInfoItem);
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
