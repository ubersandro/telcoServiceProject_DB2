package controllers;

import java.io.IOException;

import javax.ejb.EJB;
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
import exceptions.*;
import services.*;
import entities.*;

@WebServlet ("/Login")
public class DoLogin extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@EJB
	private ConsumerService cs ;	
	private TemplateEngine templateEngine; 
	
	public DoLogin() {}

	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); 
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String username = req.getParameter("loginUsername"); 
		String password = req.getParameter("loginPassword");
		Consumer c;
		String path;
		try {
			c = cs.checkCredentials(username, password);
			req.getSession().setAttribute("user", c); // binds a customer object to the session
			path = getServletContext().getContextPath() + "/HomePage";
			resp.sendRedirect(path);
		} catch (WrongCredentialsException | UserNotFoundException e) {  
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale()); //setting the error message in the template 
			ctx.setVariable("loginFailedMSG", "Login FAILED! Incorrect username or password");
			path = "/index.html"; //user still in the LandingPage
			templateEngine.process(path, ctx, resp.getWriter());
			e.printStackTrace();
		}
			
	}//doPost
	
	@Override
	public void destroy() {}
	
}
