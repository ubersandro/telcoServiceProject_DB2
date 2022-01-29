package services;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.OptionalProduct;
import exceptions.TupleAlreadyExistentException;
/**
 * The class is and Enterprise Java Bean which supports the management of the Optional Products entities. It is stateless because no state needs to be preserved in between user interactions with the application. 
 *  
 * @author ubersandro
 *
 */
@Stateless
public class OptionalProductService {
	@PersistenceContext(unitName =  "telcoServiceEJB_v2")
	private EntityManager em;

	public OptionalProductService() {}


	/**
	 * By means of a named query @see  Optional product
	 * @return
	 */
	public List<OptionalProduct> findAllOptionalProducts() {
		List<OptionalProduct> ops = (List<OptionalProduct>) em.createNamedQuery("OptionalProduct.findAll",
				OptionalProduct.class).getResultList();
		return ops;

	}

	public OptionalProduct createOptionalProduct(String name, double fee) throws TupleAlreadyExistentException {
		if(em.find(OptionalProduct.class, name )!=null)
			throw new TupleAlreadyExistentException("Optional product already existent"); 
		OptionalProduct op = new OptionalProduct();
		op.setName(name);
		op.setFee(fee);

		em.persist(op);

		return op;

	}

	public OptionalProduct findOptionalProductByName(String name) {
		return em.find(OptionalProduct.class, name);
	}

}
