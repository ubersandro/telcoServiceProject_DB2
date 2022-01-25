package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		double fee = Double.parseDouble(req.getParameter("fee"));
		ops.createOptionalProduct(name, fee);
		// TODO associate OptionalProd to Service Packages
		String[] packageIdStrings = req.getParameterValues("servicePackage");
		if (packageIdStrings != null) { //TODO it can never be null
			for (String idString : packageIdStrings) {
				int packageID = Integer.parseInt(idString);
				sps.associateOptionalProduct(name, packageID);
			}
		}
		resp.sendRedirect(getServletContext().getContextPath() + "/EmployeeHomePage");
	}
// TODO put error message when you select no services... 
	@Override
	public void init() throws ServletException {

	}

}
