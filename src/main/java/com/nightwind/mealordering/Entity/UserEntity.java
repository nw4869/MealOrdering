package com.nightwind.mealordering.Entity;

import javax.persistence.*;

/**
 * Created by nightwind on 15/7/6.
 */
@Entity
@Table(name = "user", schema = "", catalog = "mealordering")
public class UserEntity {
    private String username;
    private String password;
    private String name;
    private String status;
    private Integer admin;

    @Id
    @Column(name = "username", nullable = false, insertable = true, updatable = true, length = 45)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password", nullable = false, insertable = true, updatable = true, length = 64)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "status", nullable = true, insertable = true, updatable = true, length = 45)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "admin", nullable = true, insertable = true, updatable = true, length = 11)
    public Integer getAdmin() {
        return admin;
    }

    public void setAdmin(Integer admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity entity = (UserEntity) o;

        if (username != null ? !username.equals(entity.username) : entity.username != null) return false;
        if (password != null ? !password.equals(entity.password) : entity.password != null) return false;
        if (name != null ? !name.equals(entity.name) : entity.name != null) return false;
        if (status != null ? !status.equals(entity.status) : entity.status != null) return false;
        if (admin != null ? !admin.equals(entity.admin) : entity.admin != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (admin != null ? admin.hashCode() : 0);
        return result;
    }
}
