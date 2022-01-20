package entities;

import javax.persistence.*;

/**
 * Class for the FATHER entity of the Service Hierararchy. 
 * @author ubersandro
 *
 */
@Entity 
@Table(name = "Service")
@Inheritance (strategy = InheritanceType.JOINED)
@NamedQueries(
		{
			@NamedQuery (name = "Service.findAll", query = "SELECT s FROM Service s")
		})
public class Service {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	 private int id; 

	 
	public Service() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	} 
	
	 
}
