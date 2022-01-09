package entities;

import javax.persistence.Id;
/**
 * Class modeling the Consumer of the telco service. 
 * If the user is Insolvent , then a namedQuery has to be executed to fetch all the REJECTED orders submitted by the specific user. 
 * @author ubersandro
 *
 */
public class Consumer {
	@Id 
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
