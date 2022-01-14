package entities;

import javax.persistence.*;

/**
 * Employee class models an employee of the telco company.
 * 
 * @author ubersandro
 *
 */
@Entity(name = "Employee")
public class Employee {
	@Id
	private String username;
	private String password;
	private String email;

	public Employee() {
	}

	public Employee(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
