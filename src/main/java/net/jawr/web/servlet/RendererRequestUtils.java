/**
 * Copyright 2007-2009 Jordi Hern�ndez Sell�s, Matt Ruby, Ibrahim Chaehoi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.jawr.web.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.jawr.web.JawrConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.config.jmx.JawrApplicationConfigManager;
import net.jawr.web.context.ThreadLocalJawrContext;
import net.jawr.web.resource.bundle.generator.GeneratorRegistry;

import org.apache.log4j.Logger;

/**
 * Utilities for tag rendering components, which help in handling request lifecycle aspects.
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Matt Ruby
 * @author Ibrahim Chaehoi
 * 
 */
public class RendererRequestUtils {
	
	/** The logger */
	private static final Logger log = Logger.getLogger(RendererRequestUtils.class.getName());
	
	/** The added collection log */
	private static final String ADDED_COLLECTIONS_LOG = "net.jawr.web.taglib.ADDED_COLLECTIONS_LOG";

	/**
	 * Retrieve or create the set to store all added bundles, which is used to avoid adding a bundle more than once during a single request.
	 * 
	 * @param request
	 * @return
	 */
	public static Set getAddedBundlesLog(ServletRequest request) {
		Set set = null;
		if (null != request.getAttribute(ADDED_COLLECTIONS_LOG))
			set = (Set) request.getAttribute(ADDED_COLLECTIONS_LOG);
		else {
			set = new HashSet();
			request.setAttribute(ADDED_COLLECTIONS_LOG, set);
		}
		return set;
	}

	/**
	 * Determines wether gzip is suitable for the current request given the current config.
	 * 
	 * @param req
	 * @param jawrConfig
	 * @return
	 */
	public static boolean isRequestGzippable(HttpServletRequest req, JawrConfig jeesConfig) {
		boolean rets;
		// If gzip is completely off, return false.
		if (!jeesConfig.isGzipResourcesModeOn())
			rets = false;
		else if (req.getHeader("Accept-Encoding") != null && req.getHeader("Accept-Encoding").indexOf("gzip") != -1) {

			// If gzip for IE6 or less is off, the user agent is checked to avoid compression.
			if (!jeesConfig.isGzipResourcesForIESixOn()) {
				String agent = req.getHeader("User-Agent");
				if (log.isDebugEnabled())
					log.debug("User-Agent for this request:" + agent);

				if (null != agent && agent.indexOf("MSIE") != -1) {
					rets = agent.indexOf("MSIE 4") == -1 && agent.indexOf("MSIE 5") == -1 && agent.indexOf("MSIE 6") == -1;
					if (log.isDebugEnabled())
						log.debug("Gzip enablement for IE executed, with result:" + rets);
				} else
					rets = true;
			} else
				rets = true;
		} else
			rets = false;
		return rets;
	}

	/**
	 * Determines wether to override the debug settings. Sets the debugOverride status on ThreadLocalJawrContext
	 * 
	 * @param req the request
	 * @param jawrConfig the jawr config
	 * 
	 */
	public static void setRequestDebuggable(HttpServletRequest req, JawrConfig jawrConfig) {

		// make sure we have set an overrideKey
		// make sure the overrideKey exists in the request
		// lastly, make sure the keys match
		if (jawrConfig.getDebugOverrideKey().length() > 0 && null != req.getParameter("overrideKey")
				&& jawrConfig.getDebugOverrideKey().equals(req.getParameter("overrideKey"))) {
			ThreadLocalJawrContext.setDebugOverriden(true);
		} else {
			ThreadLocalJawrContext.setDebugOverriden(false);
		}

		// Inherit the debuggable property of the session if the session is a debuggable one
		inheritSessionDebugProperty(req);

	}

	/**
	 * Sets a request debuggable if the session is a debuggable session.
	 * 
	 * @param req the request
	 */
	public static void inheritSessionDebugProperty(HttpServletRequest request) {

		HttpSession session = request.getSession();
		if (session != null) {
			String sessionId = session.getId();

			JawrApplicationConfigManager appConfigMgr = (JawrApplicationConfigManager) session.getServletContext().getAttribute(
					JawrConstant.JAWR_APPLICATION_CONFIG_MANAGER);

			// If the session ID is a debuggable session ID, activate debug mode for the request.
			if (appConfigMgr != null && appConfigMgr.isDebugSessionId(sessionId)) {
				ThreadLocalJawrContext.setDebugOverriden(true);
			}
		}
	}
	
	/**
	 * Returns true if the request URL is a SSL request (https://) 
	 * @param request the request
	 * @return true if the request URL is a SSL request
	 */
	public static boolean isSslRequest(HttpServletRequest request) {
		
		String requestUrl = request.getRequestURL().toString();
		return requestUrl.toLowerCase().startsWith(JawrConstant.HTTPS_URL_PREFIX);
	}

	/**
	 * converts a generation path (such as jar:/some/path/file) into 
	 * a request path that the request handler can understand and process.  
	 * @param path
	 * @return
	 */
	public static String createGenerationPath(String path, GeneratorRegistry registry){
		try {
			path = registry.getDebugModeGenerationPath(path) 
				+ "?" 
				+ JawrRequestHandler.GENERATION_PARAM 
				+ "=" 
				+ URLEncoder.encode(path, "UTF-8");
		} catch (UnsupportedEncodingException neverHappens) {
			/*URLEncoder:how not to use checked exceptions...*/
			throw new RuntimeException("Something went unexpectedly wrong while encoding a URL for a generator. ",
										neverHappens);
		}
		return path;
	}

	/**
	 * Adds a key and value to the request path
	 * & or ? will be used as needed
	 * 
	 * path + ? or & + parameterKey=parameter
	 * 
	 * @param path the url to add the parameterKey and parameter too
	 * @param parameterKey the key in the get request (parameterKey=parameter)
	 * @param parameter the parameter to add to the end of the url
	 * @return a String with the url parameter added: path + ? or & + parameterKey=parameter
	 */
	public static String addGetParameter(String path, String parameterKey, String parameter){
		StringBuffer sb = new StringBuffer(path); 
		if(path.indexOf("?") > 0) {
			sb.append("&");
		} else {
			sb.append("?");
		}
		sb.append(parameterKey + "="+ parameter);
		return sb.toString();
	}

	
}
