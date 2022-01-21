package entities;

import javax.persistence.*;

/**
 * @author ubersandro
 *
 */
@Entity
@DiscriminatorValue ("MPS")
@Table(schema="telcoServiceDB")
public class MobilePhoneService extends Service{
	private int minutes; 
	private double extraMinutesFee;
	private int SMSs; 
	private double extraSMSsFee;

	public MobilePhoneService() {}
	public int getMinutes() {
		return minutes;
	}

	@Override
	public String toString() {
		return "MobilePhoneService [minutes=" + minutes + ", extraMinutesFee=" + extraMinutesFee + ", SMSs=" + SMSs
				+ ", extraSMSsFee=" + extraSMSsFee + "]";
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
		
}
