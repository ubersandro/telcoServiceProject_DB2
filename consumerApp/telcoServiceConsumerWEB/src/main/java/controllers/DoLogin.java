package controllers;

import java.io.IOException;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.Consumer;
import entities.TelcoUser;
import exceptions.TupleNotFoundException;
import exceptions.WrongCredentialsException;
import services.OrderService;
import services.UserService;

@WebServlet("/Login")
public class DoLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private UserService userService;
	@EJB 
	private OrderService os; 
	private TemplateEngine templateEngine;

	public DoLogin() {
	}

	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "/");
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("loginUsername");
		String password = req.getParameter("loginPassword");
		TelcoUser user;
		String path; 
		try {
			user = userService.checkUserCredentials(username, password);//check whether or not the user exists
			
			if(userService.isConsumer(user)) {
				//Consumer login 
				Consumer c = userService.retrieveConsumer(user); 
				req.getSession().setAttribute("user", c);
				
				if(req.getSession().getAttribute("tmpOrder")==null) { // if the user is not logging after attempting to pay an order 
					path = getServletContext().getContextPath() + "/HomePage"; 
					resp.sendRedirect(path);
				}
				else { //there is a temporary order to pay 
					path = getServletContext().getContextPath() +"/Confirmation";  
					resp.sendRedirect(path); 
				}//else tmpOrderNotNull 
			}//if is consumer 
			
			else {//employee login 
				req.getSession().setAttribute("user", userService.retrieveEmployee(user));
				path = getServletContext().getContextPath() + "/EmployeeHomePage"; 
				resp.sendRedirect(path);
			}
		} catch (WrongCredentialsException | TupleNotFoundException e) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale()); // setting the error
																								// message in the
																								// template
			ctx.setVariable("loginFailedMSG", "Login FAILED! Incorrect username or password");
			path = "index"; // user still in the LandingPage
			templateEngine.process(path, ctx, resp.getWriter());
		}
	}// doPost

	@Override
	public void destroy() {
	}
}
