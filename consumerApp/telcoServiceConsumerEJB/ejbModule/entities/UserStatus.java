package entities;

/**
 * Class modeling the Consumer of the telco service. If the user is Insolvent ,
 * then a namedQuery has to be executed to fetch all the REJECTED orders
 * submitted by the specific insolvent user.
 * 
 * @author ubersandro
 *
 */
public enum UserStatus {
	SOLVENT /* 0 */, INSOLVENT /* 1 */ 
}