/*******************************************************************************
 * Copyright (c) 2015-2016, WSO2.Telco Inc. (http://www.wso2telco.com) 
 * 
 * All Rights Reserved. WSO2.Telco Inc. licences this file to youunder the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.identity.application.authentication.endpoint.oauth2;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class OAuth2Login.
 */
public class OAuth2Login extends HttpServlet {

	 
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The pages. */
	private static Map<String, String> pages = new HashMap<String, String>();

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// load all of em
		Enumeration initParams = config.getInitParameterNames();
		while (initParams.hasMoreElements()) {
			String paramName = (String) initParams.nextElement();
			pages.put(paramName, config.getInitParameter(paramName));
		}

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                              IOException {

		String loginPage = null;
        String authzPage = null;
		String errorPage = null;
		String consentPage = null;

		// loading configured pages
		String applicationName = request.getParameter("application");

		if (applicationName != null) {
			String loginPageParam = applicationName.trim() + "-LoginPage";
            String authzPageParam = applicationName.trim() + "-AuthzPage";
			String errorPageParam = applicationName.trim() + "-ErrorPage";
			String consentPageParam = applicationName.trim() + "-ConsentPage";

			String value = pages.get(loginPageParam);
			if (value != null) {
				loginPage = value;
			}
			value = pages.get(errorPageParam);
			if (value != null) {
				errorPage = value;
			}
			value = pages.get(consentPageParam);
			if (value != null) {
				consentPage = value;
			}
            value = pages.get(authzPageParam);
            if(value != null){
                authzPage = value;
            }
		}

		// setting the login page
		if (request.getRequestURI().contains("/oauth2_login.do")) {
			String queryString = getSafeText(request.getQueryString());
			if (loginPage != null) {
				response.sendRedirect(loginPage + "?" + queryString);
			} else if (pages.get("Global-LoginPage") != null) {
				response.sendRedirect(pages.get("Global-LoginPage") + "?" + queryString);
			} else {
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}

        // setting the authorization page
        } else if (request.getRequestURI().contains("/oauth2_authz.do")) {
            String queryString = getSafeText(request.getQueryString());
            if (errorPage != null) {
                response.sendRedirect(authzPage + "?" + queryString);
            } else if (pages.get("Global-AuthzPage") != null) {
                response.sendRedirect(pages.get("Global-AuthzPage") + "?" + queryString);
            } else {
                request.getRequestDispatcher("oauth2_authz.jsp").forward(request, response);
            }

		// setting the error page	
		} else if (request.getRequestURI().contains("/oauth2_error.do")) {
			String queryString = getSafeText(request.getQueryString());
			if (errorPage != null) {
				response.sendRedirect(errorPage + "?" + queryString);
			} else if (pages.get("Global-ErrorPage") != null) {
				response.sendRedirect(pages.get("Global-ErrorPage") + "?" + queryString);
			} else {
				request.getRequestDispatcher("oauth2_error.jsp").forward(request, response);
			}

		// setting the consent page	
		} else if (request.getRequestURI().contains("/oauth2_consent.do")) {
			String queryString = getSafeText(request.getQueryString());
			if (consentPage != null) {
				response.sendRedirect(consentPage + "?" + queryString);
			} else if (pages.get("Global-ConsentPage") != null) {
				response.sendRedirect(pages.get("Global-ConsentPage") + "?" + queryString);
			} else {
				request.getRequestDispatcher("oauth2_consent.jsp").forward(request, response);
			}

		} else {
			request.getRequestDispatcher("oauth2_error.jsp").forward(request, response);

		}

	}
	
	/**
	 * Gets the safe text.
	 *
	 * @param text the text
	 * @return the safe text
	 */
	public static String getSafeText(String text) {
		if (text == null) {
			return text;
		}
		text = text.trim();
		if (text.indexOf('<') > -1) {
			text = text.replace("<", "&lt;");
		}
		if (text.indexOf('>') > -1) {
			text = text.replace(">", "&gt;");
		}
		return text;
	}

}