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
package net.jawr.web.resource.bundle.handler;

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler;
import net.jawr.web.resource.bundle.iterator.ResourceBundlePathsIterator;

/**
 * Main interface to work with resource bundles. It helps in resolving groups of resources
 * which are served as a single one, and provides methods to generate urls that point to either
 * the full bundle or its individual resources. 
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 *
 */
public interface ResourceBundlesHandler {
	
	/**
	 * Determines which bundle corresponds to a path. The path may be
	 * a directory or file path. This path will not include any prefix, it is intended
	 * to be the path normally used for a tag library. 
	 * @param path
	 * @return String The bundle ID that can be used to retrieve it.  
	 */
	public JoinableResourceBundle resolveBundleForPath(String path);
	
	/**
	 * Returns true if the bundle Id is the Id a global resource bundle
	 * @param resourceBundleId the resource bundle ID 
	 * @return the global resource bundle path iterator
	 */
	public boolean isGlobalResourceBundle(String resourceBundleId);
	
	/**
	 * Returns the global resource bundle path iterator
	 * @param commentCallbackHandler the comment callback handler
	 * @param variants the variant map
	 * @return the global resource bundle path iterator
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(ConditionalCommentCallbackHandler commentCallbackHandler, Map variants);
	
	/**
	 * Returns the global resource bundle path iterator
	 * @param debugMode the debug mode
	 * @param commentCallbackHandler the comment callback handler
	 * @param variants the variant map
	 * @return the global resource bundle path iterator
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(boolean debugMode, ConditionalCommentCallbackHandler commentCallbackHandler, Map variants);
	
	/**
	 * Returns the global resource bundle path iterator for one global bundle
	 * @param commentCallbackHandler the comment callback handler
	 * @param variants the variant map
	 * @return the global resource bundle path iterator
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(String bundlePath,
			ConditionalCommentCallbackHandler commentCallbackHandler, Map variants);
	
	/**
	 * Returns an ordered list of the paths to use when accesing a resource bundle. 
	 * Each implementation may return one or several paths depending on wether all resources
	 * are unified into one or several bundles. The paths returned should include the prefix
	 * that uniquely identify the bundle contents. 
	 * 
	 * @param debugMode the debug mode
	 * @param bundleId the bundle ID
	 * @param commentCallbackHandler the comment callback handler
	 * @param variants the variant map
	 * @return the iterator of bundle paths
	 */
	public ResourceBundlePathsIterator getBundlePaths(String bundleId, 
														ConditionalCommentCallbackHandler commentCallbackHandler, 
														Map variants);
	
	/**
	 * Returns an ordered list of the paths to use when accesing a resource bundle. 
	 * Each implementation may return one or several paths depending on wether all resources
	 * are unified into one or several bundles. The paths returned should include the prefix
	 * that uniquely identify the bundle contents. 
	 * 
	 * @param debugMode the debug mode
	 * @param bundleId the bundle ID
	 * @param commentCallbackHandler the comment callback handler
	 * @param variants the variant map
	 * @return the iterator of bundle paths
	 */
	public ResourceBundlePathsIterator getBundlePaths(boolean debugMode, String bundleId, 
														ConditionalCommentCallbackHandler commentCallbackHandler, 
														Map variants);
	
	
	/**
	 * Writes data using the supplied writer, representing a unified bundle of resources. 
	 * @param bundlePath
	 * @return
	 */
	public void writeBundleTo(String bundlePath, Writer writer) throws ResourceNotFoundException;
	
	/**
	 * Writes the bytes of a bundle to the specified OutputStream.
	 * This method is used to copy the gzip data in the output stream. 
	 * @param bundlePath the bundle path
	 * @param out the output stream
	 */
	public void streamBundleTo(String bundlePath, OutputStream out) throws ResourceNotFoundException;
	
	/**
	 * Returns the context bundles
	 * @return the context bundles
	 */
	public List getContextBundles();
	
	/**
	 * Generates all file bundles so that they will be ready to attend requests. 
	 */
	public void initAllBundles();
	
	/**
	 * Retrieves the configuration for this bundler
	 * @return
	 */
	public JawrConfig getConfig();
	
	
	/**
	 * Returns the client side handler generator
	 * @return the client side handler generator
	 */
	public ClientSideHandlerGenerator getClientSideHandler();

	/**
	 * Returns true if the requested path contains a valid bundle hashcode for the
	 * requested path
	 * @param requestedPath the requested path
	 * @return true if the requested path contains a valid bundle hashcode
	 */
	public boolean containsValidBundleHashcode(String requestedPath);
	
}
