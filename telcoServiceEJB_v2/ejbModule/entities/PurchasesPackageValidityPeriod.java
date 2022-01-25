package entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import entities.embeddable.PackageVPKEY;

/**
 * 
 * @author ubersandro
 *
 */

@NamedQueries({
	@NamedQuery (name="PurchasesPackageValidityPeriod.findAll", query = "SELECT p FROM PurchasesPackageValidityPeriod p"),
	
	@NamedQuery (name="PurchasesPackageValidityPeriod.purchasesSP", 
	query = "SELECT p.key.servicePackage, sum(p.counter) FROM PurchasesPackageValidityPeriod p GROUP BY p.key.servicePackage"),
	
	@NamedQuery (name = "PurchasesPackageValidityPeriod.purchasesNoOptionalProducts", 
	query = "SELECT p.key.servicePackage, sum(p.counter)- (S.purchasesWithOptionalProducts)  "
			+ "FROM PurchasesPackageValidityPeriod p, SalesSP_OP S "
			+ "WHERE p.key.servicePackage = S.packageID "
			+ "GROUP BY p.key.servicePackage")
})
@Entity			
@Table(name = "purchasesPerPackageVP", schema = "telcoServiceDB")
public class PurchasesPackageValidityPeriod implements Serializable{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private PackageVPKEY key;
	private int counter;

	public PackageVPKEY getKey() {
		return key;
	}

	public void setKey(PackageVPKEY key) {
		this.key = key;
	}

	public PurchasesPackageValidityPeriod() {
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

}
