package com.nightwind.mealordering.service.impl;

import com.nightwind.mealordering.service.User;
import com.nightwind.mealordering.service.UserImpl;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nightwind on 15/7/7.
 */
public class UserImplTest {

    @Test
    public void testGetName() throws Exception {
        User user = new UserImpl("nw");
        assertEquals(user.getName(), "NightWind");
    }

    @Test
    public void testLogin() throws Exception {
        User user = new UserImpl("nw");
        assert user.login("4869");
    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testLogout() throws Exception {

    }

    @Test
    public void testUpdatePassword() throws Exception {

    }

    @Test
    public void testGetUsername() throws Exception {

    }

    @Test
    public void testUpdateName() throws Exception {

    }

    @Test
    public void testIsEnable() throws Exception {

    }

    @Test
    public void testIsAdmin() throws Exception {

    }
}