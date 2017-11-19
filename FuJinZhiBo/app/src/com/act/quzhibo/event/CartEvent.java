package com.act.quzhibo.event;

import com.act.quzhibo.bean.ShoppingCart;

public class CartEvent {
    public ShoppingCart cart;
    public int position;

    public CartEvent(ShoppingCart cart, int position) {
        this.cart = cart;
        this.position = position;
    }
}
