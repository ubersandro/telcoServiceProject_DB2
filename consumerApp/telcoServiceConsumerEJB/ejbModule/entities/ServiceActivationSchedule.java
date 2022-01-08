package entities;
import javax.persistence.*;
/**
 *	Class which models a ServicePackge .   
 * @author ubersandro
 *
 */
@Entity (name="ServicePackage")
public class ServiceActivationSchedule {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id; 
	private String nome; 
	
	public ServiceActivationSchedule() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
		
}
