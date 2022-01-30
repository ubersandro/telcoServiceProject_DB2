package entities;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Class for the FATHER entity of the Service Hierararchy. 
 * @author ubersandro
 *
 */
@Entity 
@Inheritance (strategy = InheritanceType.JOINED)
@NamedQueries(
		{
			@NamedQuery (name = "Service.findAll", query = "SELECT s FROM Service s")
		})
@Table(name = "Service", 	schema="telcoServiceDB")
public class Service implements Serializable{ 
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	 protected int id; 

	 
	public Service() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	} 
	
	 
}
