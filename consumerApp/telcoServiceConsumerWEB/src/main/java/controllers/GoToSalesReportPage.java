package controllers;

		
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import org.thymeleaf.TemplateEngine;

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
	}
	
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates"); 
	}
}
