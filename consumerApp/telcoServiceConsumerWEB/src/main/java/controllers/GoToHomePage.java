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
import exceptions.NoSuchTupleException;
import services.OrderService;
import services.ServicePackageService;
import services.UserService;

@WebServlet("/HomePage")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private ServicePackageService sps;
	@EJB
	private UserService userService;
	@EJB
	private OrderService orderService;

	private TemplateEngine templateEngine;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// retrieve all the service packages
		List<ServicePackage> servicePackages = sps.findAllServicePackages();
		// if user is logged in and is insolvent 
		List<Order> rejectedOrders = null;
		if (req.getSession().getAttribute("user") != null) {
			Consumer c = (Consumer) req.getSession().getAttribute("user");
			// this method REFRESHES the entity to check whether changes to the DB
			// occurred (independently from the application) 
			if (userService.consumerIsInsolvent(c.getUsername()))  
				rejectedOrders = orderService.findRejectedOrdersByUsername(c.getUsername());
		} // if user il s

		// insertion of paramters into the template
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
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); // templates root
	}

}
