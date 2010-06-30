/**
 * Copyright 2007-2010 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.factory.util;

import java.util.List;
import java.util.Map;

import net.jawr.web.servlet.JawrRequestHandler;

/**
 * Transfer object meant for a factory to use to create a JoinableResourceBundle.
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 * 
 */
public class ResourceBundleDefinition {

	/** The bundle ID, the URL path which will be used to reference the bundle */
	private String bundleId;
	
	/** The bundle name, which is used to define the bundle in the properties ( for ex : jawr.js.bundle.<bundleName>.mapping ) */
	private String bundleName;
	
	/** The mappings */
	private List mappings;
	
	/** The flag indicating if the bundle is a global one */
	private boolean isGlobal;
	
	/** The flag indicating if the bundle is a composite one */
	private boolean isComposite;
	
	/** The order for the inclusion */
	private int inclusionOrder;
	
	/** The flag indicating if this bundle must only be included in debug */
	private boolean debugOnly = false;
	
	/** The flag indicating if this bundle must never be included in debug */
	private boolean debugNever = false;
	
	/** The comma separated list of per file post processors */
	private String unitaryPostProcessorKeys;
	
	/** The comma separated list of bundle post processors */
	private String bundlePostProcessorKeys;
	
	/** The IE specific conditional expression, which is used to defined if the bundle must be included or not */
	private String ieConditionalExpression;
	
	/** The alternate production URL */
	private String alternateProductionURL;
	
	/** The list of children for composite bundle */
	private List children;
	
	/** The variant map */
	private Map variants;

	/** The list of dependencies */
	private List dependencies;
	
	/**
	 * Returns the variant map
	 * @param variants the variant map
	 */
	public Map getVariants() {
		return this.variants;
	}
	
	/**
	 * Returns the variant map
	 * @param variants the variant map
	 */
	public void setVariants(Map variants) {
		this.variants = variants;
	}
	
	/**
	 * Returns the list of children for composite bundle
	 * @return the list of children for composite bundle
	 */
	public List getChildren() {
		return children;
	}

	/**
	 * Sets the list of children for composite bundle
	 * @param children the list to set
	 */
	public void setChildren(List children) {
		this.children = children;
	}

	/**
	 * Returns the bundle ID
	 * @return the bundle ID
	 */
	public String getBundleId() {
		return bundleId;
	}

	/**
	 * Sets the bundle ID
	 * @param bundleId the bundle ID to set
	 */
	public void setBundleId(String bundleId) {
		if (JawrRequestHandler.CLIENTSIDE_HANDLER_REQ_PATH.equals(bundleId))
			throw new IllegalArgumentException("The provided id [" + JawrRequestHandler.CLIENTSIDE_HANDLER_REQ_PATH
					+ "] can't be used since it's the same as the clientside handler path. Please change this id (or the name of the script)");
		this.bundleId = bundleId;
	}
	
	/**
	 * Returns the bundle name
	 * @return the bundle name
	 */
	public String getBundleName() {
		return bundleName;
	}
	
	/**
	 * Sets the bundle name
	 * @param bundleName the name to set 
	 */
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	/**
	 * Returns the path mappings for the bundle
	 * @return the path mappings for the bundle
	 */
	public List getMappings() {
		return mappings;
	}

	/**
	 * Sets the path mappings for the bundle
	 * @param mappings the mappings to set
	 */
	public void setMappings(List mappings) {
		this.mappings = mappings;
	}

	/**
	 * Returns the bundle dependencies
	 * @return the bundle dependencies
	 */
	public List getDependencies() {
		return dependencies;
	}

	/**
	 * Sets the bundle dependencies
	 * @param dependencies the bundle dependencies
	 */
	public void setDependencies(List dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * Returns the flag indicating if the bundle is a global one
	 * @return true if the bundle is a global one, false otherwise
	 */
	public boolean isGlobal() {
		return isGlobal;
	}

	/**
	 * Sets the flag indicating if the bundle is a global one
	 * @param isGlobal the flag to set
	 */
	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	/**
	 * Returns the inclusion order of the bundle
	 * @return the inclusion order of the bundle
	 */
	public int getInclusionOrder() {
		return inclusionOrder;
	}

	/**
	 * Sets the inclusion order of the bundle
	 * @param inclusionOrder the inclusion order to set 
	 */
	public void setInclusionOrder(int inclusionOrder) {
		this.inclusionOrder = inclusionOrder;
	}

	/**
	 * Returns the flag indicating if the bundle must be included only in debug
	 * @return true if the bundle must be included only in debug, false otherwise
	 */
	public boolean isDebugOnly() {
		return debugOnly;
	}

	/**
	 * Sets the flag indicating if the bundle must be included only in debug
	 * @param debugOnly the flag to set
	 */
	public void setDebugOnly(boolean debugOnly) {
		this.debugOnly = debugOnly;
	}

	/**
	 * Returns the flag indicating if the bundle must never be included in debug
	 * @return true if the bundle must be included never in debug, false otherwise
	 */
	public boolean isDebugNever() {
		return debugNever;
	}

	/**
	 * Sets the flag indicating if the bundle must never be included in debug
	 * @param debugNever the flag to set
	 */
	public void setDebugNever(boolean debugNever) {
		this.debugNever = debugNever;
	}

	/**
	 * Returns the comma separated list of file post processors for the bundle
	 * @return the comma separated list of file post processors for the bundle
	 */
	public String getUnitaryPostProcessorKeys() {
		return unitaryPostProcessorKeys;
	}

	/**
	 * Sets the list of file post processors for the bundle
	 * @param unitaryPostProcessorKeys the list of file post processors to set 
	 */
	public void setUnitaryPostProcessorKeys(String unitaryPostProcessorKeys) {
		this.unitaryPostProcessorKeys = unitaryPostProcessorKeys;
	}

	/**
	 * Returns the comma separated list of post processors for the bundle
	 * @return the comma separated list of post processors for the bundle
	 */
	public String getBundlePostProcessorKeys() {
		return bundlePostProcessorKeys;
	}

	/**
	 * Sets the list of post processors for the bundle
	 * @param bundlePostProcessorKeys the list of post processors to set 
	 */
	public void setBundlePostProcessorKeys(String bundlePostProcessorKeys) {
		this.bundlePostProcessorKeys = bundlePostProcessorKeys;
	}

	/**
	 * Returns true if the bundle is a composite one
	 * @return true if the bundle is a composite one, false otherwise
	 */
	public boolean isComposite() {
		return isComposite;
	}

	/**
	 * Sets the flag indicating if the bundle is a composite one
	 * @param isComposite the flag to set
	 */
	public void setComposite(boolean isComposite) {
		this.isComposite = isComposite;
	}

	/**
	 * Returns the IE specific conditional expression, which is used to defined if the bundle must be included or not
	 * @return the IE specific conditional expression, which is used to defined if the bundle must be included or not
	 */
	public String getIeConditionalExpression() {
		return ieConditionalExpression;
	}

	/**
	 * Sets the IE specific conditional expression, which is used to defined if the bundle must be included or not
	 * @param ieConditionalExpression
	 */
	public void setIeConditionalExpression(String ieConditionalExpression) {
		this.ieConditionalExpression = ieConditionalExpression;
	}

	/**
	 * Returns the alternate production URL
	 * @return the alternate production URL
	 */
	public String getAlternateProductionURL() {
		return alternateProductionURL;
	}

	/**
	 * Sets the alternate production URL
	 * @param alternateProductionURL the alternateProductionURL to set
	 */
	public void setAlternateProductionURL(String alternateProductionURL) {
		this.alternateProductionURL = alternateProductionURL;
	}

}
