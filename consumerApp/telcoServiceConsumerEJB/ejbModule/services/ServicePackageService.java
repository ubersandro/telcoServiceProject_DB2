package services;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.OptionalProduct;
import entities.Service;
import entities.ServicePackage;
import entities.ValidityPeriod;

public class ServicePackageService {
	@PersistenceContext(name = "telcoServiceEJB" )
	private EntityManager em;
	
	public ServicePackageService() {}
	
	/**
	 * The method allows to list all the service packages in the DBMS
	 * @return
	 */
	public List<ServicePackage> findAllServicePackages(){	
		
		@SuppressWarnings("unchecked")
		List<ServicePackage> l =(List<ServicePackage>) em.createNamedQuery( "ServicePackage.findAll", ServicePackage.class );
		return l;
		
	}
	
	/**
	 * The method allows the employee to create a new service package
	 * @param name
	 * @param services
	 * @param optionalProducts
	 * @param costs
	 * @return
	 */
	public ServicePackage createServicePackage (String name, List<Service> services, List<OptionalProduct> optionalProducts, Map<ValidityPeriod, Double> costs ) {
		
		ServicePackage sp = new ServicePackage();
		
		sp.setName(name);
		sp.setServices(services);
		sp.setOptionalProducts(optionalProducts);
		sp.setCosts(costs);
		
		em.persist(sp);
		
		return sp;
	
	}
	
	


}
