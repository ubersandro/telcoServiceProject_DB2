package entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;

/**
 * Persistent class for the Order table in the database.
 * 
 * @author ubersandro
 */

@NamedQueries(

{

		@NamedQuery(name = "Order.findOrdersByUserAndStatus", query = "SELECT o FROM Order o WHERE o.consumer = :consumer and o.status = :status"),

		@NamedQuery(name = "Order.findOrdersByStatus", query = "SELECT o FROM Order o WHERE o.status=:status") })
@Entity
@Table(name = "Order", schema = "telcoServiceDB")
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.TIME)
	private Date time;

	@Temporal(TemporalType.DATE)
	private Calendar date;

	private double totalValue;

	@Temporal(TemporalType.DATE)
	private Calendar startingDate;

	@Enumerated(EnumType.ORDINAL)
	private OrderStatus status = OrderStatus.NEWLY_CREATED;

	// Order is the owner of the MANY-TO-ONE relation between Order and Consumer
	@ManyToOne (fetch = FetchType.LAZY)// and no operation is cascaded. 
	@JoinColumn(name = "consUsername")
	private Consumer consumer;

	@ManyToOne(fetch = FetchType.LAZY) // because whenever a package is fetched its associated services are fetched as
										// well, so it is not advisable to have services fetched with the order 
	@JoinColumn(name = "packageID")
	private ServicePackage servicePackage;

	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "vpMonths")
	private ValidityPeriod validityPeriod;

	@OneToOne(mappedBy = "order", fetch = FetchType.LAZY, 
			orphanRemoval = true, cascade = {CascadeType.REMOVE,CascadeType.DETACH, 
			CascadeType.MERGE, CascadeType.REFRESH})
	// being fetched eagerly by default, given that there is no need to fetch it, fetch type is overridden
	// not persisted because it is created by the DB whenever it is needed 
	private ServiceActivationSchedule serviceActivationSchedule;

	@ManyToMany(fetch = FetchType.LAZY) //craving for efficiency, optional products are lazily fetched. 
	@JoinTable(name = "Includes", joinColumns = @JoinColumn(name = "orderID"),
	inverseJoinColumns = @JoinColumn(name = "productName"), schema = "telcoServiceDB")
	private Collection<OptionalProduct> includedOptionalProducts;

	public Order() {}

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

	@Override
	public String toString() {
		return "Order [time=" + time + ", date=" + date + ", totalValue=" + totalValue + ", startingDate="
				+ startingDate + ", status=" + status + ", consumer=" + consumer + ", servicePackage=" + servicePackage
				+ ", validityPeriod=" + validityPeriod + ", includedOptionalProducts=" + includedOptionalProducts + "]";
	}

	public Order(Date time, Calendar date, double totalValue, Calendar startingDate, Consumer consumer,
			ServicePackage servicePackage, ValidityPeriod validityPeriod,
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
