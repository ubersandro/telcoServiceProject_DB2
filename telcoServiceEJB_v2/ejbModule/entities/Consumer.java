package entities;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@NamedQueries({
@NamedQuery(name = "Consumer.checkCredentials", query = "SELECT c FROM Consumer c  WHERE c.username = :username and c.password = :password"),   
@NamedQuery(name = "Consumer.findUserByStatus", query = "SELECT c FROM Consumer c WHERE c.status = :status") 
}) 
@Entity 
@Table (name = "Consumer", schema = "telcoServiceDB")
@DiscriminatorValue("CONS")
public class Consumer extends TelcoUser{

	private static final long serialVersionUID = 1L;
	
	@Enumerated(EnumType.ORDINAL)
	private UserStatus status  = UserStatus.SOLVENT;; 
	

	public Consumer() {}
	
	public Consumer(String username, String email, String password) {
		super(username, email, password);
	}

	
	public UserStatus getStatus() {
		return status;
	}
	
	public boolean isInsolvent() {
		return status.equals(UserStatus.INSOLVENT); 
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}



}
