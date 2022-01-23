package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import services.*;
import entities.*;

/**
 * This servlet manages the display of Order data for confirmation. In case of
 * confirmation, it redirects the user to the DoBuy servlet accessible at
 * /BuyServicePackage.
 * 
 * @author ubersandro
 *
 */
@WebServlet("/Confirmation")
public class GoToConfirmationPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	private ServicePackageService sps;
	@EJB
	private OptionalProductService optServ;

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//session retrieval 
		HttpSession session = req.getSession(); 
		System.err.println("SESSION:"+ session.getId());//debug 
		
		// info retrieval from request 
		String[] chosenOptsKeys = req.getParameterValues("optionalProduct"); // can be null!
		String startingDateString = req.getParameter("startingDate");
		int validityPeriodKEY = Integer.parseInt(req.getParameter("validityPeriod")); //MONTHS OF SUBSCRIPTION

		// object retrieval
		ServicePackage servicePackage = (ServicePackage) session.getAttribute("chosenServicePackage"); 
		assert(servicePackage!=null); //debug
		List<OptionalProduct> chosenOptionalProducts = new LinkedList<OptionalProduct>();
		if(chosenOptsKeys!=null) //if at least a product is chosen 
			for (String prodName : chosenOptsKeys)
				chosenOptionalProducts.add(optServ.findOptionalProductByName(prodName));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar startingDate = Calendar.getInstance(); // TODO refactor!
		try {
			date = sdf.parse(startingDateString);
			startingDate = Calendar.getInstance();
			startingDate.setTime(date);
			//TODO CHECK AFTER TODAY
		} catch (ParseException e) {
			// TODO manage wrong date format ... CHECK AFTER TODAY, FORMAT ECC. 
		}
		
		ValidityPeriod chosenVP = new ValidityPeriod(validityPeriodKEY); 
		int monthsOfSubscription = validityPeriodKEY; // to be clear 
		
		// total value computation
		double servicePackageMonthlyFee = servicePackage.getCosts().get(chosenVP);
		double totalFeeOptionalProducts = 0D;
		for (OptionalProduct op : chosenOptionalProducts) //TODO check this calculation, orders have 0 value 
			totalFeeOptionalProducts += op.getFee();
		double totalValue = (servicePackageMonthlyFee + totalFeeOptionalProducts) * monthsOfSubscription;
		//once all values have been extracted -> put into a session object 
		 
		Order tmp = new Order(); //with no time and date and customer
		tmp.setStartingDate(startingDate);
		tmp.setIncludedOptionalProducts(chosenOptionalProducts);
		tmp.setServicePackage(servicePackage);
		tmp.setValidityPeriod(chosenVP);
		session.removeAttribute("chosenServicePackage");
		session.setAttribute("tmpOrder", tmp);
		
		
		//redirection to confirmation page
		ServletContext servletContext = this.getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", servicePackage);
		ctx.setVariable("chosenOptionalProducts", chosenOptionalProducts);
		ctx.setVariable("chosenValidityPeriod", chosenVP); 
		ctx.setVariable("chosenStartingDate", startingDate);
		ctx.setVariable("totalValue", totalValue);
		String templatePath = "ConfirmationPage";
		templateEngine.process(templatePath, ctx, resp.getWriter());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Order tmp = (Order) req.getSession().getAttribute("tmpOrder"); 
		ServletContext servletContext = this.getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", tmp.getServicePackage());
		ctx.setVariable("chosenOptionalProducts", tmp.getIncludedOptionalProducts());
		ctx.setVariable("chosenValidityPeriod", tmp.getValidityPeriod()); 
		ctx.setVariable("chosenStartingDate", tmp.getStartingDate()); 
		ctx.setVariable("totalValue", tmp.getTotalValue());
		String templatePath = "ConfirmationPage";
		templateEngine.process(templatePath, ctx, resp.getWriter());
	}
	
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/");
	}
}
