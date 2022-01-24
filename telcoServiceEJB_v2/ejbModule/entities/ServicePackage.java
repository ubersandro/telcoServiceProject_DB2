package entities;
import javax.persistence.*;
import javax.ws.rs.client.CompletionStageRxInvoker;

import java.io.Serializable;
import java.util.*;

/**
 *	Class which models a ServicePackge .   
 * @author ubersandro
 *
 */

@Entity
@NamedQueries({ @NamedQuery (name = "ServicePackage.findAll", 
		query = "SELECT s FROM ServicePackage s")}) 
@Table (name = "ServicePackage", schema = "telcoServiceDB")
public class ServicePackage implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id; 
	private String name; 
	
	//CASCADE 
	// PERSIST because on creation of a ServicePackage, Services could be created as well and have to be persisted 
	// DELETE --> NO because of the manyToMany relationship 
	// MERGE  --> what changes can I make to a ServicePackage? NONE (in the app) , so merging is immaterial 
	// REFRESH 
	// DETACH -> no because there could be more ServicePackages in the PC. 
	@ManyToMany (fetch = FetchType.EAGER ) // ASSUMING THAT in the Home page services have to be displayed immediately
	@JoinTable (name="SPS", joinColumns = 
			@JoinColumn(name = "packageID"), //owner (according to this particular choice).  
			inverseJoinColumns = @JoinColumn(name = "serviceID"), schema ="telcoServiceDB") //service FATHER entity
	private List<Service> services ;
	
	
	/*
	 * Fetch type is LAZY because optional products that could be associated with a given product 
	 * are retrieved only when the user explicitly asks.    
	 */
	@ManyToMany (fetch = FetchType.LAZY)  
	@JoinTable (name="Offers", joinColumns = 
			@JoinColumn(name = "packageID"), //packageID is the owner of the relation according to this choice (no natural owner). 
			inverseJoinColumns = @JoinColumn(name = "productName"),schema ="telcoServiceDB")
	private List<OptionalProduct> optionalProducts;
	
	
	
	/*in the Home page neither the available validity periods
	nor do the fee associated with the couple (validityPeriod, ServicePackage) have to be displayed.
	Since the relationship has one attribute (monthlyFee), an ElementCollection tag has to be used.
	DIRECTION ServicePackage -> ValidityPeriod   
	*/
	@ElementCollection (fetch = FetchType.EAGER) //entity key element collection (no cascading, inverse and orphan removal) 
	@CollectionTable (name = "HasValidity", 
			joinColumns = @JoinColumn(name = "packageID"), schema = "telcoServiceDB") //this column holds the PK to the ServicePackage. 
	@MapKeyJoinColumn (name = "validityMonths") //Specification of the column holding the PK of the ValidityPeriod entity used as an index in the map. 
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
