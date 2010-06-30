/**
 * Copyright 2008-2010 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.locale;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.jawr.web.JawrConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.generator.AbstractJavascriptGenerator;
import net.jawr.web.resource.bundle.generator.ConfigurationAwareResourceGenerator;
import net.jawr.web.resource.bundle.generator.GeneratorContext;
import net.jawr.web.resource.bundle.generator.GeneratorRegistry;
import net.jawr.web.resource.bundle.generator.ResourceGenerator;
import net.jawr.web.resource.bundle.generator.variant.VariantResourceGenerator;
import net.jawr.web.resource.bundle.locale.message.MessageBundleScriptCreator;
import net.jawr.web.resource.bundle.variant.VariantSet;

import org.apache.log4j.Logger;

/**
 * A generator that creates a script from message bundles.
 * The generated script can be used to reference the message literals easily from javascript.  
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 *
 */
public class ResourceBundleMessagesGenerator extends AbstractJavascriptGenerator implements ResourceGenerator, VariantResourceGenerator, ConfigurationAwareResourceGenerator {
	
	/** The logger */
	private static final Logger LOGGER = Logger.getLogger(ResourceBundleMessagesGenerator.class);
	
	/** The grails message generator */
	private static final String GRAILS_MESSAGE_CREATOR = "net.jawr.web.resource.bundle.locale.message.GrailsMessageBundleScriptCreator";
	
	/** The resource path prefix for grails i18n messages */
	private static final String GRAILS_APP_I18N_RESOURCE_PREFIX = "grails-app.i18n.";

	/** The servlet context */
	private ServletContext servletContext;
	
	/** The flag indicating if we are in a grails context */
	private boolean grailsContext;
	
	/** The flag indicating if we are in a grails war is deployed */
	private boolean grailsWarDeployed;
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ConfigurationAwareResourceGenerator#setConfig(net.jawr.web.config.JawrConfig)
	 */
	public void setConfig(JawrConfig config){
		servletContext = config.getContext();
		grailsContext = servletContext.getAttribute(JawrConstant.GRAILS_WAR_DEPLOYED) != null;
		if(grailsContext){
			grailsWarDeployed = ((Boolean)servletContext.getAttribute(JawrConstant.GRAILS_WAR_DEPLOYED)).booleanValue();
		}
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#createResource(java.lang.String, java.nio.charset.Charset)
	 */
	public Reader createResource(GeneratorContext context) {
		MessageBundleScriptCreator creator = null;
		// In grails apps, the generator uses a special implementation
		if(grailsContext){
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("Using grails messages generator. ");
			// Loading this way prevents unwanted dependencies in non grails applications. 
			Object[] param = {context};
			creator = (MessageBundleScriptCreator) ClassLoaderResourceUtils.buildObjectInstance(GRAILS_MESSAGE_CREATOR,param);
		}
		else {
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("Using standard messages generator. ");
			creator = new MessageBundleScriptCreator(context);
		}
		
		return creator.createScript(context.getCharset());
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#getMappingPrefix()
	 */
	public String getMappingPrefix() {
		return GeneratorRegistry.MESSAGE_BUNDLE_PREFIX;
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#getDebugModeBuildTimeGenerationPath(java.lang.String)
	 */
	public String getDebugModeBuildTimeGenerationPath(String path) {
		
		String debugPath = path.replaceFirst(GeneratorRegistry.PREFIX_SEPARATOR, JawrConstant.URL_SEPARATOR);
		if(debugPath.endsWith("@")){
			debugPath = debugPath.replaceAll("@", "");
		}else{
			debugPath = debugPath.replaceAll("@", "_");
			debugPath = debugPath.replaceAll("\\|", "_");
		}
		return debugPath+"."+JawrConstant.JS_TYPE;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.LocaleAwareResourceReader#getAvailableLocales(java.lang.String)
	 */
	public List getAvailableLocales(String resource) {
		
		List availableLocales = null;
		if(grailsContext && grailsWarDeployed && resource.startsWith(GRAILS_APP_I18N_RESOURCE_PREFIX)){
			availableLocales = LocaleUtils.getAvailableLocaleSuffixesForBundle(resource, servletContext); 
		}else{
			availableLocales = LocaleUtils.getAvailableLocaleSuffixesForBundle(resource);
		}
		return availableLocales;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.variant.VariantResourceGenerator#getAvailableVariants(java.lang.String)
	 */
	public Map getAvailableVariants(String resource) {
		
		List localeVariants = getAvailableLocales(resource);
		if(localeVariants.isEmpty()){
			throw new BundlingProcessException("Enable to find the resource bundle : "+resource);
		}
		Map variants = new HashMap();
		VariantSet variantSet = new VariantSet(JawrConstant.LOCALE_VARIANT_TYPE, "", localeVariants);
		variants.put(JawrConstant.LOCALE_VARIANT_TYPE, variantSet);
		return variants;
	}

}
