package com.nightwind.mealordering.service;

/**
 * Created by nightwind on 15/7/6.
 */
public interface Admin {

    void modifyPassword(User user, String newPwd);

    void registerUser(User user);

    void disableUser(User user);

    void enableUser(User user);
}
