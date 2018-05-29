package Repair.Company;

import Repair.Models.CompanyReply;
import Repair.Models.CompanyRequest;
import Repair.Models.RepairRequest;
import Repair.Models.RepairTypes;
import com.rabbitmq.client.*;
import com.sun.org.apache.xml.internal.security.Init;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CompanyController {

	private Connection connection;
	private String _companyName;

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


	public void InitializeConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
	}


	public void setRepairTypes(){
		repairTypes.getItems().addAll(RepairTypes.values());
		repairTypes.setValue("PC");
	}

	@FXML
	public void RegisterButtonAction() throws IOException {
		_companyName = companyName.getText();

		Channel channel = connection.createChannel();
		channel.queueDeclare(repairTypes.getValue().toString(), false, false, false, null);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				Platform.runLater(() ->{
					CompanyRequest cr = SerializationUtils.deserialize(body);
					incomingAssignments.getItems().add(cr);
				});
			}
		};
		channel.basicConsume(repairTypes.getValue().toString(), true, consumer);
	}

	@FXML
	public void sendOfferButtonAction() throws IOException, TimeoutException {
		CompanyRequest cr = (CompanyRequest) incomingAssignments.getSelectionModel().getSelectedItem();
		CompanyReply creply = new CompanyReply(Double.parseDouble(price.getText()),_companyName,repairDescription.getText(),cr.replyQueue, cr.correlationId);
		Channel channel = connection.createChannel();
		channel.queueDeclare("CompanyReply", false, false, false, null);
		channel.basicPublish("", "CompanyReply", null, SerializationUtils.serialize(creply));
		channel.close();
		incomingAssignments.getItems().remove(incomingAssignments.getSelectionModel().getSelectedItem());
	}
}
