package entities;

import javax.persistence.*;

/**
 * @author ubersandro
 *
 */
@Entity (name = "MobilePhoneService")
@DiscriminatorValue ("2")
public class MobilePhoneService extends Service{
	private int minutes; 
	private double extraMinutesFee;
	private int SMSs; 
	private double extraSMSsFee;
	
	@OneToOne @JoinColumn (name="serviceID") //todo CHECK IF IT IS NECESSARY 
	private Service father; 
	
	public MobilePhoneService() {}
	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public double getExtraMinutesFee() {
		return extraMinutesFee;
	}

	public void setExtraMinutesFee(double extraMinutesFee) {
		this.extraMinutesFee = extraMinutesFee;
	}

	public int getSMSs() {
		return SMSs;
	}

	public void setSMSs(int sMSs) {
		SMSs = sMSs;
	}

	public double getExtraSMSsFee() {
		return extraSMSsFee;
	}

	public void setExtraSMSsFee(double extraSMSsFee) {
		this.extraSMSsFee = extraSMSsFee;
	}

	public Service getFather() {
		return father;
	}

	public void setFather(Service father) {
		this.father = father;
	} 
		
}
