package Repair.Models;

import java.io.Serializable;

public class RepairReply implements Serializable {

	public double rate;

	public String explanation;

	public String companyName;

	public RepairReply(double rate, String explanation, String companyName) {
		this.rate = rate;
		this.explanation = explanation;
		this.companyName = companyName;
	}

	@Override
	public String toString(){
		return companyName + " Will do the repairs for this price: " + rate;
	}
}
