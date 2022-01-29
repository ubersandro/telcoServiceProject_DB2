package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.OptionalProduct;
import entities.Order;
import entities.ServicePackage;
import entities.ValidityPeriod;
import services.OptionalProductService;
import services.OrderService;
import services.ServicePackageService;

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
	@EJB
	private OrderService orderService; 
	
	/**
	 * This method assumes that a generic user (even not logged in) has created an order request. It fecthes the 
	 * parameters of the order request (service subscription) but it does not create an order. Only logged customers can 
	 * create orders, so the method redirects the user to a Confirmation page which displays all the information about the
	 * given TENTATIVE purchase.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//session retrieval 
		// info retrieval from request 
		String[] chosenOptsKeys = req.getParameterValues("optionalProduct"); // can be null!
		String startingDateString = req.getParameter("startingDate");
		int validityPeriodKEY = Integer.parseInt(req.getParameter("validityPeriod")); //months of subscription 
		int packageID = Integer.parseInt(req.getParameter("packageID"));  
		// object retrieval
		ServicePackage servicePackage = sps.findServicePackage(packageID);  

		List<OptionalProduct> chosenOptionalProducts = new LinkedList<OptionalProduct>();
		if(chosenOptsKeys!=null) //if at least a product is chosen 
			for (String prodName : chosenOptsKeys)
				chosenOptionalProducts.add(optServ.findOptionalProductByName(prodName));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar startingDate = Calendar.getInstance(); 
		try {
			date = sdf.parse(startingDateString);
			startingDate = Calendar.getInstance();
			startingDate.setTime(date);
		} catch (ParseException e) {}
		
		ValidityPeriod chosenVP = new ValidityPeriod(validityPeriodKEY); 
		int monthsOfSubscription = validityPeriodKEY; 
		
		// total value computation
		double servicePackageMonthlyFee = servicePackage.getCosts().get(chosenVP);
		double totalFeeOptionalProducts = 0D;
		for (OptionalProduct op : chosenOptionalProducts)
			totalFeeOptionalProducts += op.getFee();
		double totalValue = (servicePackageMonthlyFee + totalFeeOptionalProducts) * monthsOfSubscription;
		//once all values have been extracted -> put into a session object 
		 
		Order tmp = new Order(); //with no time and date and customer
		tmp.setStartingDate(startingDate);
		tmp.setIncludedOptionalProducts(chosenOptionalProducts);
		tmp.setServicePackage(servicePackage);
		tmp.setValidityPeriod(chosenVP);
		tmp.setTotalValue(totalValue);
		HttpSession session = req.getSession(); 		
		session.setAttribute("tmpOrder", tmp); //persist the temporary order in the session 
		
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
	
	/**
	 * Two cases have to be distinguished
	 * 1) an user has logged in and is ready to pay the incomplete not yet persisted order he was creating
	 * 2) an INSOLVENT user wants to attempt a new payment for an OLD already persisted order 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Order tmp; 
		ServicePackage servicePackage; 
		List<OptionalProduct> chosenOptionalProducts = null;  
		if(req.getParameter("rejectedOrderID")!=null) { 
			tmp = orderService.findOrderByID(Integer.parseInt(req.getParameter("rejectedOrderID"))); //it already exists, it will have an associate customer and a date/time
			servicePackage = orderService.retrieveServicePackageFromOrderId(tmp.getId()); 
			chosenOptionalProducts = orderService.retrieveIncludedOptionalProductsFromOrderId(tmp.getId()) ;
		}else {//tmp order not yet written to the DB 
			tmp = (Order) req.getSession().getAttribute("tmpOrder"); 
			servicePackage = tmp.getServicePackage(); 
			chosenOptionalProducts = //optional products in the tmpOrder can't be null 
					tmp.getIncludedOptionalProducts()!=null ?
							new LinkedList<>(tmp.getIncludedOptionalProducts()): null; 
		}
		
		ServletContext servletContext = this.getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", servicePackage);
		ctx.setVariable("chosenOptionalProducts", chosenOptionalProducts);
		ctx.setVariable("chosenValidityPeriod", tmp.getValidityPeriod()); 
		ctx.setVariable("chosenStartingDate", tmp.getStartingDate());
		ctx.setVariable("totalValue", tmp.getTotalValue()); 
		if(req.getParameter("rejectedOrderID")!=null) 
			ctx.setVariable("rejectedOrderID", Integer.parseInt(req.getParameter("rejectedOrderID"))); //mark it
		String templatePath = "ConfirmationPage";
		templateEngine.process(templatePath, ctx, resp.getWriter());
	}
	
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/");
	}
}
