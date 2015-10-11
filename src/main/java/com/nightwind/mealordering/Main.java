package com.nightwind.mealordering;

import com.nightwind.mealordering.Entity.UserEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by nightwind on 15/7/6.
 */
public class Main {

    public static void main(String[] args) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = null;
//        try {
//            tx = session.beginTransaction();
//
//            UserEntity userEntity = (UserEntity) session.get(UserEntity.class, "nw");
//            System.out.println(userEntity.getName());
//
//            Criteria cr = session.createCriteria(UserEntity.class);
//            cr.add(Restrictions.eq("username", "nw"));
//            List result = cr.list();
//
//            for (Object userEntity1: result) {
//                System.out.println(userEntity1);
//            }
//
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx != null) tx.rollback();
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
    }

}
