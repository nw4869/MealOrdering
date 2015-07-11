package com.nightwind.mealordering.service;

import com.nightwind.mealordering.Entity.DishEntity;
import com.nightwind.mealordering.utils.HibernateUtil;
import org.hibernate.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nightwind on 15/7/10.
 */
public abstract class DishManager implements Subject<Dish> {

    public static DishManager getInstance() {
        return new DishManagerImpl();
    }

    public abstract List<Dish> getDishes();

    public abstract void insert(String name, double cost, String info);

    public abstract void delete(Dish dish);

    public static class DishManagerImpl extends DishManager {

        private List<Listener<Dish>> listeners = new ArrayList<>();

        @Override
        public List<Dish> getDishes() {
            List<Dish> dishes = new ArrayList<>();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Criteria cr = session.createCriteria(DishEntity.class);
                List<DishEntity> entities = cr.list();
                for (DishEntity entity: entities) {
                    dishes.add(new DefaultDish(entity));
                }

                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
            return dishes;
        }

        @Override
        public void insert(String name, double cost, String info) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                DishEntity entity = new DishEntity();
                entity.setName(name);
                entity.setCost(cost);
                entity.setInfo(info);
                entity.setStatus("normal");
                session.save(entity);
                entity = (DishEntity) session.get(DishEntity.class, entity.getId());

                for (Listener<Dish> listener: listeners) {
                    listener.insert(new DefaultDish(entity));
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
        public void delete(Dish dish) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                DishEntity entity = (DishEntity) session.load(DishEntity.class, dish.getId());
                session.delete(entity);

                for (Listener<Dish> listener: listeners) {
                    listener.delete(dish);
                }

                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
        }

        private void notifyObserver(Dish dish) {
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
}
