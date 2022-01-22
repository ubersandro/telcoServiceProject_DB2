package controllers;

import java.io.IOException;
import java.util.*;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.*;
import services.*;

/**
 * This servlet presents the user with all the information circa the
 * ServicePackage chosen. ServicePackage infos are a) validity periods and
 * respective fees b) associable optional products c) package name d) services
 * 
 * @author ubersandro
 *
 */

@WebServlet("/BuyPage")
public class GoToBuyPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private ServicePackageService sps;
	private TemplateEngine templateEngine; 
	
	/**
	 * REQUEST PARAMETERS: servicePackageID
	 * RESPONSE : servicePackage, services, optionalProducts, ValidityPeriods 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// retrieve the package from the request
		Integer packageID = Integer.parseInt(req.getParameter("pid")); 
		ServicePackage sp = sps.findServicePackage(packageID);
		List<OptionalProduct> opts = sps.findAssociableOptionalProducts(packageID);
		
		//template parameters insertion  
		String template = "BuyPage"; 
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", sp); //String representation with services included 
		ctx.setVariable("optionalProducts", opts); 
		// TODO what about validity periods ? 
		templateEngine.process(template, ctx, resp.getWriter());
	}

	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this); 
	}

}
