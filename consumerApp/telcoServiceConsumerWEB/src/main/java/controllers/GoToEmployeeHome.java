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
import entities.OptionalProduct;
import entities.ServicePackage;
import services.OptionalProductService;
import services.ServicePackageService;

@WebServlet("/EmployeeHomePage")
public class GoToEmployeeHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	@EJB
	private ServicePackageService sps; 
	@EJB
	private OptionalProductService optService; 
	
	/**
	 * After the login the object session.user contains an Employee object. 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// retrieve all the service packages
		List<ServicePackage> l = sps.findAllServicePackages();
		List<OptionalProduct> opts = optService.findAllOptionalProducts(); 
		String template = "EmployeeHomePage";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackages", l);		
		ctx.setVariable("optionalProducts", opts);
		
		templateEngine.process(template, ctx, resp.getWriter());
	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); 
	}
}
