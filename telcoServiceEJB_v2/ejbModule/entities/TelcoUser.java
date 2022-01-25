package entities;

import java.io.Serializable;

import javax.persistence.*;

@NamedQuery(name = "TelcoUser.checkCredentials", query = "SELECT t FROM TelcoUser t  WHERE t.username = :username and t.password = :password")   

@Entity 
@Table (name ="TelcoUser", schema = "telcoServiceDB")
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE") //defaults to a String 
public class TelcoUser implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String username;
	private String email;
	private String password;
	
	public TelcoUser() {}

	public TelcoUser(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
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
	

	
	
	
	@Override
	public String toString() {
		return  username ;
	}
}
