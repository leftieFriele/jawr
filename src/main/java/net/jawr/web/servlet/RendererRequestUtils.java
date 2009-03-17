/**
 * Copyright 2007 Jordi Hern�ndez Sell�s
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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.config.ThreadLocalDebugOverride;

import org.apache.log4j.Logger;

/**
 * Utilities for tag rendering components, which help in handling request lifecycle aspects. 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class RendererRequestUtils {
	private static final Logger log = Logger.getLogger(RendererRequestUtils.class.getName());
	private static final String ADDED_COLLECTIONS_LOG = "net.jawr.web.taglib.ADDED_COLLECTIONS_LOG";
	
	
	/**
	 * Retrieve or create the set to store all added bundles, which is used
	 * to avoid adding a bundle more than once during a single request. 
	 * @param request
	 * @return
	 */
	public static Set getAddedBundlesLog(ServletRequest request)
	{
		Set set = null;
		if(null != request.getAttribute(ADDED_COLLECTIONS_LOG))
			set = (Set) request.getAttribute(ADDED_COLLECTIONS_LOG);
		else
		{
			set = new HashSet();
			request.setAttribute(ADDED_COLLECTIONS_LOG, set);
		}
		return set;
	}
	
    /**
     * Determines wether gzip is suitable for the current request given the current config. 
     * @param req 
	 * @param jawrConfig
     * @return 
     */	
	public static boolean isRequestGzippable(HttpServletRequest req,
			JawrConfig jeesConfig) {
		boolean rets;
		// If gzip is completely off, return false. 
        if(!jeesConfig.isGzipResourcesModeOn())
            rets = false;
        else if(req.getHeader("Accept-Encoding") != null && 
	    req.getHeader("Accept-Encoding").indexOf("gzip") != -1 ) {
            
            // If gzip for IE6 or less is off, the user agent is checked to avoid compression. 
            if(!jeesConfig.isGzipResourcesForIESixOn()) {
                String agent = req.getHeader("User-Agent");
                if(log.isDebugEnabled())
                    log.debug("User-Agent for this request:" +agent);                    
                
                if(null != agent && agent.indexOf("MSIE") != -1) {
                    rets =  agent.indexOf("MSIE 4") == -1 && 
                            agent.indexOf("MSIE 5") == -1 && 
                            agent.indexOf("MSIE 6") == -1;
                    if(log.isDebugEnabled())
                        log.debug("Gzip enablement for IE executed, with result:" + rets);
                }
                else rets = true;
            }
            else rets = true;                
        }
        else rets = false;
		return rets;
	}
	
	/**
     * Determines wether to override the debug settings. 
     * Sets the debugOverride status on ThreadLocalDebugOverride
     * @param req 
	 * @param jawrConfig
     * 
     */	
	public static void setRequestDebuggable(HttpServletRequest req,
			JawrConfig jeesConfig) {
		
		// make sure we have set an overrideKey
		// make sure the overrideKey exists in the request
		// lastly, make sure the keys match
		if( jeesConfig.getDebugOverrideKey().length() > 0
			&& null != req.getParameter("overrideKey")
			&& jeesConfig.getDebugOverrideKey().equals(req.getParameter("overrideKey"))
			) {
			ThreadLocalDebugOverride.setDebugOverride(true);
		} else {
			ThreadLocalDebugOverride.setDebugOverride(false);
		}
		
	}
}
