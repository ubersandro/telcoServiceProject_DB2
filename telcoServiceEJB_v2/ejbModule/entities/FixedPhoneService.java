package entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author ubersandro
 *
 */
@Entity 
@DiscriminatorValue ("FPS")
@Table (name = "FixedPhoneService", schema = "telcoServiceDB")
public class FixedPhoneService extends Service{

	@Override
	public String toString() {
		return "FixedPhoneService []";
	}
	
}
 