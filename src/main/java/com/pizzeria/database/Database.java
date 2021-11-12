package com.pizzeria.database;

import java.sql.*;
import java.util.Objects;

import org.apache.commons.codec.digest.*;

public class Database {

    private final Connection con;
    private ResultSet rs;

    /** default constructor for Database
     * @throws SQLException if the program can't connect to the database
     */
    public Database() throws SQLException {
        this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pizzeria", "gergocs", "");
    }

    /** getter for the result set
     * @return ResultSet which was the result of the previous SQL query
     */
    public ResultSet getRs() {
        return this.rs;
    }

    /** read Data from the database
     * @param table the table from you want read
     * @param condition the where clause of the SQL query
     * @param order if null no order if true ascending if false descending
     * @param orderRow by which row order the data
     * @param from how many rows should it show
     * @param offset offset of the rows
     * @throws SQLException if the query was bad
     */
    public void readData(String table, String condition, Boolean order, String orderRow, Integer from, Integer offset) throws SQLException {
        String query = "select * from " + table + (condition == null ? "" : " where " + condition)
                       + (order == null ? "" : (" Order by " + orderRow + (order ? "" : " desc")))
                       + (from == null ? "" : " limit " + from + (offset == null ? "" : ", " + offset))
                       + ";";

        Statement stmt = this.con.createStatement();
        this.rs = stmt.executeQuery(query);
    }

    /** read Data from the database
     * @param query the full SQL query to be executed
     * @throws SQLException if the query was bad
     */
    public void readDataCustom(String query) throws SQLException {
        Statement stmt = this.con.createStatement();
        this.rs = stmt.executeQuery(query);
    }

    /** write Data to the database
     * @param table where you want to write
     * @param data the data to write to table
     *             example:
     *             a;b;c
     * @throws SQLException if the query was bad
     */
    public void writeData(String table, String data, Blob image) throws SQLException {
        String[] values = data.split(";");
        PreparedStatement preparedStmt = null;
        String clientsQuery = "insert into clients (USERNAME, PWD, PHONENUMBER, ADDRESS) values (?, ?, ?, ?)";
        String ordersQuery = "insert into orders (USERNAME, TIME, PRICE) values (?, ?, ?)";
        String toppingsQuery = "insert into toppings (NAME) values (?)";
        String pizzasQuery = "insert into pizzas (NAME, PRICE, IMAGE) values (?, ?, ?)";
        String pizzatoppingsQuery = "insert into pizzatoppings (PIZZAID, TOPPINGID) values (?, ?)";
        String orderPizzasQuery = "insert into orderpizzas (TIME, PIZZAID) values (?, ?)";
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                preparedStmt.setString(1, values[0]);
                preparedStmt.setString(2, sdf.format(dt));
                preparedStmt.setString(3, values[1]);
            }
            case "pizzas" -> {
                preparedStmt = this.con.prepareStatement(pizzasQuery);
                preparedStmt.setString(1, values[0]);
                preparedStmt.setInt(2, Integer.parseInt(values[1]));
                preparedStmt.setBlob(3, image);
            }
            case "toppings" -> {
                preparedStmt = this.con.prepareStatement(toppingsQuery);
                preparedStmt.setString(1, values[0]);
            }
        }

        assert preparedStmt != null;
        preparedStmt.execute();

        if (table.equals("pizzas")){
            preparedStmt = this.con.prepareStatement(pizzatoppingsQuery);

            this.readDataCustom("select PIZZAID from pizzas WHERE NAME = \"" + values[0] + "\";");
            this.rs.next();

            int id = this.rs.getInt(1);

            String[] toppings = values[2].split(", ");

            for (String topping : toppings) {
                this.readDataCustom("select TOPPINGID from toppings WHERE TOPPINGID = " + topping + ";");
                this.rs.next();

                preparedStmt.setInt(1, id);
                preparedStmt.setInt(2, this.rs.getInt(1));
                preparedStmt.execute();
            }
        } else if (table.equals("orders")){
            preparedStmt = this.con.prepareStatement(orderPizzasQuery);

            String[] products = values[2].split(",");

            for (String product : products){
                this.readDataCustom("select PIZZAID from pizzas WHERE NAME = \"" + product + "\";");
                this.rs.next();

                preparedStmt.setString(1, sdf.format(dt));
                preparedStmt.setInt(2, this.rs.getInt(1));
                preparedStmt.execute();
            }
        }
    }

    /** update data in the database
     * @param table where you want to update
     * @param column the column which identifies the data what you want to update
     * @param value the new value of the column
     * @param condition the where clause of the SQL query
     * @throws SQLException if the query was bad
     */
    public void updateData(String table, String column, String value, String condition, Blob blob) throws SQLException {
        String query = "update LOW_PRIORITY " + table + " set " + column + " = ? where " + condition;
        PreparedStatement preparedStmt = this.con.prepareStatement(query);
        if (Objects.equals(column, "PRICE")){
            preparedStmt.setInt(1, Integer.parseInt(value));
        } else if (Objects.equals(column, "IMAGE")) {
            preparedStmt.setBlob(1, blob);
        } else {
            preparedStmt.setString(1, Objects.equals(column, "PWD") ? DigestUtils.shaHex(value) : (Objects.equals(column, "TOPPINGS") ? value.replace(", ", ";") : value));
        }

        preparedStmt.executeUpdate();

    }

    /** delete row in the database
     * @param table where you want to delete
     * @param condition the where clause of the SQL query
     * @throws SQLException if the query was bad
     */
    public void deleteData(String table, String condition) throws SQLException {
        String query = "delete from " + table + " where " + condition;
        PreparedStatement preparedStmt = this.con.prepareStatement(query);
        preparedStmt.executeUpdate();
    }

    /** If the connection was opened it must be closed
     * @throws SQLException if error happened
     */
    public void close() throws SQLException {
        if (this.con != null) {
            this.con.close();
            return;
        }
        throw new SQLException("connection was null");
    }
}
