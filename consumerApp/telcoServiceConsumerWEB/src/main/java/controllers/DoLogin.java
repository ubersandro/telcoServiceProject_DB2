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

import exceptions.*;
import services.*;
import entities.*;

@WebServlet ("/DoLogin")
public class DoLogin extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@EJB
	private ConsumerService cs ;	
	private TemplateEngine templateEngine; 
	
	public DoLogin() {}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
			req.getSession().setAttribute("user", c); // parameter set -> logged user is redirected home 
			path = getServletContext().getContextPath() + "/HomePage";
			resp.sendRedirect(path);
		} catch (WrongCredentialsException | UserNotFoundException e) {  
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale()); //setting the error message in the template 
			ctx.setVariable("loginFailedMSG", "Login FAILED! Incorrect username or password");
			path = "/index.html";
			templateEngine.process(path, ctx, resp.getWriter());
			e.printStackTrace();
		}
			
	}//doPost
	
	@Override
	public void destroy() {}
	
}
