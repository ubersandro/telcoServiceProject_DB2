package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import exceptions.*;	
import entities.*;

@Stateless 
public class UserService { 
	@PersistenceContext(unitName = "telcoServiceEJB_v2")
	private EntityManager em ; 
	
	public UserService () {}; 
	
	public TelcoUser checkUserCredentials(String username, String password )
			throws WrongCredentialsException, UserNotFoundException{	
		TelcoUser user = em.find(TelcoUser.class, username); //TODO improve with named query 
		if(user == null) throw new UserNotFoundException();
		if(!user.getPassword().equals(password)) throw new WrongCredentialsException(); 
		return user; //detached 
	}
	
	public boolean isConsumer(TelcoUser user) {
		return em.find(Consumer.class, user.getUsername())!=null; 
	}
	
	public Consumer retrieveConsumer(TelcoUser u) {
		return em.find(Consumer.class, u.getUsername()); 
	}
	
	public Employee retrieveEmployee(TelcoUser u) {
		return em.find(Employee.class, u.getUsername()); 
	}
	
	/**
	 * The method allows a user to register to the site.
	 * If the username provived links to an already existing user, an exception is thrown. 
	 * Otherwise, true is returned. 
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 */
	public void doRegistration(String username, String password, String email ) throws UserAlreadyExistentException {
		if(em.find(Consumer.class,username)!=null)
			throw new UserAlreadyExistentException(); 
		Consumer consumer = new Consumer(username, email, password);
		em.persist(consumer); //bound to be written to DB 
		em.flush(); //so that changes are written ASAP 
		return ; 
	}
	
	
	/**
	 * This method retrieves all the rejected orders for a given customer. 
	 * If the user is INSOLVENT, then the result is a NON EMPTY list of REJECTED orders.
	 * Otherwise it returns NULL. 
	 * @precondition : such a Consumer has to exist -> checked into the method 
	 * @param c
	 * @return
	 */
	public List<Order> findRejectedOrders(String consumer) throws NoSuchUserException{
		Consumer c = em.find(Consumer.class, consumer); 
		if(c==null) throw new NoSuchUserException("Rejected orders retrieval failed because there is no such user."); 
		//using JPQL -> DATA COMING DIRECTLY from DB 
		//This grants fresh data at each fetching. 
		List<Order> l = (List<Order>) em.createNamedQuery("Oder.findOrdersByUserAndStatus", Order.class)
				.setParameter("c", c).setParameter("s", OrderStatus.REJECTED).getResultList(); 
		return l; 
	}
}