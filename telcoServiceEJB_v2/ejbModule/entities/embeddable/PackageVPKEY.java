package entities.embeddable;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PackageVPKEY implements Serializable{
	private static final long serialVersionUID = 1L;
	int servicePackage; 
	public int getServicePackage() {
		return servicePackage;
	}
	public void setServicePackage(int servicePackage) {
		this.servicePackage = servicePackage;
	}
	public int getValidityPeriodMonths() {
		return validityPeriodMonths;
	}
	public void setValidityPeriodMonths(int validityPeriodMonths) {
		this.validityPeriodMonths = validityPeriodMonths;
	}

	int validityPeriodMonths; 
	public PackageVPKEY() {}
	@Override
	public int hashCode() {
		return 71*servicePackage+87*validityPeriodMonths;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false; 
		if(!(obj instanceof PackageVPKEY)) return false; 
		PackageVPKEY x = (PackageVPKEY) obj; 
		return x.servicePackage==this.servicePackage && x.validityPeriodMonths==this.validityPeriodMonths; 
	}
	
	
	
}