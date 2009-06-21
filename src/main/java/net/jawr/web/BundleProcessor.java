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
package net.jawr.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.context.ThreadLocalJawrContext;
import net.jawr.web.resource.FileNameUtils;
import net.jawr.web.resource.ImageResourcesHandler;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.factory.util.PathNormalizer;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.renderer.buildtime.BuildTimeBundleRenderer;
import net.jawr.web.resource.bundle.renderer.buildtime.RenderedLink;
import net.jawr.web.servlet.JawrRequestHandler;
import net.jawr.web.servlet.JawrServlet;
import net.jawr.web.servlet.mock.MockServletConfig;
import net.jawr.web.servlet.mock.MockServletContext;
import net.jawr.web.servlet.mock.MockServletRequest;
import net.jawr.web.servlet.mock.MockServletResponse;
import net.jawr.web.servlet.mock.MockServletSession;
import net.jawr.web.util.FileUtils;
import net.jawr.web.util.StringUtils;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The bundle processor is managing the bundle processing at build time.
 * 
 * @author Ibrahim Chaehoi
 */
public class BundleProcessor {

	private static final String CDN_DIR_NAME = "/CDN";

	public static final String IMG_MAPPING_PREFIX = "img.mapping.";

	/** The logger */
	private static Logger logger = Logger.getLogger(BundleProcessor.class);
	
	/** The path to the web.xml file from the web application root directory */
	private static final String WEB_XML_FILE_PATH = "WEB-INF/web.xml";

	/** The name of the param-name tag */
	private static final String PARAM_NAME_TAG_NAME = "param-name";

	/** The name of the param-value tag */
	private static final String PARAM_VALUE_TAG_NAME = "param-value";

	/** The name of the servlet tag */
	private static final String SERVLET_TAG_NAME = "servlet";

	/** The name of the servlet-class tag */
	private static final String SERVLET_CLASS_TAG_NAME = "servlet-class";

	/** The name of the servlet-name tag */
	private static final String SERVLET_NAME_TAG_NAME = "servlet-name";

	/** The name of the param-value tag */
	private static final String INIT_PARAM_TAG_NAME = "init-param";

	/** The name of the load on startup tag */
	private static final String LOAD_ON_STARTUP_TAG_NAME = "load-on-startup";

	/** The init type parameter */
	private static final String TYPE_INIT_PARAMETER = "type";

	/**
	 * Launch the bundle processing
	 * 
	 * @param baseDirPath the base directory path
	 * @param tmpDirPath the temp directory path
	 * @param destDirPath the destination directory path
	 * @param generateCdnFiles the flag indicating if we should generate the CDN files or not
	 * @throws Exception if an exception occurs
	 */
	public void process(String baseDirPath, String tmpDirPath, String destDirPath, boolean generateCdnFiles) throws Exception {

		process(baseDirPath, tmpDirPath, destDirPath, new ArrayList(), generateCdnFiles);
	}

	/**
	 * Launch the bundle processing
	 * 
	 * @param baseDirPath the base directory path
	 * @param tmpDirPath the temp directory path
	 * @param destDirPath the destination directory path
	 * @param servletNames the list of the name of servlets to initialized
	 * @param generateCdnFiles the flag indicating if we should generate the CDN files or not
	 * @throws Exception if an exception occurs
	 */
	public void process(String baseDirPath, String tmpDirPath, String destDirPath, List servletNames, boolean generateCdnFiles) throws Exception {

		// Retrieve the parameters from baseDir+"/WEB-INF/web.xml"
		File webXml = new File(baseDirPath, WEB_XML_FILE_PATH);
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.parse(webXml);
		NodeList servletNodes = doc.getElementsByTagName(SERVLET_TAG_NAME);
		
		List servletDefinitions = new ArrayList();
		ServletContext servletContext = new MockServletContext(baseDirPath, tmpDirPath);
		
		for (int i = 0; i < servletNodes.getLength(); i++) {

			String servletName = null;
			Class servletClass = null;
			MockServletConfig config = new MockServletConfig(servletContext);
			int order = i;

			Node servletNode = servletNodes.item(i);
			Map initParameters = new HashMap();
			NodeList childNodes = servletNode.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node servletChildNode = childNodes.item(j);
				if (servletChildNode.getNodeName().equals(SERVLET_NAME_TAG_NAME)) {

					servletName = servletChildNode.getFirstChild().getNodeValue();
					config.setServletName(servletName);

				} else if (servletChildNode.getNodeName().equals(SERVLET_CLASS_TAG_NAME)) {

					String servletClassName = servletChildNode.getFirstChild().getNodeValue();
					servletClass = getClass().getClassLoader().loadClass(servletClassName);

				} else if (servletChildNode.getNodeName().equals(INIT_PARAM_TAG_NAME)) {

					initializeInitParams(servletChildNode, initParameters);
				} else if (servletChildNode.getNodeName().equals(LOAD_ON_STARTUP_TAG_NAME)) {

					order = Integer.parseInt(servletChildNode.getFirstChild().getNodeValue());
				}
			}

			// Initialize the servlet config with the init parameters
			config.setInitParameters(initParameters);

			// If the servlet name is part of the list of servlet to initialized
			// Set the flag accordingly
			if (servletNames.contains(servletName) || JawrServlet.class.isAssignableFrom(servletClass)) {
				ServletDefinition servletDef = new ServletDefinition(servletClass, config, order);
				servletDefinitions.add(servletDef);
			}
		}

		// Initialize the servlets and retrieve the jawr servlet definitions
		List jawrServletDefinitions = initServlets(servletDefinitions);

		// Stores the output stream
		FileUtils.copyDirectory(new File(tmpDirPath), new File(destDirPath));
		
		if(generateCdnFiles){
			// Process the Jawr servlet to generate the bundles
			processJawrServlets(destDirPath, jawrServletDefinitions);
		}
	}

	/**
	 * Initialize the servlets and returns only the list of Jawr servlets
	 * 
	 * @param servletDefinitions the list of servlet definition
	 * @throws Exception if an exception occurs
	 */
	private List initServlets(List servletDefinitions) throws Exception {

		// Sort the list taking in account the load-on-startup attribute
		Collections.sort(servletDefinitions);

		// Sets the Jawr context at "bundle processing at build time"
		ThreadLocalJawrContext.setBundleProcessingAtBuildTime(true);
		
		List jawrServletDefinitions = new ArrayList();
		for (Iterator iterator = servletDefinitions.iterator(); iterator.hasNext();) {
			ServletDefinition servletDefinition = (ServletDefinition) iterator.next();
			servletDefinition.initServlet();
			if (servletDefinition.isJawrServletDefinition()) {
				jawrServletDefinitions.add(servletDefinition);
			}
		}

		return jawrServletDefinitions;
	}

	/**
	 * Initialize the init parameters define in the servlet config
	 * 
	 * @param initParameters the map of initialization parameters
	 */
	private void initializeInitParams(Node initParamNode, Map initParameters) {

		String paramName = null;
		String paramValue = null;

		NodeList childNodes = initParamNode.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			String nodeName = childNode.getNodeName();
			if (nodeName.equals(PARAM_NAME_TAG_NAME)) {
				paramName = childNode.getFirstChild().getNodeValue();
			} else if (nodeName.equals(PARAM_VALUE_TAG_NAME)) {
				paramValue = childNode.getFirstChild().getNodeValue();
			}
		}

		initParameters.put(paramName, paramValue);
	}

	/**
	 * Process the Jawr Servlets
	 *
	 * @param destDirPath the destination directory path
	 * @param jawrServletDefinitions the destination directory
	 * @throws Exception if an exception occurs.
	 */
	private void processJawrServlets(String destDirPath, List jawrServletDefinitions) throws Exception {

		for (Iterator iterator = jawrServletDefinitions.iterator(); iterator.hasNext();) {

			ServletDefinition servletDef = (ServletDefinition) iterator.next();
			ServletConfig servletConfig = servletDef.getServletConfig();

			// Force the production mode, and remove config listener parameters
			Map initParameters = ((MockServletConfig) servletConfig).getInitParameters();
			initParameters.remove("jawr.config.reload.interval");
			
			String servletMapping = servletConfig.getInitParameter(JawrConstant.SERVLET_MAPPING_PROPERTY_NAME);
			ResourceBundlesHandler bundleHandler = null;
			ImageResourcesHandler imgRsHandler = null;

			// Retrieve the bundle Handler
			ServletContext servletContext = servletConfig.getServletContext();
			String type = servletConfig.getInitParameter(TYPE_INIT_PARAMETER);
			if (type == null || type.equals(JawrConstant.JS_TYPE)) {
				bundleHandler = (ResourceBundlesHandler) servletContext.getAttribute(JawrConstant.JS_CONTEXT_ATTRIBUTE);
			} else if (type.equals(JawrConstant.CSS_TYPE)) {
				bundleHandler = (ResourceBundlesHandler) servletContext.getAttribute(JawrConstant.CSS_CONTEXT_ATTRIBUTE);
			} else if (type.equals(JawrConstant.IMG_TYPE)) {
				imgRsHandler = (ImageResourcesHandler) servletContext.getAttribute(JawrConstant.IMG_CONTEXT_ATTRIBUTE);
			}

			String cdnDestDirPath = destDirPath + CDN_DIR_NAME;
			if (bundleHandler != null) {
				createBundles(servletDef.getServlet(), bundleHandler, cdnDestDirPath, servletMapping);
			} else if (imgRsHandler != null) {
				createImageBundle(servletDef.getServlet(), imgRsHandler, cdnDestDirPath, servletMapping);
			}
		}
	}

	/**
	 * Creates the bundles in the destination directory
	 * 
	 * @param servlet the servlet
	 * @param bundleHandler the bundles handler
	 * @param destDirPath the destination directory path
	 * @param servletMapping the mapping of the servlet
	 * @throws IOException if an IO exception occurs
	 * @throws ServletException if a servlet exception occurs
	 */
	private void createBundles(HttpServlet servlet, ResourceBundlesHandler bundleHandler, String destDirPath, String servletMapping) throws IOException,
			ServletException {

		List bundles = bundleHandler.getContextBundles();

		Iterator bundleIterator = bundles.iterator();
		MockServletResponse response = new MockServletResponse();
		MockServletRequest request = new MockServletRequest();
		MockServletSession session = new MockServletSession(servlet.getServletContext());
		request.setSession(session);

		String resourceType = servlet.getServletConfig().getInitParameter(TYPE_INIT_PARAMETER);
		if(resourceType == null){
			resourceType = JawrConstant.JS_TYPE;
		}
		
		// For the list of bundle defines, create the file associated
		while (bundleIterator.hasNext()) {
			JoinableResourceBundle bundle = (JoinableResourceBundle) bundleIterator.next();
			
			// Check if there is a resource file, which could be in conflict with the bundle name 
			URL url = servlet.getServletContext().getResource(bundle.getId());
			if(url != null){
				logger.error("It is not recommended to use a bundle name which could be in conflict with a resource.\n" +
						"Please rename your bundle '"+bundle.getId()+"' to avoid any issue");
				
			}
			
			List localVariantKeys = bundle.getLocaleVariantKeys();
			if(localVariantKeys == null){
				localVariantKeys = new ArrayList();
			}
			if(localVariantKeys.isEmpty()){
				localVariantKeys.add("");
			}
			
			// Update the bundle mapping
			//JoinableResourceBundlePropertySerializer.serializeInProperties(bundle, resourceType, bundleMapping);
			
			for (Iterator iterator = localVariantKeys.iterator(); iterator.hasNext();) {
				String localVariantKey = (String) iterator.next();
				
				List linksToBundle = createLinkToBundle(bundleHandler, bundle.getId(), localVariantKey);
				for (Iterator iteratorLinks = linksToBundle.iterator(); iteratorLinks.hasNext();) {
					RenderedLink renderedLink = (RenderedLink) iteratorLinks.next();
					String path = (String) renderedLink.getLink();
					
					// Force the debug mode of the config to match what was used in the generated link
					JawrConfig config = bundleHandler.getConfig();
					config.setDebugModeOn(renderedLink.isDebugMode());
					
					String finalBundlePath = getFinalBundlePath(path, config, localVariantKey);
					File bundleFile = new File(destDirPath, finalBundlePath);
					createBundleFile(servlet, response, request, path, bundleFile, servletMapping);	
				}
			}
		}
	}

	/**
	 * Retrieves the final path, where the servlet mapping and the cache prefix have been removed,
	 * and take also in account the jawr generator URLs. 
	 * 
	 * <pre>
	 * 	"/N1785986402/js/bundle/msg.js" -> "/js/bundle/msg.js"
	 *  "/jawr_generator.js?generationConfigParam=messages%3Amessages%40fr" -> "/jawr_generator/js/messages/messages_fr.js"
	 *  "/cssJawrPath/1414653084/folder/core/component.css" -> "folder/core/component.css"
	 * </pre>
	 * 
	 * @param path the path
	 * @param jawrConfig the jawr config
	 * @param localVariantKey The local variant key
	 * @return the final path
	 */
	public String getFinalBundlePath(String path, JawrConfig jawrConfig, String localVariantKey) {

		String finalPath = path;
		int jawrGenerationParamIdx = finalPath.indexOf(JawrRequestHandler.GENERATION_PARAM);
		if(jawrGenerationParamIdx != -1){
			
			try {
				finalPath = URLDecoder.decode(path, "UTF-8");
			} catch (UnsupportedEncodingException neverHappens) {
				/*URLEncoder:how not to use checked exceptions...*/
				throw new RuntimeException("Something went unexpectedly wrong while decoding a URL for a generator. ",
											neverHappens);
			}
			
			// Remove servlet mapping if it exists.
			finalPath = removeServletMappingFromPath(finalPath, jawrConfig.getServletMapping());
			
			finalPath = jawrConfig.getGeneratorRegistry().getDebugModeBuildTimeGenerationPath(finalPath);
		
		}else{
			
			// Remove servlet mapping if it exists.
			finalPath = removeServletMappingFromPath(finalPath, jawrConfig.getServletMapping());
			if (finalPath.startsWith("/")) {
				finalPath = finalPath.substring(1);
			}

			// remove cache prefix, when not in debug mode
			if(!jawrConfig.isDebugModeOn()){
			
				int idx = finalPath.indexOf("/");
				finalPath = finalPath.substring(idx + 1);
			}
			
			// For localized bundle add the local info in the file name
			// For example, with local variant = 'en'
			// /bundle/myBundle.js -> /bundle/myBundle_en.js 
			if(StringUtils.isNotEmpty(localVariantKey)){
				int extensionIndex = FileNameUtils.indexOfExtension(finalPath);
				if(extensionIndex != -1){
					finalPath = finalPath.substring(0, extensionIndex)+"_"+localVariantKey+finalPath.substring(extensionIndex);
				}
			}
		}
		
		return finalPath;
	}

	/**
	 * Retrieves the image final path, where the servlet mapping and the cache prefix have been removed
	 * 
	 * @param path the path
	 * @param jawrConfig the jawr config
	 * @return the final path
	 */
	public String getImageFinalPath(String path, JawrConfig jawrConfig) {

		String finalPath = path;

		// Remove servlet mapping if it exists.
		finalPath = removeServletMappingFromPath(finalPath, jawrConfig.getServletMapping());
		if (finalPath.startsWith("/")) {
			finalPath = finalPath.substring(1);
		}

		// remove cache prefix
		int idx = finalPath.indexOf("/");
		finalPath = finalPath.substring(idx + 1);
		
		return finalPath;
	}

	/**
	 * Remove the servlet mapping from the path
	 * @param path the path
	 * @param mapping the servlet mapping
	 * @return the path without the servlet mapping
	 */
	private String removeServletMappingFromPath(String path, String mapping) {
		if (mapping != null && mapping.length() > 0) {
			int idx = path.indexOf(mapping);
			if (idx > -1) {
				path = path.substring(idx + mapping.length());
			}

			path = PathNormalizer.asPath(path);
		}
		return path;
	}
	
	/**
	 * Create the image bundle
	 * 
	 * @param servlet the servlet
	 * @param imgRsHandler the image resource handler
	 * @param destDirPath the destination directory path
	 * @param servletMapping the mapping
	 * @throws IOException if an IOExceptin occurs
	 * @throws ServletException if an exception occurs
	 */
	private void createImageBundle(HttpServlet servlet, ImageResourcesHandler imgRsHandler, String destDirPath, String servletMapping) throws IOException,
			ServletException {
		Map bundleImgMap = imgRsHandler.getImageMap();

		Iterator bundleIterator = bundleImgMap.keySet().iterator();
		MockServletResponse response = new MockServletResponse();
		MockServletRequest request = new MockServletRequest();

		// For the list of bundle defines, create the file associated
		while (bundleIterator.hasNext()) {
			String imgPath = (String) bundleIterator.next();
			String path = (String) bundleImgMap.get(imgPath);

			String imageFinalPath = getImageFinalPath(path, imgRsHandler.getJawrConfig());
			File destFile = new File(destDirPath, imageFinalPath);
			
			// Update the bundle mapping
			createBundleFile(servlet, response, request, path, destFile, servletMapping);
		}
	}

	/**
	 * Create the bundle file
	 * 
	 * @param servlet the servlet
	 * @param response the response
	 * @param request the request
	 * @param path the path
	 * @param destFile the destination file
	 * @param mapping the mapping
	 * @throws IOException if an IO exception occurs
	 * @throws ServletException if an exception occurs
	 */
	private void createBundleFile(HttpServlet servlet, MockServletResponse response, MockServletRequest request, String path, File destFile,
			String mapping) throws IOException, ServletException {
		
		request.setRequestPath(mapping, path);
				
		// Create the parent directory of the destination file
		if (!destFile.exists()) {
			destFile.getParentFile().mkdirs();
		}

		// Set the response mock to write in the destination file
		try{
			response.setOutputStream(new FileOutputStream(destFile));
			servlet.service(request, response);
		}finally{
			response.close();
		}
		
		if(destFile.length() == 0){
			logger.warn("No content retrieved for file '"+destFile.getAbsolutePath()+"', which is associated to the path : "+path);
			System.out.println("No content retrieved for file '"+destFile.getAbsolutePath()+"', which is associated to the path : "+path);
		}
	}

	/**
	 * Returns the link to the bundle
	 * 
	 * @param handler the resource bundles handler
	 * @param path the path
	 * @param variantKey the local variant key
	 * @return the link to the bundle
	 * @throws IOException if an IO exception occurs
	 */
	private List createLinkToBundle(ResourceBundlesHandler handler, String path, String variantKey) throws IOException {

		List linksToBundle = new ArrayList();
		
		BuildTimeBundleRenderer bundleRenderer = new BuildTimeBundleRenderer(handler);
		StringWriter sw = new StringWriter();
		
		// The gzip compression will be made by the CDN server
		// So we force it to false.
		boolean useGzip = false;
		
		// The generation of bundle is the same in SSL and non SSL mode 
		boolean isSslRequest = false;
		
		// First deals with the production mode
		handler.getConfig().setDebugModeOn(false);
		handler.getConfig().setGzipResourcesModeOn(useGzip);
		
		bundleRenderer.renderBundleLinks(path, "", variantKey, new HashSet(), useGzip, isSslRequest, sw);
		
		// Then take in account the debug mode
		handler.getConfig().setDebugModeOn(true);
		bundleRenderer.renderBundleLinks(path, "", variantKey, new HashSet(), useGzip, isSslRequest, sw);
		
		List renderedLinks = bundleRenderer.getRenderedLinks();
		// Remove context override path if it's defined.
		String contextPathOverride = handler.getConfig().getContextPathOverride();
		for (Iterator iterator = renderedLinks.iterator(); iterator.hasNext();) {
			RenderedLink renderedLink = (RenderedLink) iterator.next();
			String renderedLinkPath = renderedLink.getLink();
			// Remove the context path override
			if (StringUtils.isNotEmpty(contextPathOverride) && renderedLinkPath.startsWith(contextPathOverride)) {
				renderedLinkPath = renderedLinkPath.substring(contextPathOverride.length());
			}
			renderedLink.setLink(PathNormalizer.asPath(renderedLinkPath));
			linksToBundle.add(renderedLink);
		}
		
		return linksToBundle;
	}

	/**
	 * This class is used internally to handle the definition of a servlet.
	 * 
	 * @author Ibrahim Chaehoi
	 */
	private class ServletDefinition implements Comparable {

		/** The servlet instance */
		private HttpServlet servlet;

		/** The servlet class */
		private Class servletClass;

		/** The servlet config */
		private ServletConfig servletConfig;

		/** The initialization order of the servlet */
		private int order;

		/**
		 * Default constructor
		 */
		public ServletDefinition() {

		}

		/**
		 * Constructor.
		 * 
		 * @param servletClass the servlet class
		 * @param servletConfig the servlet config
		 * @param order the order
		 */
		public ServletDefinition(Class servletClass, ServletConfig servletConfig, int order) {
			super();
			this.servletClass = servletClass;
			this.servletConfig = servletConfig;
			this.order = order;
		}

		/**
		 * Returns the servlet class
		 * 
		 * @return the servletClass
		 */
		public Class getServletClass() {
			return servletClass;
		}

		/**
		 * Sets the servlet class
		 * 
		 * @param servletClass the servletClass to set
		 */
		public void setServletClass(Class servletClass) {
			this.servletClass = servletClass;
		}

		/**
		 * @return the servlet
		 */
		public HttpServlet getServlet() {
			return servlet;
		}

		/**
		 * @param servlet the servlet to set
		 */
		public void setServlet(HttpServlet servlet) {
			this.servlet = servlet;
		}

		/**
		 * Returns the servlet config
		 * 
		 * @return the servletConfig
		 */
		public ServletConfig getServletConfig() {
			return servletConfig;
		}

		/**
		 * Sets the servlet config
		 * 
		 * @param servletConfig the servletConfig to set
		 */
		public void setServletConfig(ServletConfig servletConfig) {
			this.servletConfig = servletConfig;
		}

		/**
		 * Returns the order
		 * 
		 * @return the order
		 */
		public int getOrder() {
			return order;
		}

		/**
		 * Sets the order
		 * 
		 * @param order the order to set
		 */
		public void setOrder(int order) {
			this.order = order;
		}

		/**
		 * Create a new instance of the servlet and initialize it.
		 * 
		 * @param servletClass the servlet class
		 * @param servletConfig the servlet config
		 * @throws ServletException if a servlet exception occurs.
		 */
		public HttpServlet initServlet() throws Exception {

			servlet = (HttpServlet) servletClass.newInstance();
			servlet.init(servletConfig);
			return servlet;
		}

		/**
		 * Returns true if the servlet definition is a definition for a Jawr servlet
		 * 
		 * @return true if the servlet definition is a definition for a Jawr servlet
		 */
		public boolean isJawrServletDefinition() {
			return JawrServlet.class.isAssignableFrom(servletClass);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(T)
		 */
		public int compareTo(Object arg0) {

			return order - ((ServletDefinition) arg0).order;
		}
	}
	
}
