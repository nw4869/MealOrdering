package com.nightwind.mealordering.model;

import com.nightwind.mealordering.Entity.MenuEntity;
import com.nightwind.mealordering.Entity.OrderEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nightwind on 15/7/11.
 */
public class OrderImpl implements Order {

    public static final java.lang.String ADD_MENU_ITEM = "add_menu_item";
    public static final String REMOVE_MENU_ITEM = "remove_menu_item";
    public static final String COMMIT = "commit";
    public static final String CANEL = "commit";
    public static final java.lang.String UPDATE_MENU_ITEM = "update_menu_item";
    public static final java.lang.String CLEAR = "clear";
    public static final String REFRESH = "refresh";

    private Timestamp time;

    private User user;

    List<MenuItem> menuItems = new ArrayList<>();

    private String status;
    private int id;

    private List<ActionListener> listeners = new ArrayList<>();

    public OrderImpl() {

    }

    private OrderImpl(OrderEntity entity, List<MenuItem> menu) {
        time = entity.getTime();
        user = new UserImpl(entity.getUsername());
        menuItems = menu;
        status = entity.getStatus();
        id = entity.getId();
    }

    @Override
    public List<Order> getAllOrder() {

        List<Order> orders = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            for (Object entity: session.createQuery("from OrderEntity").list()) {
                OrderEntity orderEntity = (OrderEntity) entity;

                // get menus
                Criteria cr = session.createCriteria(MenuItem.class);
                cr.add(Restrictions.eq("orderId", orderEntity.getId()));

                List<MenuItem> menuItems = new ArrayList<>();
                for (Object menuItemEntity: cr.list()) {
                   menuItems.add(new MenuItemImpl((MenuEntity) menuItemEntity));
                }

                orders.add(new OrderImpl(orderEntity, menuItems));
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return Collections.unmodifiableList(orders);
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void commit(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // create order
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setUsername(user.getUsername());
            orderEntity.setTime(new Timestamp(System.currentTimeMillis()));
            orderEntity.setStatus("normal");
            session.save(orderEntity);

            // save menuItem
            for (MenuItem item: menuItems) {
                if (item.getNumber() > 0) {
                    MenuEntity menuEntity = new MenuEntity();
                    menuEntity.setOrderId(orderEntity.getId());
                    menuEntity.setDishId(item.getDish().getId());
                    menuEntity.setDishNumber(item.getNumber());

                    session.save(menuEntity);
                }
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void cancel() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            OrderEntity entity = (OrderEntity) session.get(OrderEntity.class, id);
            entity.setStatus("cancel");
            session.update(entity);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return Collections.unmodifiableList(menuItems);
    }

    @Override
    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        processEvent(new ActionEvent(menuItem, ActionEvent.ACTION_PERFORMED, ADD_MENU_ITEM));
    }

    @Override
    public void removeMenuItem(MenuItem menuItem) {
        menuItems.remove(menuItem);
        processEvent(new ActionEvent(menuItem, ActionEvent.ACTION_PERFORMED, REMOVE_MENU_ITEM));
    }

    @Override
    public void saveOrUpdateMenuItem(MenuItem menuItem) {
        boolean found = false;
        for (MenuItem item: menuItems) {
            if (item.getDish().getId().equals(menuItem.getDish().getId())) {
                found = true;
                item.setNumber(menuItem.getNumber());
                processEvent(new ActionEvent(item, ActionEvent.ACTION_PERFORMED, UPDATE_MENU_ITEM));
                break;
            }
        }
        if (!found) {
            addMenuItem(menuItem);
        }
    }

    @Override
    public void clear() {
        menuItems.clear();
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, CLEAR));
    }

    @Override
    public void refresh() {
        menuItems.clear();
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, REFRESH));
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Timestamp getTime() {
        return time;
    }

    @Override
    public synchronized void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }

    private void processEvent(ActionEvent e) {
        for(ActionListener listener: listeners) {
            listener.actionPerformed(e);
        }
    }
}
