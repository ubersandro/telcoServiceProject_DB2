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
import entities.Service;
import entities.ServicePackage;
import services.OptionalProductService;
import services.ServicePackageService;
import services.ServicesUtils;

@WebServlet("/ServicePackageCreationPage")
public class GoToServicePackageCreationPage extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	private ServicePackageService sps; 
	@EJB
	private OptionalProductService ops; 
	@EJB 
	private ServicesUtils srvcUtil; 
	
	private TemplateEngine templateEngine; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<OptionalProduct> optionalProducts = ops.findAllOptionalProducts();
		List<Service> services = srvcUtil.findAllServices(); 
		String template = "ServicePackageCreationPage";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("optionalProducts", optionalProducts);
		ctx.setVariable("services", services);
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
