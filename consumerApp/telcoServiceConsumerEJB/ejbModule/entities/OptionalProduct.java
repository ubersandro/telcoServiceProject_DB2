package entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * The class models the entity OptionalProduct. 
 * The entity set (ER MODEL) has a ManyToMany relationship with ServicePackage but this side (OP -> PS) 
 * doesn't need to be mapped. The same holds true for the ManyToMany relationship with Order. 
 * @author ubersandro
 *
 */
@Entity (name = "OptionalProduct")
@NamedQueries({ @NamedQuery (name = "OptionalProduct.findAll", query = "SELECT ops FROM OptionalProduct ops")}) 

public class OptionalProduct {
	@Id 
	private String name; 
	private double fee;
	public OptionalProduct() {}
	public OptionalProduct(String name, double fee) {
		this.name = name;
		this.fee = fee;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	} 
	
	
	
}
