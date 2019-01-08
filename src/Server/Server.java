package Server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ServerSocket listener;
		
		int clientid=0;
		
		try 
		{
			 listener = new ServerSocket(10000,10);
			 
			 while(true)
			 {
				System.out.println("Main thread listening for incoming new connections");
				Socket newconnection = listener.accept();
				
				System.out.println("New connection received and spanning a thread");
				Connecthandler t = new Connecthandler(newconnection, clientid);
				clientid++;
				t.start();
			 }
			
		} 
		
		catch (IOException e) 
		{
			System.out.println("Socket not opened");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


class Connecthandler extends Thread
{
	//class variables
	
	Socket individualconnection;
	int socketid;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	String readMessage;
	String passMessage;
	
	boolean loggedIn;
	
	Date userDate = new Date();
	
	LocalDateTime dt = LocalDateTime.now();
	
	ArrayList<Employee> empList = new ArrayList<>();
	
	ArrayList<BugRecord> bugRecords = new ArrayList<>();
	
	ArrayList<BugRecord> assignedRecords = new ArrayList<>();
	
	HashMap<String, Employee> loginMap = new HashMap<>();
	
	HashMap<Integer, BugRecord> bugMap = new HashMap<>();
	
	HashMap<BugRecord, Employee> employeeBugRecordMap = new HashMap<>();
	
	int selectedBugId, selectedOption;
	
	Employee emp;
	
//-------------------------------------------------------

	//constructor
	
	public Connecthandler(Socket s, int i)
	{
		individualconnection = s;
		socketid = i;
	}

//-------------------------------------------------------

	//send message to client 
	
	private void sendMessage(String msg){
		
		try {
			
			out.writeObject(msg);
			out.flush();
			System.out.println("message from server - " + msg);
		
		} catch (IOException e) {
	
			e.printStackTrace();
		}
	}//sendMessage
	
//-------------------------------------------------------

	//send collection size to client
	
	private void sendCollectionSize(int size){
		
		Integer sendNum = new Integer(size);
		
		try {
			
			out.writeObject(sendNum);
			out.flush();
			System.out.println("message from server collection size is - " + sendNum);
		
		} catch (IOException e) {
			e.printStackTrace();
		}//catch
		
	}//sendCollectionSize
	
//-------------------------------------------------------
	//send login status to client
	
	private void sendLoginStatus(boolean login){
		
		Boolean b = new Boolean(login);
		
		try {
			out.writeObject(b);
			out.flush();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}//sendLogin
	
//-------------------------------------------------------
	
	//write registered users to file after run method returns
	//synchronized one thread write to file at a time
	
	private synchronized void writeRegisteredUsers(){
		
		//write employees to file
		File users = new File("Files/RegisteredUsers.txt");
		
		FileWriter fr;
		String line;
		
		try {
			
			fr = new FileWriter(users);
			
			BufferedWriter br = new BufferedWriter(fr);
			
			//loop for all employees write to file
			for(Employee e: empList){
				
				line = e.getEmpName() + " " + e.getEmpId() + " " + e.getEmpEmail() + " " + e.getEmpDept() + "\n";
				
				br.write(line);
			}//for
			
			//close buffered writer
			br.close();
		
		} 
		catch(FileNotFoundException e){
			
			e.printStackTrace();
		}
		catch(IOException e){
			
			e.printStackTrace();
		}//catch
			
	}//writeUsers
	
//-------------------------------------------------------
	//write bug records to file after run method returns
	//synchronized one thread write to file at a time
	
	private synchronized void writeBugRecords(){
		
		//write bug records
		File bugFile = new File("Files/BugRecords.txt");
		
		FileWriter fr;
		String line;
		
		try {
			
			fr = new FileWriter(bugFile);
			
			BufferedWriter br = new BufferedWriter(fr);
			
			//loop for all bug records & write to file
			for(BugRecord b: bugRecords){
				
				line = b.getBugId() + " " + b.getApplicationName() + " " + b.getDateStamp() + " " + b.getPlatform() + " " + b.getBugDescription() + " " + b.getStatus() + "\n";
				
				br.write(line);
			}//for
			br.close();
		
		} 
		catch(FileNotFoundException e){
	
			e.printStackTrace();
		} 
		catch(IOException e){
			
			e.printStackTrace();
		}//catch
		
	}//writeRecords
	
//-------------------------------------------------------
	//load bug records from file
	//synchronized one thread read from file at a time
	
	private synchronized void loadBugRecords(){
		
		File bugs = new File("Files/BugRecords.txt");
		
		bugRecords = new ArrayList<>();
		bugMap = new HashMap<>();
		
		FileReader fr;
		String line;
		String date;
		
		try {
			
			fr = new FileReader(bugs);
			
			BufferedReader br = new BufferedReader(fr);
			
			//loop for contents of file
			while((line = br.readLine()) != null){
				
				//new bug record
				BugRecord record = new BugRecord();
				
				//split whitespace from line
				String[] bugDetails = line.split(" ");
				
				String[] description;
				String appName = "";
				String desc = "";
				
				//control variable
				int index = 1;
				
				//set bug record id
				record.setBugId(Integer.parseInt(bugDetails[0]));
				System.out.println("bug id - " + bugDetails[0]);
				
				//append to app name until date stamp read 
				while(bugDetails[index].length() < 20){
					appName += bugDetails[index] + " ";
					
					//increase index for string array index
					index++;
				
				}//while
				
				//set bug record app name
				record.setApplicationName(appName.trim());
				
				//set bug record date stamp
				record.setDateStamp(bugDetails[index].trim());
				
				//set bug record platform 
				record.setPlatform(bugDetails[index + 1].trim());
				
				//loop over description until last index which is status variable
				for(int i = index + 2; i < bugDetails.length - 1; i++){
					
					desc += bugDetails[i] + " ";
				
				}//for
				
				//set bug record description
				record.setBugDescription(desc.trim());
				
				//set bug record status last index in array
				record.setStatus(bugDetails[bugDetails.length - 1].trim());
				
				//add to list & map collections
				bugRecords.add(record);
				bugMap.put(record.getBugId(), record);
				
			}//while
			
			//close buffered reader
			br.close();
		
		} 
		catch(FileNotFoundException e){
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}//catch
	
	}//loadBugRecords
	
//-------------------------------------------------------
	//load registered users from file
	//synchronized one thread read from file at a time
	
	private synchronized void loadUsers(){
		
		File users = new File("Files/RegisteredUsers.txt");
		
		loginMap = new HashMap<>();
		empList = new ArrayList<>();
		
		FileReader fr;
		String line;
		
		try {
			
			fr = new FileReader(users);
			
			BufferedReader br = new BufferedReader(fr);
			
			//loop for file contents
			while((line = br.readLine()) != null){
				
				//string array from line split whitespace
				String[] empDetails = line.split(" ");
				
				//add new employee to list
				empList.add(new Employee(empDetails[0].trim() + " " + empDetails[1].trim(), empDetails[2].trim(), empDetails[3].trim(), empDetails[4].trim()));
				
				//add to login map
				loginMap.put(empDetails[2], new Employee(empDetails[0].trim() + " " + empDetails[1].trim(), empDetails[2].trim(), empDetails[3].trim(), empDetails[4].trim()));
				
			}//while
			
			//close reader
			br.close();
		
		} 
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}//loadUsers
	
//-------------------------------------------------------

	//check emp id exists for login
	
	private boolean validateEmpId(String id){
		
		return loginMap.containsKey(id);
		
	}//validateId

//-------------------------------------------------------

	//check if email exists for login
	
	private boolean validateEmpEmail(String email){
		
		for(Employee e: empList){
			
			if(e.getEmpEmail().equals(email)){
				return true;
			}//if
			
		}//for
		
		return false;
	}//validateEmail
	
//-------------------------------------------------------
	//read already assigned bugs from file
	//synchronized one thread read from file at a time
	
	private synchronized void loadAssignedBugs(){
		
		//get file
		File assignedBugs = new File("Files/AssignedBugs.txt");
		
		FileReader fr;
		String line;
		
		assignedRecords = new ArrayList<>();
		employeeBugRecordMap = new HashMap<>();
		
		try {
			
			fr = new FileReader(assignedBugs);
			
			BufferedReader br = new BufferedReader(fr);
			
			//loop for file contents
			while((line = br.readLine()) != null){
				
				String[] ids = line.split(" ");
				
				//if employee id exists
				if(loginMap.containsKey(ids[0].trim()))
				{
					//add to assigned records list & map collections
					assignedRecords.add(bugMap.get(Integer.parseInt(ids[1].trim())));
					employeeBugRecordMap.put(bugMap.get(Integer.parseInt(ids[1].trim())), loginMap.get(ids[0].trim()));
					
					System.out.println("Employee id" + ids[0] + " added Bug Id: " + ids[1] + " added");
				}//if
				
			}//while
			
			//close reader
			br.close();
		
		} 
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}//catch
		
	}//loadAssignedBugs
//-------------------------------------------------------
	//write assigned records to file
	//synchronized one thread write to file at a time
	
	private synchronized void writeAssignedBugRecords(){
		
		//write bug records
		File bugFile = new File("Files/AssignedBugs.txt");
		
		FileWriter fr;
		String line;
		
		try {
			
			fr = new FileWriter(bugFile);
			
			BufferedWriter br = new BufferedWriter(fr);
			
			//loop for assigned employees in map
			for(BugRecord b: assignedRecords){
				
				//format line
				line = employeeBugRecordMap.get(b).getEmpId() + " " + b.getBugId() + "\n";
				
				System.out.println("Written to Assigned Bug Records - " + line);
				//write to file
				br.write(line);
			}//for
			
			//close reader
			br.close();
		
		} catch (FileNotFoundException e) {
	
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
				
	}//writeAssignedBugRecords
	
//-------------------------------------------------------

	
	private void registerEmployee(){
		
	try {
		//new employee
		emp = new Employee();
		
		//send message to client
		sendMessage("Register new user - Enter Employee Details");
		sendMessage("Please Enter Full Name: ");
		
		//read & set employee name from client
		readMessage = (String)in.readObject();
		emp.setEmpName(readMessage);
		
		//send & set employee id from client
		sendMessage("Please Enter Employee ID: ");
		readMessage = (String)in.readObject();
		emp.setEmpId(readMessage);
		
		//send & set employee email from client
		sendMessage("Please Enter E-mail: ");
		readMessage = (String)in.readObject();
		emp.setEmpEmail(readMessage);
		
		//send & set employee department from client
		sendMessage("Please Enter Employee Dept: ");
		readMessage = (String)in.readObject();
		emp.setEmpDept(readMessage);
		
		//check employee doesn't already exist
		if(validateEmpId(emp.getEmpId()) || validateEmpEmail(emp.getEmpEmail())){

			//failed - send message
			sendMessage("Register Failed Employee Email or Employee Id already exists\n Try again...");
		}
		else{
			
			//success - send message
			sendMessage("Register Successful you are now Registered to Bug System");
			
			//syncronize adding employee to collections
			synchronized (empList) {
				empList.add(emp);
				loginMap.put(emp.getEmpId(), emp);
			}//synchronized
			
			//call synchronized method
			writeRegisteredUsers();
		
		}//if
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}//catch
		
	}//registerEmployee

//-------------------------------------------------------
	
	private void loginEmployee()
	{
	  try{
		  
		
		//new employee	
		emp = new Employee();
		
		//send login message
		sendMessage("Login Enter Employee Details");
		
		//set email address for login
		sendMessage("Please Enter E-mail: ");
		readMessage = (String)in.readObject();
		emp.setEmpEmail(readMessage);
		
		//set employee id for login
		sendMessage("Please Enter Employee ID: ");
		readMessage = (String)in.readObject();
		emp.setEmpId(readMessage);
		
		//check both email & id valid
		if(validateEmpEmail(emp.getEmpEmail()) && validateEmpId(emp.getEmpId())){
			
			//set employee
			emp = loginMap.get(emp.getEmpId());
			//change login status
			loggedIn = true;
			
			//send login status & message
			sendMessage("Login Successfull - Hello " + emp.getEmpName() + " Welcome back to Bug Record System");
			sendLoginStatus(loggedIn);
		}
		else{
			//failed login send status & message
			sendMessage("Login failed E-mail or Employee Id not correct!\nPlease Try again...");
			sendLoginStatus(loggedIn);
		}//if
		
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}//catch
	
	}//loginEmployee
//-------------------------------------------------------	
	
	private void addBugRecord(){
		
		try{
			//new bug record
			BugRecord bug = new BugRecord();
			
			//check if first bug to be added
			if(bugRecords.size() == 0){
				//set bug id
				bug.setBugId(1);
			}
			else{
				
				//set bug id 
				int id = bugRecords.size() + 1;
				bug.setBugId(id);
			}//if
			
			//send & set bug record app name
			sendMessage("Employee " + emp.getEmpId() + "\nAdding Bug Record at " + userDate.toString() + 
						"\nEnter Application name: ");
			readMessage = (String)in.readObject();
			bug.setApplicationName(readMessage);
			
			//send & set bug record platform
			sendMessage("\nEnter Platform name (E.g Windows, Linux, MacOS): ");
			readMessage = (String)in.readObject();
			bug.setPlatform(readMessage);
			
			//send & set bug record problem description
			sendMessage("\nEnter Bugs problem Description: ");
			readMessage = (String)in.readObject();
			bug.setBugDescription(readMessage);
			
			//send & set bug record status
			sendMessage("\nStatus can be - (Open, Assigned, Closed)\nEnter Status: ");
			readMessage = (String)in.readObject();
			bug.setStatus(readMessage);
			
			//set bug record date stamp
			bug.setDateStamp(dt.toString());
			
			//synchronize adding bug to list & map collection
			synchronized (bugRecords) {
				bugRecords.add(bug);
				bugMap.put(bug.getBugId(), bug);
			}//synchronized
			
			//call synchronized method
			writeBugRecords();
			
		} 
		catch(ClassNotFoundException e1) {
			e1.printStackTrace();
		} 
		catch(IOException e1) {
			e1.printStackTrace();
		}//catch
		
	}//addBugRecord
	
//-------------------------------------------------------
	
	private void assignBugRecordToEmployee(){
		
		try{
			
			//send collection size to client
			sendCollectionSize(bugRecords.size());
			
			//send message to client
			sendMessage("Assign Bug Record to Registered user\nSelect from listed Bug Id's: ");
			
			//loop for size sent to client
			for(int i = 0; i < bugRecords.size(); i++){
				
				sendMessage(bugRecords.get(i).getBugId() + "." + bugRecords.get(i).getApplicationName() + "- Platform: " + bugRecords.get(i).getPlatform() + "\nDescription: " + bugRecords.get(i).getBugDescription() + "\n");
			}//for
			
			//send & read from client
			sendMessage("Enter from Numbers listed to assign Bug ID: ");
			readMessage = (String)in.readObject();
			
			//set selected id from client
			selectedBugId = Integer.parseInt(readMessage);
			
			//send collection size to client
			sendCollectionSize(empList.size());
			
			//message to client
			sendMessage("Enter from Employee list employee to assign Bug Record too: ");
			
			//loop for size sent to client
			for(int i = 0; i < empList.size(); i++){
				
				sendMessage(empList.get(i).getEmpId());
			}//for
			
			//send & read from client
			sendMessage("Enter Listed Employee Id to assign Bug id " + selectedBugId + " too: ");
			readMessage = (String)in.readObject();
			
			//set employee & bug record to assign
			Employee assignEmployee = loginMap.get(readMessage);
			BugRecord assignBug = bugMap.get(selectedBugId);
			assignBug.setStatus("Assigned");
			
			//synchronize adding to list & map collection
			synchronized (assignedRecords) {
				assignedRecords.add(assignBug);
				employeeBugRecordMap.put(assignBug, assignEmployee);
			}//synchronized
			
			//call synchronized method
			writeBugRecords();
			writeAssignedBugRecords();
			
			//send message to client
			sendMessage("Employee " + employeeBugRecordMap.get(assignBug).getEmpId() + " is assigned to Bug Record ID: " + assignBug.getBugId());
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}//catch
		
	}//assignBugToEmployee
	
//-------------------------------------------------------

	private synchronized void listBugRecordsNotAssigned(){
		
	//send collection size to client
	sendCollectionSize(bugRecords.size());
	
	//send message to client
	sendMessage("\nAll Bug Records not assigned to any Developers:");
	
	//loop for collection size
	for(int i = 0; i < bugRecords.size(); i++){
		
		//skip if bug is assigned to employee
		if(!bugRecords.get(i).getStatus().equalsIgnoreCase("assigned"))
		{
			//send message to client
			sendMessage("\n\nID: " + bugRecords.get(i).getBugId() + " - App Name: " + bugRecords.get(i).getApplicationName() + 
						"\nPlatform - " + bugRecords.get(i).getPlatform() + " - Description: " + bugRecords.get(i).getBugDescription() + 
						"\nDate Added: " + bugRecords.get(i).getDateStamp() + " Status: " + bugRecords.get(i).getStatus() + "\n");
		}
		else
		{
			sendMessage("continue");
		}//if
		
	}//for
	
}//listBugsNotAssigned
	
//-------------------------------------------------------
private synchronized void listAllBugRecords(){
	
	//send collection size to client
	sendCollectionSize(bugRecords.size());
	
	//send message to client
	sendMessage("All Bug Records in System:");
	
	//loop for collection size sent
	for(int i = 0; i < bugRecords.size(); i++){
		
		sendMessage("ID: " + bugRecords.get(i).getBugId() + " - App Name: " + bugRecords.get(i).getApplicationName() + 
				"\nPlatform - " + bugRecords.get(i).getPlatform() + " - Description: " + bugRecords.get(i).getBugDescription() + 
				"\nDate Added: " + bugRecords.get(i).getDateStamp() + " Status: " + bugRecords.get(i).getStatus() + "\n");
	}//for

}//listAllBugRecords

//-------------------------------------------------------

	private void updateBugRecordMenu(){
	
		try{
			//send collection size to client
			sendCollectionSize(bugRecords.size());
			
			//send message to client
			sendMessage("All Bug Records Enter ID of Record to update");
			
			//loop for collection size sent to client
			for(int i = 0; i < bugRecords.size(); i++){
				
				sendMessage("ID: " + bugRecords.get(i).getBugId() + " - App Name: " + bugRecords.get(i).getApplicationName() + "\nStatus: " + bugRecords.get(i).getStatus() + "\n");
			}//for
			
			//send & read from client
			sendMessage("Enter from listed Bug Id's - Bug Status to update: ");
			readMessage = (String)in.readObject();
			
			//set selected bug id
			selectedBugId = Integer.parseInt(readMessage);
			
			sendMessage("Update Bug ID - " + selectedBugId + " - App Name: " + bugMap.get(selectedBugId).getApplicationName() + 
						"\n1. Update Status\n2. Append to Bug problem description\n3. Change assigned Engineer \nEnter option number: ");
			
			//set selected menu option
			selectedOption = (int)in.readObject();
			
			//switch from menu options
			switch(selectedOption){
			
			case 1:
				//send message to update bug status
				sendMessage("Update Status - " + bugMap.get(selectedBugId).getBugId() + " - App Name: " + bugMap.get(selectedBugId).getApplicationName() + 
						    "\n Current status is " + bugMap.get(selectedBugId).getStatus() + 
						    "\nStatus must be open, closed or assigned\nEnter new Status: ");
				
				//read new status
				readMessage = (String)in.readObject();
				
				//set new status
				synchronized (bugMap) {
					bugMap.get(selectedBugId).setStatus(readMessage);
				}//sync
				
				//call synchronized method
				writeBugRecords();
				
				//send message showing status updated
				sendMessage("Bug ID: " + bugMap.get(selectedBugId).getBugId() + " - App Name: " + bugMap.get(selectedBugId).getApplicationName() + " Status set to " + bugMap.get(selectedBugId).getStatus());
				break;
				
			case 2:
				//send message to append to bug record description
				sendMessage("Append to Bug problem description - " + bugMap.get(selectedBugId).getBugId() + " - App Name: " + bugMap.get(selectedBugId).getApplicationName() + 
							"\nDescription: " + bugMap.get(selectedBugId).getBugDescription() + 
							"\nEnter text to append to Description: ");
				
				//read text to append
				readMessage = (String)in.readObject();
				
				//append to bug record description
				synchronized (bugMap) {
					bugMap.get(selectedBugId).setBugDescription(bugMap.get(selectedBugId).getBugDescription() + " - " + readMessage);
				}//sync
				
				//call synchronized method
				writeBugRecords();
				
				//send updated description 
				sendMessage("Bug ID: " + bugMap.get(selectedBugId).getBugId() + " - App Name: " + bugMap.get(selectedBugId).getApplicationName() + 
							"\nNew Description after appending: " + bugMap.get(selectedBugId).getBugDescription());
				break;
				
			case 3:
				//send collection size to client
				sendCollectionSize(empList.size());
				
				//check whether bug already has assigned engineer
				if(employeeBugRecordMap.containsKey(bugMap.get(selectedBugId))){
					
					//send if has engineer
					sendMessage("Change Bug Record Engineer - Bug ID: " + bugMap.get(selectedBugId).getBugId() + " Current Engineer: " + employeeBugRecordMap.get(bugMap.get(selectedBugId)).getEmpName()
							+ "\nEnter from Employee Id's Listed to assign new Engineer: ");							
				}
				else
				{
					//send if no engineer
					sendMessage("Change Bug Record Engineer - Bug ID: " + bugMap.get(selectedBugId).getBugId() + " hasn't been Assigned an Engineer yet continue to Add one now"
							+ "\nEnter from Employee Id's Listed to assign new Engineer: \n");
				}//if
				
				//loop for size sent to client
				for(int i = 0; i < empList.size(); i++){
					
					sendMessage("Employee ID: " + empList.get(i).getEmpId() + " - Employee Name: " + empList.get(i).getEmpName());
				}//for
				
				//send & read from client
				sendMessage("Enter Employee ID Listed you wish to assign Bug ID: " + bugMap.get(selectedBugId).getBugId() + "\nDescription: " + bugMap.get(selectedBugId).getBugDescription() + "\nStatus: " + bugMap.get(selectedBugId).getStatus() + "\n\nEnter Employee ID: ");
				readMessage = (String)in.readObject();
				
				synchronized (bugMap) {
					
					//if bug status not set to assigned then set to assigned
					if(!bugMap.get(selectedBugId).getStatus().equalsIgnoreCase("assigned")){
					
						bugMap.get(selectedBugId).setStatus("Assigned");
					}//if
				
					//bug assigned to new engineer remove if already in map
					if(employeeBugRecordMap.containsKey(bugMap.get(selectedBugId)))
					{
						employeeBugRecordMap.remove(bugMap.get(selectedBugId));
					}//if
					
					//put Bug & new Engineer in map collection
					employeeBugRecordMap.put(bugMap.get(selectedBugId), loginMap.get(readMessage));
				
				}//sync
				
				//call synchronized method
				writeAssignedBugRecords();
				
				//send message for assigned bug & employee to client
				sendMessage("Employee ID: " + loginMap.get(readMessage).getEmpId() + " - Name: " + loginMap.get(readMessage).getEmpName() + 
							"\nAssigned Bug ID: " + bugMap.get(selectedBugId).getBugId() + " - Description: " + bugMap.get(selectedBugId).getBugDescription());
				break;
				
			}//switch
			
		} 
		catch(ClassNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch(IOException e1) {
			e1.printStackTrace();
		}//catch
		
		
	}//updateBugRecordMenu
//-------------------------------------------------------

	public void run()
	{
		
		try 
		{
			//establish connection
			out = new ObjectOutputStream(individualconnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(individualconnection.getInputStream());
			
			System.out.println("Connection"+ socketid+" from IP address "+individualconnection.getInetAddress());
			//Commence the conversation with the client......
			
			//set login status
			loggedIn = false;
			
		do{
			
			
			//load registered users
			loadUsers();
			
			//send & read from client
			sendMessage("Client/Server Bug Tracking System\n Enter from listed options:\n 1. Register User to system\n 2. Login Registered user to system \n\n Enter option: ");
			readMessage = (String)in.readObject();
			
			//check for option selected
			if(readMessage.equalsIgnoreCase("1")){
				//register new employee
				registerEmployee();
			}
			else if(readMessage.equalsIgnoreCase("2")){
				//login registered employee
				loginEmployee();
			}//if
			
			//check if logged in
			if(loggedIn){
				
				do{
					//load bug records from file
					loadBugRecords();
					
					//load assigned bug records
					loadAssignedBugs();
					
					//send to client
					sendMessage("Employee " + emp.getEmpId() + " - " + emp.getEmpName() + 
								"\nEnter option from system list\n3. Add Bug Record\n4. Assign Bug to Registered user\n"
								+ "5. List Bug Records not assigned to any Developers\n6. List all Bug Records in system\n7. Update Bug Record\n\nEnter option: ");
					//read from client
					readMessage = (String)in.readObject();
					System.out.println(readMessage);
					
					//switch option selected
					switch(readMessage){
						
					case "3":
						//add bug record
						addBugRecord();
						break;
						
					case "4":
						//assign bug record to registered user
						assignBugRecordToEmployee();
						break;
						
					case "5":
						//list all bug records not assigned to user
						listBugRecordsNotAssigned();
						break;
					
					case "6":
						//list all bug records in system
						listAllBugRecords();
						break;
						
					case "7":
						//update bug record
						updateBugRecordMenu();
						break;
						
					}//switch
					
					//send continue message to client
					sendMessage("Would you like to continue?\nPress Y to stay Logged in and N to Log out\n");
					readMessage = (String)in.readObject();
					
				}while(readMessage.equalsIgnoreCase("y"));
				
			}//if
			
			//send continue message to client
			sendMessage("Press Y to continue and N to Exit\n");
			readMessage = (String)in.readObject();
			
		}while(readMessage.equalsIgnoreCase("y"));
		
		
		}//try
		catch (IOException io) 
		{
			io.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}//catch
		
		finally
		{
			try 
			{
				//close connections
				out.close();
				in.close();
				individualconnection.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}//catch
			
		}//finally
	
	}//run
	
//-------------------------------------------------------	

}//class

