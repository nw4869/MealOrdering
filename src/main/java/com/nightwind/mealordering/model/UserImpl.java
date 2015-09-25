package com.nightwind.mealordering.model;

import com.nightwind.mealordering.Entity.UserEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by nightwind on 15/7/7.
 */
public class UserImpl implements User {

    public static String STATUS_NORMAL = "normal";

    public static String STATUS_DISABLE = "disable";

    public static final int ADMIN = 1;

    public static final int CHEF = 2;

    private UserEntity entity;

    public UserImpl(String username) {
        entity = new UserEntity();
        entity.setUsername(username);
    }

    UserImpl(UserEntity entity) {
        this.entity = entity;
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
            if (!isEnable()) {
                return false;
            }

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
        return entity.getStatus().equals(STATUS_NORMAL);
    }

    public boolean isAdmin() {
        return entity.getAdmin() == ADMIN;
    }

    public boolean isChef() {
        return entity.getAdmin() == CHEF;
    }
}
