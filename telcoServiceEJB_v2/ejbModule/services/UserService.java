package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Consumer;
import entities.Employee;
import entities.TelcoUser;
import entities.UserStatus;
import exceptions.TupleAlreadyExistentException;
import exceptions.TupleNotFoundException;
import exceptions.WrongCredentialsException;

@Stateless 
public class UserService { 
	@PersistenceContext(unitName = "telcoServiceEJB_v2")
	private EntityManager em ; 
	
	public UserService () {}; 
	
	public TelcoUser checkUserCredentials(String username, String password )
			throws WrongCredentialsException, TupleNotFoundException{	
		List<TelcoUser> user = em.createNamedQuery("TelcoUser.checkCredentials", TelcoUser.class).
				setParameter("username", username).setParameter("password", password).getResultList(); //because there can only be one user with the given username   
		if(user == null || user.size()==0 ) throw new TupleNotFoundException();
		if(!user.get(0).getPassword().equals(password)) throw new WrongCredentialsException(); 
		return user.get(0);  
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
