package entities;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity 
public class ValidityPeriod {
	/*
	 *MANYTOONE side of relationship between ValidityPeriod and ServicePackage can be omitted because 
	 *no access from a given validityPeriod to its associated ServicePackages is done in the application. 
	 * */ 
	@Id
	private int months;

	public ValidityPeriod() {
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public ValidityPeriod(int months) {
		this.months = months;
	} 
	
	
}
