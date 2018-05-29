package Repair.Models;

import java.io.Serializable;

public class CompanyRequest implements Serializable {

	public String replyQueue;

	public String requestDescription;

	public String correlationId;

	public CompanyRequest(String replyQueue, String requestDescription, String correlationId) {
		this.replyQueue = replyQueue;
		this.requestDescription = requestDescription;
		this.correlationId = correlationId;
	}


	@Override
	public String toString() {
		return requestDescription;
	}
}
