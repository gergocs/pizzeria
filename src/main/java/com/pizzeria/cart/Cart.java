package com.pizzeria.cart;

import java.util.HashMap;

public class Cart {
    private int sum;
    private final HashMap<String, Integer> items;

    public Cart() {
        this.items = new HashMap<>();
        this.sum = 0;
    }

    public int getPrice() {
        return this.sum;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void addItem(String name, int price){
        items.put(name, price);
        updatePrice();
    }

    private void updatePrice(){
        this.sum = 0;
        for (int value : items.values()){
            this.sum += value;
        }
    }

    @Override
    public String toString() {
        return "Cart{" +
                "sum=" + sum +
                ", items=" + items +
                '}';
    }
}
