package controllers;

import java.io.IOException;
import java.util.*;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.*;
import services.*;

/**
 * This servlet presents the user with all the information circa the
 * ServicePackage chosen. ServicePackage infos are a) validity periods and
 * respective fees b) associable optional products c) package name d) services
 * 
 * @author ubersandro
 *
 */

@WebServlet("/BuyPage")
public class GoToBuyPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private ServicePackageService sps;
	private TemplateEngine templateEngine; 
	
	/**
	 * REQUEST PARAMETERS: servicePackageID
	 * RESPONSE : servicePackage, services, optionalProducts, ValidityPeriods 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// retrieve the package from the request
		int packageID = 0; // TODO find a way to extract it from a request 
		ServicePackage sp = sps.findServicePackage(packageID);
		
		
//		List<OptionalProduct> associableProducts = sps.findAssociableOptionalProducts(packageID);
//		Map<ValidityPeriod, Double> costs = sp.getCosts(); // eagerly fetched
//		List<Service> services = sp.getServices(); // TODO diversification of Services
//		
		
		//template retrieval and parameters insertion  
		String template = "BuyPage"; 
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", sp);
		// TODO check if you need to extract the parameters here or the template can do it itself. 
		templateEngine.process(template, ctx, resp.getWriter());
	}

	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this); 
	}

}
