package Repair.Gateways;

import Repair.Broker.BrokerController;
import Repair.Models.CompanyReply;
import Repair.Models.CompanyRequest;
import Repair.Models.RepairReply;
import Repair.Models.RepairRequest;
import com.rabbitmq.client.*;
import javafx.application.Platform;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BrokerGateway {
	Connection connection;

	private BrokerController _controller;

	public BrokerGateway(BrokerController controller) throws IOException, TimeoutException {
		_controller = controller;
		InitializeConnection();
		InitializeClientToBroker();
		InitializeBrokerToClient();
	}

	public void InitializeConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
	}
	public void InitializeClientToBroker() throws IOException {
		Channel channel = connection.createChannel();
		Channel channel2 = connection.createChannel();
		channel.queueDeclare("RepairRequest", false, false, false, null);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				Platform.runLater(() ->{
					RepairRequest rr =SerializationUtils.deserialize(body);
					_controller.editTextLog(rr.toString());
					CompanyRequest req = new CompanyRequest(properties.getReplyTo(),rr.content, properties.getCorrelationId());
					try {
						channel2.exchangeDeclare(rr.repairType,"fanout");
						channel2.basicPublish( rr.repairType, "", null, SerializationUtils.serialize(req));
					} catch (IOException e) {
						e.printStackTrace();
					}

				});

			}
		};
		channel.basicConsume("RepairRequest", true, consumer);
	}

	public void InitializeBrokerToClient() throws IOException {
		Channel channel = connection.createChannel();
		channel.queueDeclare("CompanyReply", false, false, false, null);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				Platform.runLater(() ->{
					CompanyReply cr = SerializationUtils.deserialize(body);
					_controller.editTextLog(cr.toString());
					AMQP.BasicProperties props = new AMQP.BasicProperties
							.Builder()
							.correlationId(cr.correlationId)
							.build();
					RepairReply rr = new RepairReply(cr.price,cr.repairDescription,cr.companyName);
					try {
						channel.basicPublish( "", cr.replyQueue, props, SerializationUtils.serialize(rr));
					} catch (IOException e) {
						e.printStackTrace();
					}

				});


			}
		};
		channel.basicConsume("CompanyReply", true, consumer);
	}
}
