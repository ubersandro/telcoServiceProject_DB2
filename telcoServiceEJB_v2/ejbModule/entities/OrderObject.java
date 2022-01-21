package entities;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;

/**
 * Persistent class for the Order table in the database. 
 * @author ubersandro 
 *
 */

@NamedQueries (
		/**
		 * The following namedQuery takes two parameters (a consumer c and a status s). 
		 * @author ubersandro
		 *
		 */
		{@NamedQuery (name = "Order.findOrdersByUserAndStatus",
		query = "SELECT o FROM OrderObject o WHERE o.consumer = :consumer and o.status = :status")})  
@Entity 
public class OrderObject {
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
	private OrderStatus status; //DEFAULT NEWLY-CREATED -> SPECIFY ? 
	
	//Order is the owner of the MANY-TO-ONE relation between Order and Consumer 
	@ManyToOne @JoinColumn (name="consUsername") 
	private Consumer consumer;  
	
	@ManyToOne (fetch=FetchType.LAZY) //because whenever a package is fetched its associated services are fetched as well 
	@JoinColumn(name="packageID")
	private ServicePackage servicePackage; 
	
	@ManyToOne @JoinColumn(name="vpMonths")
	private ValidityPeriod validityPeriod;
	
	@OneToOne (mappedBy = "order") 
	private ServiceActivationSchedule serviceActivationSchedule ;
	
	@ManyToMany (fetch = FetchType.LAZY) //check
	@JoinTable (name="Includes", joinColumns = 
			@JoinColumn(name = "orderID"), 
			inverseJoinColumns = @JoinColumn(name = "productName"))
	private Collection<OptionalProduct> includedOptionalProducts;
	
	public OrderObject(){}

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

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public ServicePackage getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(ServicePackage servicePackage) {
		this.servicePackage = servicePackage;
	}

	public ValidityPeriod getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(ValidityPeriod validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public ServiceActivationSchedule getServiceActivationSchedule() {
		return serviceActivationSchedule;
	}

	public void setServiceActivationSchedule(ServiceActivationSchedule serviceActivationSchedule) {
		this.serviceActivationSchedule = serviceActivationSchedule;
	}

	public Collection<OptionalProduct> getIncludedOptionalProducts() {
		return includedOptionalProducts;
	}

	public void setIncludedOptionalProducts(Collection<OptionalProduct> includedOptionalProducts) {
		this.includedOptionalProducts = includedOptionalProducts;
	}

	public OrderObject(Date time, Calendar date, double totalValue, Calendar startingDate,
			Consumer consumer, ServicePackage servicePackage, ValidityPeriod validityPeriod,
			Collection<OptionalProduct> includedOptionalProducts) {
		this.time = time;
		this.date = date;
		this.totalValue = totalValue;
		this.startingDate = startingDate;
		this.status = OrderStatus.NEWLY_CREATED;
		this.consumer = consumer;
		this.servicePackage = servicePackage;
		this.validityPeriod = validityPeriod;
		this.includedOptionalProducts = includedOptionalProducts;
	} 
	

	
}
