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
 * This servlet presents the user with all the information circa the ServicePackage chosen. 
 * ServicePackage infos are
 * 							a) validity periods and respective fees 
 * 							b) associable optional products 
 * 							c) package name 
 * 							d) services  					
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
				ServicePackage sp = sps.findServicePackage(packageID); 
				String packageName = sp.getName(); 
				
				List<OptionalProduct> associableProducts = sps.findAssociableOptionalProducts(packageID);
				
				Map<ValidityPeriod, Double > costs = sp.getCosts(); //eagerly fetched 
				
				List<Service> services = sp.getServices(); // TODO diversification of Services 
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
