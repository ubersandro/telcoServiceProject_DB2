package entities;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity 
@Table (name ="Payment", schema = "telcoServiceDB")
public class Payment {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private int paymentId; 
	@OneToOne @JoinColumn (name = "user")
	private Consumer user; 
	@OneToOne @JoinColumn (name = "orderID") 
	private Order order; 
	@Enumerated(EnumType.ORDINAL)
	private PaymentStatus status ; 
	
	private Calendar date; 
	private Date time; 
	private double totalValue ;
	
	
	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

	public Consumer getUser() {
		return user;
	}

	public void setUser(Consumer user) {
		this.user = user;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	
	public Payment() {}

}
