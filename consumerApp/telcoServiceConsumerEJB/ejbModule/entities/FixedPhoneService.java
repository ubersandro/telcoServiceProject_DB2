package entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author ubersandro
 *
 */
@Entity (name = "FixedPhoneService")
@DiscriminatorValue ("1")
public class FixedPhoneService extends Service{
	
}
