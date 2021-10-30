package com.pizzeria;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

public class PizzeriaApplication extends Application {

    Stage window;
    Scene login, register, home;

    GridPane layout1 = new GridPane();
    GridPane layout2 = new GridPane();
    BorderPane layout3 = new BorderPane();
    HBox layout4 = new HBox();
    VBox layout5 = new VBox();
    FlowPane layout6 = new FlowPane();

    Text username = new Text("Username");
    Text password = new Text("Password");
    Text passwordAgain = new Text("Password Again");
    Text phoneNumber = new Text("Phone number");
    Text address = new Text("Address");

    Text loginUserName = new Text("Username");
    Text loginPassword = new Text("Password");

    String errorMessage = "";

    Database database = new Database();

    private void setScenes(){
        /* TextFields */
        TextField tFieldUserName = new TextField();
        TextField tFieldPassword = new TextField();
        TextField tFieldPasswordAgain = new TextField();
        TextField tFieldPhoneNumber = new TextField();
        TextField tFieldAddress = new TextField();

        TextField tFieldLoginUserName = new TextField();
        TextField tFieldLoginPassword = new TextField();

        /* Buttons */
        Button bLogin = new Button("Login");
        bLogin.setDefaultButton(true);
        bLogin.setLayoutX(0);
        bLogin.setLayoutY(0);
        bLogin.setOnAction(e -> {
            String uname = tFieldLoginUserName.getText();
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
                    ex.printStackTrace();
                }
            }

            if (!errorMessage.equals("")){
                layout2.add(new Text(errorMessage), 1, 6);
                try {
                    login = new Scene(layout2, 800, 512);
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

            /*if (!pattern.matcher(password).matches()){
                errorMessage += "Wrong password\n";
            }*/

            if (address.equals("") || address.contains(" ")){
                errorMessage += "Wrong address\n";
            }

            if (!Objects.equals(errorMessage, "")){
                layout1.add(new Text(errorMessage), 1, 6);
                try {
                    register = new Scene(layout1, 800, 512);
                }catch (IllegalArgumentException ignored){}

                this.window.setScene(register);
                return;
            }

            database.writeData("clients",uname+";"+password+";"+phoneNumber+";"+address);

            database.close();

            tFieldUserName.clear();
            tFieldPassword.clear();
            tFieldPhoneNumber.clear();
            tFieldPasswordAgain.clear();
            tFieldAddress.clear();

            this.window.setScene(login);
        });

        Button bChangeToRegister = new Button("chRegister");
        bChangeToRegister.setLayoutX(250);
        bChangeToRegister.setLayoutY(220);
        bChangeToRegister.setOnAction(e -> this.window.setScene(this.register));
        Button bChangeToLogin = new Button("chLogin");
        bChangeToLogin.setLayoutX(200);
        bChangeToLogin.setLayoutY(220);
        bChangeToLogin.setOnAction(e -> this.window.setScene(this.login));

        /* layouts */

        layout1.setPadding(new Insets(10, 10, 10, 10));
        layout1.setVgap(5);
        layout1.setHgap(5);
        layout1.setAlignment(Pos.CENTER);
        layout1.add(this.username, 0, 0);
        layout1.add(tFieldUserName, 1, 0);
        layout1.add(this.password,0, 1);
        layout1.add(tFieldPassword, 1, 1);
        layout1.add(passwordAgain, 0, 2);
        layout1.add(tFieldPasswordAgain, 1, 2);
        layout1.add(phoneNumber,0, 3);
        layout1.add(tFieldPhoneNumber, 1, 3);
        layout1.add(address, 0, 4);
        layout1.add(tFieldAddress, 1, 4);
        layout1.add(bRegister, 0, 5);
        layout1.add(bChangeToLogin, 1, 5);
        layout1.add(new Text(this.errorMessage), 1, 6);

        layout2.setPadding(new Insets(10, 10, 10, 10));
        layout2.setVgap(5);
        layout2.setHgap(5);
        layout2.setAlignment(Pos.CENTER);
        layout2.add(loginUserName, 0, 0);
        layout2.add(tFieldLoginUserName, 1, 0);
        layout2.add(loginPassword, 0, 1);
        layout2.add(tFieldLoginPassword, 1, 1);
        layout2.add(bLogin, 0, 2);
        layout2.add(bChangeToRegister, 1, 2);
        this.login = new Scene(layout2, 800, 512);
        this.register = new Scene(layout1, 800, 512);
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
        bCart.setOnAction(e -> this.window.setScene(home));
        bPizza.setOnAction(e -> this.window.setScene(home));

        layout5.getChildren().add(bPizza);
        layout5.getChildren().add(hFiller1);
        layout5.getChildren().add(bCart);
        layout5.getChildren().add(hFiller2);
        layout5.getChildren().add(bExit);

        layout5.setBackground((new Background(new BackgroundFill(Color.rgb(187, 153, 255), CornerRadii.EMPTY, Insets.EMPTY))));
        layout4.setBackground((new Background(new BackgroundFill(Color.rgb(51, 204, 204), CornerRadii.EMPTY, Insets.EMPTY))));

        layout4.getChildren().add(vFiller);
        layout4.getChildren().add(bHome);

        layout3.setPadding(new Insets(20));
        layout3.setTop(layout4);
        layout3.setLeft(layout5);
        layout3.setCenter(layout6);
        layout3.setBottom(new BorderPane());

        this.home = new Scene(layout3, 800, 512);
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

    public static void main(String[] args) {
        launch();
    }
}