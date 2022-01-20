package entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author ubersandro
 *
 */
@Entity (name = "FixedPhoneService")
@DiscriminatorValue ("FPS")
public class FixedPhoneService extends Service{
	
}
