package com.nightwind.mealordering.service.impl;

import com.nightwind.mealordering.Entity.UserEntity;
import com.nightwind.mealordering.service.User;
import com.nightwind.mealordering.service.UserManager;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by nightwind on 15/7/7.
 */
public class UserImpl implements User {

    private UserEntity entity;

    public UserImpl(String username) {
        entity = new UserEntity();
        entity.setUsername(username);
    }

    private static UserEntity getEntity(String username) {

        UserEntity userEntity = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            userEntity = (UserEntity) session.get(UserEntity.class, username);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return userEntity;
    }

    public boolean login(String password) {
        if (verifyPassword(password)) {

            entity = getEntity(entity.getUsername());

            UserManager.getInstance().setCurrentUser(this);
            return true;
        }
        return false;
    }

    public void logout() {

    }

    public boolean verifyPassword(String password) {
        boolean ok = false;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Criteria cr = session.createCriteria(UserEntity.class);
            cr.add(Restrictions.eq("username", entity.getUsername()));
            cr.add(Restrictions.eq("password", password));
            List list = cr.list();
            if (list.size() > 0) {
                ok = true;
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return ok;
    }

    public void updatePassword(String newPwd) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Query query = session.createQuery("update UserEntity set password = :password where username = :username");
            query.setString("username", getUsername());
            query.setString("password", newPwd);
            query.executeUpdate();

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public String getUsername() {
        return entity.getUsername();
    }

    public String getName() {
        return entity.getName();
    }

    public void updateName(String name) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Query query = session.createQuery("update UserEntity set name = :name where username = :username");
            query.setString("username", getUsername());
            query.setString("name", name);
            query.executeUpdate();

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public boolean isEnable() {
        return entity.getStatus().equals("normal");
    }

    public boolean isAdmin() {
        return false;
    }
}
