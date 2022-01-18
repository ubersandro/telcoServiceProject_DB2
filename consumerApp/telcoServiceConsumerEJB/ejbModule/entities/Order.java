package entities;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;

/**
 * Code associated to the Order to distinguish newly created ones from rejected/accepted others. 
 * @author ubersandro
 *
 */

enum OrderStatus { 
	NEWLY_CREATED/*value 0*/, REJECTED/*value 1*/, ACCEPTED/*value 2*/  
}

/**
 * Persistent class for the Order table in the database. 
 * @TODO Cascading and fetching 
 * @author ubersandro
 *
 */

@NamedQueries ({@NamedQuery (name = "Order.findRejectedOrdersByUserID",
		query = "SELECT o FROM `Order` o WHERE o.consumer = :consumerID")})
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
	private Calendar startingDate;
	
	@Enumerated(EnumType.ORDINAL)
	private OrderStatus status; //because under the hood status is an integer
	
	//Order is the owner of the MANY-TO-ONE relation between Order and Consumer 
	@ManyToOne @JoinColumn (name="consUsername") 
	private Consumer consumer;  
	
	@ManyToOne (fetch=FetchType.LAZY) //because whenever a package is fetched its associated services are fetched as well 
	@JoinColumn(name="packageID")
	private int packageID; // servicePackage is fetched together with all the services potentially associable with it. 
	
	@ManyToOne @JoinColumn(name="vpMonths")
	private ValidityPeriod validityPeriod;
	
//	@OneToOne (mappedBy = "orderID") NOT NECESSARY 
//	private ServiceActivationSchedule serviceActivationSchedule ;
	
	@ManyToMany (fetch = FetchType.LAZY) //check
	@JoinTable (name="Includes", joinColumns = 
			@JoinColumn(name = "orderID"), 
			inverseJoinColumns = @JoinColumn(name = "productName"))
	private Collection<OptionalProduct> includedOptionalProducts;
	
	public Order() {} 
	
	public Order(int id, Date time, Calendar date, double totalValue, Calendar startingDate, OrderStatus status,
			Consumer consumer, int packageID, ValidityPeriod validityPeriod,
			Collection<OptionalProduct> includedOptionalProducts) {
		this.id = id;
		this.time = time;
		this.date = date;
		this.totalValue = totalValue;
		this.startingDate = startingDate;
		this.status = status;
		this.consumer = consumer;
		this.packageID = packageID;
		this.validityPeriod = validityPeriod;
		this.includedOptionalProducts = includedOptionalProducts;
	}




	public Consumer getConsumer() {
		return consumer;
	}
	
	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}
	
	public ValidityPeriod getValidityPeriod() {
		return validityPeriod;
	}
	
	public void setValidityPeriod(ValidityPeriod validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
	public Collection<OptionalProduct> getIncludedOptionalProducts() {
		return includedOptionalProducts;
	}
	public void addOptionalProduct(OptionalProduct p) {
		getIncludedOptionalProducts().add(p);  
	}
	
	public void removeOptionalProduct(OptionalProduct p) {
		getIncludedOptionalProducts().remove(p);  
	}
	
//	public ServiceActivationSchedule getServiceActivationSchedule() {
//		return serviceActivationSchedule;
//	}
//	public void setServiceActivationSchedule(ServiceActivationSchedule serviceActivationSchedule) {
//		this.serviceActivationSchedule = serviceActivationSchedule;
//	}
	
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
	public Calendar getStartingDate() {
		return startingDate;
	}
	public void setStartingDate(Calendar startingDate) {
		this.startingDate = startingDate;
	}
	public OrderStatus getStatus() {
		return status;
	}
	
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	
	public int getPackageID() {
		return packageID;
	}
	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}
	
	
}