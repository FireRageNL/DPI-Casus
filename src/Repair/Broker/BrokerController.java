package Repair.Broker;

import Repair.Gateways.BrokerGateway;
import Repair.Models.CompanyReply;
import Repair.Models.CompanyRequest;
import Repair.Models.RepairReply;
import Repair.Models.RepairRequest;
import com.rabbitmq.client.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BrokerController {

	private BrokerGateway gateway;


	@FXML
	public ListView Text;

	@FXML
	public Label label1;


	public void editTextLog(String s) {
		Text.getItems().add(s);
	}


	public void Initialize() throws IOException, TimeoutException {
		gateway = new BrokerGateway(this);
	}
}


