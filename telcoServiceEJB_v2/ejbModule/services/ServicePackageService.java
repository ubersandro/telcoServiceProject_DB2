package services;

import java.util.*;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.OptionalProduct;
import entities.Service;
import entities.ServicePackage;
import entities.ValidityPeriod;
import exceptions.TupleAlreadyExistentException;

@Stateless
public class ServicePackageService {
	@PersistenceContext(unitName = "telcoServiceEJB_v2")
	private EntityManager em;

	public ServicePackageService() {
	}

	public List<ServicePackage> findAllServicePackages() {
		List<ServicePackage> l = (List<ServicePackage>) em.createNamedQuery("ServicePackage.findAll",
				ServicePackage.class).getResultList();
		return l;

	}

	public ServicePackage addServicePackage(String name, List<Service> services,
			List<OptionalProduct> optionalProducts, Map<ValidityPeriod, Double> costs) throws TupleAlreadyExistentException {
		// we assume that this method is only called with a name which is not already assigned to a service package
		// identity checks embedded into the application so that error management and recovery is way simpler and errors 
		// messages are more precise 
		if(em.find(ServicePackage.class, "name")!=null)
			throw new TupleAlreadyExistentException("A service package with the same name already exists!"); 
		ServicePackage sp = new ServicePackage();
		
		sp.setName(name);
		sp.setServices(services);
		sp.setOptionalProducts(optionalProducts);
		sp.setCosts(costs);

		em.persist(sp); 
		return sp;
	}


	public Map<ValidityPeriod, Double> findValidityPeriodsAndFees(int id) {
		ServicePackage sp = em.find(ServicePackage.class, id); 
		return sp.getCosts(); 
	}

	
	public ServicePackage findServicePackage(int id) {
		ServicePackage sp = em.find(ServicePackage.class, id);
		return sp;
	}
	
	/**
	 * Optional Products are lazily fetchced, once the given optional product is retrieved a transactional method 
	 * is needed anyways to fetch optional products. 
	 * @param servicePackageID
	 * @return
	 */
	public List<OptionalProduct> findAssociableOptionalProducts(int servicePackageID){
		ServicePackage sp = em.find(ServicePackage.class, servicePackageID); 
		return sp.getOptionalProducts();  
	}
	
	
	public void associateOptionalProduct (String productName, int packageID) {
		ServicePackage sp = findServicePackage(packageID);
		OptionalProduct op = em.find(OptionalProduct.class, productName); 
		sp.addOptionalProduct(op);
		em.merge(sp);
	}
	
	/**
	 * ASSUMPTION MADE : apart from being identified by an ID, a ServicePackage is identified by its name (candidate key). 
	 * @param sp
	 * @return
	 * @throws TupleAlreadyExistentException
	 */
	public ServicePackage addServicePackage(ServicePackage sp) throws TupleAlreadyExistentException {
		List<String> l = (List<String>) em.createQuery("Select s.name from ServicePackage s", String.class).getResultList();  
		if(l.contains(sp.getName())) 
			throw new TupleAlreadyExistentException("A service package with the same name already exists!"); 
		em.persist(sp);
		return sp ;
	}
	

}
