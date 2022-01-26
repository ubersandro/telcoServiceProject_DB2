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
import exceptions.NoSuchTupleException;

@Stateless 
public class OrderService {
	@PersistenceContext(unitName = "telcoServiceEJB_v2")
	private EntityManager em ; 

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
		em.persist(o); // it has not to be already persisted ... 
		return o; 
	}
	

	public void markAsPaid(int orderID) {
		Order o = em.find(Order.class, orderID); //now managed 
		o.setStatus(OrderStatus.ACCEPTED); 
		em.flush(); 
	}
	
	public void markAsRejected(int orderID) {
		Order o = em.find(Order.class, orderID); 
		o.setStatus(OrderStatus.REJECTED);
		em.flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<Order> findAllRejectedOrders(){  
		return ((List<Order>) em.createNamedQuery("Order.findOrdersByStatus")
				.setParameter("status", OrderStatus.REJECTED).getResultList()); 
	}
	
	public Order findOrderByID (int id) {
		return em.find(Order.class, id);
	}
	
	
	public List<Order> findRejectedOrdersByUsername(String consumer){
		Consumer c = em.find(Consumer.class, consumer);  
		List<Order> l = (List<Order>) em.createNamedQuery("Order.findOrdersByUserAndStatus", Order.class)
				.setParameter("consumer", c).setParameter("status", OrderStatus.REJECTED).getResultList(); 
		return l; 
	}
	
	
	//DEBUG - DEMO purposes only methods 
	
	public List<ServiceActivationSchedule> findAllSASchedules (){
		return (List<ServiceActivationSchedule> ) 
				em.createNamedQuery("ServiceActivationSchedule.findAll", 
						ServiceActivationSchedule.class).getResultList(); 
	}
	
	public ServicePackage retrieveServicePackageFromOrderId(int orderID){
		return em.find(Order.class, orderID).getServicePackage(); //navigation
	}
		
	public List<OptionalProduct> retrieveIncludedOptionalProductsFromOrderId (int orderID){
		return new LinkedList<>(em.find(Order.class, orderID).getIncludedOptionalProducts()); //navigation
	}
}
