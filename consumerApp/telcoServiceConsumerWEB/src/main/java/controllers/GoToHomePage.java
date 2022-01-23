package controllers;

import java.io.IOException;
import java.util.List;

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
import entities.ServicePackage;
import services.ServicePackageService;

@WebServlet("/HomePage")
public class GoToHomePage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@EJB
	private ServicePackageService sps; 
	
	private TemplateEngine templateEngine; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//retrieve all the service packages 
		List<ServicePackage> l  = sps.findAllServicePackages(); 
		//insert into template 
		String template = "HomePage";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackages", l); 
		//include a session ID to identify a non logged user and associate the state of his request (order from buy page) 
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
