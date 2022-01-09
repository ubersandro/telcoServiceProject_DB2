package entities;
import javax.persistence.*;
import java.util.*;
/**
 *	Class which models a ServicePackge .   
 * @author ubersandro
 *
 */
@Entity (name="ServicePackage")
@NamedQueries({ @NamedQuery (name = "ServicePackage.findAll", query = "SELECT s FROM ServicePackage s")}) 
public class ServicePackage {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id; 
	private String name; 
	
	@ManyToMany (fetch = FetchType.EAGER) // ASSUMING THAT in the Home page services have to be displayed immediately. This assumption does not impact 
	//on performance that much because there are only FOUR types of services, so loading them in the Persistent Context is not a big deal. 
	@JoinTable (name="SPS", joinColumns = 
			@JoinColumn(name = "packageID"), //owner (according to this particular choice).  
			inverseJoinColumns = @JoinColumn(name = "serviceID")) //service FATHER entity
	private List<Service> services ;
	
	/*
	 * Fetch type is LAZY because optional products that could be associated with a given product 
	 * are retrieved only when the user explicitly   
	 */
	@ManyToMany (fetch = FetchType.LAZY) 
	@JoinTable (name="Offers", joinColumns = 
			@JoinColumn(name = "packageID"), //packageID is the owner of the relation according to this choice (no natural owner). 
			inverseJoinColumns = @JoinColumn(name = "productName"))
	private List<OptionalProduct> optionalProducts;
	
	
	
	/*in the Home page neither the available validity periods
	nor do the fee associated with the couple (validityPeriod, ServicePackage) have to be displayed.
	Since the relationship has one attribute (monthlyFee), an ElementCollection tag has to be used.
	DIRECTION ServicePackage -> ValidityPeriod   
	*/
	@ElementCollection (fetch = FetchType.LAZY) //entity key element collection (no cascading, inverse and orphan removal) 
	@CollectionTable (name = "HasValidity", 
			joinColumns = @JoinColumn(name = "packageID")) //this column holds the PK to the ServicePackage. 
	@MapKeyJoinColumn (name = "validityMonths") //Specification of the column holding the PK of the ValidityPeriod entity used as an index in the map. 
	@Column (name = "monthlyFee")
	private Map<ValidityPeriod, Double> availableValidityPeriods; 
	
	
	
	
	
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
	
	
		
}
