package controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.taglibs.standard.tag.common.fmt.ParseNumberSupport;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.OptionalProduct;
import entities.Service;
import entities.ServicePackage;
import entities.ValidityPeriod;
import exceptions.TupleAlreadyExistentException;
import services.OptionalProductService;
import services.ServicePackageService;
import services.ServicesUtils;

/**
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
	private TemplateEngine templateEngine;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// services optionalproducts costs
		String serviceIDStrings[] = req.getParameterValues("service");

		if (serviceIDStrings == null) {
			error("Select at least one service!", req, resp); 
			return;
		}

		Double fee12 = Double.parseDouble(req.getParameter("fee12"));
		Double fee24 = Double.parseDouble(req.getParameter("fee24"));
		Double fee36 = Double.parseDouble(req.getParameter("fee36"));

		String name = req.getParameter("packageName");
		String productNames[] = req.getParameterValues("optionalProduct");

		int[] serviceIDs = new int[serviceIDStrings.length];
		for (int i = 0; i < serviceIDStrings.length; i++)
			serviceIDs[i] = Integer.parseInt(serviceIDStrings[i]);
		// objects
		List<OptionalProduct> optionalProducts = new LinkedList<>();
		if (productNames != null) {
			for (String ps : productNames) {
				OptionalProduct tmp = ops.findOptionalProductByName(ps);
				optionalProducts.add(tmp);
			}
		}
		
		List<Service> services = new LinkedList<>();
		for (int id : serviceIDs) {
			Service tmp = servUtil.findServiceById(id);
			services.add(tmp);
		}

		Map<ValidityPeriod, Double> costs = new HashMap<>();
		costs.put(new ValidityPeriod(12), fee12);
		costs.put(new ValidityPeriod(24), fee24);
		costs.put(new ValidityPeriod(36), fee36);
		
		ServicePackage sp = new ServicePackage();
		// if a service package with the very same name doesn't exist then it is created
		// and populated
		sp.setName(name);
		sp.setCosts(costs);
		sp.setOptionalProducts(optionalProducts);
		sp.setServices(services);
		
		try {
			sps.addServicePackage(sp);
		} catch (TupleAlreadyExistentException e) {
			error(e.getMessage(),req, resp) ;
			return;
		}
		resp.sendRedirect(getServletContext().getContextPath() + "/EmployeeHomePage");
	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/");
	}
	
	private void error(String msg, HttpServletRequest req, HttpServletResponse resp) {
		String template = "ServicePackageCreationPage";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale()); // template
		ctx.setVariable("optionalProducts", ops.findAllOptionalProducts());
		ctx.setVariable("services", servUtil.findAllServices());
		ctx.setVariable("creationFailedMSG", "Creation failed! Select at least one service. ");
		try {
			templateEngine.process(template, ctx, resp.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
