package Server;

import java.util.Date;

public class BugRecord {

	//class variables
	
	private int bugId;
	
	private String applicationName;
	
	private String platform;
	
	private String dateStamp;
	
	private String bugDescription;
	
//------------------------------------------------------------
	
	private enum Status {
		OPEN("Open"), ASSIGNED("Assigned"), CLOSED("Closed");
		
		private String status;
		
		private String getStatus(){
			return this.status;
		}
		
		private Status(String s){
			this.status = s;
		}
	};
	
//------------------------------------------------------------

	//enum variable
	private Status st;
	
//------------------------------------------------------------

	//constructors
	
	public BugRecord(){
		
	}//BugRecord

	
	public BugRecord(int id, String appName, String pltfrm, String date, String desc, String status){
		this.bugId = id;
		this.applicationName = appName;
		this.platform = pltfrm;
		this.bugDescription = desc;
		this.setStatus(status);
	}//BugRecord
	
//------------------------------------------------------------

	//Getter & Setter Methods

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getBugDescription() {
		return bugDescription;
	}

	public void setBugDescription(String bugDescription) {
		this.bugDescription = bugDescription;
	}
	
	public void setStatus(String s){
		
		if(s.equalsIgnoreCase("open")){
			this.st = Status.OPEN;
			return;
		}
		
		if(s.equalsIgnoreCase("assigned")){
			this.st = Status.ASSIGNED;
			return;
		}
		
		if(s.equalsIgnoreCase("closed")){
			this.st = Status.CLOSED;
			return;
		}
		
	}
	
	public String getStatus(){
		
		return this.st.getStatus();
	}

	public int getBugId() {
		return bugId;
	}

	public void setBugId(int bugId) {
		this.bugId = bugId;
	}

	public String getDateStamp() {
		return dateStamp;
	}

	public void setDateStamp(String dateStamp) {
		this.dateStamp = dateStamp;
	}

//------------------------------------------------------------

}//class