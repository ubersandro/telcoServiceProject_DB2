package entities;
import javax.persistence.*;
/**
 *	Class which models a ServicePackge .   
 * @author ubersandro
 * @TODO -> set fetch type only at the end, you first need to know very well the app logic. Cascadign can be done in advance instead. 
 *
 */
@Entity (name="ServicePackage")
public class ServicePackage {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id; 
	private String name; 
	
	public ServicePackage() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name= name;
	}
		
}
