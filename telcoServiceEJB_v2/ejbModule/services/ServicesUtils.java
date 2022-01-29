package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Service;
/**
 * Stateless EJB which carries out maintenance and utility tasks on the Service entities. 
 * @author ubersandro
 *
 */
@Stateless 
public class ServicesUtils {
	@PersistenceContext(unitName = "telcoServiceEJB_v2")
	private EntityManager em ; 
	
	public List<Service> findAllServices(){
		return (List<Service> ) 
				em.createNamedQuery("Service.findAll", 
						Service.class).getResultList(); 
	}
	
	public Service findServiceById(int id){
		return (Service) em.find(Service.class, id); 
	}	
	
}
