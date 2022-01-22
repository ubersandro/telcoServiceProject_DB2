package controllers;

import java.io.IOException;
import java.util.List;

import javax.ejb.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import controllers.utils.ServletUtils;
import entities.*;
import services.*;

@WebServlet("/HomePage")
public class GoToHomePage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@EJB
	private ServicePackageService sps; 
	
	private TemplateEngine templateEngine; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//retrieve all the service packages 
		List<ServicePackage> l  = sps.findAllServicePackages(); //TODO fix named query not working
		//insert packages in the template (packages, services and validityPeriod) TODO check how to retrieve parameters
		String template = "HomePage";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackages", l); 
		templateEngine.process(template, ctx, resp.getWriter());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp); 
	}

	@Override
	public void destroy() {}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); //templates root 
	}
	
}
