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
import exceptions.NoSuchUserException;

@Stateless 
public class OrderService {
	@PersistenceContext(unitName = "telcoServiceEJB_v2")
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
	public Order addOrder(Consumer c, ServicePackage sp, List<OptionalProduct> ops, Calendar startingDate, 
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
		return o; 
	}
	
	public Order addOrder(Order o) {
		em.persist(o); 
		return o; 
	}
	
	/**
	 * Given the ID of an order, it marks it as paid (update) . 
	 * TRIGGERS !!! 
	 * @param orderID
	 */
	public void markAsPaid(int orderID) { //TODO change to order and do MERGE
		Order o = em.find(Order.class, orderID); //now managed 
		o.setStatus(OrderStatus.ACCEPTED); 
		//em.flush(); 
	}
	
	public void markAsRejected(int orderID) {
		Order o = em.find(Order.class, orderID); 
		o.setStatus(OrderStatus.REJECTED);
		//em.flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<Order> findAllRejectedOrders(){ //TODO write it 
		return ((List<Order>) em.createNamedQuery("Order.findOrdersByStatus")
				.setParameter("status", OrderStatus.REJECTED).getResultList()); 
	}
	
	public Order findOrderByID (int id) {
		return em.find(Order.class, id);
	}
	
	//DEBUG - DEMO purposes only methods 
	
	public List<ServiceActivationSchedule> findAllSASchedules (){
		return (List<ServiceActivationSchedule> ) 
				em.createNamedQuery("ServiceActivationSchedule.findAll", 
						ServiceActivationSchedule.class).getResultList(); 
	}
	
	public List<Order> findRejectedOrdersByUsername(String consumer) throws NoSuchUserException{
		Consumer c = em.find(Consumer.class, consumer); 
		if(c==null) throw new NoSuchUserException("Rejected orders retrieval failed because there is no such user."); 
		List<Order> l = (List<Order>) em.createNamedQuery("Oder.findOrdersByUserAndStatus", Order.class)
				.setParameter("c", c).setParameter("s", OrderStatus.REJECTED).getResultList(); 
		return l; 
	}
	
		
}
