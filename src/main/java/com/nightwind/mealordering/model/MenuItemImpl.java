package com.nightwind.mealordering.model;

import com.nightwind.mealordering.Entity.DishEntity;
import com.nightwind.mealordering.Entity.MenuEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nightwind on 15/7/11.
 */
public class MenuItemImpl implements MenuItem {

    private static final java.lang.String ACTION_UPDATE_STATUS = "ACTION_UPDATE_STATUS";
    private int id;
    private int number;
    private Dish dish;
    private int status;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_CANCEL = 2;

    private List<ActionListener> listeners = new ArrayList<>();

    public MenuItemImpl() {
    }

    public MenuItemImpl(MenuEntity entity) {
        id = entity.getId();
        number = entity.getDishNumber();
        dish = new DefaultDish(entity.getDishId());
        status = entity.getStatus();
    }

    public MenuItemImpl(Dish dish, int number) {
        this.number = number;
        this.dish = dish;
    }

    @Override
    public Dish getDish() {
        return dish;
    }

    @Override
    public void setDish(Dish dish) {
        this.dish = dish;
    }

    @Override
    public Integer getNumber() {
        return number;
    }

    @Override
    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public void setStatus(Integer status) {
        int org = this.status;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            MenuEntity entity = (MenuEntity) session.get(MenuEntity.class, id);
            entity.setStatus(status);
            session.update(entity);

            tx.commit();
            this.status = status;
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ACTION_UPDATE_STATUS));
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            this.status = org;
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void processEvent(ActionEvent e) {
        for (ActionListener listener : listeners) {
            listener.actionPerformed(e);
        }
    }

    @Override
    public synchronized void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
}
