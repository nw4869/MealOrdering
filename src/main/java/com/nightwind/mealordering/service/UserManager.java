package com.nightwind.mealordering.service;

import com.nightwind.mealordering.Entity.UserEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

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

    public abstract List<User> getUsers();

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
        public List<User> getUsers() {
            List<User> users = new ArrayList<>();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Criteria cr = session.createCriteria(UserEntity.class);
                cr.add(Restrictions.ne("admin", UserImpl.ADMIN));
                List<UserEntity> entities = cr.list();
                for (UserEntity entity: entities) {
                    users.add(new UserImpl(entity));
                }

                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
            return users;
        }

        @Override
        public void modifyPassword(User user, String newPwd) {
            user.updatePassword(newPwd);
        }

        @Override
        public void registerUser(String username, String password) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                UserEntity entity = new UserEntity();
                entity.setUsername(username);
                entity.setPassword(password);
                session.save(entity);

                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
        }

        @Override
        public void disableUser(User user) {
            setUserStatus(user, false);
        }

        @Override
        public void enableUser(User user) {
            setUserStatus(user, true);
        }

        private void setUserStatus(User user, boolean enable) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("update UserEntity set status = :status where username = :username");
                query.setString("username", user.getUsername());
                query.setString("status", enable ? UserImpl.STATUS_NORMAL : UserImpl.STATUS_DISABLE);
                query.executeUpdate();

                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
        }
    }
}
