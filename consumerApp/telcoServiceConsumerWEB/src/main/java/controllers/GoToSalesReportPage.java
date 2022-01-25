package controllers;

		
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import controllers.utils.ServletUtils;
import entities.Auditing;
import entities.Consumer;
import entities.OptionalProduct_sales;
import entities.Order;
import entities.PurchasesPackageValidityPeriod;
import services.OrderService;
import services.SalesReportService;
import services.UserService;

@WebServlet("/SalesReportPage")
public class GoToSalesReportPage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine ; 
	@EJB 
	private SalesReportService salesReport; 
	@EJB
	private UserService userService; 
	@EJB
	private OrderService orderService ; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Object []> salesPerSP = salesReport.findSalesAllSP(); // total purchases per package 
		List<Auditing> alerts = salesReport.findAllAuditing();  
		List<OptionalProduct_sales> bestSellers = salesReport.findBestSeller(); // only select the first one ! 
		List<PurchasesPackageValidityPeriod> salesSpVp = salesReport.findSalesAllSPVP();
		List<Object []> salesSpWithOpts = salesReport.findTotalSalesWithOPs();
		List<Object[]> salesSpWithoutOpts = salesReport.findTotalSalesWithoutOPs();
		List<Consumer> insolventUsers = userService.findInsolventUsers();
		List<Order> rejectedOrders = orderService.findAllRejectedOrders(); 
		List<Object []> avgOptsSP = salesReport.findAvgOpts(); 
		
		// insert data into the template 
		String template = "SalesReport"; 
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		ctx.setVariable("salesPerSP", salesPerSP); 
		ctx.setVariable("alerts", alerts); 
		ctx.setVariable("bestSellers", bestSellers); 
		ctx.setVariable("salesSpVp", salesSpVp); 
		ctx.setVariable("salesSpWithOpts", salesSpWithOpts); 
		ctx.setVariable("salesSpWithoutOpts", salesSpWithoutOpts); 
		ctx.setVariable("insolventUsers", insolventUsers); 
		ctx.setVariable("rejectedOrders", rejectedOrders); 
		ctx.setVariable("avgOptsSP", avgOptsSP); 
		templateEngine.process(template, ctx, resp.getWriter()); 
		
	}//doGet
		//TODO fix negative numbers in total purchases no opts 
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates/"); 
	}
}
