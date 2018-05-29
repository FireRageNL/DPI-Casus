package Repair.Broker;

import Repair.Models.CompanyReply;
import Repair.Models.CompanyRequest;
import Repair.Models.RepairReply;
import Repair.Models.RepairRequest;
import com.rabbitmq.client.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BrokerMain extends Application {

	private BrokerController controller;
	@Override
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("Broker.fxml"));
		Parent root = Loader.load();
		controller = Loader.getController();
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		controller.InitializeConnection();
		controller.InitializeClientToBroker();
		controller.InitializeBrokerToClient();
		controller.editTextLog("Startup completed!");
	}
}