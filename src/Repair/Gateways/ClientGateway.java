package Repair.Gateways;

import Repair.Client.ClientController;
import Repair.Models.RepairReply;
import Repair.Models.RepairRequest;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ClientGateway {

	private ClientController _controller;
	final String corrId = UUID.randomUUID().toString();
	private Connection _connection;


	public ClientGateway(ClientController controller) throws IOException, TimeoutException {
		_controller = controller;
		InitializeConnection();
	}

	private void InitializeConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		_connection = factory.newConnection();
	}

	public void SendMessage(RepairRequest rr) throws IOException {
		Channel channel = _connection.createChannel();
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
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
				if (properties.getCorrelationId().equals(corrId)) {
					RepairReply reply = SerializationUtils.deserialize(body);
					_controller.addReplyToList(reply);
				}
			}
		});
	}
}
