package com.pizzeria;

import com.pizzeria.cart.Cart;
import com.pizzeria.database.Database;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class PizzeriaApplication extends Application {

    private Stage window;
    private Scene login, register, home, checkOut, user;

    private String errorMessage = "";
    private String uname;

    private Database database;
    private final Cart cart = new Cart();

    private final ArrayList<Integer> allowedItems = new ArrayList<>();
    private final ArrayList<Integer> notAllowedItems = new ArrayList<>();

    private boolean topFive = false;

    private void setScenes(){
        GridPane registerLayout = new GridPane();
        GridPane loginLayout = new GridPane();

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
                try {
                    database.readData("CLIENTS",
                            "USERNAME=\"" + uname + "\" AND PWD=\"" + DigestUtils.shaHex(password) + "\""
                            , null, null
                            , null,
                            null);
                    ResultSet rs = database.getRs();
                    if (!rs.isBeforeFirst()){
                        errorMessage += "Invalid username or Password";
                    }
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
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

            createHomePage();
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
                try {
                    database.readData("CLIENTS"
                            ,"USERNAME=\"" + uname + "\""
                            ,null
                            ,null
                            ,null
                            ,null);
                    ResultSet rs = database.getRs();
                    if (rs.isBeforeFirst()){
                        errorMessage += "Username already in use";
                    }
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
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
            try{
                database.writeData("clients",uname+";"+password+";"+phoneNumber+";"+address);
            } catch (SQLException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }


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
    }

    private void createHomePage() {
        BorderPane homePageLayout = new BorderPane();
        HBox homePageHMenuLayout = new HBox();
        VBox homePageVMenuLayout = new VBox();
        ScrollPane homePagePizzasLayout = new ScrollPane();
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
        vFiller.setPrefSize(10000,1);
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
        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(login);
        });
        bCart.setOnAction(e -> {
            createCheckOutPage();
            this.window.setScene(checkOut);
        });
        bPizza.setOnAction(e -> {
            createHomePage();
            this.window.setScene(home);
        });
        bUser.setOnAction(e -> {
            createUserPager();
            this.window.setScene(user);
        });
        bFilter.setOnAction(e -> {
            createHomePage();
            this.window.setScene(home);
        });
        bClear.setOnAction(e -> {
            this.allowedItems.clear();
            this.notAllowedItems.clear();
            createHomePage();
            this.window.setScene(home);
        });
        bTop.setOnAction(e -> {
            this.allowedItems.clear();
            this.notAllowedItems.clear();
            topFive = true;
            createHomePage();
            this.window.setScene(home);
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
        GridPane g = generateItems();

        homePagePizzasLayout.setContent(g);
        homePagePizzasLayout.setMaxSize(500,500);

        VBox homePageFilterLayout = new VBox();

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
                        notAllowedItems.remove(Integer.valueOf(Integer.parseInt(cb.getId())));
                    } else if (cb.isSelected()) {
                        cb.setSelected(false);
                        allowedItems.remove(Integer.valueOf(Integer.parseInt(cb.getId())));
                        notAllowedItems.add(Integer.parseInt(cb.getId()));
                    } else if (!cb.isSelected()) {
                        cb.setSelected(true);
                        cb.setIndeterminate(true);
                        cb.setAllowIndeterminate(true);
                        allowedItems.add(Integer.parseInt(cb.getId()));
                    }
                });
                homePageFilterLayout.getChildren().add(cb);
            }
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        BorderPane homePageMainLayout = new BorderPane();
        homePageMainLayout.setRight(homePageFilterLayout);
        homePageMainLayout.setCenter(homePagePizzasLayout);

        homePageLayout.setTop(homePageHMenuLayout);
        homePageLayout.setLeft(homePageVMenuLayout);
        homePageLayout.setCenter(homePageMainLayout);
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
        vFiller.setPrefSize(10000,1);
        Region hFiller1 = new Region();
        hFiller1.setPrefSize(1,200);
        Region hFiller2 = new Region();
        hFiller2.setPrefSize(1,200);

        Button bExit = new Button("", exitImg);
        Button bCart = new Button("", cartImg);
        Button bPizza = new Button("", pizzaImg);
        Button bUser = new Button("", userImg);
        Button bPay = new Button("Pay");
        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(login);
        });
        bCart.setOnAction(e -> {
            createCheckOutPage();
            this.window.setScene(checkOut);
        });
        bPizza.setOnAction(e -> {
            createHomePage();
            this.window.setScene(home);
        });
        bPay.setOnAction(e -> {
            try{
                this.database.writeData("orders", this.uname + ";" + this.cart.getPrice() + ";" + this.cart.getItemAsString());
            } catch (SQLException exception){
                System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                System.out.println(exception);
            }

            this.cart.removeEverything();
            this.window.setScene(home);
        });

        bUser.setOnAction(e -> {
            createUserPager();
            this.window.setScene(user);
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

        GridPane g = new GridPane();

        for (int i = 0; i < cart.getKeys().size(); i++) {
            String key = cart.getKeys().get(i);
            Integer value = cart.getValues().get(i);

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

    private void createUserPager() {
        BorderPane userPageLayout = new BorderPane();
        BorderPane userPageMenuLayout = new BorderPane();
        ScrollPane userPageOrdersLayout = new ScrollPane();
        GridPane userPageModifyLayout = new GridPane();
        HBox userPageHMenuLayout = new HBox();
        VBox userPageVMenuLayout = new VBox();

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
        } catch (FileNotFoundException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        Region vFiller = new Region();
        vFiller.setPrefSize(10000,1);
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
        bUpdate.setLayoutX(0);
        bUpdate.setLayoutY(0);
        bExit.setOnAction(e -> {
            this.cart.removeEverything();
            this.window.setScene(login);
        });
        bCart.setOnAction(e -> {
            createCheckOutPage();
            this.window.setScene(checkOut);
        });
        bPizza.setOnAction(e -> {
            createHomePage();
            this.window.setScene(home);
        });

        bUser.setOnAction(e -> {
            createUserPager();
            this.window.setScene(user);
        });

        bUpdate.setOnAction(e -> {
            this.errorMessage = "";
            if (!tFieldUserName.getText().equals("")){
                    try {
                        database.readData("CLIENTS"
                                , "USERNAME=\"" + uname + "\""
                                , null
                                , null
                                , null
                                , null);
                        ResultSet rs = database.getRs();
                        if (rs.isBeforeFirst()) {
                            this.errorMessage += "Username already in use";
                        }
                    } catch (SQLException exception){
                        System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                        System.out.println(exception);
                    }
                if (errorMessage.equals("")){
                    try{
                        database.updateData("clients", "USERNAME", tFieldUserName.getText(), "USERNAME = \"" + this.uname + "\"");
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
                if (errorMessage.equals("")){
                    try{
                        database.updateData("clients", "PWD", tFieldUserName.getText(), "USERNAME = \"" + this.uname + "\"");
                    } catch (SQLException exception){
                        System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                        System.out.println(exception);
                    }
                }
            }
            if (!tFieldPhoneNumber.getText().equals("")){
                try{
                    database.updateData("clients", "PHONENUMBER", tFieldPhoneNumber.getText(), "USERNAME = \"" + this.uname + "\"");
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
            }
            if (!tFieldAddress.getText().equals("")){
                try{
                    database.updateData("clients", "ADDRESS", tFieldAddress.getText(), "USERNAME = \"" + this.uname + "\"");
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }
            }
            if (this.errorMessage.equals("")){
                this.window.setScene(login);
                return;
            }
            createUserPager();
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

        GridPane g = new GridPane();
        try {
            this.database.readData("orders","USERNAME = \"" + this.uname + "\"", true, "TIME", null, null);
            ResultSet rs = this.database.getRs();
            g.add(new Text("Username"), 0, 0);
            g.add(new Text("Time"), 1, 0);
            g.add(new Text("Price"), 2, 0);
            g.add(new Text("Items"), 3, 0);
            for (int i = 1; rs.next(); i++) {
                g.add(new Text(rs.getString(1)), 0, i);
                g.add(new Text(rs.getString(2)), 1, i);
                g.add(new Text(rs.getString(3)), 2, i);
                g.add(new Text(rs.getString(4)), 3, i);
            }
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(10, 10, 10, 10));
        g.setPrefWidth(400);
        userPageOrdersLayout.setContent(g);
        userPageOrdersLayout.setPrefWidth(400);
        userPageOrdersLayout.setMaxSize(400,500);

        userPageMenuLayout.setCenter(userPageModifyLayout);
        userPageMenuLayout.setRight(userPageOrdersLayout);

        userPageLayout.setTop(userPageHMenuLayout);
        userPageLayout.setLeft(userPageVMenuLayout);
        userPageLayout.setCenter(userPageMenuLayout);
        userPageLayout.setBottom(new BorderPane());

        this.user = new Scene(userPageLayout, 800, 512);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        try{
            this.database = new Database();
        } catch (SQLException exception){
            System.out.println(exception);
            Platform.exit();
        }

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
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }
    }

    private GridPane generateItems() {
        GridPane gridItems = new GridPane();
        gridItems.setHgap(100);
        gridItems.setVgap(100);
        gridItems.setPadding(new Insets(10, 10, 10, 10));
        if (this.allowedItems.isEmpty() && this.notAllowedItems.isEmpty()) {
            if (!this.topFive){
                try{
                    database.readData("PIZZAS", null, true, "NAME", null , null);
                } catch (SQLException exception){
                    System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
                    System.out.println(exception);
                }

            }else{
                try{
                    database.readData("ORDERS", null, null, null, null , null);
                    ResultSet rs = database.getRs();
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
                    database.readDataCustom(bobTheBuilder.toString());
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

        ResultSet rs = database.getRs();

        try{
            boolean left = false;
            int lineCounter = 0;
            while(rs.next()){
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream("src/resources/images/add.png");
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
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

        return gridItems;
    }

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
    public void stop(){
        try{
            this.database.close();
        } catch (SQLException exception){
            System.out.println("Senpai Okotte wa ikemasenga, erā ga hassei shimashita");
            System.out.println(exception);
        }

    }

    public static void main(String[] args) {
        launch();
    }
}