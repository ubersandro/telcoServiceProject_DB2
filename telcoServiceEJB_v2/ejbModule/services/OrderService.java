package services;

import java.util.*; 

import javax.ejb.Stateless;
import javax.persistence.*;

import entities.OptionalProduct ;
import entities.Order;
import entities.OrderStatus;
import entities.Payment;
import entities.PaymentStatus;
import entities.ServiceActivationSchedule;
import entities.Consumer; 
import entities.ServicePackage;
import entities.ValidityPeriod;

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
		em.persist(o);  
		return o; 
	}
	

	public void markAsPaid(int orderID, String username) {
		Order o = em.find(Order.class, orderID); 
		Consumer c = em.find(Consumer.class, username); 
		Payment p = new Payment();
		p.setOrder(o);
		p.setUser(c); 
		p.setStatus(PaymentStatus.APPROVED);
		p.setDate(Calendar.getInstance());
		p.setTime(new Date()); 
		p.setTotalValue(o.getTotalValue());
		em.persist(p); 
	}
	
	public void markAsRejected(int orderID, String username) {
		Order o = em.find(Order.class, orderID); 
		Consumer c = em.find(Consumer.class, username); 
		Payment p = new Payment();
		p.setOrder(o);
		p.setUser(c); 
		p.setStatus(PaymentStatus.REJECTED);
		p.setDate(Calendar.getInstance());
		p.setTime(new Date()); 
		p.setTotalValue(o.getTotalValue());
		em.persist(p); 
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
	/**
	 * The following methods are introduced because Order ServicePackage and OptionalProducts associated 
	 * to a given Order are lazily fetched. So, the code of the methods being transactional, navigating 
	 * after finding the Order by id using the Entity Manager allows requested objects to retrieved. 
	 * @param orderID
	 * @return
	 */
	
	public ServicePackage retrieveServicePackageFromOrderId(int orderID){
		return em.find(Order.class, orderID).getServicePackage(); //navigation -> done this way because service package is lazily fetched
	}
		
	public List<OptionalProduct> retrieveIncludedOptionalProductsFromOrderId (int orderID){
		return new LinkedList<>(em.find(Order.class, orderID).getIncludedOptionalProducts()); //navigation
	}
	
	public ValidityPeriod findValidityPeriod (int orderID) {
		return em.find(Order.class, orderID).getValidityPeriod(); 
	}
}
