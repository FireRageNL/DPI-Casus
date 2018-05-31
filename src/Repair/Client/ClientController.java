package Repair.Client;

import Repair.Gateways.ClientGateway;
import Repair.Models.RepairReply;
import Repair.Models.RepairRequest;
import Repair.Models.RepairTypes;
import com.rabbitmq.client.*;
import com.sun.org.apache.xml.internal.security.Init;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import jfxtras.labs.dialogs.MonologFX;
import org.apache.commons.lang3.SerializationUtils;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ClientController {

	private ClientGateway _gateway;

	@FXML
	public ComboBox dropdown;
	@FXML
	public Label dropdownLabel;
	@FXML
	public TextArea repairRequest;
	@FXML
	public ListView repairReplies;

	public void Initialize() throws IOException, TimeoutException {
		InitializeDropdown();
		_gateway = new ClientGateway(this);
	}

	public void InitializeDropdown(){
		dropdown.getItems().addAll(RepairTypes.values());
		dropdown.setValue("PC");
	}

	@FXML
	public void processClick() throws IOException, TimeoutException {
		if(repairRequest.getText() != null) {
			RepairRequest rr = new RepairRequest(repairRequest.getText(), dropdown.getValue().toString());
			_gateway.SendMessage(rr);
		}
	}

	@FXML
	private void onClickInList() throws IOException {
		RepairReply reply = (RepairReply) repairReplies.getSelectionModel().getSelectedItem();
		if(reply != null){
			MonologFX monolog = new MonologFX(MonologFX.Type.INFO);
			monolog.setMessage(reply.explanation);
			monolog.show();
		}
	}

	public void addReplyToList(RepairReply reply){
		Platform.runLater(() ->{
			repairReplies.getItems().add(reply);
		});
	}

}
