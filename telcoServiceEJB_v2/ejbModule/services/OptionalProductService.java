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

	public OptionalProductService() {}


	public List<OptionalProduct> findAllOptionalProducts() {
		List<OptionalProduct> ops = (List<OptionalProduct>) em.createNamedQuery("OptionalProduct.findAll",
				OptionalProduct.class).getResultList();
		return ops;

	}

	public OptionalProduct createOptionalProduct(String name, double fee) {
		OptionalProduct op = new OptionalProduct();
		// TODO name already existent ... 
		op.setName(name);
		op.setFee(fee);

		em.persist(op);

		return op;

	}

	public OptionalProduct findOptionalProductByName(String name) {
		return em.find(OptionalProduct.class, name);
	}

}
