package com.nightwind.mealordering.model;

import com.nightwind.mealordering.Entity.MenuEntity;
import com.nightwind.mealordering.Entity.OrderEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.*;
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
    public static final String STATUS_CANCEL = "cancel";
    public static final String STATUS_NORMAL = "normal";
    public static final String STATUS_COMPLETED = "completed";

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
    public List<Order> getAllOrder(boolean justNormal) {

        List<Order> orders = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Query query;
            if (justNormal) {
                query = session.createQuery("from OrderEntity where status = :status");
                query.setString("status", STATUS_NORMAL);
            } else {
                query = session.createQuery("from OrderEntity");
            }

            for (Object entity: query.list()) {
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
            orderEntity.setStatus(STATUS_NORMAL);
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
            entity.setStatus(STATUS_CANCEL);
            session.update(entity);

            tx.commit();
            status = STATUS_CANCEL;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            status = STATUS_NORMAL;
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
            entity.setStatus(STATUS_NORMAL);
            session.update(entity);

            tx.commit();
            status = STATUS_NORMAL;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            status = STATUS_CANCEL;
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
            entity.setStatus(STATUS_COMPLETED);
            session.update(entity);

            tx.commit();
            status = STATUS_COMPLETED;
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
            list.add(new Status(STATUS_NORMAL));
            list.add(new Status(STATUS_CANCEL));
            list.add(new Status(STATUS_COMPLETED));
            LIST = new ArrayList<>(list);
        }
    }

    public static class OrderDetailModel extends AbstractTableModel {

        protected Order order;

        protected final String[] DISPLAY_NAME = {"菜品", "数量", "完成", "可用"};

        public OrderDetailModel(Order order) {
            this.order = order;
        }

        @Override
        public int getRowCount() {
            if (order != null && order.getMenuItems() != null) {
                return order.getMenuItems().size();
            }
            return 0;
        }

        @Override
        public int getColumnCount() {
            return DISPLAY_NAME.length;
        }

        @Override
        public String getColumnName(int column) {
            return DISPLAY_NAME[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MenuItem menuItem = order.getMenuItems().get(rowIndex);
            Object object = null;
            switch (columnIndex) {
                case 0:
                    object = menuItem.getDish().getName();
                    break;
                case 1:
                    object = menuItem.getNumber();
                    break;
                case 2:
                    object = menuItem.getStatus() == 1;
                    break;
                case 3:
                    object = !order.getStatus().equals(STATUS_CANCEL) && menuItem.getStatus() != 2;
                    break;
            }
            return object;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (order.getStatus().equals(STATUS_CANCEL)) {
                // order canceled
                return false;
            }
            return (columnIndex == 2 && (Boolean)getValueAt(0, 3)) || columnIndex == 3;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    public static class OrdersTableModel extends AbstractTableModel {

        protected List<Order> orders = loadOrderList();

        protected String[] COLUMN = {"id", "menuInfo", "totalCost", "time", "username", "status"};

        protected String[] DISPLAY_NAME = {"编号", "菜单信息", "总价", "时间", "用户名", "状态"};

        protected List<Order> loadOrderList() {
            return new OrderImpl().getAllOrder(false);
        }

        public Order getOrder(int index) {
            return orders.get(index);
        }

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
                    // [0, 19): return date and time exclude millisecond
                    obj = order.getTime().toString().substring(0, 19);
                    break;
                case 4:
                    obj = order.getUser().getUsername();
                    break;
                case 5:
//                    obj = order.getStatus().equals(STATUS_NORMAL);
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
            orders = loadOrderList();
//            fireTableRowsUpdated(0, orders.size());
            fireTableRowsInserted(0, orders.size()-1);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Order order = orders.get(rowIndex);
            if (aValue instanceof Status) {
                if (aValue.toString().equals(STATUS_CANCEL)) {
                    order.cancel();
                } else if (aValue.toString().equals(STATUS_NORMAL)){
                    order.enable();
                } else {
                    order.completed();
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    public static class ChefOrdersTableModel extends OrdersTableModel {

        @Override
        protected List<Order> loadOrderList() {
            return new OrderImpl().getAllOrder(true);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 5) {
                return orders.get(rowIndex).getStatus().equals(STATUS_COMPLETED);
            }
            return super.getValueAt(rowIndex, columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 5) {
                return Boolean.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 5) {
                if (aValue.equals(false)) {
                    orders.get(rowIndex).enable();
                } else {
                    orders.get(rowIndex).completed();
                }
            }
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }
}
