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
		
		
		//
		//
		OptionalProduct a = new OptionalProduct("op1", 2.1);
		OptionalProduct b = new OptionalProduct("op2", 2.2);
		OptionalProduct c = new OptionalProduct("op3", 2.3);
		OptionalProduct d = new OptionalProduct("op4", 2.4);
		OptionalProduct e = new OptionalProduct("op5", 2.5);
		
		opts.add(a);
		opts.add(b);
		opts.add(c);
		opts.add(d);
		opts.add(e);
		//
		//
		
		
		//template parameters insertion  
		String template = "BuyPage"; 
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackage", sp); //String representation with services included 
		ctx.setVariable("associableOptionalProducts", opts); 
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
