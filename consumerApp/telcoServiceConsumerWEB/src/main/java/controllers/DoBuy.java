package controllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.Consumer	;
import entities.Order;
import entities.OrderStatus;
import services.OptionalProductService;
import services.OrderService;
import services.ServicePackageService;

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
		Order toBePaid;
		String rejectedOrderIDString = req.getParameter("rejectedOrderID"); //input hidden field with id if the order put in confirmation is rejected 
		if(rejectedOrderIDString!=null) {  
			toBePaid = os.findOrderByID(Integer.parseInt(rejectedOrderIDString));   
		}
		else { //there is a temporary order in the session 
			HttpSession session = req.getSession(); 
			Order tmpOrder = (Order) session.getAttribute("tmpOrder"); //ORDER OBJECT MISSING SOME ATTRIBUTES
			session.removeAttribute("tmpOrder"); 
			//order completion 
			tmpOrder.setConsumer((Consumer) session.getAttribute("user"));
			tmpOrder.setDate(Calendar.getInstance()); //DATE 
			tmpOrder.setTime(new Date()); //TIME OF CREATION 
			tmpOrder.setStatus(OrderStatus.NEWLY_CREATED); 
			toBePaid = os.addOrder(tmpOrder); // orderPersisted 
		}
		//PAYMENT SIMULATION
		boolean orderAccepted = req.getParameter("PAYMENT_MODE")!=null? req.getParameter("PAYMENT_MODE").equalsIgnoreCase("RIGHT"):true;
		//PAYMENT SIMULATION
		Consumer c = (Consumer) req.getSession().getAttribute("user"); 
		if (orderAccepted) {
			// mark order as paid
			os.markAsPaid(toBePaid.getId(), c.getUsername()); 
			resp.sendRedirect("HomePage"); 
		} else {// rejection
				// mark as rejected --> this activates the TRIGGERS 
			os.markAsRejected(toBePaid.getId(), c.getUsername());
			String orderErrorTemplate = "OrderRejected";
			final WebContext ctx = new WebContext(req, resp, getServletContext(), req.getLocale());
			ctx.setVariable("orderID", toBePaid.getId()); //TODO explain this extra step 
			templateEngine.process(orderErrorTemplate, ctx, resp.getWriter());
		}

	}
	
	

}
