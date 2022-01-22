package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity 
@Table(name = "ValidityPeriod", schema="telcoServiceDB")
public class ValidityPeriod implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 *MANYTOONE side of relationship between ValidityPeriod and ServicePackage can be omitted because 
	 *no access from a given validityPeriod to its associated ServicePackages is done in the application. 
	 * */ 
	@Id
	private int months;

	public ValidityPeriod() {
	}

	@Override
	public String toString() {
		return months+"";
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
