package com.pizzeria;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.commons.codec.digest.DigestUtils;

import com.pizzeria.cart.Cart;
import com.pizzeria.database.Database;

public class PizzeriaApplication extends Application {

    private boolean topFive = false;
    private boolean isAdmin = false;
    private Boolean ascending = false;

    private String errorMessage = "";
    private String uname;
    private String previousValue = null;

    private final ArrayList<Integer> allowedItems = new ArrayList<>();
    private final ArrayList<Integer> notAllowedItems = new ArrayList<>();

    private Database database;
    private final Cart cart = new Cart();

    private Stage window;
    private Scene login, register, home, checkOut, user;

    /** create scenes
     */
    private void setScenes(){
        GridPane registerLayout = new GridPane();
        GridPane loginLayout = new GridPane();

        TextField tFieldUserName = new TextField();
        PasswordField tFieldPassword = new PasswordField();
        PasswordField tFieldPasswordAgain = new PasswordField();
        TextField tFieldPhoneNumber = new TextField();
        TextField tFieldAddress = new TextField();

        TextField tFieldLoginUserName = new TextField();
        PasswordField tFieldLoginPassword = new PasswordField();

        Button bLogin = new Button("Login");
        Button bRegister = new Button("Register");
        Button bChangeToRegister = new Button("Register");
        Button bChangeToLogin = new Button("Login");
        bLogin.setDefaultButton(true);
        bLogin.getStyleClass().add("success");
        bLogin.setOnAction(e -> {
            this.uname = tFieldLoginUserName.getText();
            String password = tFieldLoginPassword.getText();
            this.errorMessage = "";

            if (this.uname.equals("")){
                this.errorMessage += "Wrong username\n";
            }

            if (password.equals("")){
                this.errorMessage += "Wrong password\n";
            }

            if (this.errorMessage.equals("")){
                try {
                    this.database.readData("CLIENTS",
                            "USERNAME=\"" + this.uname + "\" AND PWD=\"" + DigestUtils.shaHex(password) + "\"",
                            null,
                            null,
                            null,
                            null);
                    ResultSet rs = this.database.getRs();
                    if (!rs.isBeforeFirst()){
                        this.errorMessage += "Invalid username or Password";
                    } else {
                        rs.next();
                        this.isAdmin = rs.getBoolean(5);
                    }
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
            }

            if (!this.errorMessage.equals("")){
                loginLayout.add(new Text(this.errorMessage), 1, 6);
                try {
                    this.login = new Scene(loginLayout, 800, 512);
                }catch (IllegalArgumentException ignored){}

                this.window.setScene(this.login);
                return;
            }

            tFieldLoginUserName.clear();
            tFieldLoginPassword.clear();

            this.createHomePage();
            this.window.setScene(this.home);
        });

        bRegister.setDefaultButton(true);
        bRegister.getStyleClass().add("success");
        bRegister.setOnAction(e -> {
            this.errorMessage = "";
            String uname = tFieldUserName.getText();
            String password = tFieldPassword.getText();
            String passwordAgain = tFieldPasswordAgain.getText();
            String phoneNumber = tFieldPhoneNumber.getText();
            String address = tFieldAddress.getText();

            if (uname.equals("") || uname.contains(" ")){
                this.errorMessage += "Wrong username\n";
            }

            if (!Objects.equals(password, passwordAgain)){
                this.errorMessage += "The passwords doesn't match\n";
            }

            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,20}$";

            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

            if (!pattern.matcher(password).matches()){
                this.errorMessage += "Wrong password\n";
            }

            if (address.equals("")){
                this.errorMessage += "Wrong address\n";
            }

            if (this.errorMessage.equals("")){
                try {
                    this.database.readData("CLIENTS"
                            ,"USERNAME=\"" + uname + "\"",
                            null,
                            null,
                            null,
                            null);
                    ResultSet rs = this.database.getRs();
                    if (rs.isBeforeFirst()){
                        this.errorMessage += "Username already in use";
                    }
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
            }

            if (!Objects.equals(this.errorMessage, "")){
                registerLayout.add(new Text(this.errorMessage), 1, 6);
                try {
                    this.register = new Scene(registerLayout, 800, 512);
                    this.register.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
                }catch (IllegalArgumentException ignored){}

                this.window.setScene(this.register);
                return;
            }
            try{
                this.database.writeData("clients",uname+";"+password+";"+phoneNumber+";"+address);
            } catch (SQLException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }


            tFieldUserName.clear();
            tFieldPassword.clear();
            tFieldPhoneNumber.clear();
            tFieldPasswordAgain.clear();
            tFieldAddress.clear();

            this.window.setScene(this.login);
        });

        bChangeToRegister.setLayoutX(250);
        bChangeToRegister.setLayoutY(220);
        bChangeToRegister.getStyleClass().add("info");
        bChangeToRegister.setOnAction(e -> this.window.setScene(this.register));

        bChangeToLogin.setLayoutX(200);
        bChangeToLogin.setLayoutY(220);
        bChangeToLogin.getStyleClass().add("info");
        bChangeToLogin.setOnAction(e -> this.window.setScene(this.login));

        registerLayout.setPadding(new Insets(10, 10, 10, 10));
        registerLayout.setVgap(5);
        registerLayout.setHgap(5);
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.add(new Text("Username"), 0, 0);
        registerLayout.add(tFieldUserName, 1, 0);
        registerLayout.add(new Text("Password"),0, 1);
        registerLayout.add(tFieldPassword, 1, 1);
        registerLayout.add(new Text("Password Again"), 0, 2);
        registerLayout.add(tFieldPasswordAgain, 1, 2);
        registerLayout.add(new Text("Phone number"),0, 3);
        registerLayout.add(tFieldPhoneNumber, 1, 3);
        registerLayout.add(new Text("Address"), 0, 4);
        registerLayout.add(tFieldAddress, 1, 4);
        registerLayout.add(bRegister, 0, 5);
        registerLayout.add(bChangeToLogin, 1, 5);
        registerLayout.add(new Text(this.errorMessage), 1, 6);

        loginLayout.setPadding(new Insets(10, 10, 10, 10));
        loginLayout.setVgap(5);
        loginLayout.setHgap(5);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.add(new Text("Username"), 0, 0);
        loginLayout.add(tFieldLoginUserName, 1, 0);
        loginLayout.add(new Text("Password"), 0, 1);
        loginLayout.add(tFieldLoginPassword, 1, 1);
        loginLayout.add(bLogin, 0, 2);
        loginLayout.add(bChangeToRegister, 1, 2);
        this.login = new Scene(loginLayout, 800, 512);
        this.register = new Scene(registerLayout, 800, 512);
        this.login.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
        this.register.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
    }

    /** Create homePage
     */
    private void createHomePage() {
        BorderPane homePageMainLayout = new BorderPane();
        BorderPane homePageLayout = new BorderPane();
        HBox homePageHMenuLayout = new HBox();
        VBox homePageVMenuLayout = new VBox();
        ScrollPane homePagePizzasLayout = new ScrollPane();
        VBox homePageFilterLayout = new VBox();
        ScrollPane homePageScrollLayout = new ScrollPane();
        GridPane g = this.generateItems();
        ImageView exitImg = null;
        ImageView cartImg = null;
        ImageView pizzaImg = null;
        ImageView userImg = null;
        try {
            FileInputStream input1 = new FileInputStream("src/resources/images/exit.png");
            FileInputStream input3 = this.isAdmin ? new FileInputStream("src/resources/images/statistics.png") : new FileInputStream("src/resources/images/cart.png");
            FileInputStream input4 = new FileInputStream("src/resources/images/pizza.jpg");
            FileInputStream input5 = new FileInputStream("src/resources/images/user.png");
            exitImg = new ImageView(new Image(input1));
            exitImg.setFitHeight(50);
            exitImg.setFitWidth(50);
            cartImg = new ImageView(new Image(input3));
            cartImg.setFitHeight(50);
            cartImg.setFitWidth(50);
            pizzaImg = new ImageView(new Image(input4));
            pizzaImg.setFitHeight(50);
            pizzaImg.setFitWidth(50);
            userImg = new ImageView(new Image(input5));
            userImg.setFitHeight(50);
            userImg.setFitWidth(50);
        } catch (FileNotFoundException exception) {
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(750,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        Button bExit = new Button("", exitImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        Button bUser = new Button("", userImg);
        Button bFilter = new Button("Filter");
        Button bClear = new Button("Clear filter");
        Button bTop = new Button("Top 5");

        bFilter.getStyleClass().add("primary");
        bClear.getStyleClass().add("danger");
        bTop.getStyleClass().add("success");

        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(this.login);
        });
        bCart.setOnAction(e -> {
            if (this.isAdmin) {
                this.createStatisticsPage();
            } else {
                this.createCheckOutPage();
            }
            this.window.setScene(this.checkOut);
        });
        bPizza.setOnAction(e -> {
            this.createHomePage();
            this.window.setScene(this.home);
        });
        bUser.setOnAction(e -> {
            if (this.isAdmin){
                this.createPizzaCreatorPage();
            }else{
                this.createUserPager();
            }
            this.window.setScene(this.user);
        });
        bFilter.setOnAction(e -> {
            this.createHomePage();
            this.window.setScene(this.home);
        });
        bClear.setOnAction(e -> {
            this.allowedItems.clear();
            this.notAllowedItems.clear();
            this.createHomePage();
            this.window.setScene(this.home);
        });
        bTop.setOnAction(e -> {
            this.allowedItems.clear();
            this.notAllowedItems.clear();
            this.topFive = true;
            this.createHomePage();
            this.window.setScene(this.home);
        });

        homePageVMenuLayout.getChildren().add(bPizza);
        homePageVMenuLayout.getChildren().add(hFiller1);
        homePageVMenuLayout.getChildren().add(bCart);
        homePageVMenuLayout.getChildren().add(hFiller2);
        homePageVMenuLayout.getChildren().add(bExit);

        homePageVMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        homePageHMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        homePageHMenuLayout.getChildren().add(vFiller);
        homePageHMenuLayout.getChildren().add(bUser);

        homePagePizzasLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        homePagePizzasLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        homePagePizzasLayout.setContent(g);
        homePagePizzasLayout.setMaxSize(500,500);

        homePageScrollLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        homePageScrollLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        homePageScrollLayout.setMinWidth(150);

        homePageFilterLayout.getChildren().add(bTop);
        homePageFilterLayout.getChildren().add(bFilter);
        homePageFilterLayout.getChildren().add(bClear);
        try {
            this.database.readData("toppings", null, null, null, null, null);
            ResultSet rs = this.database.getRs();
            while (rs.next()) {
                CheckBox cb = new CheckBox(rs.getString(2));
                cb.allowIndeterminateProperty();
                cb.setId(String.valueOf(rs.getInt(1)));
                cb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                    if (cb.isIndeterminate()) {
                        cb.setSelected(true);
                        cb.setIndeterminate(false);
                        cb.setAllowIndeterminate(false);
                        this.notAllowedItems.remove(Integer.valueOf(Integer.parseInt(cb.getId())));
                    } else if (cb.isSelected()) {
                        cb.setSelected(false);
                        this.allowedItems.remove(Integer.valueOf(Integer.parseInt(cb.getId())));
                        this.notAllowedItems.add(Integer.parseInt(cb.getId()));
                    } else if (!cb.isSelected()) {
                        cb.setSelected(true);
                        cb.setIndeterminate(true);
                        cb.setAllowIndeterminate(true);
                        this.allowedItems.add(Integer.parseInt(cb.getId()));
                    }
                });
                homePageFilterLayout.getChildren().add(cb);
            }
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        homePageScrollLayout.setContent(homePageFilterLayout);

        homePageMainLayout.setRight(homePageScrollLayout);
        homePageMainLayout.setCenter(homePagePizzasLayout);

        homePageLayout.setTop(homePageHMenuLayout);
        homePageLayout.setLeft(homePageVMenuLayout);
        homePageLayout.setCenter(homePageMainLayout);
        homePageLayout.setBottom(new BorderPane());

        this.home = new Scene(homePageLayout, 800, 512);
        this.home.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
    }

    /** Create checkoutPage
     */
    private void createCheckOutPage() {
        BorderPane checkOutLayout = new BorderPane();
        VBox checkOutPayLayout = new VBox();
        ScrollPane checkOutOrdersLayout = new ScrollPane();
        HBox checkOutHMenuLayout = new HBox();
        VBox checkOutVMenuLayout = new VBox();
        GridPane checkOutCartLayout = new GridPane();

        ImageView exitImg = null;
        ImageView cartImg = null;
        ImageView pizzaImg = null;
        ImageView userImg = null;
        try {
            FileInputStream input1 = new FileInputStream("src/resources/images/exit.png");
            FileInputStream input3 = new FileInputStream("src/resources/images/cart.png");
            FileInputStream input4 = new FileInputStream("src/resources/images/pizza.jpg");
            FileInputStream input5 = new FileInputStream("src/resources/images/user.png");
            exitImg = new ImageView(new Image(input1));
            exitImg.setFitHeight(50);
            exitImg.setFitWidth(50);
            cartImg = new ImageView(new Image(input3));
            cartImg.setFitHeight(50);
            cartImg.setFitWidth(50);
            pizzaImg = new ImageView(new Image(input4));
            pizzaImg.setFitHeight(50);
            pizzaImg.setFitWidth(50);
            userImg = new ImageView(new Image(input5));
            userImg.setFitHeight(50);
            userImg.setFitWidth(50);
        } catch (FileNotFoundException exception) {
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(750,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        Button bExit = new Button("", exitImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        Button bUser = new Button("", userImg);
        Button bPay = new Button("Pay");

        bPay.getStyleClass().add("success");

        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(this.login);
        });
        bCart.setOnAction(e -> {
            this.createCheckOutPage();
            this.window.setScene(this.checkOut);
        });
        bPizza.setOnAction(e -> {
            this.createHomePage();
            this.window.setScene(this.home);
        });
        bPay.setOnAction(e -> {
            try{
                this.database.writeData("orders", this.uname + ";" + this.cart.getPrice() + ";" + this.cart.getItemAsString());
            } catch (SQLException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }

            this.cart.removeEverything();
            this.window.setScene(this.home);
        });

        bUser.setOnAction(e -> {
            if (this.isAdmin){
                this.createPizzaCreatorPage();
            }else{
                this.createUserPager();
            }
            this.window.setScene(this.user);
        });

        bPay.setPrefSize(100,100);

        checkOutVMenuLayout.getChildren().add(bPizza);
        checkOutVMenuLayout.getChildren().add(hFiller1);
        checkOutVMenuLayout.getChildren().add(bCart);
        checkOutVMenuLayout.getChildren().add(hFiller2);
        checkOutVMenuLayout.getChildren().add(bExit);

        checkOutVMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        checkOutHMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        checkOutHMenuLayout.getChildren().add(vFiller);
        checkOutHMenuLayout.getChildren().add(bUser);

        checkOutOrdersLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        checkOutOrdersLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        for (int i = 0; i < this.cart.getKeys().size(); i++) {
            String key = this.cart.getKeys().get(i);
            Integer value = this.cart.getValues().get(i);

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream("src/resources/images/remove.png");
            } catch (FileNotFoundException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }
            assert fileInputStream != null;
            ImageView removeFromCartImg = new ImageView(new Image(fileInputStream));
            removeFromCartImg.setFitHeight(50);
            removeFromCartImg.setFitWidth(50);

            Button removeFromCart = new Button("", removeFromCartImg);

            removeFromCart.getStyleClass().add("danger");

            removeFromCart.setOnAction(e -> {
                this.cart.removeItem(key);
                this.createCheckOutPage();
                this.window.setScene(this.checkOut);
            });

            checkOutCartLayout.add(new Text(key), 0, i);
            checkOutCartLayout.add(new Text(value.toString()), 1, i);
            checkOutCartLayout.add(removeFromCart, 2, i);
        }

        if (this.cart.getKeys().size() == 0){
            checkOutCartLayout.add(new Text("There is nothing in your cart :("),0,0);
        }else{
            checkOutPayLayout.getChildren().add(new Text("Total:"));
            checkOutPayLayout.getChildren().add(new Text(this.cart.getPrice().toString()));
            checkOutPayLayout.getChildren().add(bPay);
        }

        checkOutOrdersLayout.setContent(checkOutCartLayout);
        checkOutOrdersLayout.setMaxSize(400,500);

        checkOutLayout.setTop(checkOutHMenuLayout);
        checkOutLayout.setLeft(checkOutVMenuLayout);
        checkOutLayout.setCenter(checkOutOrdersLayout);
        checkOutLayout.setRight(checkOutPayLayout);
        checkOutLayout.setBottom(new BorderPane());

        this.checkOut = new Scene(checkOutLayout, 800, 512);
        this.checkOut.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
    }

    /** Create userPage
     */
    private void createUserPager() {
        BorderPane userPageLayout = new BorderPane();
        BorderPane userPageMenuLayout = new BorderPane();
        ScrollPane userPageOrdersLayout = new ScrollPane();
        GridPane userPageModifyLayout = new GridPane();
        HBox userPageHMenuLayout = new HBox();
        VBox userPageVMenuLayout = new VBox();
        GridPane userPageOrderLayout = new GridPane();

        ImageView exitImg = null;
        ImageView cartImg = null;
        ImageView pizzaImg = null;
        ImageView userImg = null;
        try {
            FileInputStream input1 = new FileInputStream("src/resources/images/exit.png");
            FileInputStream input3 = this.isAdmin ? new FileInputStream("src/resources/images/statistics.png") : new FileInputStream("src/resources/images/cart.png");
            FileInputStream input4 = new FileInputStream("src/resources/images/pizza.jpg");
            FileInputStream input5 = new FileInputStream("src/resources/images/user.png");
            exitImg = new ImageView(new Image(input1));
            exitImg.setFitHeight(50);
            exitImg.setFitWidth(50);
            cartImg = new ImageView(new Image(input3));
            cartImg.setFitHeight(50);
            cartImg.setFitWidth(50);
            pizzaImg = new ImageView(new Image(input4));
            pizzaImg.setFitHeight(50);
            pizzaImg.setFitWidth(50);
            userImg = new ImageView(new Image(input5));
            userImg.setFitHeight(50);
            userImg.setFitWidth(50);
        } catch (FileNotFoundException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(750,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        /* TextFields */
        TextField tFieldUserName = new TextField();
        PasswordField tFieldPassword = new PasswordField();
        PasswordField tFieldPasswordAgain = new PasswordField();
        TextField tFieldPhoneNumber = new TextField();
        TextField tFieldAddress = new TextField();

        Button bExit = new Button("", exitImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        Button bUser = new Button("", userImg);
        Button bUpdate = new Button("Update");
        Button bDelete = new Button("Delete registration");

        bUpdate.getStyleClass().add("success");
        bDelete.getStyleClass().add("danger");

        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(this.login);
        });
        bCart.setOnAction(e -> {
            if (this.isAdmin) {
                this.createStatisticsPage();
            } else {
                this.createCheckOutPage();
            }
            this.window.setScene(this.checkOut);
        });
        bPizza.setOnAction(e -> {
            this.createHomePage();
            this.window.setScene(this.home);
        });

        bUser.setOnAction(e -> {
            if (this.isAdmin){
                this.createPizzaCreatorPage();
            }else{
                this.createUserPager();
            }
            this.window.setScene(this.user);
        });

        bUpdate.setOnAction(e -> {
            this.errorMessage = "";
            if (!tFieldUserName.getText().equals("")){
                    try {
                        this.database.readData("CLIENTS",
                                "USERNAME=\"" + this.uname + "\"",
                                null,
                                null,
                                null,
                                null);
                        ResultSet rs = this.database.getRs();
                        if (rs.isBeforeFirst()) {
                            this.errorMessage += "Username already in use";
                        }
                    } catch (SQLException exception){
                        System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                        System.out.println(exception);
                    }
                if (this.errorMessage.equals("")){
                    try{
                        this.database.updateData("clients", "USERNAME", tFieldUserName.getText(), "USERNAME = \"" + this.uname + "\"");
                    } catch (SQLException exception){
                        System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                        System.out.println(exception);
                    }
                }
            }
            if (!tFieldPassword.getText().equals("")){
                if (!Objects.equals(tFieldPassword.getText(), tFieldPasswordAgain.getText())){
                    this.errorMessage += "The passwords doesn't match\n";
                }

                String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,20}$";

                Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

                if (!pattern.matcher(tFieldPassword.getText()).matches()){
                    this.errorMessage += "Wrong password\n";
                }
                if (this.errorMessage.equals("")){
                    try{
                        this.database.updateData("clients", "PWD", tFieldUserName.getText(), "USERNAME = \"" + this.uname + "\"");
                    } catch (SQLException exception){
                        System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                        System.out.println(exception);
                    }
                }
            }
            if (!tFieldPhoneNumber.getText().equals("")){
                try{
                    this.database.updateData("clients", "PHONENUMBER", tFieldPhoneNumber.getText(), "USERNAME = \"" + this.uname + "\"");
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
            }
            if (!tFieldAddress.getText().equals("")){
                try{
                    this.database.updateData("clients", "ADDRESS", tFieldAddress.getText(), "USERNAME = \"" + this.uname + "\"");
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
            }
            if (this.errorMessage.equals("")){
                this.window.setScene(this.login);
                return;
            }
            this.createUserPager();
            this.window.setScene(this.user);
        });

        bDelete.setOnAction(e -> {
            try {
                this.database.readData("orders","USERNAME = \"" + this.uname + "\"", true, "TIME", null, null);
                ResultSet rs = this.database.getRs();
                while (rs.next()) {
                    this.database.updateData("orders","USERNAME", this.uname + "[DELETED]", "USERNAME = \"" +  rs.getString(1) + "\" and TIME = \"" + rs.getString(2) + "\"");
                }
            } catch (SQLException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }
            try{
                this.database.deleteData("clients", "USERNAME = \"" + this.uname + "\"");
            } catch (SQLException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }


            this.window.setScene(this.login);
        });

        userPageModifyLayout.add(new Text("Username:"),0,0);
        userPageModifyLayout.add(tFieldUserName,1,0);
        userPageModifyLayout.add(new Text("Password:"),0,1);
        userPageModifyLayout.add(tFieldPassword,1,1);
        userPageModifyLayout.add(new Text("Password again:"),0,2);
        userPageModifyLayout.add(tFieldPasswordAgain,1,2);
        userPageModifyLayout.add(new Text("Phone number:"),0,3);
        userPageModifyLayout.add(tFieldPhoneNumber,1,3);
        userPageModifyLayout.add(new Text("Address:"),0,4);
        userPageModifyLayout.add(tFieldAddress,1,4);
        userPageModifyLayout.add(bUpdate,1,5);
        userPageModifyLayout.add(new Text(this.errorMessage),2,5);
        userPageModifyLayout.add(bDelete,1,6);


        userPageModifyLayout.setHgap(10);
        userPageModifyLayout.setVgap(10);
        userPageModifyLayout.setPadding(new Insets(10, 10, 10, 10));

        userPageVMenuLayout.getChildren().add(bPizza);
        userPageVMenuLayout.getChildren().add(hFiller1);
        userPageVMenuLayout.getChildren().add(bCart);
        userPageVMenuLayout.getChildren().add(hFiller2);
        userPageVMenuLayout.getChildren().add(bExit);

        userPageVMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        userPageHMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        userPageHMenuLayout.getChildren().add(vFiller);
        userPageHMenuLayout.getChildren().add(bUser);

        userPageOrdersLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        userPageOrdersLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        try {
            this.database.readData("orders","USERNAME = \"" + this.uname + "\"", this.ascending != null && this.ascending, this.ascending == null ? "PRICE" : "TIME", null, null);
            ResultSet rs = this.database.getRs();
            CheckBox cb = new CheckBox("");
            cb.allowIndeterminateProperty();
            if (this.ascending == null){
                cb.setIndeterminate(true);
            } else {
                cb.setSelected(this.ascending);
            }

            cb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                if (cb.isIndeterminate()) {
                    cb.setSelected(true);
                    cb.setIndeterminate(false);
                    cb.setAllowIndeterminate(false);
                    this.ascending = false;
                } else if (cb.isSelected()) {
                    cb.setSelected(false);
                    this.ascending = null;
                } else if (!cb.isSelected()) {
                    cb.setSelected(true);
                    cb.setIndeterminate(true);
                    cb.setAllowIndeterminate(true);
                    this.ascending = true;
                }
                this.createUserPager();
                this.window.setScene(this.user);
            });
            userPageOrderLayout.add(new Text("Username"), 0, 0);
            userPageOrderLayout.add(new Text("Time"), 1, 0);
            userPageOrderLayout.add(new Text("Price"), 2, 0);
            userPageOrderLayout.add(new Text("Items"), 3, 0);
            userPageOrderLayout.add(cb, 4, 0);
            userPageOrderLayout.add(new Text(this.ascending == null ? "decreasing" : (this.ascending ? "ascending" : "decreasing")), this.ascending == null ? 2 : 1, 1);
            for (int i = 2; rs.next(); i++) {
                String tmp = rs.getString(4);
                tmp = tmp.replace(",",",\n");

                userPageOrderLayout.add(new Text(rs.getString(1)), 0, i);
                userPageOrderLayout.add(new Text(rs.getString(2)), 1, i);
                userPageOrderLayout.add(new Text(rs.getString(3)), 2, i);
                userPageOrderLayout.add(new Text(tmp), 3, i);
            }
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        userPageOrderLayout.setHgap(10);
        userPageOrderLayout.setVgap(10);
        userPageOrderLayout.setPadding(new Insets(10, 10, 10, 10));
        userPageOrderLayout.setPrefWidth(450);
        userPageOrdersLayout.setContent(userPageOrderLayout);
        userPageOrdersLayout.setPrefWidth(450);
        userPageOrdersLayout.setMaxSize(450,500);

        userPageMenuLayout.setCenter(userPageModifyLayout);
        userPageMenuLayout.setRight(userPageOrdersLayout);

        userPageLayout.setTop(userPageHMenuLayout);
        userPageLayout.setLeft(userPageVMenuLayout);
        userPageLayout.setCenter(userPageMenuLayout);
        userPageLayout.setBottom(new BorderPane());

        this.user = new Scene(userPageLayout, 800, 512);
        this.user.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
    }

    /** Create statisticsPage
     */
    private void createStatisticsPage(){
        BorderPane statisticsLayout = new BorderPane();
        VBox statisticsPayLayout = new VBox();
        HBox statisticsHMenuLayout = new HBox();
        VBox statisticsVMenuLayout = new VBox();

        ImageView exitImg = null;
        ImageView cartImg = null;
        ImageView pizzaImg = null;
        ImageView userImg = null;
        try {
            FileInputStream input1 = new FileInputStream("src/resources/images/exit.png");
            FileInputStream input3 = new FileInputStream("src/resources/images/statistics.png");
            FileInputStream input4 = new FileInputStream("src/resources/images/pizza.jpg");
            FileInputStream input5 = new FileInputStream("src/resources/images/user.png");
            exitImg = new ImageView(new Image(input1));
            exitImg.setFitHeight(50);
            exitImg.setFitWidth(50);
            cartImg = new ImageView(new Image(input3));
            cartImg.setFitHeight(50);
            cartImg.setFitWidth(50);
            pizzaImg = new ImageView(new Image(input4));
            pizzaImg.setFitHeight(50);
            pizzaImg.setFitWidth(50);
            userImg = new ImageView(new Image(input5));
            userImg.setFitHeight(50);
            userImg.setFitWidth(50);
        } catch (FileNotFoundException exception) {
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(750,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        Button bExit = new Button("", exitImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        Button bUser = new Button("", userImg);
        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(this.login);
        });
        bCart.setOnAction(e -> {
            if (this.isAdmin) {
                this.createStatisticsPage();
            } else {
                this.createCheckOutPage();
            }
            this.window.setScene(this.checkOut);
        });
        bPizza.setOnAction(e -> {
            this.createHomePage();
            this.window.setScene(this.home);
        });
        bUser.setOnAction(e -> {
            if (this.isAdmin){
                this.createPizzaCreatorPage();
            }else{
                this.createUserPager();
            }
            this.window.setScene(this.user);
        });

        statisticsVMenuLayout.getChildren().add(bPizza);
        statisticsVMenuLayout.getChildren().add(hFiller1);
        statisticsVMenuLayout.getChildren().add(bCart);
        statisticsVMenuLayout.getChildren().add(hFiller2);
        statisticsVMenuLayout.getChildren().add(bExit);

        statisticsVMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        statisticsHMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        statisticsHMenuLayout.getChildren().add(vFiller);
        statisticsHMenuLayout.getChildren().add(bUser);

        try{
            this.database.readData("ORDERS", null, null, null, null , null);
            ResultSet rs = this.database.getRs();
            HashMap<String, Integer> unOrderedOrders = new HashMap<>();
            HashMap<String, Integer> unOrderedUsers = new HashMap<>();

            int sumOrders = 0;
            int sumUsers = 0;
            while (rs.next()) {
                String[] pizzas = rs.getString(4).split(",");
                if (unOrderedUsers.containsKey(rs.getString(1))) {
                    unOrderedUsers.put(rs.getString(1), unOrderedUsers.get(rs.getString(1)) + pizzas.length - 1);
                } else {
                    unOrderedUsers.put(rs.getString(1), pizzas.length - 1);
                }
                sumUsers += pizzas.length - 1;

                for (String pizza : pizzas) {
                    sumOrders++;
                    if (unOrderedOrders.containsKey(pizza)) {
                        unOrderedOrders.put(pizza, unOrderedOrders.get(pizza) + 1);
                    } else {
                        unOrderedOrders.put(pizza, 1);
                    }
                }
            }

            ObservableList<PieChart.Data> pieChartOrders = FXCollections.observableArrayList();
            ObservableList<PieChart.Data> pieChartUsers = FXCollections.observableArrayList();

            for (Map.Entry<String, Integer> set : unOrderedOrders.entrySet()) {
                pieChartOrders.add(new PieChart.Data(set.getKey(), Double.parseDouble(String.valueOf(set.getValue()))/sumOrders));
            }

            for (Map.Entry<String, Integer> set : unOrderedUsers.entrySet()) {
                pieChartUsers.add(new PieChart.Data(set.getKey(), Double.parseDouble(String.valueOf(set.getValue()))/sumUsers));
            }

            final PieChart orderChart = new PieChart(pieChartOrders);
            final PieChart userChart = new PieChart(pieChartUsers);

            for (final PieChart.Data data : orderChart.getData()) {
                data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, e ->
                {
                    this.previousValue = data.getName();
                    data.setName((String.valueOf(BigDecimal.valueOf(data.getPieValue()).setScale(3, RoundingMode.HALF_UP).doubleValue()*100).substring(0,2) + "%"));
                });
                data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, e ->
                {
                    data.setName(this.previousValue);
                    this.previousValue = null;
                });
            }


            for (final PieChart.Data data : userChart.getData()) {
                data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, e ->
                {
                    this.previousValue = data.getName();
                    data.setName((String.valueOf(BigDecimal.valueOf(data.getPieValue()).setScale(3, RoundingMode.HALF_UP).doubleValue()*100).substring(0,2) + "%"));
                });
                data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, e ->
                {
                    data.setName(this.previousValue);
                    this.previousValue = null;
                });
            }

            orderChart.setTitle("Sold pizzas");
            orderChart.setLabelLineLength(10);
            orderChart.setLegendSide(Side.BOTTOM);

            userChart.setTitle("Who ordered most?");
            userChart.setLabelLineLength(10);
            userChart.setLegendSide(Side.BOTTOM);

            ScrollPane tmpLayout = new ScrollPane();
            VBox chartsLayout = new VBox();

            tmpLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            tmpLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            tmpLayout.setMaxSize(700,500);

            chartsLayout.getChildren().add(orderChart);
            chartsLayout.getChildren().add(userChart);

            tmpLayout.setContent(chartsLayout);

            statisticsLayout.setCenter(tmpLayout);

        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        statisticsLayout.setTop(statisticsHMenuLayout);
        statisticsLayout.setLeft(statisticsVMenuLayout);
        statisticsLayout.setRight(statisticsPayLayout);
        statisticsLayout.setBottom(new BorderPane());

        this.checkOut = new Scene(statisticsLayout, 800, 512);
        this.checkOut.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
    }

    /** Create pizzaCreatorPage
     */
    private void createPizzaCreatorPage(){
        BorderPane pizzaCreatorPageLayout = new BorderPane();
        HBox pizzaCreatorPageHMenuLayout = new HBox();
        VBox pizzaCreatorPageVMenuLayout = new VBox();
        VBox pizzaCreatorPageFilterLayout = new VBox();
        ScrollPane pizzaCreatorPageMainLayout = new ScrollPane();

        ImageView exitImg = null;
        ImageView cartImg = null;
        ImageView pizzaImg = null;
        ImageView userImg = null;
        try {
            FileInputStream input1 = new FileInputStream("src/resources/images/exit.png");
            FileInputStream input3 = this.isAdmin ? new FileInputStream("src/resources/images/statistics.png") : new FileInputStream("src/resources/images/cart.png");
            FileInputStream input4 = new FileInputStream("src/resources/images/pizza.jpg");
            FileInputStream input5 = new FileInputStream("src/resources/images/user.png");
            exitImg = new ImageView(new Image(input1));
            exitImg.setFitHeight(50);
            exitImg.setFitWidth(50);
            cartImg = new ImageView(new Image(input3));
            cartImg.setFitHeight(50);
            cartImg.setFitWidth(50);
            pizzaImg = new ImageView(new Image(input4));
            pizzaImg.setFitHeight(50);
            pizzaImg.setFitWidth(50);
            userImg = new ImageView(new Image(input5));
            userImg.setFitHeight(50);
            userImg.setFitWidth(50);
        } catch (FileNotFoundException exception) {
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(750,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        Button bExit = new Button("", exitImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        Button bUser = new Button("", userImg);
        Button bCreatePizza = new Button("Create Pizza");
        Button bCreateTopping = new Button("Create Topping");
        Button bClear = new Button("Clear items");

        bCreatePizza.getStyleClass().add("success");
        bCreateTopping.getStyleClass().add("info");
        bClear.getStyleClass().add("danger");

        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(this.login);
        });
        bCart.setOnAction(e -> {
            if (this.isAdmin) {
                this.createStatisticsPage();
            } else {
                this.createCheckOutPage();
            }
            this.window.setScene(this.checkOut);
        });
        bPizza.setOnAction(e -> {
            this.createHomePage();
            this.window.setScene(this.home);
        });
        bUser.setOnAction(e -> {
            if (this.isAdmin){
                this.createPizzaCreatorPage();
            }else{
                this.createUserPager();
            }
            this.window.setScene(this.user);
        });
        bCreatePizza.setOnAction(e -> {
            String string = this.allowedItems.toString();
            string = string.substring(1, string.length() - 1);
            Stage dialogStage = new Stage();
            GridPane dialogBoxLayout = new GridPane();
            TextField nameField = new TextField("");
            TextField priceField = new TextField("");
            Button bDone = new Button("Done");
            Button bCancel = new Button("Cancel");

            bDone.getStyleClass().add("success");
            bCancel.getStyleClass().add("danger");

            String finalString = string;
            bDone.setOnAction(ee -> {
                try{
                    String str = nameField.getText();
                    String str2 = priceField.getText();
                    if (str.isEmpty() || str2.isEmpty()){
                        dialogStage.close();
                        return;
                    }

                    this.database.readData("pizzas", "NAME = \"" + str + "\"", null, null, null, null);

                    ResultSet rs = this.database.getRs();

                    if (rs.isBeforeFirst()){
                        this.database.updateData("pizzas", "PRICE" , str2, "NAME = \"" + str + "\"");
                        this.database.updateData("pizzas", "TOPPINGS" , finalString, "NAME = \"" + str + "\"");
                    } else {
                        this.database.writeData("pizzas",str + ";" + str2 + ";" + finalString);
                    }


                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
                this.allowedItems.clear();
                this.createPizzaCreatorPage();
                dialogStage.close();
                this.window.setScene(this.user);
            });

            bCancel.setOnAction(ee -> {
                this.allowedItems.clear();
                this.createPizzaCreatorPage();
                dialogStage.close();
                this.window.setScene(this.user);
            });

            dialogBoxLayout.add(new Text("Pizza name:"), 0, 0);
            dialogBoxLayout.add(nameField, 0, 1);
            dialogBoxLayout.add(new Text("Pizza price:"), 0, 2);
            dialogBoxLayout.add(priceField, 0, 3);
            dialogBoxLayout.add(bDone, 0, 4);
            dialogBoxLayout.add(bCancel, 0, 5);

            Scene stageScene = new Scene(dialogBoxLayout, 150, 150);
            stageScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
            dialogStage.setScene(stageScene);
            dialogStage.setTitle("PizzaCreator420");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.show();
        });
        bCreateTopping.setOnAction(e -> {
            Stage dialogStage = new Stage();
            GridPane dialogBoxLayout = new GridPane();
            TextField nameField = new TextField("");
            Button bDone = new Button("Done");
            Button bCancel = new Button("Cancel");

            bDone.getStyleClass().add("success");
            bCancel.getStyleClass().add("danger");

            bDone.setOnAction(ee -> {
                try{
                    String str = nameField.getText();
                    if (str.isEmpty()){
                        dialogStage.close();
                        return;
                    }

                    this.database.readData("toppings", "NAME = \"" + str + "\"", null, null, null, null);

                    ResultSet rs = this.database.getRs();

                    if (rs.isBeforeFirst()){
                        dialogStage.close();
                        return;
                    }

                    this.database.writeData("toppings",str);
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
                this.allowedItems.clear();
                this.createPizzaCreatorPage();
                dialogStage.close();
                this.window.setScene(this.user);
            });

            bCancel.setOnAction(ee -> {
                this.allowedItems.clear();
                this.createPizzaCreatorPage();
                dialogStage.close();
                this.window.setScene(this.user);
            });
            dialogBoxLayout.add(new Text("Topping name:"), 0, 0);
            dialogBoxLayout.add(nameField, 0, 1);
            dialogBoxLayout.add(bDone, 0, 2);
            dialogBoxLayout.add(bCancel, 0, 3);

            Scene stageScene = new Scene(dialogBoxLayout, 150, 150);
            stageScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
            dialogStage.setScene(stageScene);
            dialogStage.setTitle("EpicToppingCreator");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.show();
        });
        bClear.setOnAction(e -> {
            this.allowedItems.clear();
            this.notAllowedItems.clear();
            this.createPizzaCreatorPage();
            this.window.setScene(this.user);
        });

        pizzaCreatorPageVMenuLayout.getChildren().add(bPizza);
        pizzaCreatorPageVMenuLayout.getChildren().add(hFiller1);
        pizzaCreatorPageVMenuLayout.getChildren().add(bCart);
        pizzaCreatorPageVMenuLayout.getChildren().add(hFiller2);
        pizzaCreatorPageVMenuLayout.getChildren().add(bExit);

        pizzaCreatorPageVMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        pizzaCreatorPageHMenuLayout.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        pizzaCreatorPageHMenuLayout.getChildren().add(vFiller);
        pizzaCreatorPageHMenuLayout.getChildren().add(bUser);

        pizzaCreatorPageMainLayout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pizzaCreatorPageMainLayout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        pizzaCreatorPageFilterLayout.getChildren().add(bCreatePizza);
        pizzaCreatorPageFilterLayout.getChildren().add(bClear);
        try {
            this.database.readData("toppings", null, null, null, null, null);
            ResultSet rs = this.database.getRs();
            while (rs.next()) {
                CheckBox cb = new CheckBox(rs.getString(2));
                cb.setId(String.valueOf(rs.getInt(1)));
                cb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                    if (cb.isSelected()) {
                        cb.setSelected(false);
                        this.allowedItems.remove(Integer.valueOf(Integer.parseInt(cb.getId())));
                    } else if (!cb.isSelected()) {
                        cb.setSelected(true);
                        cb.setIndeterminate(true);
                        cb.setAllowIndeterminate(true);
                        this.allowedItems.add(Integer.parseInt(cb.getId()));
                    }
                });
                pizzaCreatorPageFilterLayout.getChildren().add(cb);
            }
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        pizzaCreatorPageFilterLayout.getChildren().add(bCreateTopping);

        pizzaCreatorPageMainLayout.setContent(pizzaCreatorPageFilterLayout);

        pizzaCreatorPageLayout.setTop(pizzaCreatorPageHMenuLayout);
        pizzaCreatorPageLayout.setLeft(pizzaCreatorPageVMenuLayout);
        pizzaCreatorPageLayout.setCenter(pizzaCreatorPageMainLayout);
        pizzaCreatorPageLayout.setBottom(new BorderPane());

        this.user = new Scene(pizzaCreatorPageLayout, 800, 512);
        this.user.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/bootstrap3.css")).toExternalForm());
    }

    /** generate pizzas for the homePage
     */
    private GridPane generateItems() {
        GridPane gridItems = new GridPane();
        gridItems.setHgap(100);
        gridItems.setVgap(100);
        gridItems.setPadding(new Insets(10, 10, 10, 10));
        if (this.allowedItems.isEmpty() && this.notAllowedItems.isEmpty()) {
            if (!this.topFive){
                try{
                    this.database.readData("PIZZAS", null, true, "NAME", null , null);
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }

            }else{
                try{
                    this.database.readData("ORDERS", null, null, null, null , null);
                    ResultSet rs = this.database.getRs();
                    HashMap<String, Integer> unOrderedOrders = new HashMap<>();
                    while (rs.next()) {
                        String[] pizzas = rs.getString(4).split(",");
                        for (String pizza : pizzas) {
                            if (unOrderedOrders.containsKey(pizza)) {
                                unOrderedOrders.put(pizza, unOrderedOrders.get(pizza) + 1);
                            } else {
                                unOrderedOrders.put(pizza, 1);
                            }
                        }
                    }
                    Map<String, Integer> sortedMapDsc = sortByComparator(unOrderedOrders);
                    StringBuilder bobTheBuilder = new StringBuilder();
                    bobTheBuilder.append("select * from pizzas where NAME in (");
                    int counter = 0;
                    for (Map.Entry<String, Integer> entry : sortedMapDsc.entrySet()){
                        if (counter < 5){
                            bobTheBuilder.append("\"").append(entry.getKey()).append("\",");
                            counter++;
                            continue;
                        }
                        break;
                    }
                    bobTheBuilder.deleteCharAt(bobTheBuilder.length() - 1);
                    bobTheBuilder.append(");");
                    this.database.readDataCustom(bobTheBuilder.toString());
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                } finally {
                    this.topFive = !this.topFive;
                }
            }

        } else {
            StringBuilder bobTheBuilder = new StringBuilder();
            bobTheBuilder.append("select * from PIZZAS where TOPPINGS REGEXP ");

            for (int i = 0; i < this.allowedItems.size(); i++) {
                bobTheBuilder.append("'([^0-9]|^)").append(this.allowedItems.get(i)).append("([^0-9]|$)'");
                if (i + 1 < this.allowedItems.size()){
                    bobTheBuilder.append(" and TOPPINGS REGEXP ");
                }
            }
            for (Integer notAllowedItem : this.notAllowedItems) {
                bobTheBuilder.append(" and TOPPINGS NOT REGEXP ").append("'([^0-9]|^)").append(notAllowedItem).append("([^0-9]|$)'");
            }
            bobTheBuilder.append(";");
            try{
                this.database.readDataCustom(bobTheBuilder.toString());
            } catch (SQLException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }

            this.notAllowedItems.clear();
            this.allowedItems.clear();
        }

        ResultSet rs = this.database.getRs();

        try{
            boolean left = false;
            int lineCounter = 0;
            while(rs.next()){
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = this.isAdmin ? new FileInputStream("src/resources/images/remove.png") : new FileInputStream("src/resources/images/add.png");
                } catch (FileNotFoundException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
                assert fileInputStream != null;
                BorderPane item = new BorderPane();
                ImageView addToCartImg = new ImageView(new Image(fileInputStream));
                addToCartImg.setFitHeight(50);
                addToCartImg.setFitWidth(50);
                Button addToCart = new Button("", addToCartImg);
                if (this.isAdmin){
                    addToCart.getStyleClass().add("danger");
                } else {
                    addToCart.getStyleClass().add("success");
                }
                String name = rs.getString(2);
                int price = rs.getInt(3);
                String toppingIds = rs.getString(4).replace(";",",");

                addToCart.setOnAction(e -> {
                    if (!this.isAdmin){
                        this.cart.addItem(name, price);
                    } else {
                        try {
                            this.database.deleteData("pizzas","NAME=\"" + name + "\"");
                            this.createHomePage();
                            this.window.setScene(this.home);
                        } catch (SQLException exception) {
                            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                            System.out.println(exception);
                        }
                    }

                });

                this.database.readData("TOPPINGS","TOPPINGID IN (" + toppingIds + ")",true, "NAME", null,null);
                ResultSet rs2 = this.database.getRs();

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
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        return gridItems;
    }

    /** Sort String-integer Map
     */
    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap)
    {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        try{
            this.database = new Database();
        } catch (SQLException exception){
            System.out.println(exception);
            Platform.exit();
        }

        this.window.setResizable(false);
        this.setScenes();
        this.window.setScene(this.login);
        this.window.setTitle("Pizzeria");
        this.window.show();
    }

    @Override
    public void stop(){
        try{
            this.database.close();
        } catch (SQLException | NullPointerException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}