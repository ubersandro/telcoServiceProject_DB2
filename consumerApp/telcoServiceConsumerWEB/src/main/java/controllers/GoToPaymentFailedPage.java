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
 * This servlet manages the display of Order data for confirmation.
 * In case of confirmation, it redirects the user to the DoBuy servlet accessible at /BuyServicePackage. 
 * @author ubersandro
 *
 */
@WebServlet("/OrderError")
public class GoToPaymentFailedPage extends HttpServlet{
	private static final long serialVersionUID = 1L; 
	private TemplateEngine templateEngine; 
	
	/**
	 * The failed order ID is included in the request (orderID parameter). 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		//signal the error, display orderID only 
		
		// TODO Put a go home button on the template 
	}//doPost
	
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); 
	}
}