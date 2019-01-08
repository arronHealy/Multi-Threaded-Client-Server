package Server;

public class Employee {
	
	//class variables
	
	private String empName;
	
	private String empId;
	
	private String empEmail;
	
	private String empDept;
	
//------------------------------------------------------------

	//constructors
	
	public Employee(){
		
	}//Employee
	
	public Employee(String name, String id, String email, String dept){
		
		this.empName = name;
		this.empId = id;
		this.empEmail = email;
		this.empDept = dept;
	}//Employee
	
//------------------------------------------------------------

	//Getter & Setter Methods
	
	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public String getEmpDept() {
		return empDept;
	}

	public void setEmpDept(String empDept) {
		this.empDept = empDept;
	}
	
//------------------------------------------------------------

}//class
