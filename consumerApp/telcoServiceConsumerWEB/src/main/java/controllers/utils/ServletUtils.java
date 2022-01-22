package controllers.utils;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public final class ServletUtils {
	private ServletUtils() {} 
	/**
	 * HTML mode template engine initializer. 
	 * @param s HttpServlet whose templateEngine has to be initialized. 
	 * @param subFolder : something like "WEB-INF/templates/" where templates can be found. 
	 * @return
	 */
	public static TemplateEngine initHelper (HttpServlet s, String subFolder) {
		ServletContext servletContext = s.getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		templateResolver.setPrefix("/"+subFolder);
		return templateEngine; 
	}
}
