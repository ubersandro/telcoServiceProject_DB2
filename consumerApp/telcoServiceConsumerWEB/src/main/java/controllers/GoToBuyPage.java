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
 * This servlet manages the choice of optional products and validity period 
 * to associate with a given service package along with the starting date of the subscription. 
 * After choosing all the parameters, the user is redirected to a 
 * CONFIRMATION page which DISPLAYS ALL THE INFO about the purchase.
 * @author ubersandro
 *
 */

@WebServlet("/BuyPage")
public class GoToBuyPage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@EJB 
	private ServicePackageService sps; 
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//retrieve the package from the request
		int packageID = 0 ; // TODO find a way to extract it from a request 
		List<OptionalProduct> associableProducts = sps.findAssociableOptionalProducts(packageID);
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}
	
}
