package com.nightwind.mealordering.model;

import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by nightwind on 15/7/13.
 */
public class OrderImplTest {

    @Test
    public void testGetAllOrder() throws Exception {
        List<Order> orders = new OrderImpl().getAllOrder();
        for (Order order: orders) {
            for (MenuItem item: order.getMenuItems()) {
                System.out.println(item.getDish().getName() + item.getNumber());
            }
        }
    }
}