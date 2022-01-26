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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "FixedPhoneService "+ id;
	}
	
}
 