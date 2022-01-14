package entities;

import javax.persistence.*;

/**
 * @author ubersandro
 *
 */
@Entity (name = "MobileInternetService")
@DiscriminatorValue ("4")
public class MobileInternetService {
	private int gigabytes; 
	private double fee; 
	
	@OneToOne @JoinColumn (name="serviceID") //todo CHECK IF IT IS NECESSARY 
	private Service father; 
	
	public MobileInternetService() {} 
	
	public MobileInternetService(int id, int gigabytes, double fee) {
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
	public Service getFather() {
		return father;
	}

	public void setFather(Service father) {
		this.father = father;
	}
}
