package com.nightwind.mealordering.view;

import com.nightwind.mealordering.Entity.UserEntity;
import com.nightwind.mealordering.model.User;
import com.nightwind.mealordering.model.UserImpl;
import com.nightwind.mealordering.utils.HibernateUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nightwind on 15/7/7.
 */
public class LoginForm {

    private static JFrame frame;

    private JPanel panel;
    private JTextField usernameFiled;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton quitButton;

    public LoginForm() {
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        User user = new UserImpl(usernameFiled.getText());
        try {
            boolean ok = user.login(String.valueOf(passwordField.getPassword()));
            if (ok) {

                if (user.isChef()) {
                    frame.setVisible(false);
                    new OrderManageForm().show();
                } else {

//            JOptionPane.showMessageDialog(null, "Login success", "Login success", JOptionPane.INFORMATION_MESSAGE);
//            new UserInfoForm().show();
                    new MainForm().show();

                    frame.setVisible(false);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Login failed", "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ExceptionInInitializerError e) {
            JOptionPane.showMessageDialog(null, "Database connect failed", "Database connect failed", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Database connect failed", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        frame = new JFrame("登陆");
        frame.setContentPane(new LoginForm().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);  //center
        frame.setVisible(true);
        HibernateUtil.getSessionFactory();
    }

    public static void show() {
        frame.setVisible(true);
    }

    public void setData(UserEntity data) {
        usernameFiled.setText(data.getName());
        passwordField.setText(data.getPassword());
    }

    public void getData(UserEntity data) {
        data.setName(usernameFiled.getText());
        data.setPassword(passwordField.getText());
    }
}
