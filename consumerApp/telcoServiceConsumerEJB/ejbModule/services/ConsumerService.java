package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mysql.cj.x.protobuf.MysqlxCrud.Find;

import entities.Consumer;
import exceptions.UserNotFoundException;
import exceptions.WrongCredentialsException;	

@Stateless 
public class ConsumerService { 
	@PersistenceContext(name = "telcoServiceEJB")
	private EntityManager em ; 
	@EJB 
	private OrderService orderService; 
	
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
	public Consumer checkCredentials(String username, String password ) throws WrongCredentialsException, UserNotFoundException{
		Consumer consumer = em.find(Consumer.class, username);
		if(consumer == null) throw new UserNotFoundException();
		if(!consumer.getPassword().equalsIgnoreCase(password)) throw new WrongCredentialsException(); 
		return consumer; 
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
		em.persist(consumer); 
		return ; 
	}
	
	//check pending orders -> rejected orders 
}
