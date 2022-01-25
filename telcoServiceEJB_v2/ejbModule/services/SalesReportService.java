package services;


import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.*; 
/**
 * This class supports the retrieval of sales report information. TODO create entities 
 * @author ubersandro
 *
 */

@Stateless
public class SalesReportService {
	@PersistenceContext (unitName = "telcoServiceEJB_v2")
	private EntityManager em; 
	public SalesReportService() {}
	
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
	
	public List<Object[]> findAvgOpts(){
		List<Object[]> salesSP = findSalesAllSP();
		List<Object[]> ret = new LinkedList<>();  //TODO try to fix named query 
		final int servicePackage = 0, totalSales =1; 
		for(Object [] couple : salesSP) {
			int packageID = (Integer) couple[servicePackage]; 
			long  sales = (Long) couple[totalSales]; 
			int totalOpSold = (Integer) em.createQuery("SELECT S.totalOptionalProducts FROM SalesSP_OP S WHERE S.packageID = :ID").setParameter("ID", packageID).getSingleResult();  
			Object [] o = new Object[2];
			o[0] = packageID; 
			o[1] = (0D + totalOpSold)/sales; 
			ret.add(o); 
		}
		return ret; 
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findTotalSalesWithOPs(){
		return (List<Object[]>) em.createNamedQuery("SalesSP_OP.purchasesOptionalProducts")
				.getResultList(); 
	}
	
	
	
	public List<Auditing> findAllAuditing (){
		return (List<Auditing>) em.createNamedQuery("Auditing.findAll", Auditing.class).getResultList(); 
	}
	
	public List<Consumer> findInsolventUsers (){ 
		return (List<Consumer>) em.createNamedQuery("Consumer.findUserByStatus", Consumer.class).getResultList(); 
	}

	public List<OptionalProduct_sales> findBestSeller () {
		return (List<OptionalProduct_sales>) em.createNamedQuery("OptionalProduct_sales.findBestSeller", OptionalProduct_sales.class).getResultList(); 
	}

	
}
