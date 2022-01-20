package services;

import java.util.List;
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
	@PersistenceContext(name = "telcoServiceEJB")
	private EntityManager em;

	public ServicePackageService() {
	}

	/**
	 * The method allows to list all the service packages in the DBMS
	 * 
	 * @return
	 */
	public List<ServicePackage> findAllServicePackages() {
//		List<ServicePackage> l = (List<ServicePackage>) em.createNamedQuery("ServicePackage.findAll",
//				ServicePackage.class).getResultList();
		List<ServicePackage> l = (List<ServicePackage>) em.createQuery("SELECT s FROM ServicePackage s",
				ServicePackage.class).getResultList();
		return l;

	}

	/**
	 * The method allows the employee to create a new service package
	 * 
	 * @param name
	 * @param services
	 * @param optionalProducts
	 * @param costs
	 * @return
	 */
	public ServicePackage createServicePackage(String name, List<Service> services,
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
		return sp.getCosts();  //@todo change to getFee 
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
		return sp.getOptionalProducts(); //associable optional products -> here DETACHED 
	}
	
	

}
