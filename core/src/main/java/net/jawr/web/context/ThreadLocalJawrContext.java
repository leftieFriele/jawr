/**
 * Copyright 2009 * @author Matt Ruby, Ibrahim Chaehoi
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
package net.jawr.web.context;

import javax.management.ObjectName;

/**
 * This class defines the context for Jawr, it holds the context in a ThreadLocal object.
 * 
 * @author Matt Ruby
 * @author Ibrahim Chaehoi
 */
public final class ThreadLocalJawrContext {

	/** The java version system property name */
	private static final String JAVA_VERSION_SYSTEM_PROPERTY = "java.version";

	/** The java 1.4 version prefix */
	private static final String JAVA_VERSION_1_4_PREFIX = "1.4";

	/**
	 * debugOverride will allow us to override production mode on a request by request basis.
	 * ThreadLocal is used to hold the overridden status throughout a given request.
	 */
	private static ThreadLocal jawrContext = new ThreadLocal(){

		/* (non-Javadoc)
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		protected Object initialValue() {
			return new JawrContext();
		}
	    
	};
	
	/**
	 * The debugOverride will be automatially set to false
	 */
	private ThreadLocalJawrContext() {
		
	}
	
	/**
	 * Returns the mbean object name of the Jawr config manager
	 * @return the mbean object name of the Jawr config manager
	 */
	public static ObjectName getJawrConfigMgrObjectName() {
		
		return ((JawrContext) jawrContext.get()).getJawrConfigMgrObjectName();
	}

	/**
	 * Sets the mbean object name of the Jawr config manager
	 * @param mbeanObjectName the mbean object name of the Jawr config manager
	 */
	public static void setJawrConfigMgrObjectName(ObjectName mbeanObjectName) {

		((JawrContext) jawrContext.get()).setJawrConfigMgrObjectName(mbeanObjectName);
	}
	
	/**
	 * Get the flag stating that production mode should be overridden
	 * @return the flag stating that production mode should be overridden
	 */
	public static boolean isDebugOverriden() {
		
		return ((JawrContext) jawrContext.get()).isDebugOverriden();
	}

	/**
	 * Set the override flag that will live only for this request
	 * @param override the flag to set
	 */
	public static void setDebugOverriden(boolean override) {

		((JawrContext) jawrContext.get()).setDebugOverriden(override);
	}
	
	/**
	 * Returns the flag indicating that we are using making a bundle processing at build time
	 * @return the flag indicating that we are using making a bundle processing at build time
	 */
	public static boolean isBundleProcessingAtBuildTime() {
		return ((JawrContext) jawrContext.get()).isBundleProcessingAtBuildTime();
	}

	/**
	 * Sets the flag indicating that we are using making a bundle processing at build time
	 * @param bundleProcessingAtBuildTime the flag to set
	 */
	public static void setBundleProcessingAtBuildTime(boolean bundleProcessingAtBuildTime) {
		((JawrContext) jawrContext.get()).setBundleProcessingAtBuildTime(bundleProcessingAtBuildTime);
	}
	
	/**
	 * Returns the current request
	 * @return the request
	 */
	public static String getRequestURL() {
		return ((JawrContext) jawrContext.get()).getRequestURL();
	}

	/**
	 * Sets the request
	 * @param request the request to set
	 */
	public static void setRequest(String requestURL) {
		((JawrContext) jawrContext.get()).setRequestURL(requestURL);
	}
	
	/**
	 * Sets the mbean object name
	 * @param mbeanObjectName the mbean object name
	 */
	public static void reset() {

		if (System.getProperty(JAVA_VERSION_SYSTEM_PROPERTY).startsWith(JAVA_VERSION_1_4_PREFIX)) {
			((JawrContext) jawrContext.get()).reset();
		}else{
			jawrContext.remove();
		}
		
	}
	
}
