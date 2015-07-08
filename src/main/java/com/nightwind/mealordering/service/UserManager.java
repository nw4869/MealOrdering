package com.nightwind.mealordering.service;

/**
 * Created by nightwind on 15/7/7.
 */
public abstract class UserManager implements Admin{

    private static UserManager userManager;

    private UserManager() {}

    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManagerImpl();
        }
        return userManager;
    }

    public abstract User getCurrentUser();

    public abstract void setCurrentUser(User user);

    public static class UserManagerImpl extends UserManager {

        private static User currentUser;

        @Override
        public User getCurrentUser() {
            return currentUser;
        }

        @Override
        public void setCurrentUser(User user) {
            currentUser = user;
        }

        @Override
        public void modifyPassword(User user, String newPwd) {
            user.updatePassword(newPwd);
        }

        @Override
        public void registerUser(User user) {

        }

        @Override
        public void disableUser(User user) {

        }

        @Override
        public void enableUser(User user) {

        }
    }
}
