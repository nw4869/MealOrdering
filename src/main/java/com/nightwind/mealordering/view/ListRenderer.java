package com.nightwind.mealordering.view;

import com.nightwind.mealordering.model.MenuItem;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.text.NumberFormat;

/**
 * Created by nightwind on 15/7/12.
 */
public class ListRenderer {
    private JPanel panel;
    private JTextField a0TextField;
    private JLabel dishNameLabel;
    private JLabel costLabel;

    private void createUIComponents() {
        a0TextField = new JTextField("0");
        a0TextField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return ((JTextField)input).getText().matches("\\d+");
            }
        });

    }

    public ListCellRenderer<MenuItem> getListCellRenderer() {
        return new ListCellRenderer<MenuItem>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends MenuItem> list, MenuItem value, int index, boolean isSelected, boolean cellHasFocus) {
                dishNameLabel.setText(value.getDish().getName());
                costLabel.setText(String.valueOf(value.getDish().getCost()));
                a0TextField.setText(String.valueOf(value.getNumber()));
                return panel;
            }
        };
    }

    public JPanel getPanel() {
        return panel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ListRenderer");
        frame.setContentPane(new ListRenderer().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
