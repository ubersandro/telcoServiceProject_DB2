package entities;

import javax.persistence.Entity;

/**
 * The class models the entity OptionalProduct. 
 * The entity set (ER MODEL) has a ManyToMany relationship with ServicePackage but this side (OP -> PS) 
 * doesn't need to be mapped. The same holds true for the ManyToMany relationship with Order. 
 * @author ubersandro
 *
 */
@Entity 
public class OptionalProduct {
	
}
