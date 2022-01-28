package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery (name="OptionalProduct_sales.findBestSeller", // there could be more than one best seller 
			query = "SELECT S"
					+ " FROM OptionalProduct_sales S "
					+ "ORDER BY S.sales DESC")
})

@Entity
@Table (name="optionalProduct_sales" , schema = "telcoServiceDB")
public class OptionalProduct_sales implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id 
	private String productName; 
	private int sales  ;
	
	
	public OptionalProduct_sales() {}
	public String getProductName() {
		return productName;
	}	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getSales() {
		return sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}
}
