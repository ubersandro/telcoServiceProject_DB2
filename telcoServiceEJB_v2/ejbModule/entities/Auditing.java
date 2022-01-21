package entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
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
	public Auditing(double value, Date time, Consumer insolventConsumer) {
		this.value = value;
		this.time = time;
		this.insolventConsumer = insolventConsumer;
	}
	public String getUsername() {
		return username;
	}
	//@TODO review setters and getters -> maybe they are just not necessary 
//	public void setUsername(String username) {
//		this.username = username;
//	}
	public String getEmail() {
		return email;
	}
//	public void setEmail(String email) {
//		this.email = email;
//	}
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
	
	public void setInsolventConsumer(Consumer insolventConsumer) {
		this.insolventConsumer = insolventConsumer;
		username = insolventConsumer.getUsername(); 
		email = insolventConsumer.getEmail(); 
		//value ???? 
	}
	
	
	
}
