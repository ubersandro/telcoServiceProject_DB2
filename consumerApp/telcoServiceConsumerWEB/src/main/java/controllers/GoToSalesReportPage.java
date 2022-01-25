package controllers;

		
import java.io.IOException;
import java.util.Arrays;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import org.thymeleaf.TemplateEngine;

import controllers.utils.ServletUtils;
import services.SalesReportService;

@WebServlet("/SalesReportPage")
public class GoToSalesReportPage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine ; 
	@EJB 
	private SalesReportService salesReport; 
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Object []> l = salesReport.findSalesAllSP(); 
		for(Object [] a : l) {
			int spID = (Integer) a[0]; 
			long count = (Long) a[1];
			System.err.println("SERVICE PACKAGE : "+ spID + " "+ "count : " + count );
			System.err.println("SERVICE PACKAGE : "+ a[0] + " "+ "count : " + a[1] );
			
		}
	}
	
	@Override
	public void init() throws ServletException {
		templateEngine = ServletUtils.initHelper(this, "WEB-INF/templates"); 
	}
}
