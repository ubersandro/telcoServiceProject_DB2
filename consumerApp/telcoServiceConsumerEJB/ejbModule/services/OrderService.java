package services;

import java.util.*; 

import javax.ejb.Stateless;
import javax.persistence.*;

import entities.OptionalProduct ;
import entities.Order;
import entities.OrderStatus;
import entities.ServiceActivationSchedule;
import entities.Consumer; 
import entities.ServicePackage;
import entities.ValidityPeriod;
import io.opentracing.tag.IntTag;

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
		Order o = new Order ();
		o.setTime(now);
		o.setDate(today);
		o.setStartingDate(startingDate);
		o.setConsumer(c);
		o.setIncludedOptionalProducts(ops);
		o.setStatus(OrderStatus.NEWLY_CREATED); //payment not yet attempted 
		o.setTotalValue(totalValue);
		o.setServicePackage(sp);
		em.persist(o); 
	}
	
	/**
	 * Given the ID of an order, it marks it as paid (update) . 
	 * TRIGGERS !!! 
	 * @param orderID
	 */
	public void markAsPaid(int orderID) {
		Order o = em.find(Order.class, orderID); //now managed 
		o.setStatus(OrderStatus.ACCEPTED); 
		//em.flush(); 
	}
	
	public void markAsRejected(int orderID) {
		Order o = em.find(Order.class, orderID); 
		o.setStatus(OrderStatus.REJECTED);
		//em.flush();
	}
	
	
	
	public Order findOrderByID (int id) {
		return em.find(Order.class, id); //the returned object is managed ????
	}
	
	//DEBUG - DEMO purposes only methods 
	
	public List<ServiceActivationSchedule> findAllSASchedules (){
		return (List<ServiceActivationSchedule> ) 
				em.createNamedQuery("ServiceActivationSchedule.findAll", 
						ServiceActivationSchedule.class).getResultList(); 
	}
	
		
}
