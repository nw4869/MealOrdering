package com.nightwind.mealordering.Entity;

import javax.persistence.*;

/**
 * Created by nightwind on 15/7/6.
 */
@Entity
@Table(name = "menu", schema = "", catalog = "mealordering")
public class MenuEntity {
    private int id;
    private int dishId;
    private String dishNumber;
    private int orderId;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "dish_id", nullable = false, insertable = true, updatable = true)
    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    @Basic
    @Column(name = "dish_number", nullable = false, insertable = true, updatable = true, length = 45)
    public String getDishNumber() {
        return dishNumber;
    }

    public void setDishNumber(String dishNumber) {
        this.dishNumber = dishNumber;
    }

    @Basic
    @Column(name = "order_id", nullable = false, insertable = true, updatable = true)
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuEntity that = (MenuEntity) o;

        if (id != that.id) return false;
        if (dishId != that.dishId) return false;
        if (orderId != that.orderId) return false;
        if (dishNumber != null ? !dishNumber.equals(that.dishNumber) : that.dishNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + dishId;
        result = 31 * result + (dishNumber != null ? dishNumber.hashCode() : 0);
        result = 31 * result + orderId;
        return result;
    }
}
