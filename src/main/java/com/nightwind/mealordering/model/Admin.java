package com.nightwind.mealordering.model;

/**
 * Created by nightwind on 15/7/6.
 */
public interface Admin {

    void modifyPassword(User user, String newPwd);

    void registerUser(String username, String password);

    void disableUser(User user);

    void enableUser(User user);
}
