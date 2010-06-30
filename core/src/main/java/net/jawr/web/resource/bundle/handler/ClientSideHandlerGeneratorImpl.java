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
package net.jawr.web.resource.bundle.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.jawr.web.JawrConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.resource.bundle.IOUtils;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.factory.util.PathNormalizer;
import net.jawr.web.resource.bundle.generator.JavascriptStringUtil;
import net.jawr.web.resource.bundle.generator.dwr.DWRParamWriter;
import net.jawr.web.resource.bundle.postprocess.impl.JSMinPostProcessor;
import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.servlet.RendererRequestUtils;

import org.apache.log4j.Logger;

/**
 * Implementation of ClientSideHandlerGenerator
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public class ClientSideHandlerGeneratorImpl implements
		ClientSideHandlerGenerator {
	
	/** The logger */
	private static final Logger LOGGER = Logger.getLogger(ClientSideHandlerGeneratorImpl.class.getName());
	
	/**
	 * Global bundles, to include in every page
	 */
	private List globalBundles;
	
	/**
	 * Bundles to include upon request
	 */
	private List contextBundles;
	
	/** The Jawr config */
	private JawrConfig config;
	
	/** The main script template */
	private static StringBuffer mainScriptTemplate;
	
	/** The debug script template */
	private static StringBuffer debugScriptTemplate;

	/**
	 * Constructor
	 * @param globalBundles the global bundles 
	 * @param contextBundles teh context bundles
	 * @param config the Jawr configuration
	 */
	public ClientSideHandlerGeneratorImpl(List globalBundles,
			List contextBundles, JawrConfig config) {
		super();
		if(null == mainScriptTemplate){
			mainScriptTemplate = loadScriptTemplate(SCRIPT_TEMPLATE);
		}
		if(null == debugScriptTemplate){
			debugScriptTemplate = loadScriptTemplate(DEBUG_SCRIPT_TEMPLATE);
		}
		this.globalBundles = globalBundles;
		this.contextBundles = contextBundles;
		this.config = config;
	}


	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ClientSideHandlerGenerator#getClientSideHandler(javax.servlet.http.HttpServletRequest)
	 */
	public StringBuffer getClientSideHandlerScript(HttpServletRequest request) {
		
		boolean useGzip = RendererRequestUtils.isRequestGzippable(request, this.config);
		StringBuffer sb = new StringBuffer(mainScriptTemplate.toString());
		Map variants = this.config.getGeneratorRegistry().resolveVariants(request);
		sb.append("JAWR.app_context_path='").append(request.getContextPath()).append("';\n");
		
		if(null != this.config.getDwrMapping()){
			sb.append(DWRParamWriter.buildDWRJSParams(request.getContextPath(),PathNormalizer.joinPaths(request.getContextPath(),  
																						 			this.config.getDwrMapping())));
			sb.append("if(!window.DWR)window.DWR={};\nDWR.loader = JAWR.loader;\n");
		}
		sb.append("JAWR.loader.mapping='").append(getPathPrefix(request, this.config)).append("';\n");
		
		// Start an self executing function
		sb.append("(function(){\n");
		
		// shorthand for creating ResourceBundles. makes the script shorter. 
		sb.append("function r(n, p, i,ie,aU){return new JAWR.ResourceBundle(n, p, i,ie,aU);}\n");
		
		sb.append("JAWR.loader.jsbundles = [");
		sb.append(getClientSideBundles(variants, useGzip));
		sb.append("]\n");
		
		// Retrieve the resourcehandler for CSS if there is one. 
		ResourceBundlesHandler rsHandler = (ResourceBundlesHandler)  request.getSession()
																			.getServletContext()
																			.getAttribute(JawrConstant.CSS_CONTEXT_ATTRIBUTE);
		boolean isCSSHandler = false;
		if(null != rsHandler) {
			ClientSideHandlerGenerator generator = rsHandler.getClientSideHandler();
			// If this is not a pointer to the same generator it means this generator is the javascript one, 
			// so we add CSS bundles to the script
			if(this != generator){
				sb.append(";JAWR.loader.cssbundles = [");
				sb.append(generator.getClientSideBundles(variants, useGzip));
				sb.append("];\n");
			}
			else isCSSHandler = true;
			
			// Add the mapping for css resources
			sb.append("JAWR.loader.cssmapping='").append(getPathPrefix(request,rsHandler.getConfig())).append("';\n");
		}
		// End self executing function
		sb.append("})();");
		
		
		// Appends extra functions for debug mode
		if(this.config.isDebugModeOn()) {
			sb.append(debugScriptTemplate.toString()).append("\n");
		}
		
		// The global bundles are already sorted and filtered by the bundleshandler
		for(Iterator it = globalBundles.iterator();it.hasNext();){
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();
			String func = isCSSHandler ? "JAWR.loader.style(" : "JAWR.loader.script(";
			sb.append(func)
				.append(JavascriptStringUtil.quote(bundle.getId()))
				.append(");\n");
		}
		
		// Minify the result
		if(!this.config.isDebugModeOn()){
			JSMinPostProcessor p = new JSMinPostProcessor();
			try {
				sb = p.minifyStringBuffer(sb,config.getResourceCharset());
			}catch(Exception e) {
				throw new BundlingProcessException("Unexpected error creating client side resource handler",e);
			}
		}
		return sb;
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ClientSideHandlerGenerator#getClientSideBundles(java.lang.String, boolean)
	 */
	public StringBuffer getClientSideBundles(Map variants, boolean useGzip){
		StringBuffer sb = new StringBuffer();
		addAllBundles(globalBundles,variants,sb,useGzip);
		if(!globalBundles.isEmpty() && !contextBundles.isEmpty()){
			sb.append(",");
		}
		addAllBundles(contextBundles,variants,sb,useGzip);
		
		return sb;
	}
	
	/**
	 * Determines which prefix should be used for the links, according to 
	 * the context path override if present, or using the context path and 
	 * possibly the jawr mapping. 
	 * @param request the request
	 * @return the path prefix
	 */
	private String getPathPrefix(HttpServletRequest request, JawrConfig config) {
		if(null != config.getContextPathOverride()) {
			return config.getContextPathOverride();
		}
		String mapping = null == config.getServletMapping() ? "" : config.getServletMapping();
		String path = PathNormalizer.joinPaths(request.getContextPath(), mapping);
		path = path.endsWith("/") ? path : path +'/';
		return path;
	}
	
	/**
	 * Adds a javascript Resourcebundle representation for each member of a
	 * List containing JoinableResourceBundles
	 * @param bundles the bundles
	 * @param variants the variant map
	 * @param buf the buffer
	 * @param useGzip the flag indicating if we use gzip compression or not.
	 */
	private void addAllBundles(List bundles, Map variants, StringBuffer buf,boolean useGzip){
		for(Iterator it = bundles.iterator();it.hasNext();){
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();
			appendBundle(bundle,variants,buf,useGzip);
			
			if(it.hasNext())
				buf.append(",");
		}
	}
	
	/**
	 * Creates a javascript objet that represents a bundle
	 * @param bundle the bundle
	 * @param variants the variant map
	 * @param buf the buffer
	 * @param useGzip the flag indicating if we use gzip compression or not.
	 */
	private void appendBundle(JoinableResourceBundle bundle,Map variants, StringBuffer buf,boolean useGzip){
		buf.append("r(")
			.append(JavascriptStringUtil.quote(bundle.getId()))
			.append(",");
		if(useGzip){
			String path = bundle.getURLPrefix(variants);
			path = path.substring(1); // remove leading '/'
			buf.append(JavascriptStringUtil.quote(BundleRenderer.GZIP_PATH_PREFIX + path ));
		}
		else buf.append(JavascriptStringUtil.quote(bundle.getURLPrefix(variants)));
		
		boolean skipItems = false;
		if(bundle.getItemPathList().size() == 1 && null == bundle.getExplorerConditionalExpression()){
			skipItems = bundle.getItemPathList().get(0).equals(bundle.getId());
		}
		
		if(!skipItems) {
			buf.append(",[");
			for(Iterator it = bundle.getItemPathList(variants).iterator();it.hasNext();){
				String path = (String) it.next();
				if(this.config.getGeneratorRegistry().isPathGenerated(path)) {
					path = PathNormalizer.createGenerationPath(path,this.config.getGeneratorRegistry());
				}
				if("".equals(this.config.getContextPathOverride()) && path.startsWith("/"))
					path = path.substring(1);
				buf.append(JavascriptStringUtil.quote(path));
				if(it.hasNext())
					buf.append(",");
			}
			buf.append("]");
			
			if(null != bundle.getExplorerConditionalExpression()) {
				buf.append(",'").append(bundle.getExplorerConditionalExpression()).append("'");
			}
		}
		if(null != bundle.getAlternateProductionURL()) {
			// Complete the parameters if needed, since the alternate param goes afterwards
			if(skipItems)
				buf.append(",null,null");
			else if(null == bundle.getExplorerConditionalExpression())
				buf.append(",null");
			buf.append("," +JavascriptStringUtil.quote(bundle.getAlternateProductionURL()));
				
		}
		buf.append(")");
		
	}
	
	/**
	 * Loads a template containing the functions which convert properties into methods. 
	 * @return the script template
	 */
	private StringBuffer loadScriptTemplate(String path) {
		StringWriter sw = new StringWriter();
		
		InputStream is = null;
		try {
			is = ClassLoaderResourceUtils.getResourceAsStream(path,this);
			int i;
			while((i = is.read()) != -1) {
				sw.write(i);
			}
			
		} catch (IOException e) {
			LOGGER.fatal("a serious error occurred when initializing ClientSideHandlerGeneratorImpl");
			throw new BundlingProcessException("Classloading issues prevent loading the loader template to be loaded. ",e);
		}finally{
			IOUtils.close(is);
		}
		
		return sw.getBuffer();
	}
}
