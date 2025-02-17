package com.contacts.filters;

import java.io.IOException;

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
import com.contacts.logger.MyCustomLogger;
import com.contacts.model.Session;
import com.contacts.model.User;

@WebFilter("/*")
public class AuthFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;
	private static final MyCustomLogger logger = LoggerFactory
			.getLogger("/home/karthik-tt0479/eclipse-workspace/FirstProject/src/main/resources/logs/access.log");

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		boolean isResources = httpReq.getRequestURI().endsWith(".css") || httpReq.getRequestURI().endsWith(".js")
				|| httpReq.getRequestURI().endsWith(".svg") || httpReq.getRequestURI().endsWith(".ico")
				|| httpReq.getRequestURI().endsWith("notifyAvailableServerUpdate")
				|| httpReq.getRequestURI().endsWith("notifyUserUpdate")
				|| httpReq.getRequestURI().endsWith("notifySessionChange")
				|| httpReq.getRequestURI().endsWith("notify");
		if (isResources) {
			chain.doFilter(request, response);
			return;
		}
		HttpSession httpsession = httpReq.getSession();
		String sessionId = "";
		int userId = 0;
		String logMessage = "";
		boolean isAuthenticated = false;
		Cookie[] cookies = httpReq.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals("session")) {
					sessionId = c.getValue();
					if (SessionCache.activeSessionObjects.containsKey(sessionId)) {
						SessionCache.activeSessionObjects.get(sessionId).setLastAccessedAt(System.currentTimeMillis());
						userId = SessionCache.activeSessionObjects.get(sessionId).getUserId();
						System.out.println(SessionCache.activeSessionObjects);
						isAuthenticated = true;
					} else {
						Session session = UserDAO.getUserSession(sessionId);
						if (session != null) {
							SessionCache.activeSessionObjects.put(sessionId, session);
							userId = session.getUserId();
							User user = UserDAO.getUserInfo(userId);
							SessionCache.addUserToCache(userId, user);
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
		if (userId != 0 && !sessionId.equals(""))
			logMessage = "User ID: " + userId + " Session ID: " + sessionId;
		triggerLog(httpReq, logMessage);
		if (httpReq.getRequestURI().endsWith("logout")) {
			if (isAuthenticated) {
				chain.doFilter(request, response);
				return;
			}
			httpRes.sendRedirect("/home.jsp");
			return;
		}
//		|| httpReq.getRequestURI().endsWith("login-with-google") || httpReq.getRequestURI().endsWith("google-callback") || httpReq.getRequestURI().endsWith("/profile")
		boolean isPublicPage = httpReq.getRequestURI().endsWith("/") || httpReq.getRequestURI().endsWith("index.jsp")
				|| httpReq.getRequestURI().endsWith("login.jsp") || httpReq.getRequestURI().endsWith("login")
				|| httpReq.getRequestURI().endsWith("signup.jsp") || httpReq.getRequestURI().endsWith("signup");
		if (isPublicPage) {
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

	public void triggerLog(HttpServletRequest request, String message) {
		logger.info("GET", request.getRemoteAddr(), request.getRequestURI(), message);
	}

}
