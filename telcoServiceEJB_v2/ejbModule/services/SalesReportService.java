package services;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Auditing;
import entities.Consumer;
import entities.OptionalProduct_sales;
import entities.PurchasesPackageValidityPeriod; 

/**
 * This class supports the retrieval of statistics for the sales report page. 
 * It is a Java Bean and it is stateless because there is no need to store status between method invocations.  	
 * @author ubersandro
 *
 */
@Stateless
public class SalesReportService {
	@PersistenceContext (unitName = "telcoServiceEJB_v2")
	private EntityManager em; 
	public SalesReportService() {}
	
	/**
	 * Retrieves all the sales 
	 * @return
	 */
	public List<PurchasesPackageValidityPeriod> findSalesAllSPVP (){
		return (List<PurchasesPackageValidityPeriod>) 
				em.createNamedQuery("PurchasesPackageValidityPeriod.findAll",PurchasesPackageValidityPeriod.class).getResultList(); 
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findSalesAllSP () { 
		return (List<Object[]>) em.createNamedQuery("PurchasesPackageValidityPeriod.purchasesSP").getResultList();  		
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findTotalSalesWithoutOPs(){
		return (List<Object[]>) em.createNamedQuery("PurchasesPackageValidityPeriod.purchasesNoOptionalProducts")
				.getResultList(); 
	}
	
	public Map<Integer, Double> findAvgOpts(){
		List<Object[]> salesSP = findSalesAllSP();
		Map<Integer, Double> ret = new HashMap<>();  
		final int servicePackage = 0, totalSales =1; 
		for(Object [] couple : salesSP) {
			int packageID = (Integer) couple[servicePackage]; 
			long  sales = (Long) couple[totalSales]; 
			int totalOpSold = (Integer) em.createQuery("SELECT S.totalOptionalProducts"
													+ " FROM SalesSP_OP S "
													 + "WHERE S.packageID = :ID").
														setParameter("ID", packageID).getSingleResult(); // there will always be single a tuple!
			ret.put(packageID , sales!=0?((0D + totalOpSold)/sales):0D); //otherwise -> division by zero!
		}
		return ret; 
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findTotalSalesWithOPs(){
		return (List<Object[]>) em.createNamedQuery("SalesSP_OP.purchasesOptionalProducts")
				.getResultList(); 
	}
	
	
	
	public List<Auditing> findAllAuditing (){
		return (List<Auditing>) 
				em.createNamedQuery("Auditing.findAll", Auditing.class).getResultList(); 
	}
	
	public List<Consumer> findInsolventUsers (){ 
		return (List<Consumer>) 
				em.createNamedQuery("Consumer.findUserByStatus", Consumer.class).getResultList(); 
	}

	
	/**
	 * This query returns the first best seller optional products. 
	 * If more than one product is sold the maximum number of times, it (presumably non-deterministically) 
	 * returns only one. There always will be a result because for each product in the database there 
	 * exists a correspondant tuple in the queried table. 
	 * @return
	 */
	public OptionalProduct_sales findBestSeller () {
		return (OptionalProduct_sales) 
				em.createNamedQuery("OptionalProduct_sales.findBestSeller", 
						OptionalProduct_sales.class).getResultList().get(0); 
	}

	
}
