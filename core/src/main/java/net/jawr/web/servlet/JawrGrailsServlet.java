/**
 * Copyright 2008-2009 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
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

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jawr.web.JawrConstant;

import org.apache.log4j.Logger;

/**
 * Specialized subclass of the JawrServlet which s automatically mapped to the servlet context 
 * in grails applications.  
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public class JawrGrailsServlet extends JawrServlet {

	private static final Logger log = Logger.getLogger(JawrGrailsServlet.class);
	
	private static final long serialVersionUID = -7749799838520309579L;
	public static final String JAWR_GRAILS_JS_CONFIG  = "net.jawr.grails.js.config";
	public static final String JAWR_GRAILS_CSS_CONFIG = "net.jawr.grails.css.config";
	public static final String JAWR_GRAILS_IMG_CONFIG = "net.jawr.grails.img.config";
	public static final String JAWR_GRAILS_CONFIG_HASH = "net.jawr.grails.config.hash";
	public static final String PROPERTIES_KEY = "configProperties";
	private Integer configHash;
	
	/* (non-Javadoc)
	 * @see net.jawr.web.servlet.JawrServlet#init()
	 */
	public void init() throws ServletException {
		Map config = null;
		String type = getServletConfig().getInitParameter("type");
		
		configHash = (Integer)getServletContext().getAttribute(JAWR_GRAILS_CONFIG_HASH);
		
		if(JawrConstant.CSS_TYPE.equals(type))
			config = (Map) getServletContext().getAttribute(JAWR_GRAILS_CSS_CONFIG);
		else if(JawrConstant.IMG_TYPE.equals(type))
			config = (Map) getServletContext().getAttribute(JAWR_GRAILS_IMG_CONFIG);
		else
			config = (Map) getServletContext().getAttribute(JAWR_GRAILS_JS_CONFIG);
		
		Properties jawrProps = (Properties)config.get(PROPERTIES_KEY);
		try {
			if(JawrConstant.IMG_TYPE.equals(type)){
				this.requestHandler = new JawrImageRequestHandler(getServletContext() , config, jawrProps );
			}else{
				this.requestHandler = new JawrRequestHandler(getServletContext() , config, jawrProps );
			}
		}catch (ServletException e) {
			log.fatal("Jawr servlet with name" +  getServletConfig().getServletName() +" failed to initialize properly. ");
			log.fatal("Cause:");
			log.fatal(e.getMessage(),e);
			throw e;
		}catch (RuntimeException e) {
			log.fatal("Jawr servlet with name" +  getServletConfig().getServletName() +" failed to initialize properly. ");
			log.fatal("Cause: ");
			log.fatal(e.getMessage(),e);
			throw new ServletException(e);
		}
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.servlet.JawrServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Reload config if applies
		if(!configHash.equals((Integer)getServletContext().getAttribute(JAWR_GRAILS_CONFIG_HASH)))
			this.init();
		
		super.doGet(req, resp);
	}

	
}
