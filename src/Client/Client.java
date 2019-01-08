package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client 
{
	//class variables
	
	private Socket connection;
	
	private String readMessage;
	
	private String passMessage;
	
	private Scanner console;
	
	private String ipaddress;
	
	private int portaddress;
	
	private ObjectOutputStream out;
	
	private ObjectInputStream in;
	
	private int listSize;
	
	private int readSize;
	
	private int numEmployees;
	
	private ArrayList<String> bugIds = new ArrayList<>();
	
	private ArrayList<String> employeeIds = new ArrayList<>();
	
	private boolean loginStatus;
	
	private int updateOption;
	
//-------------------------------------------------------

	//constuctor sets ip address & tcp port
	public Client()
	{
		console = new Scanner(System.in);
		
		System.out.println("Enter the IP Address of the server");
		ipaddress = console.nextLine();
		
		System.out.println("Enter the TCP Port");
		portaddress  = console.nextInt();
		
	}
	
//-------------------------------------------------------

	//main method
	public static void main(String[] args) 
	{
			Client temp = new Client();
			temp.clientapp();
	}

//-------------------------------------------------------

	//send entered number to server
	
	private void sendNumberOption(int num){
		
		Integer n = new Integer(num);
		
		try {
			
			out.writeObject(n);
			out.flush();
			System.out.println("client number option - " + n);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//-------------------------------------------------------

	//send message to server
	
	private void sendMessage(String msg){
		
		try {
			
			out.writeObject(msg);
			out.flush();
			System.out.println("client message - " + msg);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//sendMessage

//-------------------------------------------------------

	//check for valid email address
	
	private boolean isValidEmail(String email){
		
		if(email != null){
			//check for standard email pattern
			Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
			
			Matcher m = p.matcher(email);
			
			//return match
			return m.find();
		}//if
		
		return false;
		
	}//validEmail
	
//-------------------------------------------------------
	
	//read from register employee from server
	
	private void readRegisterEmployee(){
		
		try{
			
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//print enter name
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			//scan name
			console.nextLine();
			passMessage = console.nextLine();
			sendMessage(passMessage);
		
			//print enter id
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			//scan id
			passMessage = console.next();
			sendMessage(passMessage);
		
			//print enter email
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			//scan name
			passMessage = console.next();
			
			//prompt for correct email address
			while(isValidEmail(passMessage) == false){
				
				System.out.println("Wrong Email Format");
				System.out.println(readMessage);
				//scan name
				passMessage = console.next();
				
			}//while
			
			sendMessage(passMessage);
			
			//print enter dept
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			//scan dept
			passMessage = console.next();
			sendMessage(passMessage);
			
			//print success/fail register
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}//registerEmployee
	
//-------------------------------------------------------

	//read login menu from server
	
	private void readLoginEmployee(){
		
		try{
			//print header
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//print enter email
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			//scan email
			passMessage = console.next();
			sendMessage(passMessage);
			
			//print enter id
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			//scan id
			passMessage = console.next();
			sendMessage(passMessage);
			
			//print success/fail login
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//check if success/fail login
			loginStatus = (boolean)in.readObject();
			//System.out.println("login status - " + loginStatus);
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}//loginEmployee
	
//-------------------------------------------------------
	//read add bug record menu from server
	
	private void readAddBugRecord(){
	
		try{
			
		//read bug record name
		readMessage = (String)in.readObject();
		System.out.println(readMessage);
		//scan bug record name
		console.nextLine();
		passMessage = console.nextLine();
		sendMessage(passMessage);
		
		//read bug record platform
		readMessage = (String)in.readObject();
		System.out.println(readMessage);
		//scan bug record platform
		passMessage = console.nextLine();
		sendMessage(passMessage);
		
		//read bug description
		readMessage = (String)in.readObject();
		System.out.println(readMessage);
		//scan description
		passMessage = console.nextLine();
		sendMessage(passMessage);
		
		//read status message
		readMessage = (String)in.readObject();
		System.out.println(readMessage);
		//scan status 
		passMessage = console.next();
		
		//if enter status not open, closed, assigned keep prompting 
		if(!passMessage.equalsIgnoreCase("open") && !passMessage.equalsIgnoreCase("assigned") && !passMessage.equalsIgnoreCase("closed")){
			
			do{
				System.out.println("Wrong status entered");
				System.out.println(readMessage);
				//scan status 
				passMessage = console.next();
				
			}while(!passMessage.equalsIgnoreCase("open") && !passMessage.equalsIgnoreCase("assigned") && !passMessage.equalsIgnoreCase("closed"));
		}//if
		
		sendMessage(passMessage);
		
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}//catch
		
	}//readAddBugRecord
	
//-------------------------------------------------------	
	//read assigned bug record list from server
	
	private void readAssignBugRecord(){
		
		try{
			//new list
			bugIds = new ArrayList<>();
			
			//read collection size from server
			listSize = (int)in.readObject();
			
			//read message
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//loop over list size
			for(int i = 0; i < listSize; i++){
				
				readMessage = (String)in.readObject();
				bugIds.add(Integer.toString(i + 1));
				System.out.println(readMessage);
			}//for
			
			//read & prompt for id
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			passMessage = console.next();
			
			//prompt while not valid
			if(!bugIds.contains(passMessage)){
				
				do{
					
					System.out.println("Wrong key entered to match listed Id's\n" + readMessage);
					passMessage = console.next();
				
				}while(!bugIds.contains(passMessage));
				
			}//if
			
			//send id selected
			sendMessage(passMessage);
			
			//read collection size
			numEmployees = (int)in.readObject();
			
			//read message
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//loop for size read from server
			for(int i = 0; i < numEmployees; i++){
				
				readMessage = (String)in.readObject();
				
				//add id for selection
				employeeIds.add(readMessage);
				
				//format for selection
				System.out.println("Employee " + (i + 1) + " - Id: " + readMessage);
			}//for
			
			//read message & prompt 
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			passMessage = console.next();
			
			//prompt while id not valid
			if(!employeeIds.contains(passMessage)){
				
				do{
					
					System.out.println("Wrong Id entered enter from Id's listed\n" + readMessage);
					passMessage = console.next();
				}while(!employeeIds.contains(passMessage));
			}//if
			
			//send id selected
			sendMessage(passMessage);
			
			//read message
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}//readAssignBugRecord
	
//-------------------------------------------------------

	//read from server bug records not assigned
	
	private void readBugsNotAssigned(){
		
		try{
			//read collection size from server
			readSize = (int)in.readObject();
			
			//read message from server 
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//loop for size read from server
			for(int i = 0; i < readSize; i++){
				
				readMessage = (String)in.readObject();
				
				if(!readMessage.equals("continue"))
				{
					System.out.println(readMessage);
				}
				else
				{
					continue;
				}//if
				
			}//for
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}//readBugsNotAssigned

//-------------------------------------------------------

	//read all bug records from server
	
	private void readAllBugs(){
		
		try{
			//read collection size from server
			readSize = (int)in.readObject();
			
			//read message from server
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//loop for size read from server
			for(int i = 0; i < readSize; i++){
				
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				
			}//for
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}//readAllBugs
	
//-------------------------------------------------------
	
	//read update bug menu from server
	
	private void readUpdateBugMenu(){
		
		try{
			//initialize list for ids
			bugIds = new ArrayList<>();
			
			//read collection size from server
			readSize = (int)in.readObject();
			
			//read message from server
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			
			//loop for size from server
			for(int i = 0; i < readSize; i++){
				
				readMessage = (String)in.readObject();
				//store bug ids for selection
				bugIds.add(Integer.toString(i + 1));
				System.out.println(readMessage);
			}//for
			
			//read & prompt for bug id selected
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			passMessage = console.next();
			
			//prompt while no matches to ids
			if(!bugIds.contains(passMessage)){
				
				do{
					System.out.println("ID doesn't match any ID listed - Enter matching ID:\n" + readMessage);
					passMessage = console.next();
					
				}while(!bugIds.contains(passMessage));
			}//if
			
			//send id selected
			sendMessage(passMessage);
			
			//read for menu option
			readMessage = (String)in.readObject();
			System.out.println(readMessage);
			//scan menu option
			updateOption = console.nextInt();
			
			//prompt while not valid
			if(updateOption < 1 || updateOption > 3){
				
				do{
					System.out.println("Wrong number entered enter number from list\n" + readMessage);
					updateOption = console.nextInt();
					
				}while(updateOption < 1 && updateOption > 3);
			}//if
			
			//send number selected to server
			sendNumberOption(updateOption);
			
			//switch for server option
			switch(updateOption){
			
			case 1:
				//read update status menu from server for selected bug id
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				//scan new status
				passMessage = console.next();
				
				//prompt while not valid
				if(!passMessage.equalsIgnoreCase("open") && !passMessage.equalsIgnoreCase("closed") && !passMessage.equalsIgnoreCase("assigned")){
					
					do{
						System.out.println(readMessage);
						passMessage = console.next();
					}while(!passMessage.equalsIgnoreCase("open") && !passMessage.equalsIgnoreCase("closed") && !passMessage.equalsIgnoreCase("assigned"));
				}//if
				
				//send new status entered
				sendMessage(passMessage);
				
				//read message from server
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				
				break;
				
			case 2:
				//read message from server to append to bug id selected description
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				
				//scan text to append to bug description
				console.nextLine();
				passMessage = console.nextLine();
				sendMessage(passMessage);
				
				//read message from server
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				
				break;
				
			case 3:
				//declare array for employee ids to store
				String[] empIds;
				//initialize arraylist for employee ids
				employeeIds = new ArrayList<>();
				
				//read collection size from server
				readSize = (int)in.readObject();
				//System.out.println("size of assigned bug record list - " + readSize);
				
				//read message from server
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				
				//loop for size read from server
				for(int i = 0; i < readSize; i++){
					//read from server
					readMessage = (String)in.readObject();
					
					//manipulate string to store employee ids
					empIds = readMessage.split(" ");
					employeeIds.add(empIds[2]);
					
					System.out.println(readMessage);
				}//for
				
				//read next message
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				//scan for selected id
				passMessage = console.next();
				
				//prompt while no matching id
				if(!employeeIds.contains(passMessage)){
					
					do{
						System.out.println("Employee ID doesn't exist enter matching ID\n" + readMessage);
						passMessage = console.next();
						
					}while(!employeeIds.contains(passMessage));
				}//if
				
				//send id for engineer change
				sendMessage(passMessage);
				
				//read message from server
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				
				break;
			}//switch
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}//readUpdateBugMenu

//-------------------------------------------------------

	public void clientapp()
	{
		
		try 
		{
			//establish connection
			connection = new Socket(ipaddress,portaddress);
		
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			System.out.println("Client Side ready to communicate");
		
		    /// Client App.	
			do{
				//read & print message from server
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				
				//prompt user input
				passMessage = console.next();
				
				if(!passMessage.equalsIgnoreCase("1") && !passMessage.equalsIgnoreCase("2")){
					
					do{
						
						System.out.println(readMessage);
						//prompt user while not 1 or 2
						passMessage = console.next();
						
					}while(!passMessage.equalsIgnoreCase("1") && !passMessage.equalsIgnoreCase("2"));
				}//if
				
				//send to server
				sendMessage(passMessage);
				
				//check input for register or login employee
				if(passMessage.equalsIgnoreCase("1"))
				{
					readRegisterEmployee();
			
				}
				else if(passMessage.equalsIgnoreCase("2"))
				{
					readLoginEmployee();
					
				}//if
				
				//if login been changed to true continue
				if(loginStatus){
					
					do{
						//read & print message from server 
						readMessage = (String)in.readObject();
						System.out.println(readMessage);
						
						//prompt & send to server
						passMessage = console.next();
						sendMessage(passMessage);
						
						//switch from menu options
						switch(passMessage){
						
							case "3":
								readAddBugRecord();
							break;
							
							case "4":
								readAssignBugRecord();
							break;
								
							case "5":
								readBugsNotAssigned();
							break;
								
							case "6":
								readAllBugs();
							break;
								
							case "7":
								readUpdateBugMenu();
							break;
						
							default:
								break;
								
						}//switch
						
						//read continue option from server
						readMessage = (String)in.readObject();
						System.out.println(readMessage);
						
						passMessage = console.next();
						sendMessage(passMessage);
						
					}while(passMessage.equalsIgnoreCase("y"));
					
				}//if
				
				
				//read continue option
				readMessage = (String)in.readObject();
				System.out.println(readMessage);
				passMessage = console.next();
				sendMessage(passMessage);
				
			}while(passMessage.equalsIgnoreCase("y"));
			
			//close all connections
			out.close();
			in.close();
			connection.close();
			console.close();
			
			
			System.out.println("Connections closed...App finished running");
			System.exit(0);
		} 
		
		catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
		
			e.printStackTrace();
		}
		
	}
	
}
