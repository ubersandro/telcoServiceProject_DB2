package entities;
import javax.persistence.*;

@Entity
public class Employee {
	@Id 
	private String username; 
	//mancano relazioni 
	private String password; 
	private String email; 
	
	public Employee() {
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
