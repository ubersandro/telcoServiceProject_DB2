package entities;

import javax.persistence.*;

/**
 * Employee class models an employee of the telco company.
 * 
 * @author ubersandro
 *
 */
@NamedQuery(name = "Employee.checkCredentials",
	query = "SELECT e FROM Employee e  WHERE e.username = ?1 and e.password = ?2")  
@Entity
@Table (name = "Employee", schema = "telcoServiceDB")
@DiscriminatorValue("EMP")
public class Employee extends TelcoUser {
	private static final long serialVersionUID = 1L;
}
