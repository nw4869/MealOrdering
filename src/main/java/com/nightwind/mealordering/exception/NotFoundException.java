package com.nightwind.mealordering.exception;

/**
 * Created by nightwind on 15/7/7.
 */
public class NotFoundException extends Exception {

    public NotFoundException(){
        super();
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
