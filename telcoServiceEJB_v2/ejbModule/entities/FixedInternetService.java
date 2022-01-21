package entities;

import javax.persistence.*;

/**
 * @author ubersandro
 *
 */

@Entity 
@DiscriminatorValue("FIS")
public class FixedInternetService extends Service{
	private int gigabytes; 
	private double fee; 
	
	public FixedInternetService() {} 
	
	public FixedInternetService(int id, int gigabytes, double fee) {
		super();
		this.gigabytes = gigabytes;
		this.fee = fee;
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

	@Override
	public String toString() {
		return "FixedInternetService [gigabytes=" + gigabytes + ", fee=" + fee + "]";
	}
	
}