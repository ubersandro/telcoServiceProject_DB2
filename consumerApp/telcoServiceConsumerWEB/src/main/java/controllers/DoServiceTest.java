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
@WebServlet ("/DoServiceTest")
public class DoServiceTest extends HttpServlet{
	
	@EJB  
	private OrderService os;  
	
	@EJB 
	private ServicePackageService sps; 
	@EJB
	private ServicesUtils su; 
	
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
		int id = 1; //given an id, am I able to retrieve the correct Service? 
		
	}
	
}
