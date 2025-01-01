package com.contacts.filters;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.contacts.cache.SessionCache;
import com.contacts.dao.UserDAO;
import com.contacts.model.Session;

/**
 * Servlet Filter implementation class AuthFilter
 */
@WebFilter("/*")
public class AuthFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		if (httpReq.getRequestURI().endsWith(".css") || httpReq.getRequestURI().endsWith(".js")
				|| httpReq.getRequestURI().endsWith(".svg") || httpReq.getRequestURI().endsWith(".ico")) {
			chain.doFilter(request, response);
			return;
		}
		HttpSession httpsession = httpReq.getSession();
		String sessionId = "";
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		boolean isAuthenticated = false;
		Cookie[] cookies = httpReq.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals("session")) {
					sessionId = c.getValue();
					if (SessionCache.activeSessions.containsKey(sessionId)) {
						SessionCache.updateUserSession(sessionId, LocalDateTime.now());
						isAuthenticated = true;
					} else {
						UserDAO userdao = new UserDAO();
						Session session = userdao.getUserSession(sessionId);
						if (session != null) {
							SessionCache.updateUserSession(sessionId, LocalDateTime.now());
							isAuthenticated = true;
						} else {
							c.setValue("");
							c.setMaxAge(0);
							httpRes.addCookie(c);
							httpsession.setAttribute("message", "Sorry, Your Session has been expired!");
						}
					}
				}
			}
		}
		System.out.println(httpReq.getRequestURI());
//		if (session != null) {
//			if (session.getAttribute("user") != null) {
//				isAuthenticated = true;
//			}
//		}
		if (httpReq.getRequestURI().endsWith("logout")) {
			if (isAuthenticated) {
				chain.doFilter(request, response);
				return;
			}
			httpRes.sendRedirect("/home.jsp");
			return;
		}
		if (httpReq.getRequestURI().endsWith("/") || httpReq.getRequestURI().endsWith("index.jsp")
				|| httpReq.getRequestURI().endsWith("login.jsp") || httpReq.getRequestURI().endsWith("login")
				|| httpReq.getRequestURI().endsWith("signup.jsp") || httpReq.getRequestURI().endsWith("signup")) {
			System.out.println(isAuthenticated);
			if (isAuthenticated) {
				httpRes.sendRedirect("/home.jsp");
				return;
			}
			chain.doFilter(request, response);
			return;
		} else {
			if (isAuthenticated) {
				chain.doFilter(request, response);
				return;
			} else {
				httpRes.sendRedirect("/index.jsp");
			}
		}
	}

}
