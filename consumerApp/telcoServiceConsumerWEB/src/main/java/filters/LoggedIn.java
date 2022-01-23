package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoggedIn implements Filter {
	
    public LoggedIn() {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse 
			response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		HttpSession s = req.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			res.sendRedirect(loginpath);
			System.out.println("REDIRECTING THE USER TO LOGIN"); //BEWARE if cookie servicePackage is still in the request... 
			return;
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {}

}
