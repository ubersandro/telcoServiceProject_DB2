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

	/**
	 * When provided package, optional products, starting date, and validity period
	 * this method populates the ConfirmationPage template and processes it.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// info retrieval from request 
		int packageID = Integer.parseInt(req.getParameter("servicePackageID")); 
		String[] chosenOptsKeys = req.getParameterValues("optionalProduct");
		String startingDateString = req.getParameter("startingDate");
		int validityPeriodKEY = Integer.parseInt(req.getParameter("validityPeriod")); //MONTHS OF SUBSCRIPTION

		// object retrieval
		ServicePackage servicePackage = sps.findServicePackage(packageID);

		List<OptionalProduct> chosenOptionalProducts = new LinkedList<OptionalProduct>();
		for (String prodName : chosenOptsKeys)
			chosenOptionalProducts.add(optServ.findOptionalProductByName(prodName));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar startingDate = Calendar.getInstance(); // TODO refactor!
		try {
			date = sdf.parse(startingDateString);
			startingDate = Calendar.getInstance();
			startingDate.setTime(date);
		} catch (ParseException e) {
			// TODO manage wrong date format ... CHECK AFTER TODAY, FORMAT ECC. 
		}

		ValidityPeriod chosenVP = new ValidityPeriod(validityPeriodKEY); // TODO correct it
		int monthsOfSubscription = validityPeriodKEY; // to be clear 
		
		// total value computation
		double servicePackageMonthlyFee = servicePackage.getCosts().get(chosenVP);
		double totalFeeOptionalProducts = 0D;
		for (OptionalProduct op : chosenOptionalProducts)
			totalFeeOptionalProducts += op.getFee();
		double totalValue = (servicePackageMonthlyFee + totalFeeOptionalProducts) * monthsOfSubscription;
		//redirection to confirmation page
		ServletContext servletContext = this.getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", servicePackage);
		ctx.setVariable("chosenOptionalProducts", chosenOptionalProducts);
		ctx.setVariable("chosenValidityPeriod", chosenVP); //TODO months could be put here instead...
		ctx.setVariable("chosenStartingDate", startingDate);
		ctx.setVariable("totalValue", totalValue);
		String templatePath = "Confirmation";
		templateEngine.process(templatePath, ctx);
	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/");
	}
}
