package entities;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.*;

/**
 *	Class which models a ServiceActivationSchedule. It is of no use for the application except for
 *	demo and debugging purposes. 
 * @author ubersandro
 */
@NamedQueries( 
		{@NamedQuery(name = "ServiceActivationSchedule.findAll", 
					query = "Select s from ServiceActivationSchedule s")}
		) 
@Entity 
@Table(name = "ServiceActivationSchedule" ,schema="telcoServiceDB")
public class ServiceActivationSchedule implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
	private int orderID; 
	@Temporal(value = TemporalType.DATE)
	private Calendar endDate; 
	@OneToOne @PrimaryKeyJoinColumn(name="orderID") 
	private Order order ; 
	
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order; 
		this.orderID = order.getId(); 
		endDate = (Calendar) order.getStartingDate().clone();
		endDate.add(Calendar.MONTH, order.getValidityPeriod().getMonths());	
	} 

	public ServiceActivationSchedule() {
	}
	
	public ServiceActivationSchedule (Order o) {
		orderID = o.getId(); 
		order = o; 
		endDate = (Calendar) o.getStartingDate().clone();
		endDate.add(Calendar.MONTH, o.getValidityPeriod().getMonths());	
	}

	public int getOrderID() {
		return orderID;
	}


	
}
