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

import org.thymeleaf.*;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.ServicePackage;
import exceptions.TupleAlreadyExistentException;
import services.OptionalProductService;
import services.ServicePackageService;

/**
 * Once the Employee created the product redirect it here setting creationMSG to
 * OK otherwise set it to error (template)...
 * 
 * @author ubersandro
 *
 */
@WebServlet("/CreateOptionalProduct")
public class DoCreateOptionalProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private OptionalProductService ops;
	@EJB
	private ServicePackageService sps;
	private TemplateEngine templateEngine ;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String feeString = req.getParameter("fee"); 
		double fee = 0D; 
		try{
			fee = Double.parseDouble(feeString);
		}catch (NumberFormatException n) {//unhappy choice ... TODO correct 
			error("Number format error!" , req, resp); 
		}
		try {
			ops.createOptionalProduct(name, fee);
		} catch (TupleAlreadyExistentException e) {
			error(e.getMessage(), req, resp); 
		}
		String[] packageIdStrings = req.getParameterValues("servicePackage");
		if (packageIdStrings != null) {
			for (String idString : packageIdStrings) {
				int packageID = Integer.parseInt(idString);
				sps.associateOptionalProduct(name, packageID);
			}
		}
		else // no packages associated --> redirect to creation page and print an error message 
		{
			error("Associate at least one service package", req, resp); 
		}
		resp.sendRedirect(getServletContext().getContextPath() + "/EmployeeHomePage");
	}

	
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); 
	}

	private void error(String msg, HttpServletRequest req, HttpServletResponse resp) {
		List<ServicePackage> l = sps.findAllServicePackages();
		ServletContext servletContext = getServletContext();
		String template = "/OptionalProductCreationPage" ;
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("creationFailedMSG", msg);
		ctx.setVariable("servicePackages", l);	
		try {
			templateEngine.process(template, ctx, resp.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
