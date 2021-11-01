package com.pizzeria.cart;

import java.util.ArrayList;

public class Cart {
    private int sum;
    private int size;
    private final ArrayList<String> keys;
    private final ArrayList<Integer> values;


    public Cart() {
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.sum = 0;
        this.size = 0;
    }

    public int getPrice() {
        return this.sum;
    }

    public int getSize() {
        return this.size;
    }

    public ArrayList<Integer> getValues() {
        return this.values;
    }

    public ArrayList<String> getKeys() {
        return this.keys;
    }

    public void addItem(String name, int price){
        this.keys.add(name);
        this.values.add(price);
        updatePrice();
        this.size++;
    }

    public void removeItem(String name){
        this.values.remove(this.keys.indexOf(name));
        this.keys.remove(name);
        updatePrice();
        this.size--;
    }

    private void updatePrice(){
        this.sum = 0;
        for (int value : values){
            this.sum += value;
        }
    }

    @Override
    public String toString() {
        return "Cart{" +
                "sum=" + sum +
                ", items=" + keys +
                ", " + values +
                '}';
    }
}
