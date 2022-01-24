package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table (name="salesSP_OP" , schema = "telcoServiceDB")
public class SalesSP_OP implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id 
	private int packageID ;
	private int totalOptionalProducts ;
	public int getPackageID() {
		return packageID;
	}

	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}

	public int getTotalOptionalProducts() {
		return totalOptionalProducts;
	}

	public void setTotalOptionalProducts(int totalOptionalProducts) {
		this.totalOptionalProducts = totalOptionalProducts;
	}

	public int getPurchasesWithOptionalProducts() {
		return purchasesWithOptionalProducts;
	}

	public void setPurchasesWithOptionalProducts(int purchasesWithOptionalProducts) {
		this.purchasesWithOptionalProducts = purchasesWithOptionalProducts;
	}

	private int purchasesWithOptionalProducts ;
	
	public SalesSP_OP() {
		// TODO Auto-generated constructor stub
	}

}
