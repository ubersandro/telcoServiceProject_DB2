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
import org.thymeleaf.context.*;

import controllers.utils.ServletUtils;
import entities.*;

import services.*;

/**
 * From the CONFIRMATION page, the user goes on to attempt a payment for a given order. 
 * @author ubersandro
 *
 */
@WebServlet ("/BuyServicePackage")
public class DoBuy extends HttpServlet{
	@EJB  
	private OrderService os;  
	@EJB 
	private ServicePackageService sps; 
	
	TemplateEngine templateEngine; 
	
	@Override
	public void destroy() {
			
	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); 
	}

	private static final long serialVersionUID = 1L;
	
	/**
	 * Given all the info provided in the request 
	 * (package, optProds selected, validityPeriod, startingDate, totalValue) 
	 * 1) an Order is created 
	 * 2) the payment of the order is attempted. 
	 * 3a) If it is accepted the user gets redirected to the Home Page. 
	 * 3b) Otherwise he just gets redirected to a new alert page containing some 
	 * 		error message like "You have been marked as insolvent", "your purchase failed...."  
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//info retrieval 
		int packageID = Integer.parseInt(req.getParameter("servicePackageID")); 
		ServicePackage servicePackage = sps.findServicePackage(packageID); 
		Calendar startingDate = null; // TODO  retrieve Date from request 
		List<OptionalProduct> products = null; // TODO retrieve products (list ??? JSON???) 
		ValidityPeriod validityPeriod= null; // TODO retrieve from request 
		Consumer consumer = (Consumer) req.getSession().getAttribute("user");
		double totalValue = Double.parseDouble(req.getParameter("totalValue")); 
		//order is created and written to DB 
		Order o = os.addOrder(consumer, servicePackage, products, startingDate, totalValue, validityPeriod); 
		// payment attempt 
		boolean orderAccepted = new Random().nextBoolean(); // PAYMENT SIMULATION 
		if(orderAccepted) {
			//mark order as paid  
			os.markAsPaid(o.getId()); 
			ServletContext servletContext = this.getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
			ctx.setVariable("orderSuccededMSG", "The order passed through!"); // TODO update home template 
			String templatePath = "HomePage";
			templateEngine.process(templatePath, ctx);
		}
		else {
			//mark as rejected --> this activates the TRIGGERS 
			os.markAsRejected(o.getId()); 
			//redirect to error page 
			req.getSession().setAttribute("orderID", o.getId()); 
			String errorPagePath = "/OrderError";  
			resp.sendRedirect(errorPagePath); 
			
		}
	}
	
}
