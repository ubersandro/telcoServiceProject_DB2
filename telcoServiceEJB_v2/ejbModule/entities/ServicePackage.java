package entities;
import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

/**
 *	Class which models a ServicePackge .   
 * @author ubersandro
 *
 */

@Entity
/**
 * It is very frequent that all service packages have to be fetched, so a named query could help speed up the operation. 
 * @author ubersandro
 *
 */
@NamedQueries({ @NamedQuery (name = "ServicePackage.findAll", 
							query = "SELECT s FROM ServicePackage s")}) 
@Table (name = "ServicePackage", schema = "telcoServiceDB")
public class ServicePackage implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id; 
	private String name; 

	/**	
 * Services have to be fetched eagerly because whenever a Service Package is needed, the corresponding 
 * services should be retrieved as well. 
	*/
	@ManyToMany (fetch = FetchType.EAGER )
	@JoinTable (name="SPS", joinColumns = 
			@JoinColumn(name = "packageID"), //owner
			inverseJoinColumns = @JoinColumn(name = "serviceID"), schema ="telcoServiceDB") 
	private List<Service> services ;
	
	
	/*
	 * Fetch type is LAZY because optional products that could be associated with a given product 
	 * are retrieved only when the user explicitly asks for them (in the BUY PAGE).    
	 */
	@ManyToMany (fetch = FetchType.LAZY)  
	@JoinTable (name="Offers", joinColumns = 
			@JoinColumn(name = "packageID"), //packageID is the owner of the relation according to this choice (no natural owner). 
			inverseJoinColumns = @JoinColumn(name = "productName"),schema ="telcoServiceDB")
	private List<OptionalProduct> optionalProducts;
	
	
	
	@ElementCollection (fetch = FetchType.EAGER) 
	@CollectionTable (name = "HasValidity", 
			joinColumns = @JoinColumn(name = "packageID"), schema = "telcoServiceDB")  
	@MapKeyJoinColumn (name = "validityMonths")  
	@Column (name = "monthlyFee")
	private Map<ValidityPeriod, Double> costs; 
	
	
	
	
	
	public ServicePackage() {
	}
	public ServicePackage(String name) {
		this.name = name; 
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServicePackage other = (ServicePackage) obj;
		return id == other.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name= name;
	}
	
	public List<Service> getServices() {
		return services;
	}
	
	public void addService(Service s) {
		services.add(s);
	}
	
	public void removeService(Service s) {
		services.remove(s);
	}
	
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	public void setOptionalProducts(List<OptionalProduct> optionalProducts) {
		this.optionalProducts = optionalProducts;
	}
	
	public void setCosts(Map<ValidityPeriod, Double> costs) {
		this.costs = costs;
	}
	
	public List<OptionalProduct> getOptionalProducts() {
		return optionalProducts;
	}
	
	public Map<ValidityPeriod, Double> getCosts() {
		return costs;
	}

	public void addOptionalProduct(OptionalProduct op) {
		optionalProducts.add(op);
	}
	
	public void deleteOptionalProduct(OptionalProduct op ) {
		optionalProducts.remove(op);
	}
	
	public void addCost(ValidityPeriod vp, double fee) {
		costs.put(vp, fee);
	}
	
	public void removeCost(ValidityPeriod vp) {
		costs.remove(vp);
	}
	

	
		
}
