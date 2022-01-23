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
import exceptions.UserNotFoundException;
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

	/**
	 * TODO implements class UserService with method whatTypeIsIt(username) which
	 * tells whether a User is an Employee or a Consumer. TODO include employee
	 * login
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("loginUsername");
		String password = req.getParameter("loginPassword");
		TelcoUser user;
		String path; //where to redirect the user  
		try {
			user = userService.checkUserCredentials(username, password);//the user exists
			
			if(userService.isConsumer(user)) {
				Consumer c = userService.retrieveConsumer(user); 
				req.getSession().setAttribute("user", c);
				if(req.getSession().getAttribute("tmpOrder")==null) {
					path = getServletContext().getContextPath() + "/HomePage"; 
					resp.sendRedirect(path);
				}// ifTmpOrderNull 
				else { //there is a temporder
					path = getServletContext().getContextPath() +"/Confirmation";  
					resp.sendRedirect(path); 
				}//else tmpOrderNotNull 
			}//if is consumer 
			
			else {//employee login 
				req.getSession().setAttribute("user", userService.retrieveEmployee(user));
				path = getServletContext().getContextPath() + "/EmployeeHomePage"; 
				resp.sendRedirect(path);
			}
		} catch (WrongCredentialsException | UserNotFoundException e) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale()); // setting the error
																								// message in the
																								// template
			System.out.println("CURRENT	 CONTEXT PATH " + servletContext.getContextPath());
			ctx.setVariable("loginFailedMSG", "Login FAILED! Incorrect username or password");
			path = "index"; // user still in the LandingPage
			templateEngine.process(path, ctx, resp.getWriter());
		}
	}// doPost

	@Override
	public void destroy() {
	}
}
