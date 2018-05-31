package Repair.Company;

import Repair.Gateways.CompanyGateway;
import Repair.Models.CompanyReply;
import Repair.Models.CompanyRequest;
import Repair.Models.RepairRequest;
import Repair.Models.RepairTypes;
import com.rabbitmq.client.*;
import com.sun.org.apache.xml.internal.security.Init;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import jfxtras.labs.dialogs.MonologFX;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CompanyController {

	private CompanyGateway _gateway;

	@FXML
	public TextArea	repairDescription;

	@FXML
	public Button registerButton;

	@FXML
	public Button replyButton;

	@FXML
	public TextField price;

	@FXML
	public ListView incomingAssignments;

	@FXML
	public TextField companyName;

	@FXML
	public ComboBox repairTypes;


	public void Initialize() throws IOException, TimeoutException {
		_gateway = new CompanyGateway(this);
	}


	public void setRepairTypes(){
		repairTypes.getItems().addAll(RepairTypes.values());
		repairTypes.setValue("PC");
	}

	@FXML
	public void RegisterButtonAction() throws IOException {
		_gateway.registerRepairChannel(companyName.getText(),repairTypes.getValue().toString());
	}

	@FXML
	public void sendOfferButtonAction() throws IOException, TimeoutException {
		_gateway.sendMessageHandler((CompanyRequest)incomingAssignments.getSelectionModel().getSelectedItem(),Double.parseDouble(price.getText()),repairDescription.getText());
		companyReplySent((CompanyRequest)incomingAssignments.getSelectionModel().getSelectedItem());
	}

	public void setNewAssignment(CompanyRequest request){
		Platform.runLater(() ->{
			incomingAssignments.getItems().add(request);
		});
	}

	public void companyReplySent(CompanyRequest request){
		Platform.runLater(() -> {
			price.setText("");
			repairDescription.setText("");
			incomingAssignments.getItems().remove(incomingAssignments.getSelectionModel().getSelectedItem());

		});
	}
}
