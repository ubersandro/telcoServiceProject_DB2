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
import entities.Consumer;
import entities.Order;
import entities.ServicePackage;
import entities.UserStatus;
import services.OrderService;
import services.ServicePackageService;

@WebServlet("/HomePage")
public class GoToHomePage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@EJB
	private ServicePackageService sps; 
	@EJB 
	OrderService os ; //debug
	
	private TemplateEngine templateEngine; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//retrieve all the service packages 
		List<ServicePackage> servicePackages  = sps.findAllServicePackages(); 
		List<Order> rejectedOrders = null; 
		if(req.getSession().getAttribute("user")!=null) {
			Consumer c = (Consumer) req.getSession().getAttribute("user"); 
			if(c.getStatus().equals(UserStatus.INSOLVENT))rejectedOrders = os.findAllRejectedOrders(c); 
		}
		
		//insert into template 
		String template = "HomePage";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("servicePackages", servicePackages); 
		ctx.setVariable("rejectedOrders", rejectedOrders);
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
