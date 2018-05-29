package Repair.Company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CompanyMain extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("Company.fxml"));
		Parent root = Loader.load();
		CompanyController CompanyController = Loader.getController();
		CompanyController.setRepairTypes();
		CompanyController.InitializeConnection();
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
