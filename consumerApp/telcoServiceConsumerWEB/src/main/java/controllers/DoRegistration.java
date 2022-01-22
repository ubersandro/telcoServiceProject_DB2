package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.thymeleaf.*;
import org.thymeleaf.context.*;

import controllers.utils.ServletUtils;
import exceptions.UserAlreadyExistentException;
import services.ConsumerService;

@WebServlet("/Registration")
public class DoRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private ConsumerService cs; // TODO change this, unify with Employee service
	private TemplateEngine templateEngine;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//parameters extraction 
		String username = (String) req.getParameter("regUsername");
		String email = (String) req.getParameter("regEmail");
		String password = (String) req.getParameter("regPassword");
		//context extraction 
		ServletContext servletContext = this.getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		String template = "index";
		
		try { //registration attempt 
			cs.doRegistration(username, password, email); // TODO rename method to addUser
			ctx.setVariable("regOKMSG", "The registration went fine!");

		} catch (UserAlreadyExistentException e) { //registration failed 
			ctx.setVariable("regOKMSG", "The registration went fine!");
		}
		finally { //either ways 
			templateEngine.process(template, ctx, resp.getWriter());
		}
	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, ""); //root path 
	}
}
