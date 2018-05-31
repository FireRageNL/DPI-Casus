package Repair.Gateways;

import Repair.Company.CompanyController;
import Repair.Models.CompanyReply;
import Repair.Models.CompanyRequest;
import com.rabbitmq.client.*;
import javafx.application.Platform;
import jfxtras.labs.dialogs.MonologFX;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CompanyGateway {

	private Connection _connection;
	private CompanyController _controller;
	private String _companyName;

	public CompanyGateway(CompanyController controller) throws IOException, TimeoutException {
		_controller = controller;
		InitializeConnection();
	}

	public void InitializeConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		_connection = factory.newConnection();
	}

	public void registerRepairChannel(String companyName, String RepairType) throws IOException {
		_companyName = companyName;
		Channel channel = _connection.createChannel();
		channel.exchangeDeclare(RepairType,"fanout");
		String queuename = channel.queueDeclare().getQueue();
		channel.queueBind(queuename,RepairType,"");
		MonologFX monolog = new MonologFX(MonologFX.Type.INFO);
		monolog.setMessage("Registered on "+ RepairType);
		monolog.show();

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
					CompanyRequest cr = SerializationUtils.deserialize(body);
					_controller.setNewAssignment(cr);

			}
		};
		channel.basicConsume(queuename, true, consumer);
	}

	public void sendMessageHandler(CompanyRequest request, Double price, String description) throws IOException, TimeoutException {
		CompanyReply creply = new CompanyReply(price,_companyName,description,request.replyQueue, request.correlationId);
		Channel channel = _connection.createChannel();
		channel.queueDeclare("CompanyReply", false, false, false, null);
		channel.basicPublish("", "CompanyReply", null, SerializationUtils.serialize(creply));
		channel.close();
	}
}
