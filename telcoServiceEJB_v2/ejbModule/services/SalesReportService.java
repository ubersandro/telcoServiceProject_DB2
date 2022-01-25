package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findAvgOpts(){
		return (List<Object[]>) em.createNamedQuery("SalesSP_OP.avgOpts")
				.getResultList(); 
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findTotalSalesWithOPs(){
		return (List<Object[]>) em.createNamedQuery("SalesSP_OP.purchasesOptionalProducts")
				.getResultList(); 
	}
	
	
	
	@SuppressWarnings("unchecked")
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
