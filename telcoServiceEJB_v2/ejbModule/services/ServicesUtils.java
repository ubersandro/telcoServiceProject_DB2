package services;

import java.util.*; 

import javax.ejb.Stateless;
import javax.persistence.*;

import entities.OptionalProduct ;
import entities.Order;
import entities.OrderStatus;
import entities.Service;
import entities.ServiceActivationSchedule;
import entities.Consumer; 
import entities.ServicePackage;
import entities.ValidityPeriod;
import io.opentracing.tag.IntTag;
/**
 * Stateless EJB which carries out maintenance and utility tasks on the Service entities. 
 * @author ubersandro
 *
 */
@Stateless 
public class ServicesUtils {
	@PersistenceContext(name = "telcoServiceEJB")
	private EntityManager em ; 
	
	public List<Service> findAllServices(){
		return (List<Service> ) 
				em.createNamedQuery("Service.findAll", //@todo check how to retrieve Service entities of the right type. 
						Service.class).getResultList(); 
	}
	//4 different queries ? 	
	
	
	
	
}
