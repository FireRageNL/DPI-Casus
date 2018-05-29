package Repair.Models;

import java.io.Serializable;

public class CompanyReply implements Serializable {

	public Double price;

	public String companyName;

	public String repairDescription;

	public String replyQueue;

	public String correlationId;


	public CompanyReply(Double price, String companyName, String repairDescription, String replyQueue, String correlationId) {
		this.price = price;
		this.companyName = companyName;
		this.repairDescription = repairDescription;
		this.replyQueue = replyQueue;
		this.correlationId = correlationId;
	}

	@Override
	public String toString() {
		return "Company " + companyName + "Will do these repairs: " + repairDescription + " for this price: "+ price.toString();
	}
}
