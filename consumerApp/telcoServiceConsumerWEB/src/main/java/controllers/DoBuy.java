package controllers;

import java.io.IOException;
import java.util.*;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.*;

import services.*;

/**
 * From the CONFIRMATION page, the user goes on to attempt a payment for a given order. 
 * @author ubersandro
 *
 */
@WebServlet ("/BuyPage")
public class DoBuy extends HttpServlet{
	
	@EJB  (name ="services.OrderService")
	private OrderService os;  
	@EJB 
	private ServicePackageService sps; 
	
	
	@Override
	public void destroy() {
			
	}

	@Override
	public void init() throws ServletException {
		
	}

	private static final long serialVersionUID = 1L;
	
	/**
	 * Given all the info provided in the request (package, optProds selected, validityPeriod, startingDate, totalValue) 
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
		Calendar startingDate = null; //@TODO  
		List<OptionalProduct> products = null; //@TODO
		ValidityPeriod validityPeriod= null; 
		Consumer consumer = (Consumer) req.getSession().getAttribute("user");
		
		//order is created and written to DB 
		os.addOrder(consumer, servicePackage, products, startingDate, packageID, validityPeriod); 
		
		//payment is attempted 
		boolean orderAccepted = new Random().nextBoolean(); 
		
		if(orderAccepted) {
			//redirect to home page 
		}
		else {
			//redirect to error page prior to redirect to homepage 
		}
	}
	
}
