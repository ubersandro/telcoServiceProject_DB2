package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import exceptions.NoSuchUserException;
import exceptions.UserAlreadyExistentException;
import exceptions.UserNotFoundException;
import exceptions.WrongCredentialsException;	
import entities.*; 
@Stateless 
public class ConsumerService { 
	@PersistenceContext(name = "telcoServiceEJB")
	private EntityManager em ; 
	
	public ConsumerService () {}; 
	
	/**
	 * The method check whether or not a Consumer with the given username exists. 
	 * If it exists, the password provided is checked to reassure it is the right password for the given user. 
	 * If the password is right, it just returns the Consumer identified by the username. 
	 * @param username	
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public Consumer checkCredentials(String username, String password )
			throws WrongCredentialsException, UserNotFoundException{	
		Consumer c = em.find(Consumer.class, username);
		if(c == null) throw new UserNotFoundException();
		if(!c.getPassword().equals(password)) throw new WrongCredentialsException(); 
		return c; //detached 
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
