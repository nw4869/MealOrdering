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
import java.util.*;

/**
 * Created by nightwind on 15/7/11.
 */
public class OrderImpl implements Order {

    public static final java.lang.String ADD_MENU_ITEM = "add_menu_item";
    public static final String REMOVE_MENU_ITEM = "remove_menu_item";
    public static final String COMMIT = "commit";
    public static final String ACTION_CANCEL = "cancel";
    public static final String ACTION_COMPLETED = "completed";
    public static final String ACTION_NORMAL = "normal";
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

            for (Object entity : query.list()) {
                OrderEntity orderEntity = (OrderEntity) entity;

                // get menus
                Criteria cr = session.createCriteria(MenuEntity.class);
                cr.add(Restrictions.eq("orderId", orderEntity.getId()));

                List<MenuItem> menuItems = new ArrayList<>();
                for (Object menuItemEntity : cr.list()) {
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
            final int STATUS_ITEM_CANCEL = 2;
            if (item.getStatus() != STATUS_ITEM_CANCEL) {
                cost += item.getDish().getCost() * item.getNumber();
            }
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
            for (MenuItem item : menuItems) {
                if (item.getNumber() > 0) {
                    MenuEntity menuEntity = new MenuEntity();
                    menuEntity.setOrderId(orderEntity.getId());
                    menuEntity.setDishId(item.getDish().getId());
                    menuEntity.setDishNumber(item.getNumber());

                    session.save(menuEntity);
                }
            }

            tx.commit();
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, COMMIT));
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

            // cancel menu items
//            Query query = session.createQuery("update MenuEntity set status = 2 where orderId = :orderId");
//            query.setInteger("orderId", id);
//            query.executeUpdate();

            for (MenuItem menuItem: menuItems) {
                menuItem.setStatus(0);
            }

            OrderEntity entity = (OrderEntity) session.get(OrderEntity.class, id);
            entity.setStatus(STATUS_CANCEL);
            session.update(entity);

            tx.commit();
            status = STATUS_CANCEL;
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ACTION_CANCEL));
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

            for (MenuItem menuItem: menuItems) {
                // 将完成的变成未完成，已取消的不变
                if (menuItem.getStatus() == 1) {
                    menuItem.setStatus(0);
                }
            }

            OrderEntity entity = (OrderEntity) session.get(OrderEntity.class, id);
            entity.setStatus(STATUS_NORMAL);
            session.update(entity);

            tx.commit();
            status = STATUS_NORMAL;
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ACTION_NORMAL));
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
        for (MenuItem item : menuItems) {
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

            // completed menu items
//            Query query = session.createQuery("update MenuEntity set status = 1 where status = 0 and orderId = :orderId");
//            query.setInteger("orderId", id);
//            query.executeUpdate();

            for (MenuItem menuItem: menuItems) {
                if (menuItem.getStatus() == 0) {
                    menuItem.setStatus(1);
                }
            }

            OrderEntity entity = (OrderEntity) session.get(OrderEntity.class, id);
            org = entity.getStatus();
            entity.setStatus(STATUS_COMPLETED);
            session.update(entity);

            tx.commit();
            status = STATUS_COMPLETED;
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ACTION_COMPLETED));
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            status = org;
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

    public TableModel getTableModel() {
        return null;
    }

    public static class Status {
        public Status() {
        }

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

    public static double getTotalMoney(List<Order> orders, boolean forAllOrders) {
        double totalMoney = 0;
        for (Order order: orders) {
            String status = order.getStatus();
            if ((forAllOrders && !status.equals(STATUS_CANCEL)) || status.equals(STATUS_NORMAL)) {
                totalMoney += order.getTotalCost();
            }
        }
        return totalMoney;
    }

    /**
     *  表数据模型： 所有订单的概要（每个菜品的总数表）
     */
    public static class OverviewTableModel extends AbstractTableModel {

//        private final List<Order> orders;
        private final List<String> nameList = new ArrayList<>();
        private final List<Integer> countList = new ArrayList<>();

        protected final String[] DISPLAY_NAME = {"菜品", "数量"};


        public OverviewTableModel(List<Order> orders, boolean forAllOrders) {
            // init list
            initList(orders, forAllOrders);
        }

        private void initList(List<Order> orders, boolean forAllOrders) {
            Map<String, Integer> dishCountMap = new HashMap<>();
            for (Order order : orders) {
                // check normal order
                String status = order.getStatus();
                if ((forAllOrders && !status.equals(STATUS_CANCEL)) || status.equals(STATUS_NORMAL)) {
                    // count all menu item
                    for (MenuItem menuItem : order.getMenuItems()) {
                        String name = menuItem.getDish().getName();
                        Integer count = dishCountMap.get(name);
                        if (count == null) {
                            count = 0;
                        }
                        count += menuItem.getNumber();
                        dishCountMap.put(name, count);
                    }
                }
            }
            for (String key : dishCountMap.keySet()) {
                nameList.add(key);
                countList.add(dishCountMap.get(key));
            }
        }

        @Override
        public int getRowCount() {
            return nameList.size();
        }

        @Override
        public int getColumnCount() {
            return DISPLAY_NAME.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object object = null;
            switch (columnIndex) {
                case 0:
                    object = nameList.get(rowIndex);
                    break;
                case 1:
                    object = countList.get(rowIndex);
                    break;
            }
            return object;
        }

        @Override
        public String getColumnName(int column) {
            return DISPLAY_NAME[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }
    }

    /**
     *  表数据模型： 某个订单的具体菜品和数目表
     */
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
            MenuItem menuItem = order.getMenuItems().get(rowIndex);
            if (columnIndex == 2) {
                // to be completed
                if ((boolean)aValue) {
                    menuItem.setStatus(MenuItemImpl.STATUS_COMPLETED);
                    fireTableCellUpdated(rowIndex, columnIndex);

                    // check other menu items whether completed
                    boolean allCompleted = true;
                    for (MenuItem item: order.getMenuItems()) {
                        if (item.getStatus() != MenuItemImpl.STATUS_COMPLETED) {
                            allCompleted = false;
                            break;
                        }
                    }
                    // and when to complete the order!
                    if (allCompleted) {
                        order.completed();
                    }
                }
            } else if (columnIndex == 3) {
                int newStatus = (boolean)aValue ? MenuItemImpl.STATUS_NORMAL : MenuItemImpl.STATUS_CANCEL;
                menuItem.setStatus(newStatus);
                fireTableCellUpdated(rowIndex, 2);
                fireTableCellUpdated(rowIndex, 3);
            }

        }

        public void fireAllUpdate() {
            fireTableRowsUpdated(0, getRowCount() - 1);
        }
    }


    /**
     *  表数据模型： 所有订票
     */
    public static class OrdersTableModel extends AbstractTableModel {

        protected List<Order> orders = loadOrderList();

        protected String[] COLUMN = {"id", "menuInfo", "totalCost", "time", "username", "status"};

        protected String[] DISPLAY_NAME = {"编号", "菜单信息", "总价", "时间", "用户名", "状态"};

        protected List<Order> loadOrderList() {
            return new OrderImpl().getAllOrder(false);
        }

        public Order getOrder(int index) {
            if (0 <= index && index < orders.size()) {
                return orders.get(index);
            } else {
                return null;
            }
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
            Order order = new OrderImpl();
            if (getRowCount() > 0) {
                order = orders.get(rowIndex);
            } else {
            }
            switch (columnIndex) {
                case 0:
                    obj = order.getId();
                    break;
                case 1:
                    StringBuilder menuInfo = new StringBuilder();
                    for (MenuItem item : order.getMenuItems()) {
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
            fireTableRowsInserted(0, orders.size() - 1);
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
                } else if (aValue.toString().equals(STATUS_NORMAL)) {
                    order.enable();
                } else {
                    order.completed();
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        public List<Order> getOrders() {
            return orders;
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
                fireTableCellUpdated(rowIndex, columnIndex);
            } else {
                super.setValueAt(aValue, rowIndex, columnIndex);
            }
        }
    }
}
