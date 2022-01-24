package entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table (name = "Auditing", schema = "telcoServiceDB")
public class Auditing {
	@Id 
	private String username; 
	private String email; 
	private double value ;
	@Temporal (value = TemporalType.TIME)
	private Date time; 
	@OneToOne @PrimaryKeyJoinColumn (name = "username")
	private Consumer insolventConsumer;
	
	public Auditing() {	}
	
	public String getUsername() {
		return username;
	}
	 
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Consumer getInsolventConsumer() {
		return insolventConsumer;
	}
	
}
