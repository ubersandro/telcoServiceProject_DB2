package entities;
import java.util.Calendar;
import javax.persistence.*;

/**
 *	Class which models a ServiceActivationSchedule. It is of no use for the application except for
 *	demo and debugging purposes. 
 * @author ubersandro
 */

@Entity (name="ServiceActivationSchedule")
public class ServiceActivationSchedule {
	@Id 
	private int orderID; 
	
	@Temporal(value = TemporalType.DATE)
	private Calendar endDate; 
	
	@OneToOne @PrimaryKeyJoinColumn(name="orderID") //owner of the relationship 
	private Order order ; 
	
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

//	public void setOrderID(int id) {
//		this.orderID = id;
//	}
//	
	
}
