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
			List<OptionalProduct> optionalProducts, Map<ValidityPeriod, Double> costs) {

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
	
	public ServicePackage addServicePackage(String name) {
		ServicePackage sp = new ServicePackage(); 
		sp.setName(name);
		em.persist(sp);
		return sp; 
	}
	
	public void associateOptionalProduct (String productName, int packageID) {
		ServicePackage sp = findServicePackage(packageID);
		OptionalProduct op = em.find(OptionalProduct.class, productName); 
		sp.addOptionalProduct(op);
		em.merge(sp);
	}
	
	public ServicePackage refreshServicePackage(ServicePackage sp) {
		ServicePackage x = em.find(ServicePackage.class, sp.getId()); 
		em.refresh(x);
		return x ;
	}
	

}
