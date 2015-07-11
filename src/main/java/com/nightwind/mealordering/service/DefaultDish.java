package com.nightwind.mealordering.service;

import com.nightwind.mealordering.Entity.DishEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nightwind on 15/7/10.
 */
public class DefaultDish implements Dish {

    private DishEntity entity;

    private List<Listener<Dish>> listeners = new ArrayList<>();

    public DefaultDish(int dishId) {
        entity = getEntity(dishId);
    }

    DefaultDish(DishEntity entity) {
        this.entity = entity;
    }

    private DishEntity getEntity(int dishId) {
        DishEntity entity = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            entity = (DishEntity) session.get(DishEntity.class, dishId);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return entity;
    }

    @Override
    public Integer getId() {
        return entity.getId();
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public void setName(String name) {
//        String origin = entity.getName();
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = null;
//        try {
//            tx = session.beginTransaction();
//
//            Query query = session.createQuery("update DishEntity set name = :name where id = :id");
//            query.setString("name", name);
//            query.setInteger("id", entity.getId());
//
//            tx.commit();
//            entity.setName(name);
//        } catch (HibernateException e) {
//            if (tx != null) {
//                tx.rollback();
//                entity.setName(origin);
//            }
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
        String origin = entity.getName();
        entity.setName(name);
        try {
            updateDB();
        } catch (Exception e) {
            entity.setName(origin);
        }
    }

    @Override
    public Double getCost() {
        return entity.getCost();
    }

    @Override
    public void setCost(double cost) {
        entity.setCost(cost);
        updateDB();
    }

    @Override
    public String getInfo() {
        return entity.getInfo();
    }

    @Override
    public void setInfo(String info) {
        String origin = entity.getInfo();
        entity.setInfo(info);
        try {
            updateDB();
        } catch (Exception e) {
            entity.setInfo(origin);
        }
    }

    @Override
    public String getStatus() {
        return entity.getStatus();
    }

    @Override
    public void disable() {
        entity.setStatus("disable");
        updateDB();
    }

    @Override
    public void enable() {
        entity.setStatus("normal");
        updateDB();
    }

    @Override
    public void remove() {

    }

    private void updateDB() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            session.update(entity);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void attach(Listener<Dish> listener) {
        listeners.add(listener);
    }

    @Override
    public void detach(Listener<Dish> listener) {
        listeners.remove(listener);
    }
}
