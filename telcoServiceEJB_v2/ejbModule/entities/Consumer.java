package entities;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



@NamedQuery(name = "Consumer.checkCredentials", query = "SELECT c FROM Consumer c  WHERE c.username = ?1 and c.password = ?2")  
@Entity 
@Table (name = "Consumer", schema = "telcoServiceDB")
@DiscriminatorValue("CONS")
public class Consumer extends TelcoUser{

	private static final long serialVersionUID = 1L;
	
	@Enumerated(EnumType.ORDINAL)
	private UserStatus status  = UserStatus.SOLVENT;; // default SOLVENT (0) otherwise INSOLVENT (1)
	private int counter = 0; // rejected orders TODO CHECK IF IT WORKS 
	

	public Consumer() {}
	
	public Consumer(String username, String email, String password) {
		super(username, email, password);
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



}
