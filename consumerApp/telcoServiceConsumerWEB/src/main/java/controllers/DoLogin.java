package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.templatemode.TemplateMode;
//import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.Consumer;
import exceptions.UserNotFoundException;
import exceptions.WrongCredentialsException;
import services.ConsumerService;

@WebServlet ("/DoLogin")
public class DoLogin extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/ConsumerService")
	private ConsumerService cs ;
	//private TemplateEngine templateEngine; 
	
	public DoLogin() {}

	public void init() throws ServletException {
//		ServletContext servletContext = getServletContext();
//		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
//		templateResolver.setTemplateMode(TemplateMode.HTML);
//		this.templateEngine = new TemplateEngine();
//		this.templateEngine.setTemplateResolver(templateResolver);
//		templateResolver.setSuffix(".html");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("loginUsername"); 
		String password = req.getParameter("loginPassword");
		try {
			Consumer c = cs.checkCredentials(username, password);
			req.getSession().setAttribute("user", c);
			
			
		} catch (WrongCredentialsException | UserNotFoundException e) {
			e.printStackTrace();
		} 
		
		System.out.println(username+password);
	}
	
	@Override
	public void destroy() {}
	
}
