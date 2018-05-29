package Repair.Client;

import Repair.Models.RepairReply;
import Repair.Models.RepairRequest;
import Repair.Models.RepairTypes;
import com.rabbitmq.client.*;
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

	final String corrId = UUID.randomUUID().toString();

	@FXML
	public ComboBox dropdown;
	@FXML
	public Label dropdownLabel;
	@FXML
	public TextArea repairRequest;
	@FXML
	public ListView repairReplies;

	public void InitializeDropdown(){
		dropdown.getItems().addAll(RepairTypes.values());
		dropdown.setValue("PC");
	}

	@FXML
	public void processClick(){
		RepairRequest rr = new RepairRequest(repairRequest.getText(),dropdown.getValue().toString());
		try {
			SendMessage(rr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
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

	private void SendMessage(RepairRequest rr) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		String replyQueueName = channel.queueDeclare().getQueue();
		AMQP.BasicProperties props = new AMQP.BasicProperties
				.Builder()
				.correlationId(corrId)
				.replyTo(replyQueueName)
				.build();
		channel.queueDeclare("RepairRequest", false, false, false, null);
		channel.basicPublish("", "RepairRequest", props, SerializationUtils.serialize(rr));
		channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				if (properties.getCorrelationId().equals(corrId)) {
					RepairReply repr = SerializationUtils.deserialize(body);
					repairReplies.getItems().add(repr);
				}
			}
		});
		}



}
