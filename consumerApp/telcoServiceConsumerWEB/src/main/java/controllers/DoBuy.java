package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.*;

import controllers.utils.ServletUtils;
import entities.*;

import services.*;

/**
 * From the CONFIRMATION page, the user goes on to attempt a payment for a given
 * order.
 * 
 * @author ubersandro
 *
 */
@WebServlet("/Buy")
public class DoBuy extends HttpServlet {
	@EJB
	private OrderService os;
	@EJB
	private ServicePackageService sps;
	@EJB
	private OptionalProductService optServ;

	TemplateEngine templateEngine;

	@Override
	public void destroy() {

	}

	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/");
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Given the tmpOrder object 1) an Order is created 2) the payment of the order
	 * is attempted. 3a) If it is accepted the user gets redirected to the Home Page
	 * WITH OK MSG. 3b) Otherwise he just gets redirected to a new alert page
	 * containing some error message like "You have been marked as insolvent", "your
	 * purchase failed...."
	 * 
	 * TMPORDER -> it misses Customer 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(); // TODO payment of an order already created and rejected
		Order finalOrder;
		if (session.getAttribute("tmpOrder") != null) {
			//partial order retrieval 
			Order tmpOrder = (Order) session.getAttribute("tmpOrder"); //ORDER OBJECT MISSING SOME ATTRIBUTES
			session.removeAttribute("tmpOrder"); // performance
			//order completion 
			tmpOrder.setConsumer((Consumer) session.getAttribute("user"));
			tmpOrder.setDate(Calendar.getInstance()); //DATE 
			tmpOrder.setTime(new Date()); //TIME OF CREATION 
			tmpOrder.setStatus(OrderStatus.NEWLY_CREATED); //TODO maybe it's redundant ....
			finalOrder = os.addOrder(tmpOrder); // orderPersisted 
		} else // preexistent (already persisted) order
		{
			int orderID = Integer.parseInt(req.getParameter("rejectedOrderID")); 
			finalOrder = os.findOrderByID(orderID);  
		}
		
		//PAYMENT SIMULATION
		boolean orderAccepted = req.getParameter("PAYMENT_MODE")!=null? req.getParameter("PAYMENT_MODE").equalsIgnoreCase("RIGHT"):true;
		//PAYMENT SIMULATION
		
		if (orderAccepted) {
			// mark order as paid
			os.markAsPaid(finalOrder.getId()); 
			resp.sendRedirect("HomePage"); // TODO print a message
		} else {// rejection
				// mark as rejected --> this activates the TRIGGERS TODO managed change from REJECTED TO REJECTED 
			os.markAsRejected(finalOrder.getId());
			String orderErrorTemplate = "OrderRejected";
			final WebContext ctx = new WebContext(req, resp, getServletContext(), req.getLocale());
			ctx.setVariable("orderID", finalOrder.getId()); //TODO explain this extra step 
			templateEngine.process(orderErrorTemplate, ctx, resp.getWriter());
		}

	}

}
