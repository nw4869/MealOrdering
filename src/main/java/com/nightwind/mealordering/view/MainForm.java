package com.nightwind.mealordering.view;

import com.nightwind.mealordering.controller.OrderController;
import com.nightwind.mealordering.model.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

/**
 * Created by nightwind on 15/7/8.
 */
public class MainForm implements ActionListener{
    private JFrame frame;
    private JPanel panel;
    private JList list1;
    private JButton commitButton;
    private JLabel costLabel;
    private JButton resetButton;
    private JButton refreshButton;
    private Order model;
    private OrderController controller;

    private double cost = 0;
    private UserAdminForm userAdminForm;
    private UserInfoForm userInfoForm;

    public MainForm() {
        commitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.commit();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.reset();
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.refresh();
            }
        });
    }

    private void initFrame() {
        frame = new JFrame("点餐系统主界面");
        frame.setContentPane(panel);

        setupMenu();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
    }

    private void setupMenu() {
        JMenuBar menubar = new JMenuBar();

        // order manage
        JMenu orderMenu = new JMenu("订单");
        JMenuItem orderManageItem = new JMenuItem("订单管理");
        orderManageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderManageForm.show();
            }
        });
        orderMenu.add(orderManageItem);
        menubar.add(orderMenu);

        JMenu userMenu = new JMenu("用户");
        // user info item
        JMenuItem userInfoItem = new JMenuItem("用户信息");
        userInfoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userInfoForm != null) {
                    userInfoForm.dispose();
                }
                userInfoForm = new UserInfoForm();
                userInfoForm.show();
            }
        });
        userMenu.add(userInfoItem);

        // set up admin menus
        if (UserManager.getInstance().getCurrentUser().isAdmin()) {
            // user admin
            JMenuItem userAdminItem = new JMenuItem("用户管理");
            userAdminItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (userAdminForm != null) {
                        userAdminForm.dispose();
                    }
                    userAdminForm = new UserAdminForm();
                    userAdminForm.show();
                }
            });
            userMenu.add(userAdminItem);

            // dishes admin
            JMenuItem dishesMangeItem = new JMenuItem("菜品管理");
            dishesMangeItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    new DishesForm();
                    DishesForm.show();
                }
            });
            JMenu dishMenu = new JMenu("菜品");
            dishMenu.add(dishesMangeItem);
            menubar.add(dishMenu);
        }


        JMenuItem logoutMen = new JMenuItem("登出");
        userMenu.add(logoutMen);
        logoutMen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userInfoForm != null) {
                    userInfoForm.dispose();
                }
                if (userAdminForm != null) {
                    userAdminForm.dispose();
                }
                DishesForm.dispose();
                OrderManageForm.dispose();
                frame.dispose();
                LoginForm.show();
            }
        });

        menubar.add(userMenu);

        frame.setJMenuBar(menubar);
    }

    public void show() {
        if (frame == null) {
            initFrame();
//            setModel(new OrderImpl());
        }
        frame.setVisible(true);
    }

    public void setModel(Order order) {
        this.model = order;
    }

    public static void main(String[] args) {
    }

    private void initList() {

        List<Dish> dishes = DishManager.getInstance().getAvailableDishes();
        DefaultListModel<MenuItem> listModel = new DefaultListModel<MenuItem>();

        for (Dish dish: dishes) {
            listModel.addElement(new MenuItemImpl(dish, 0));
        }

        list1 = new JList(listModel);
        list1.setCellRenderer(new ListRenderer().getListCellRenderer());
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList list = (JList)e.getSource();
                int index = list.locationToIndex(e.getPoint());
                if( index >= 0 && index < listModel.getSize()) {
                    MenuItem value = listModel.getElementAt(index);
//                    System.out.println("MouseEvent..clicks count = " + e.getClickCount() + " index = " + index + " value = " + value.getDish());
                    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                        //left click
//                    System.out.println("left click");
                        controller.inc(value);
                    }
                    if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
                        //right click
//                    System.out.println("right click");
                        controller.dec(value);
                    }
                }
                super.mouseClicked(e);
            }
        });
    }

    public void refreshList() {
        List<Dish> dishes = DishManager.getInstance().getAvailableDishes();
        DefaultListModel<MenuItem> listModel = (DefaultListModel<MenuItem>) list1.getModel();
        listModel.removeAllElements();
        for (Dish dish: dishes) {
            listModel.addElement(new MenuItemImpl(dish, 0));
        }
    }

    private void createUIComponents() {

        //init mvc
        this.model = new OrderImpl();
        controller = new OrderController();
        controller.setView(this);
        controller.setModel(this.model);
        model.addActionListener(this);

        initList();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == ActionEvent.ACTION_PERFORMED) {
            if (e.getActionCommand().equals(OrderImpl.UPDATE_MENU_ITEM)) {
                DefaultListModel<MenuItem> listModel = (DefaultListModel<MenuItem>) list1.getModel();
                MenuItem newItem = (MenuItem) e.getSource();
                int totalCost = 0;
                for (int i = 0; i < listModel.getSize(); i++) {
                    MenuItem originItem = listModel.getElementAt(i);
                    if (originItem.getDish().getId().equals(newItem.getDish().getId())) {
                        listModel.setElementAt(newItem, i);
                    }
                    totalCost += originItem.getDish().getCost() * originItem.getNumber();
                }
                cost = totalCost;
            } else if (e.getActionCommand().equals(OrderImpl.ADD_MENU_ITEM)) {
                DefaultListModel<MenuItem> listModel = (DefaultListModel<MenuItem>) list1.getModel();
                MenuItem source = (MenuItem) e.getSource();
                for (int i = 0; i < listModel.getSize(); i++) {
                    MenuItem originItem = listModel.getElementAt(i);
                    if (originItem.getDish().getId().equals(source.getDish().getId())) {
                        listModel.setElementAt(source, i);
                        break;
                    }
                }
                cost += source.getDish().getCost();
            } else if (e.getActionCommand().equals(OrderImpl.REMOVE_MENU_ITEM)) {
                DefaultListModel<MenuItem> listModel = (DefaultListModel<MenuItem>) list1.getModel();
                MenuItem source = (MenuItem) e.getSource();
                for (int i = 0; i < listModel.getSize(); i++) {
                    MenuItem originItem = listModel.getElementAt(i);
                    if (originItem.getDish().getId().equals(source.getDish().getId())) {
                        listModel.setElementAt(source, 0);
                        break;
                    }
                }
                cost -= source.getDish().getCost();
            } else if (e.getActionCommand().equals(OrderImpl.CLEAR)) {
                DefaultListModel<MenuItem> listModel = (DefaultListModel<MenuItem>) list1.getModel();
                for (int i = 0; i < listModel.getSize(); i++) {
                    MenuItem item = listModel.getElementAt(i);
                    item.setNumber(0);
                    listModel.setElementAt(item, i);
                }
                cost = 0;
            } else if (e.getActionCommand().equals(OrderImpl.REFRESH)) {
                refreshList();
                cost = 0;
            }
            // update cost label
            costLabel.setText(String.valueOf(cost));
        }
    }
}
