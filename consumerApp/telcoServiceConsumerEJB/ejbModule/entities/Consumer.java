package entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQuery;


@Entity (name ="Consumer")
@NamedQuery(name = "Consumer.checkCredentials", query = "SELECT c FROM Consumer c  WHERE c.username = ?1 and c.password = ?2")  
public class Consumer {
	@Id
	private String username;
	@Enumerated(EnumType.ORDINAL)
	private UserStatus status; // default SOLVENT (0) otherwise INSOLVENT (1)
	private int counter; // rejected orders
	private String email;
	private String password;

	
	
	
	public Consumer() {
	}

	/**
	 * Registration is carried out providing email, password and username (KEY).
	 * 
	 **/

	public Consumer(String username, String email, String password) { // registration parameters
		this.username = username;
		this.counter = 0;
		this.email = email;
		this.status = UserStatus.SOLVENT;
		this.password = password;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
