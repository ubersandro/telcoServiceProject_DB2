package entities;
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
public class ServiceActivationSchedule {
	@Id 
	private int orderID; 
	@Temporal(value = TemporalType.DATE)
	private Calendar endDate; 
	@OneToOne @PrimaryKeyJoinColumn(name="orderID") 
	private OrderObject order ; 
	
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	
	public OrderObject getOrder() {
		return order;
	}

	public void setOrder(OrderObject order) {
		this.order = order; 
		this.orderID = order.getId(); 
		endDate = (Calendar) order.getStartingDate().clone();
		endDate.add(Calendar.MONTH, order.getValidityPeriod().getMonths());	
	} 

	public ServiceActivationSchedule() {
	}
	
	public ServiceActivationSchedule (OrderObject o) {
		orderID = o.getId(); 
		order = o; 
		endDate = (Calendar) o.getStartingDate().clone();
		endDate.add(Calendar.MONTH, o.getValidityPeriod().getMonths());	
	}

	public int getOrderID() {
		return orderID;
	}


	
}