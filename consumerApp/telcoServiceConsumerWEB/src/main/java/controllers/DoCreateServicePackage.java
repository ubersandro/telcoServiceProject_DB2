package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.OptionalProduct;
import entities.Service;
import entities.ServicePackage;
import entities.ValidityPeriod;
import services.OptionalProductService;
import services.ServicePackageService;
import services.ServicesUtils;

/**
 * Once the Employee created the product redirect it here setting creationMSG to
 * OK otherwise set it to error (template)...
 * TODO fix addServicePackage not working 
 * 
 * @author ubersandro
 *
 */
@WebServlet("/CreateServicePackage")
public class DoCreateServicePackage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private OptionalProductService ops;
	@EJB
	private ServicePackageService sps;
	@EJB
	private ServicesUtils servUtil;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// services optionalproducts costs
		String serviceIDStrings[] = req.getParameterValues("service");
		String name = req.getParameter("packageName");
		String productNames[] = req.getParameterValues("optionalProduct");
		Double fee12 = Double.parseDouble(req.getParameter("fee12"));
		Double fee24 = Double.parseDouble(req.getParameter("fee24"));
		Double fee36 = Double.parseDouble(req.getParameter("fee36"));
		int[] serviceIDs = new int[serviceIDStrings.length]; 
		// objects
		List<OptionalProduct> optionalProducts = new LinkedList<>();
		if (productNames != null) {
			for (String ps : productNames) {
				OptionalProduct tmp = ops.findOptionalProductByName(ps);
				assert (tmp != null); // fine
				optionalProducts.add(tmp);
			}
		}
		List<Service> services = new LinkedList<>();
		if (serviceIDs != null) { //TODO enforce the choice of at least one service
			for (int id : serviceIDs) {
				Service tmp = servUtil.findServiceById(id);
				assert (tmp != null);
				services.add(tmp);

			}
		}
		Map<ValidityPeriod, Double> costs = new HashMap<>();
		costs.put(new ValidityPeriod(12), fee12);
		costs.put(new ValidityPeriod(24), fee24);
		costs.put(new ValidityPeriod(36), fee36);
		sps.addServicePackage(name, services, optionalProducts, costs);
		resp.sendRedirect(getServletContext().getContextPath() + "/EmployeeHomePage");
	}

	@Override
	public void init() throws ServletException {

	}

}
