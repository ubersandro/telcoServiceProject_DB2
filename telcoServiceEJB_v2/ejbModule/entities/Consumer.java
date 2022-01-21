package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



@NamedQuery(name = "Consumer.checkCredentials", query = "SELECT c FROM Consumer c  WHERE c.username = ?1 and c.password = ?2")  
@Entity 
@Table (name = "Consumer", schema = "telcoServiceDB")
public class Consumer implements Serializable{
	private static final long serialVersionUID = -6191241543634746489L;
	@Id
	private String username;
	@Enumerated(EnumType.ORDINAL)
	private UserStatus status; // default SOLVENT (0) otherwise INSOLVENT (1)
	private int counter; // rejected orders
	private String email;
	private String password;

	
	
	
	@Override
	public String toString() {
		return  username ;
	}

	public Consumer() {}

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
