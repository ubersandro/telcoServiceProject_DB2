package entities;

import javax.persistence.*;

/**
 * @author ubersandro
 *
 */
@Entity 
@DiscriminatorValue ("MIS")
@Table(name ="MobileInternetService", schema="telcoServiceDB")
public class MobileInternetService extends Service {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int gigabytes; 
	private double fee; 
	
	public MobileInternetService() {} 
	
	public MobileInternetService(int id, int gigabytes, double fee) {
		super();
		this.gigabytes = gigabytes;
		this.fee = fee;
	}
	
	@Override
	public String toString() {
		return "MobileInternetService [gigabytes=" + gigabytes + ", fee=" + fee + "]";
	}

	public int getGigabytes() {
		return gigabytes;
	}
	public void setGigabytes(int gigabytes) {
		this.gigabytes = gigabytes;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}

}
