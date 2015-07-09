package com.nightwind.mealordering.service;

/**
 * Created by nightwind on 15/7/6.
 */
public interface User {

    boolean login(String password);

    void logout();

    boolean verifyPassword(String password);

    void updatePassword(String newPwd);

    String getUsername();

    String getName();

    void updateName(String name);

    boolean isEnable();

    boolean isAdmin();
}
