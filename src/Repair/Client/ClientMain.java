package Repair.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader Loader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent root = Loader.load();
        ClientController clientController = Loader.getController();
        clientController.InitializeDropdown();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
