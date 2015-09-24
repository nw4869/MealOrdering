package com.nightwind.mealordering.model;

import com.nightwind.mealordering.Entity.MenuEntity;
import com.nightwind.mealordering.Entity.OrderEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
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
                Criteria cr = session.createCriteria(MenuEntity.class);
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
    public double getTotalCost() {
        double cost = 0;
        for (MenuItem item : getMenuItems()) {
            cost += item.getDish().getCost() * item.getNumber();
        }
        return cost;
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
            status = "cancel";
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            status = "normal";
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void enable() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            OrderEntity entity = (OrderEntity) session.get(OrderEntity.class, id);
            entity.setStatus("normal");
            session.update(entity);

            tx.commit();
            status = "normal";
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            status = "cancel";
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

    @Override
    public void completed() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        String org = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            OrderEntity entity = (OrderEntity) session.get(OrderEntity.class, id);
            org = entity.getStatus();
            entity.setStatus("completed");
            session.update(entity);

            tx.commit();
            status = "completed";
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            status = org;
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void processEvent(ActionEvent e) {
        for(ActionListener listener: listeners) {
            listener.actionPerformed(e);
        }
    }

    public TableModel getTableModel() {
        return null;
    }

    public static class Status {
        public Status() {}

        public Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Status && value.equals(((Status) obj).value);
        }

        public String value;

        public final static List<Status> LIST;

        static {
            List<Status> list = new ArrayList<>();
            list.add(new Status("normal"));
            list.add(new Status("cancel"));
            list.add(new Status("completed"));
            LIST = new ArrayList<>(list);
        }
    }

    public static class OrdersTableModel extends AbstractTableModel {

        private List<Order> orders = new OrderImpl().getAllOrder();

        private String[] COLUMN = {"id", "menuInfo", "totalCost", "time", "username", "status"};

        private String[] DISPLAY_NAME = {"编号", "菜单信息", "总价", "时间", "用户名", "状态"};

        @Override
        public int getRowCount() {
            return orders.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object obj = null;
            Order order = orders.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    obj = order.getId();
                    break;
                case 1:
                    StringBuilder menuInfo = new StringBuilder();
                    for (MenuItem item: order.getMenuItems()) {
                        Dish dish = item.getDish();
                        menuInfo.append(dish.getName()).append("*").append(item.getNumber()).append(", ");
                    }
                    if (menuInfo.length() >= 2) {
                        menuInfo.delete(menuInfo.length() - 2, menuInfo.length());
                    }
                    obj = menuInfo.toString();
                    break;
                case 2:
                    obj = order.getTotalCost();
                    break;
                case 3:
                    obj = order.getTime();
                    break;
                case 4:
                    obj = order.getUser().getUsername();
                    break;
                case 5:
//                    obj = order.getStatus().equals("normal");
                    obj = new Status(order.getStatus());
                    break;
            }
            return obj;
        }

        @Override
        public String getColumnName(int column) {
            return DISPLAY_NAME[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        public void refresh() {
            orders = new OrderImpl().getAllOrder();
//            fireTableRowsUpdated(0, orders.size());
            fireTableRowsInserted(0, orders.size());
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Order order = orders.get(rowIndex);
            if (aValue instanceof Status) {
                if (aValue.toString().equals("cancel")) {
                    order.cancel();
                } else if (aValue.toString().equals("enable")){
                    order.enable();
                } else {
                    order.completed();
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }
}
