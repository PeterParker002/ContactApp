package com.contacts.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
		System.out.println(httpReq.getRemoteAddr());
		HttpSession session = httpReq.getSession(false);
		boolean isAuthenticated = false;
		System.out.println(httpReq.getRequestURI());
		if (session != null) {
			if (session.getAttribute("user") != null) {
				isAuthenticated = true;
			}
		}
		if (httpReq.getRequestURI().endsWith(".css") || httpReq.getRequestURI().endsWith(".js")) {
			chain.doFilter(request, response);
			return;
		}
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
