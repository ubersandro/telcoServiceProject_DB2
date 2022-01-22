package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import controllers.utils.ServletUtils;
/**
 * This servlet manages the display of Order data for confirmation.
 * In case of confirmation, it redirects the user to the DoBuy servlet accessible at /BuyServicePackage. 
 * @author ubersandro
 *
 */
@WebServlet("/Confirmation")
public class GoToConfirmationPage extends HttpServlet{
	private static final long serialVersionUID = 1L; 
	private TemplateEngine templateEngine; 
	
	/**
	 * When provided package, optional products, starting date, and validity period this
	 * method populates the ConfirmationPage template and processes it. 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String templatePath = "ConfirmationPage"; 
	}
	 
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this); 
	}
}
