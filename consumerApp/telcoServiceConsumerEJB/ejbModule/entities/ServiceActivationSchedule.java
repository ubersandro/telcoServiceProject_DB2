package entities;
import javax.persistence.*;

/**
 *	Class which models a ServiceActivationSchedule. It is of no use for the application except for
 *	demo and debugging purposes. 
 * @author ubersandro
 *	Prior to decide what to do with fetching, decide what is the use of this object. 
 */
@Entity (name="ServiceActivationSchedule")
public class ServiceActivationSchedule {
	@Id 
	private int orderID; 
	
	@OneToOne @JoinColumn(name="orderID") //owner of the relationship 
	private Order order ; 
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order; //there is no need to act on the other entity (ORDER) 
	} 

	public ServiceActivationSchedule() {
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int id) {
		this.orderID = id;
	}

	
}
