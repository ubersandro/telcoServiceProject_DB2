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

	@Override
	public int hashCode() {
		return months; //naive but it works 
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidityPeriod other = (ValidityPeriod) obj;
		return months == other.months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public ValidityPeriod(int months) {
		this.months = months;
	} 
	
	
}
