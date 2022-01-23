package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.OptionalProduct;
@Stateless
public class OptionalProductService {
	@PersistenceContext(unitName =  "telcoServiceEJB_v2")
	private EntityManager em;

	public OptionalProductService() {
	}

	/**
	 * The method allows to list all the optional products in the DBMS
	 * 
	 * @return 
	 */
	public List<OptionalProduct> findAllOptionalProducts() {

		@SuppressWarnings("unchecked")
		List<OptionalProduct> ops = (List<OptionalProduct>) em.createNamedQuery("OptionalProduct.findAll",
				OptionalProduct.class);
		return ops;

	}

	/**
	 * The method allows the employee to create a new optional product
	 * 
	 * @param name
	 * @param fee
	 * @return
	 */
	public OptionalProduct createOptionalProduct(String name, double fee) {

		OptionalProduct op = new OptionalProduct();

		op.setName(name);
		op.setFee(fee);

		em.persist(op);

		return op;

	}

	/**
	 * Whenever a client has a name, it just retrieves the OptionalProduct identified by that name. 
	 * Useful because included optional products' names in a (HTTP) request are associated one by one with their relative entity.
	 * Once all the entities are retrieved an order can be placed by the Servlet (@see create order....) 
	 * @param name
	 * @return
	 */
	public OptionalProduct findOptionalProductByName(String name) {
		return em.find(OptionalProduct.class, name);
	}

}
