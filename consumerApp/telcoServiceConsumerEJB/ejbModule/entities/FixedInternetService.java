package entities;

import javax.persistence.*;

/**
 * @author ubersandro
 *
 */

@Entity (name = "FixedInternetService")
@DiscriminatorValue("3")
public class FixedInternetService extends Service{
	private int gigabytes; 
	private double fee; 
	
	@OneToOne @JoinColumn (name="serviceID") //todo CHECK IF IT IS NECESSARY 
	private Service father; 
	
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
	
	public Service getFather() {
		return father;
	}

	public void setFather(Service father) {
		this.father = father;
	}
	
}
