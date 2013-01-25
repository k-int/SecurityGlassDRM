package com.k_int.sgdrm

class JobStatus {

    String              name;
	String				description;
	Boolean				active;
	Date				lastRunTime;
	
    static constraints = {
		lastRunTime nullable: true
		description nullable: true
    }
}
