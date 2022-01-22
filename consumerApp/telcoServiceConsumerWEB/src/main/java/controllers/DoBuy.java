package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	@EJB 
	private OptionalProductService optServ; 
	
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
	 * 3a) If it is accepted the user gets redirected to the Home Page WITH OK MSG. 
	 * 3b) Otherwise he just gets redirected to a new alert page containing some 
	 * 		error message like "You have been marked as insolvent", "your purchase failed...."  
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//info retrieval from request 
		int packageID = Integer.parseInt(req.getParameter("servicePackageID")); //TODO check the request for this param
		String[] chosenOptsKeys = req.getParameterValues("optionalProduct");
		String startingDateString = req.getParameter("startingDate"); 
		int validityPeriodKEY = Integer.parseInt(req.getParameter("validityPeriod")); 
		double totalValue = Double.parseDouble(req.getParameter("totalValue")); 
		
		//object retrieval 
		ServicePackage servicePackage = sps.findServicePackage(packageID); 
		
		List<OptionalProduct> chosenOptionalProducts = new LinkedList<OptionalProduct>(); 
		for(String prodName : chosenOptsKeys) chosenOptionalProducts.add(optServ.findOptionalProductByName(prodName)); 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar startingDate = Calendar.getInstance(); //TODO refactor! 
		try {
			date = sdf.parse(startingDateString);
			startingDate = Calendar.getInstance();
			startingDate.setTime(date);
		} catch (ParseException e) {
			// TODO manage wrong date format ... BUT REMEMBER, HERE NO CHECKS NEEDED
		}
		
		ValidityPeriod chosenVP = new ValidityPeriod(validityPeriodKEY); //TODO correct it 
		
		Consumer consumer = (Consumer) req.getSession().getAttribute("user"); //TODO check login
		
		//order creation and write to DB 
		Order o = os.addOrder(consumer, servicePackage, chosenOptionalProducts, 
				startingDate, totalValue, chosenVP); 
		// payment attempt 
		boolean orderAccepted = new Random().nextBoolean(); // PAYMENT SIMULATION 
		if(orderAccepted) {
			//mark order as paid  
			os.markAsPaid(o.getId()); 
			ServletContext servletContext = this.getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
			ctx.setVariable("orderSuccededMSG", "The order passed through!"); 
			String templatePath = "HomePage";
			templateEngine.process(templatePath, ctx);
		}
		else {
			//mark as rejected --> this activates the TRIGGERS 
			os.markAsRejected(o.getId()); 
			//redirect to error page 
			String orderErrorTemplate = "OrderRejected"; 
			final WebContext ctx= new WebContext(req, resp, getServletContext(), req.getLocale());  
			ctx.setVariable("orderID", o.getId()); 
			templateEngine.process(orderErrorTemplate, ctx, resp.getWriter()); 
		}
	}
	
}
