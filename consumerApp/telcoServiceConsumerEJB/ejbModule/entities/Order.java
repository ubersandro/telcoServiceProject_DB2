package entities;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

enum OrderStatus { 
	NEWLY_CREATED, REJECTED, ACCEPTED  
}
/**
 * Persistent class for the Order table in the database. 
 * @TODO Cascading and fetching 
 * @author ubersandro
 *
 */
@Entity (name="Order")
public class Order {
	@Id 
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private int id; 
	
	@Temporal (TemporalType.TIME)
	private Date time;
	
	@Temporal(TemporalType.DATE)
	private Calendar date; 
	
	private double totalValue;
	
	@Temporal (TemporalType.DATE)
	private Date startingDate;
	
	private OrderStatus status; //@TODO Check mapping 
	
	//Order is the owner of the MANY-TO-ONE relation between Order and Consumer 
	@ManyToOne @JoinColumn (name="consUsername") 
	private String consUsername;  
	
	@ManyToOne (fetch=FetchType.LAZY) //because whenever a package is fetched its associated services are fetched as well 
	@JoinColumn(name="packageID")
	private int packageID; 
	
	@ManyToOne @JoinColumn(name="vpMonths")
	private int vpMonths;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public double getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}
	public Date getStartingDate() {
		return startingDate;
	}
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public String getConsUsername() {
		return consUsername;
	}
	public void setConsUsername(String consUsername) {
		this.consUsername = consUsername;
	}
	public int getPackageID() {
		return packageID;
	}
	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}
	public int getVpMonths() {
		return vpMonths;
	}
	public void setVpMonths(int vpMonths) {
		this.vpMonths = vpMonths;
	} 
	
}
