package com.pizzeria;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.*;

import com.pizzeria.database.*;
import com.pizzeria.cart.Cart;

public class PizzeriaApplication extends Application {

    Stage window;
    Scene login, register, home, checkOut;

    GridPane registerLayout = new GridPane();
    GridPane loginLayout = new GridPane();
    BorderPane homePageLayout = new BorderPane();
    HBox homePageHMenuLayout = new HBox();
    VBox homePageVMenuLayout = new VBox();
    ScrollPane homePagePizzasLayout = new ScrollPane();

    Text username = new Text("Username");
    Text password = new Text("Password");
    Text passwordAgain = new Text("Password Again");
    Text phoneNumber = new Text("Phone number");
    Text address = new Text("Address");

    Text loginUserName = new Text("Username");
    Text loginPassword = new Text("Password");

    String errorMessage = "";
    String uname;

    Database database = new Database();
    Cart cart = new Cart();

    private void setScenes(){
        /* TextFields */
        TextField tFieldUserName = new TextField();
        PasswordField tFieldPassword = new PasswordField();
        PasswordField tFieldPasswordAgain = new PasswordField();
        TextField tFieldPhoneNumber = new TextField();
        TextField tFieldAddress = new TextField();

        TextField tFieldLoginUserName = new TextField();
        PasswordField tFieldLoginPassword = new PasswordField();

        /* Buttons */
        Button bLogin = new Button("Login");
        bLogin.setDefaultButton(true);
        bLogin.setLayoutX(0);
        bLogin.setLayoutY(0);
        bLogin.setOnAction(e -> {
            this.uname = tFieldLoginUserName.getText();
            String password = tFieldLoginPassword.getText();
            errorMessage = "";

            if (uname.equals("")){
                errorMessage += "Wrong username\n";
            }

            if (password.equals("")){
                errorMessage += "Wrong password\n";
            }

            if (errorMessage.equals("")){
                database.readData("CLIENTS",
                        "USERNAME=\"" + uname + "\" AND PWD=\"" + DigestUtils.shaHex(password) + "\""
                        ,null, null
                        ,null,
                        null);
                ResultSet rs = database.getRs();
                try {
                    if (!rs.isBeforeFirst()){
                        errorMessage += "Invalid username or Password";
                    }
                } catch (SQLException ex) {
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    ex.printStackTrace();
                }
            }

            if (!errorMessage.equals("")){
                loginLayout.add(new Text(errorMessage), 1, 6);
                try {
                    login = new Scene(loginLayout, 800, 512);
                }catch (IllegalArgumentException ignored){}

                this.window.setScene(login);
                return;
            }

            tFieldLoginUserName.clear();
            tFieldLoginPassword.clear();

            System.out.println("good");
            window.setScene(home);
        });
        Button bRegister = new Button("Register");
        bRegister.setDefaultButton(true);
        bRegister.setLayoutX(0);
        bRegister.setLayoutY(0);
        bRegister.setOnAction(e -> {
            errorMessage = "";
            String uname = tFieldUserName.getText();
            String password = tFieldPassword.getText();
            String passwordAgain = tFieldPasswordAgain.getText();
            String phoneNumber = tFieldPhoneNumber.getText();
            String address = tFieldAddress.getText();

            if (uname.equals("") || uname.contains(" ")){
                errorMessage += "Wrong username\n";
            }

            if (!Objects.equals(password, passwordAgain)){
                errorMessage += "The passwords doesn't match\n";
            }

            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,20}$";

            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

            if (!pattern.matcher(password).matches()){
                errorMessage += "Wrong password\n";
            }

            if (address.equals("") || address.contains(" ")){
                errorMessage += "Wrong address\n";
            }

            if (errorMessage.equals("")){
                database.readData("CLIENTS"
                        ,"USERNAME=\"" + uname + "\""
                        ,null
                        ,null
                        ,null
                        ,null);
                ResultSet rs = database.getRs();
                try {
                    if (rs.isBeforeFirst()){
                        errorMessage += "Username already in use";
                    }
                } catch (SQLException ex) {
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    ex.printStackTrace();
                }
            }

            if (!Objects.equals(errorMessage, "")){
                registerLayout.add(new Text(errorMessage), 1, 6);
                try {
                    register = new Scene(registerLayout, 800, 512);
                }catch (IllegalArgumentException ignored){}

                this.window.setScene(register);
                return;
            }

            database.writeData("clients",uname+";"+password+";"+phoneNumber+";"+address);

            tFieldUserName.clear();
            tFieldPassword.clear();
            tFieldPhoneNumber.clear();
            tFieldPasswordAgain.clear();
            tFieldAddress.clear();

            this.window.setScene(login);
        });

        Button bChangeToRegister = new Button("Register");
        bChangeToRegister.setLayoutX(250);
        bChangeToRegister.setLayoutY(220);
        bChangeToRegister.setOnAction(e -> this.window.setScene(this.register));
        Button bChangeToLogin = new Button("Login");
        bChangeToLogin.setLayoutX(200);
        bChangeToLogin.setLayoutY(220);
        bChangeToLogin.setOnAction(e -> this.window.setScene(this.login));

        /* layouts */

        registerLayout.setPadding(new Insets(10, 10, 10, 10));
        registerLayout.setVgap(5);
        registerLayout.setHgap(5);
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.add(this.username, 0, 0);
        registerLayout.add(tFieldUserName, 1, 0);
        registerLayout.add(this.password,0, 1);
        registerLayout.add(tFieldPassword, 1, 1);
        registerLayout.add(passwordAgain, 0, 2);
        registerLayout.add(tFieldPasswordAgain, 1, 2);
        registerLayout.add(phoneNumber,0, 3);
        registerLayout.add(tFieldPhoneNumber, 1, 3);
        registerLayout.add(address, 0, 4);
        registerLayout.add(tFieldAddress, 1, 4);
        registerLayout.add(bRegister, 0, 5);
        registerLayout.add(bChangeToLogin, 1, 5);
        registerLayout.add(new Text(this.errorMessage), 1, 6);

        loginLayout.setPadding(new Insets(10, 10, 10, 10));
        loginLayout.setVgap(5);
        loginLayout.setHgap(5);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.add(loginUserName, 0, 0);
        loginLayout.add(tFieldLoginUserName, 1, 0);
        loginLayout.add(loginPassword, 0, 1);
        loginLayout.add(tFieldLoginPassword, 1, 1);
        loginLayout.add(bLogin, 0, 2);
        loginLayout.add(bChangeToRegister, 1, 2);
        this.login = new Scene(loginLayout, 800, 512);
        this.register = new Scene(registerLayout, 800, 512);
        createHomePage();
    }

    private void createHomePage() {
        ImageView exitImg = null;
        ImageView homeImg = null;
        ImageView cartImg = null;
        ImageView pizzaImg = null;
        try {
            FileInputStream input1 = new FileInputStream("src/resources/images/exit.png");
            FileInputStream input2 = new FileInputStream("src/resources/images/home.png");
            FileInputStream input3 = new FileInputStream("src/resources/images/cart.png");
            FileInputStream input4 = new FileInputStream("src/resources/images/pizza.jpg");
            exitImg = new ImageView(new Image(input1));
            exitImg.setFitHeight(50);
            exitImg.setFitWidth(50);
            homeImg = new ImageView(new Image(input2));
            homeImg.setFitHeight(50);
            homeImg.setFitWidth(50);
            cartImg = new ImageView(new Image(input3));
            cartImg.setFitHeight(50);
            cartImg.setFitWidth(50);
            pizzaImg = new ImageView(new Image(input4));
            pizzaImg.setFitHeight(50);
            pizzaImg.setFitWidth(50);
        } catch (FileNotFoundException e) {
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(e);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(10000,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        Button bExit = new Button("", exitImg);
        Button bHome = new Button("", homeImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        bExit.setOnAction(e -> this.window.setScene(login));
        bHome.setOnAction(e -> this.window.setScene(home));
        bCart.setOnAction(e -> {
            createCheckOutPage();
            this.window.setScene(checkOut);
        });
        bPizza.setOnAction(e -> this.window.setScene(home));

        homePageVMenuLayout.getChildren().add(bPizza);
        homePageVMenuLayout.getChildren().add(hFiller1);
        homePageVMenuLayout.getChildren().add(bCart);
        homePageVMenuLayout.getChildren().add(hFiller2);
        homePageVMenuLayout.getChildren().add(bExit);

        homePageVMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        homePageHMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        homePageHMenuLayout.getChildren().add(vFiller);
        homePageHMenuLayout.getChildren().add(bHome);

        homePagePizzasLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        homePagePizzasLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        GridPane g = generateItems();

        homePagePizzasLayout.setContent(g);
        homePagePizzasLayout.setMaxSize(500,500);

        homePageLayout.setTop(homePageHMenuLayout);
        homePageLayout.setLeft(homePageVMenuLayout);
        homePageLayout.setCenter(homePagePizzasLayout);
        homePageLayout.setBottom(new BorderPane());

        this.home = new Scene(homePageLayout, 800, 512);
    }

    private void createCheckOutPage() {
        BorderPane checkOutLayout = new BorderPane();
        VBox checkOutPayLayout = new VBox();
        ScrollPane checkOutOrdersLayout = new ScrollPane();
        HBox checkOutHMenuLayout = new HBox();
        VBox checkOutVMenuLayout = new VBox();

        ImageView exitImg = null;
        ImageView homeImg = null;
        ImageView cartImg = null;
        ImageView pizzaImg = null;
        try {
            FileInputStream input1 = new FileInputStream("src/resources/images/exit.png");
            FileInputStream input2 = new FileInputStream("src/resources/images/home.png");
            FileInputStream input3 = new FileInputStream("src/resources/images/cart.png");
            FileInputStream input4 = new FileInputStream("src/resources/images/pizza.jpg");
            exitImg = new ImageView(new Image(input1));
            exitImg.setFitHeight(50);
            exitImg.setFitWidth(50);
            homeImg = new ImageView(new Image(input2));
            homeImg.setFitHeight(50);
            homeImg.setFitWidth(50);
            cartImg = new ImageView(new Image(input3));
            cartImg.setFitHeight(50);
            cartImg.setFitWidth(50);
            pizzaImg = new ImageView(new Image(input4));
            pizzaImg.setFitHeight(50);
            pizzaImg.setFitWidth(50);
        } catch (FileNotFoundException e) {
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(e);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(10000,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        Button bExit = new Button("", exitImg);
        Button bHome = new Button("", homeImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        Button bPay = new Button("Pay");
        bExit.setOnAction(e -> this.window.setScene(login));
        bHome.setOnAction(e -> this.window.setScene(home));
        bCart.setOnAction(e -> this.window.setScene(checkOut));
        bPizza.setOnAction(e -> this.window.setScene(home));
        bPay.setOnAction(e -> {
            this.database.writeData("orders", this.uname + ";" + this.cart.getPrice() + ";" + this.cart.getItemAsString());
            this.cart.removeEverything();
            this.window.setScene(home);
        });

        checkOutVMenuLayout.getChildren().add(bPizza);
        checkOutVMenuLayout.getChildren().add(hFiller1);
        checkOutVMenuLayout.getChildren().add(bCart);
        checkOutVMenuLayout.getChildren().add(hFiller2);
        checkOutVMenuLayout.getChildren().add(bExit);

        checkOutVMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        checkOutHMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        checkOutHMenuLayout.getChildren().add(vFiller);
        checkOutHMenuLayout.getChildren().add(bHome);

        checkOutOrdersLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        checkOutOrdersLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        GridPane g = new GridPane();

        for (int i = 0; i < cart.getKeys().size(); i++) {
            String key = cart.getKeys().get(i);
            Integer value = cart.getValues().get(i);

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream("src/resources/images/remove.png");
            } catch (FileNotFoundException e) {
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                e.printStackTrace();
            }
            assert fileInputStream != null;
            ImageView removeFromCartImg = new ImageView(new Image(fileInputStream));
            removeFromCartImg.setFitHeight(50);
            removeFromCartImg.setFitWidth(50);

            Button removeFromCart = new Button("", removeFromCartImg);

            removeFromCart.setOnAction(e -> {
                cart.removeItem(key);
                createCheckOutPage();
                this.window.setScene(checkOut);
            });

            g.add(new Text(key), 0, i);
            g.add(new Text(value.toString()), 1, i);
            g.add(removeFromCart, 2, i);
        }

        if (cart.getKeys().size() == 0){
            g.add(new Text("There is nothing in your cart :("),0,0);
        }else{
            checkOutPayLayout.getChildren().add(new Text("Total:"));
            checkOutPayLayout.getChildren().add(new Text(cart.getPrice().toString()));
            checkOutPayLayout.getChildren().add(bPay);
        }

        checkOutOrdersLayout.setContent(g);
        checkOutOrdersLayout.setMaxSize(400,500);

        checkOutLayout.setTop(checkOutHMenuLayout);
        checkOutLayout.setLeft(checkOutVMenuLayout);
        checkOutLayout.setCenter(checkOutOrdersLayout);
        checkOutLayout.setRight(checkOutPayLayout);
        checkOutLayout.setBottom(new BorderPane());

        this.checkOut = new Scene(checkOutLayout, 800, 512);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        setScenes();
        window.setScene(login);
        window.setTitle("Pizzeria");
        window.show();
        try {
            database.readData("CLIENTS","USERNAME=\"alma\"",true, "USERNAME", null,null);
            ResultSet rs = database.getRs();
            while(rs.next()){
                System.out.println(rs.getString(1)+"  "+rs.getString(2)+"  "+rs.getString(3)+"  "+rs.getString(4));
            }
        }catch (Exception e){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(e);
        }
    }

    private GridPane generateItems() {
        GridPane gridItems = new GridPane();
        gridItems.setHgap(100);
        gridItems.setVgap(100);
        gridItems.setPadding(new Insets(10, 10, 10, 10));
        database.readData("PIZZAS",null,true, "NAME", null,null);
        ResultSet rs = database.getRs();

        try{
            boolean left = false;
            int lineCounter = 0;
            while(rs.next()){
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream("src/resources/images/add.png");
                } catch (FileNotFoundException e) {
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    e.printStackTrace();
                }
                assert fileInputStream != null;
                BorderPane item = new BorderPane();
                ImageView addToCartImg = new ImageView(new Image(fileInputStream));
                addToCartImg.setFitHeight(50);
                addToCartImg.setFitWidth(50);
                Button addToCart = new Button("", addToCartImg);
                String name = rs.getString(2);
                int price = rs.getInt(3);
                String toppingIds = rs.getString(4).replace(";",",");

                addToCart.setOnAction(e -> this.cart.addItem(name, price));

                database.readData("TOPPINGS","TOPPINGID IN (" + toppingIds + ")",true, "NAME", null,null);
                ResultSet rs2 = database.getRs();

                StringBuilder toppings = new StringBuilder();

                int counter = 0;

                while (rs2.next()){
                    toppings.append(rs2.getString(2)).append(", ");
                    counter++;
                    if (counter == 3){
                        counter = 0;
                        toppings.append("\n");
                    }
                }

                toppings.deleteCharAt(toppings.length() - 1);
                toppings.deleteCharAt(toppings.length() - 1);

                item.setTop(new Text(name + " " + price + " Ft\n" + toppings));
                item.setBottom(addToCart);

                gridItems.add(item, (left ? 1 : 0), lineCounter);
                left = !left;
                if (!left) {
                    lineCounter++;
                }
            }
        }catch (SQLException e){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(e);
        }

        return gridItems;
    }

    @Override
    public void stop(){
        System.out.println(this.cart.toString());
        this.database.close();
    }

    public static void main(String[] args) {
        launch();
    }
}