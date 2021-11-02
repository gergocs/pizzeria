package com.pizzeria.cart;

import java.util.ArrayList;

public class Cart {
    private int sum;
    private final ArrayList<String> keys;
    private final ArrayList<Integer> values;


    public Cart() {
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.sum = 0;
    }

    public Integer getPrice() {
        return this.sum;
    }

    public ArrayList<Integer> getValues() {
        return this.values;
    }

    public ArrayList<String> getKeys() {
        return this.keys;
    }

    public String getItemAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : this.keys) {
            stringBuilder.append(key).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public void addItem(String name, int price){
        this.keys.add(name);
        this.values.add(price);
        updatePrice();
    }

    public void removeItem(String name){
        this.values.remove(this.keys.indexOf(name));
        this.keys.remove(name);
        updatePrice();
    }

    public void removeEverything(){
        this.values.clear();
        this.keys.clear();
        updatePrice();
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
