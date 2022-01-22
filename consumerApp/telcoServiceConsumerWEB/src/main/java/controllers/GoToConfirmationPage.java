package controllers;

import java.io.IOException;
import java.util.*;

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

	/**
	 * When provided package, optional products, starting date, and validity period
	 * this method populates the ConfirmationPage template and processes it.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// info retrieval
		HttpSession session = req.getSession();
		ServicePackage sp = (ServicePackage) session.getAttribute("servicePackage");
		java.util.List<OptionalProduct> chosenOpts = null; // TODO retrieve chosenOptionalProducts
		ValidityPeriod chosenVP = (ValidityPeriod) session.getAttribute("chosenValidityPeriod"); 																		// a request
		Calendar startingDate = (Calendar) session.getAttribute("chosenStartingDate");

		// total value computation
		int monthsOfSubscription = chosenVP.getMonths();
		double servicePackageMonthlyFee = sp.getCosts().get(chosenVP);
		double totalFeeOptionalProducts = 0D;
		for (OptionalProduct op : chosenOpts)
			totalFeeOptionalProducts += op.getFee();
		double totalValue = (servicePackageMonthlyFee + totalFeeOptionalProducts) * monthsOfSubscription;
		ServletContext servletContext = this.getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", sp);
		ctx.setVariable("chosenOptionalProducts", chosenOpts);
		ctx.setVariable("chosenValidityPeriod", chosenVP);
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
