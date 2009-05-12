/**
 * Copyright 2009 Ibrahim Chaehoi
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
package net.jawr.web.config.jmx;

import java.util.Properties;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.servlet.JawrRequestHandler;

/**
 * This class defines the MBean which manage the Jawr configuration for a servlet.
 * 
 * @author Ibrahim Chaehoi
 */
public class JawrConfigManager implements JawrConfigManagerMBean {

	/** The request handler */
	private JawrRequestHandler requestHandler;
	
	/** The configuration properties */
	private Properties configProperties;
	
	/**
	 * Constructor
	 * @param requestHandler the request handler
	 * @param properties the properties
	 */
	public JawrConfigManager(JawrRequestHandler requestHandler, Properties properties){
		this.requestHandler = requestHandler;
		this.configProperties = (Properties) properties.clone();
	}
	
	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#getContextPathOverride()
	 */
	public String getContextPathOverride() {
		return configProperties.getProperty(JawrConfig.JAWR_URL_CONTEXTPATH_OVERRIDE);
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#getCssImagePathOverride()
	 */
	public String getCssImagePathOverride() {
		return configProperties.getProperty(JawrConfig.JAWR_CSS_IMAGEPATH_OVERRIDE);
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#getDebugOverrideKey()
	 */
	public String getDebugOverrideKey() {
		return configProperties.getProperty(JawrConfig.JAWR_DEBUG_OVERRIDE_KEY);
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#getDwrMapping()
	 */
	public String getDwrMapping() {
		return configProperties.getProperty(JawrConfig.JAWR_DWR_MAPPING);
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#getImageBundleDefinition()
	 */
	public String getImageBundleDefinition() {
		return configProperties.getProperty(JawrConfig.JAWR_IMAGE_BUNDLE);
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#getImageHashAlgorithm()
	 */
	public String getImageHashAlgorithm() {
		return configProperties.getProperty(JawrConfig.JAWR_IMAGE_HASH_ALGORITHM);
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#isDebugModeOn()
	 */
	public boolean isDebugModeOn() {
		return Boolean.valueOf(configProperties.getProperty(JawrConfig.JAWR_DEBUG_ON)).booleanValue();
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#isGzipResourcesForIESixOn()
	 */
	public boolean isGzipResourcesForIESixOn() {
		return Boolean.valueOf(configProperties.getProperty(JawrConfig.JAWR_GZIP_IE6_ON)).booleanValue();
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#isGzipResourcesModeOn()
	 */
	public boolean isGzipResourcesModeOn() {
		return Boolean.valueOf(configProperties.getProperty(JawrConfig.JAWR_GZIP_ON)).booleanValue();
	}

	/**
	 * @return
	 * @see net.jawr.web.config.JawrConfig#isUsingClasspathCssImageServlet()
	 */
	public boolean isUsingClasspathCssImageServlet() {
		return Boolean.valueOf(configProperties.getProperty(JawrConfig.JAWR_CSS_IMG_USE_CLASSPATH_SERVLET)).booleanValue();
	}

	/**
	 * @param charsetName
	 * @see net.jawr.web.config.JawrConfig#setCharsetName(java.lang.String)
	 */
	public void setCharsetName(String charsetName) {
		configProperties.setProperty(JawrConfig.JAWR_CHARSET_NAME, charsetName);
	}

	/**
	 * @param charsetName
	 * @see net.jawr.web.config.JawrConfig#setCharsetName(java.lang.String)
	 */
	public String getCharsetName() {
		return configProperties.getProperty(JawrConfig.JAWR_CHARSET_NAME);
	}
	
	/**
	 * @param contextPathOverride
	 * @see net.jawr.web.config.JawrConfig#setContextPathOverride(java.lang.String)
	 */
	public void setContextPathOverride(String contextPathOverride) {
		configProperties.setProperty(JawrConfig.JAWR_URL_CONTEXTPATH_OVERRIDE, contextPathOverride);
	}

	/**
	 * @param cssImagePathOverride
	 * @see net.jawr.web.config.JawrConfig#setCssImagePathOverride(java.lang.String)
	 */
	public void setCssImagePathOverride(String cssImagePathOverride) {
		configProperties.setProperty(JawrConfig.JAWR_CSS_IMAGEPATH_OVERRIDE,cssImagePathOverride);
	}

	/**
	 * @param cssLinkFlavor
	 * @see net.jawr.web.config.JawrConfig#setCssLinkFlavor(java.lang.String)
	 */
	public void setCssLinkFlavor(String cssLinkFlavor) {
		configProperties.setProperty(JawrConfig.JAWR_CSSLINKS_FLAVOR, cssLinkFlavor);
	}

	public String getCssLinkFlavor() {
		return configProperties.getProperty(JawrConfig.JAWR_CSSLINKS_FLAVOR);
	}
	
	/**
	 * @param debugMode
	 * @see net.jawr.web.config.JawrConfig#setDebugModeOn(boolean)
	 */
	public void setDebugModeOn(boolean debugMode) {
		configProperties.setProperty(JawrConfig.JAWR_DEBUG_ON, Boolean.toString(debugMode));
	}

	/**
	 * @param debugOverrideKey
	 * @see net.jawr.web.config.JawrConfig#setDebugOverrideKey(java.lang.String)
	 */
	public void setDebugOverrideKey(String debugOverrideKey) {
		configProperties.setProperty(JawrConfig.JAWR_DEBUG_OVERRIDE_KEY, debugOverrideKey);
	}

	/**
	 * @param dwrMapping
	 * @see net.jawr.web.config.JawrConfig#setDwrMapping(java.lang.String)
	 */
	public void setDwrMapping(String dwrMapping) {
		configProperties.setProperty(JawrConfig.JAWR_DWR_MAPPING, dwrMapping);
	}

	/**
	 * @param gzipResourcesForIESixOn
	 * @see net.jawr.web.config.JawrConfig#setGzipResourcesForIESixOn(boolean)
	 */
	public void setGzipResourcesForIESixOn(boolean gzipResourcesForIESixOn) {
		configProperties.setProperty(JawrConfig.JAWR_GZIP_IE6_ON, Boolean.toString(gzipResourcesForIESixOn));
	}

	/**
	 * @param gzipResourcesModeOn
	 * @see net.jawr.web.config.JawrConfig#setGzipResourcesModeOn(boolean)
	 */
	public void setGzipResourcesModeOn(boolean gzipResourcesModeOn) {
		configProperties.setProperty(JawrConfig.JAWR_GZIP_ON, Boolean.toString(gzipResourcesModeOn));
	}

	/**
	 * @param imageBundleDefinition
	 * @see net.jawr.web.config.JawrConfig#setImageBundleDefinition(java.lang.String)
	 */
	public void setImageBundleDefinition(String imageBundleDefinition) {
		configProperties.setProperty(JawrConfig.JAWR_IMAGE_BUNDLE, imageBundleDefinition);
	}

	/**
	 * @param imageHashAlgorithm
	 * @see net.jawr.web.config.JawrConfig#setImageHashAlgorithm(java.lang.String)
	 */
	public void setImageHashAlgorithm(String imageHashAlgorithm) {
		configProperties.setProperty(JawrConfig.JAWR_IMAGE_HASH_ALGORITHM, imageHashAlgorithm);
	}

	/**
	 * @param useClasspathCssImgServlet
	 * @see net.jawr.web.config.JawrConfig#setUseClasspathCssImageServlet(boolean)
	 */
	public void setUsingClasspathCssImageServlet(boolean useClasspathCssImgServlet) {
		configProperties.setProperty(JawrConfig.JAWR_CSS_IMG_USE_CLASSPATH_SERVLET, Boolean.toString(useClasspathCssImgServlet));
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.config.jmx.JawrConfigManagerMBean#refreshConfig()
	 */
	public void refreshConfig() {
		
		requestHandler.configChanged(configProperties);
	}

}
