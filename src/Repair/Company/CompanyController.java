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
import jfxtras.labs.dialogs.MonologFX;
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
		channel.exchangeDeclare(repairTypes.getValue().toString(),"fanout");
		String queuename = channel.queueDeclare().getQueue();
		channel.queueBind(queuename,repairTypes.getValue().toString(),"");
		MonologFX monolog = new MonologFX(MonologFX.Type.INFO);
		monolog.setMessage("Registered on "+ repairTypes.getValue().toString());
		monolog.show();

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
		channel.basicConsume(queuename, true, consumer);
	}

	@FXML
	public void sendOfferButtonAction() throws IOException, TimeoutException {
		CompanyRequest cr = (CompanyRequest) incomingAssignments.getSelectionModel().getSelectedItem();
		CompanyReply creply = new CompanyReply(Double.parseDouble(price.getText()),_companyName,repairDescription.getText(),cr.replyQueue, cr.correlationId);
		Channel channel = connection.createChannel();
		channel.queueDeclare("CompanyReply", false, false, false, null);
		channel.basicPublish("", "CompanyReply", null, SerializationUtils.serialize(creply));
		channel.close();
		Platform.runLater(() -> {
			price.setText("");
			repairDescription.setText("");
		});
		incomingAssignments.getItems().remove(incomingAssignments.getSelectionModel().getSelectedItem());
	}
}
