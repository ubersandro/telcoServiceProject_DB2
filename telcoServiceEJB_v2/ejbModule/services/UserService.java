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
			throws WrongCredentialsException, TupleNotFoundException{	
		TelcoUser user = em.createNamedQuery("TelcoUser.checkCredentials", Consumer.class).
				setParameter("username", username).setParameter("password", password).getSingleResult(); //because there can only be one user with the given username   
		if(user == null) throw new TupleNotFoundException();
		if(!user.getPassword().equals(password)) throw new WrongCredentialsException(); 
		return user;  
	}
	
	/**
	 * PRE : These methods are called only AFTER checkCredentials. 
	 * @param user
	 * @return
	 */
	public boolean isConsumer(TelcoUser user) {
		return em.find(Consumer.class, user.getUsername())!=null; 
	}
	
	public Consumer retrieveConsumer(TelcoUser u) { // not refreshed
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
	public void doRegistration(String username, String password, String email ) throws TupleAlreadyExistentException {
		if(em.find(Consumer.class,username)!=null)
			throw new TupleAlreadyExistentException(); 
		Consumer consumer = new Consumer(username, email, password);
		em.persist(consumer); 
		em.flush(); //so that changes are written ASAP 
		return ; 
	}
	
	
	public List<Consumer> findInsolventUsers(){
		return (List<Consumer>) em.createNamedQuery("Consumer.findUserByStatus", Consumer.class)
				.setParameter("status", UserStatus.INSOLVENT).getResultList(); 
	}
	
	public boolean consumerIsInsolvent(String username) {
		Consumer x = em.find(Consumer.class, username); 
		em.refresh(x);
		return x.isInsolvent(); 
	}
	
	
}
