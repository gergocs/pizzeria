package com.pizzeria.cart;

import java.util.ArrayList;

public class Cart {
    private int sum;
    private final ArrayList<String> keys;
    private final ArrayList<Integer> values;

    /** Default constructor for Cart class
     * Initialize keys and values as empty ArrayList and sum as 0
     */
    public Cart() {
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.sum = 0;
    }

    /** getter for the total price for the Cart
     */
    public Integer getPrice() {
        return this.sum;
    }

    /** Getter for the items price
     */
    public ArrayList<Integer> getValues() {
        return this.values;
    }

    /** getter for the items name
     */
    public ArrayList<String> getKeys() {
        return this.keys;
    }

    /** get items as String
     * Example:
     * Alma,Banana
     */
    public String getItemAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : this.keys) {
            stringBuilder.append(key).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /** add item to the lists
     * @param name  The name of the item
     * @param price  The price of the item
     */
    public void addItem(String name, int price){
        this.keys.add(name);
        this.values.add(price);
        updatePrice();
    }

    /** remove item from the lists
     * @param name  The name of the item to remove
     */
    public void removeItem(String name){
        this.values.remove(this.keys.indexOf(name));
        this.keys.remove(name);
        updatePrice();
    }

    /** clear the lists
     */
    public void removeEverything(){
        this.values.clear();
        this.keys.clear();
        updatePrice();
    }

    /** update the value of sum
     */
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
