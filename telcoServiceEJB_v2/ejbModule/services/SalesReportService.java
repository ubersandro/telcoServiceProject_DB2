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
	
	public List<PurchasesPackageValidityPeriod> findAllSPPurchasesVP (){
		return (List<PurchasesPackageValidityPeriod>) 
				em.createNamedQuery("PurchasesPackageValidityPeriod.findAll",PurchasesPackageValidityPeriod.class).getResultList(); 
	}
	
//	public Map<Integer, Integer> findAllSPsales (){
//		List<PurchasesPackageValidityPeriod> l = findAllSPPurchasesVP(); 
//		Map<Integer, Integer> ret = new HashMap<>(); 
//		for(PurchasesPackageValidityPeriod p : l) {
//			if(ret.containsKey(p.getKey().getServicePackage())) 
//				ret.put(p.getKey().getServicePackage(), ret.get(p.getKey().getServicePackage())+p.getCounter()); 
//			else ret.put(p.getKey().getServicePackage(), 1); 
//		}
//		return ret; 
//	}
//	

	@SuppressWarnings("unchecked")
	public List<Object> findSalesAllSP () {
		return (List<Object>) em.createNamedQuery("PurchasesPackageValidityPeriod.purchasesSP").getResultList(); // TODO check what concrete type has 		
	}
	
	public Map<Integer, Double> findSPOPAVG(){
		List<SalesSP_OP> l = (List<SalesSP_OP>) 
				em.createNamedQuery("SalesSP_OP.findAll",SalesSP_OP.class).getResultList();
		Map<Integer, Double> ret = new HashMap<>(); 
		for(SalesSP_OP s : l) {
			ret.put(s.getPackageID(), 0D + s.getTotalOptionalProducts()/s.getPurchasesWithOptionalProducts() ); 
		}
		return ret; 
	}
	
	 
	
	
	public List<Auditing> findAllAuditing (){
		return null; //TODO 
	}
	
	public List<Consumer> findInsolventUsers (){ //TODO NAMED QUERY 
		return null; //TODO 
	}
	
	
//	public List<Auditing> findAllAuditing (){
//		return null; //TODO 
//	}
//	
		
	
}
