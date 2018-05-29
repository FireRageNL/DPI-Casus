package Repair.Models;

import java.io.Serializable;

public class RepairRequest implements Serializable {

	public RepairRequest(String content, String repairType) {
		this.content = content;
		this.repairType = repairType;
	}

	public String content;

	public String repairType;

	@Override
	public String toString(){
		return content + " Repair Type: "+ repairType;
	}
}
