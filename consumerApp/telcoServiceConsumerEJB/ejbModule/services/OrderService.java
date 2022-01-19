package services;

import java.util.*; 

import javax.ejb.Stateless;
import javax.persistence.*;

import entities.OptionalProduct ;
import entities.Order;
import entities.OrderStatus;
import entities.Consumer; 
import entities.ServicePackage;
import entities.ValidityPeriod;

@Stateless 
public class OrderService {
	@PersistenceContext(name = "telcoServiceEJB")
	private EntityManager em ; 
	
	/**
	 * Add a new Order to the DB. 
	 * @param c 
	 * @param sp
	 * @param ops
	 * @param startingDate
	 * @param totalValue
	 * @param vp
	 */
	public void addOrder(Consumer c, ServicePackage sp, List<OptionalProduct> ops, Calendar startingDate, 
			double totalValue, ValidityPeriod vp) {
		Date now = new Date();
		Calendar today = Calendar.getInstance(); 
		Order o = new Order (now, today, totalValue, startingDate, c, sp, vp, ops);
		em.persist(o); 
	}
	
	public void markAsPaid(Order o) {
		o.setStatus(OrderStatus.ACCEPTED);
		em.merge(o); 
	}
	
	public void markAsRejected(Order o) { //MIND THE TRIGGERS 
		o.setStatus(OrderStatus.REJECTED);
		em.merge(o) ;
	}
	
		
}
