package com.pizzeria.database;

import java.sql.*;
import org.apache.commons.codec.digest.*;

public class Database {

    private Connection con;
    private ResultSet rs;

    public Database() {
        try {
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pizzeria", "gergocs", "");
        }catch (SQLException e){
            this.con = null;
            System.out.println(e);
        }

    }

    public ResultSet getRs() {
        return rs;
    }

    public void readData(String table, String condition, Boolean order, String orderRow, Integer from, Integer offset) {
        String query = "select * from " + table + (condition == null ? "" : " where " + condition)
                       + (order == null ? "" : (" Order by " + orderRow + (order ? "" : " desc")))
                       + (from == null ? "" : " limit " + from + (offset == null ? "" : ", " + offset))
                       + ";";
        try{
            Statement stmt = con.createStatement();
            this.rs = stmt.executeQuery(query);
        }catch (SQLException e){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(e);
        }
    }

    public void writeData(String table, String data) {
        String[] values = data.split(";");
        PreparedStatement preparedStmt = null;
        try {
            String clientsQuery = "insert into clients (USERNAME, PWD, PHONENUMBER, ADDRESS) values (?, ?, ?, ?)";
            String ordersQuery = "insert into orders (USERNAME, TIME, PRICE, PRODUCTS) values (?, ?, ?, ?)";
            String toppingsQuery = "insert into toppings (NAME) values (?)";
            String pizzasQuery = "insert into pizzas (NAME, PRICE, TOPPINGS) values (?, ?, ?)";
            switch (table) {
                case "clients" -> {
                    preparedStmt = this.con.prepareStatement(clientsQuery);
                    preparedStmt.setString(1, values[0]);
                    preparedStmt.setString(2, DigestUtils.shaHex(values[1]));
                    preparedStmt.setString(3, values[2]);
                    preparedStmt.setString(4, values[3]);
                }
                case "orders" -> {
                    preparedStmt = this.con.prepareStatement(ordersQuery);
                    java.util.Date dt = new java.util.Date();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    preparedStmt.setString(1, values[0]);
                    preparedStmt.setString(2, sdf.format(dt));
                    preparedStmt.setString(3, values[1]);
                    preparedStmt.setString(4, values[2]);
                }
                case "pizzas" -> {
                    preparedStmt = this.con.prepareStatement(pizzasQuery);
                    preparedStmt.setString(1, values[1]);
                    preparedStmt.setInt(2, Integer.parseInt(values[2]));
                    preparedStmt.setString(3, values[3]);
                }
                case "toppings" -> {
                    preparedStmt = this.con.prepareStatement(toppingsQuery);
                    preparedStmt.setString(1, values[0]);
                }
            }

            assert preparedStmt != null;
            preparedStmt.execute();

        } catch (SQLException e){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(e);
        }
    }

    public void close(){
        if (this.con != null) {
            try {
                this.con.close();
            } catch (SQLException e) {
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(e);
            }
        }
    }
}
