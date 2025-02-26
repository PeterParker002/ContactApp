package com.contacts.filters;

import java.io.IOException;
import java.util.logging.Logger;

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
import com.contacts.logger.LoggerFactory;
import com.contacts.model.Session;

@WebFilter("/*")
public class AuthFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger();

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		 	
		httpRes.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
		httpRes.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		httpRes.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpRes.setHeader("Access-Control-Allow-Credentials", "true");
        
        if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
        	httpRes.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
        	httpRes.setHeader("Access-Control-Allow-Credentials", "true");
        	httpRes.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        	httpRes.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        	httpRes.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
		if (isResourcePage(httpReq)) {
			chain.doFilter(request, response);
			return;
		}
		HttpSession httpsession = httpReq.getSession();
		Session session = null;
		boolean isAuthenticated = false;
		Cookie[] cookies = httpReq.getCookies();
		Cookie sessionCookie = getSessionIdFromCookie(cookies);
		if (sessionCookie != null) {
			String sessionId = sessionCookie.getValue();
			if (SessionCache.activeSessions.containsKey(sessionId)) {
				session = SessionCache.updateSessionCache(sessionId);
				request.setAttribute("user_id", session.getUserId());
				isAuthenticated = true;
			} else if ((session = UserDAO.getUserSession(sessionId)) != null) {
				SessionCache.addSessionCache(session);
				request.setAttribute("user_id", session.getUserId());
				isAuthenticated = true;
			} else {
				sessionCookie = clearCookie(sessionCookie);
				httpRes.addCookie(sessionCookie);
				httpsession.setAttribute("message", "Sorry, Your Session has been expired!");
			}
		}
		triggerLog(httpReq, session);
		boolean isPublic = isPublicPage(httpReq);
		if (isPublic && isAuthenticated) {
			httpRes.sendRedirect("/home.jsp");
		} else if ((isPublic && !isAuthenticated) || (!isPublic && isAuthenticated)) {
			chain.doFilter(request, response);
		} else {
			httpRes.sendRedirect("/index.jsp");
		}
	}

	public Cookie clearCookie(Cookie cookie) {
		cookie.setValue("");
		cookie.setMaxAge(0);
		return cookie;
	}

	public boolean isPublicPage(HttpServletRequest httpReq) {
		boolean isPublic = httpReq.getRequestURI().endsWith("/") || httpReq.getRequestURI().endsWith("index.jsp")
				|| httpReq.getRequestURI().endsWith("login.jsp") || httpReq.getRequestURI().endsWith("login")
				|| httpReq.getRequestURI().endsWith("signup.jsp") || httpReq.getRequestURI().endsWith("signup");
		return isPublic;
	}

	public void triggerLog(HttpServletRequest request, Session session) {
		StringBuilder sb = new StringBuilder();
		sb = sb.append(request.getMethod()).append(" ").append(request.getRemoteAddr()).append(" ")
				.append(request.getRequestURI()).append(" ");
		if (session != null) {
			sb = sb.append("User ID: ").append(session.getUserId()).append(" Session ID: ")
					.append(session.getSessionId());
		}
		logger.info(sb.toString());
	}

	public Cookie getSessionIdFromCookie(Cookie[] cookies) {
		Cookie session = null;
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("session")) {
				session = cookie;
				break;
			}
		}
		return session;
	}

	public boolean isResourcePage(HttpServletRequest httpReq) {
		boolean isResources = httpReq.getRequestURI().endsWith(".css") || httpReq.getRequestURI().endsWith(".js")
				|| httpReq.getRequestURI().endsWith(".svg") || httpReq.getRequestURI().endsWith(".ico")
				|| httpReq.getRequestURI().endsWith("notifyAvailableServerUpdate")
				|| httpReq.getRequestURI().endsWith("notifyUserUpdate")
				|| httpReq.getRequestURI().endsWith("notifySessionChange")
				|| httpReq.getRequestURI().endsWith("notify");
		return isResources;
	}

}
