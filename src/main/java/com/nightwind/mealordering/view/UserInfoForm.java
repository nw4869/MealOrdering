package com.nightwind.mealordering.view;

import com.nightwind.mealordering.Entity.UserEntity;
import com.nightwind.mealordering.service.User;
import com.nightwind.mealordering.service.UserManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nightwind on 15/7/7.
 */
public class UserInfoForm {
    private final User currentUser;
    private UserEntity entity;

    private JPanel panel;
    private JTextField textField1;
    private JButton updateButton;
    //    private JButton queryButton;
    private JLabel usernameLabel;
    private JPasswordField passwordField;

    public UserInfoForm() {

        //get user entity
        entity = new UserEntity();
        currentUser = UserManager.getInstance().getCurrentUser();
        entity.setName(currentUser.getName());
        entity.setUsername(currentUser.getUsername());

        // init GUI data
        setData(entity);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isModifiedName(entity)) {
                    getData(entity);
                    currentUser.updateName(entity.getName());
                }
                if (isModifiedPassword(entity)) {
                    getData(entity);
                    currentUser.updatePassword(entity.getPassword());
                }
            }
        });
//        queryButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                setData(entity);
//            }
//        });
    }

    public void show() {
        JFrame frame = new JFrame("User Info");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);  //center
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new UserInfoForm().show();
    }

    public void setData(UserEntity data) {
        textField1.setText(data.getName());
        usernameLabel.setText(data.getUsername());
    }

    public void getData(UserEntity data) {
        data.setName(textField1.getText());
        data.setPassword(String.valueOf(passwordField.getPassword()));
    }

    public boolean isModifiedName(UserEntity data) {
        if (textField1.getText() != null ? !textField1.getText().equals(data.getName()) : data.getName() != null)
            return true;
        return false;
    }

    private boolean isModifiedPassword(UserEntity data) {
        if (passwordField.getPassword() != null ? !String.valueOf(passwordField.getPassword()).equals(data.getPassword()) : data.getPassword() != null)
            return true;
        return false;
    }
}
