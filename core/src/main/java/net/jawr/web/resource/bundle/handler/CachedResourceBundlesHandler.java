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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;

import net.jawr.web.collections.ConcurrentCollectionsFactory;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler;
import net.jawr.web.resource.bundle.iterator.ResourceBundlePathsIterator;

/**
 * ResourceBundlesHandler wrapper implementation that uses a ConcurrentHashMap
 * to cache the resources. Each resource is loaded only once from the
 * ResourceBundlesHandler, then it is stored in cache and retrieved from there
 * in subsequent calls. Every method call not related to retrieving data is
 * delegated to the wrapped implementation.
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 * 
 */
public class CachedResourceBundlesHandler implements ResourceBundlesHandler {

	/** The resource bundle handler */
	private ResourceBundlesHandler rsHandler;

	/** The cache map for text resource */
	private Map textCache;

	/** The cache map for zip resource */
	private Map gzipCache;

	/**
	 * Build a cached wrapper around the supplied ResourceBundlesHandler.
	 * 
	 * @param rsHandler
	 */
	public CachedResourceBundlesHandler(ResourceBundlesHandler rsHandler) {
		super();
		this.rsHandler = rsHandler;
		this.textCache = ConcurrentCollectionsFactory.buildConcurrentHashMap();
		this.gzipCache = ConcurrentCollectionsFactory.buildConcurrentHashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#getContextBundles
	 * ()
	 */
	public List getContextBundles() {
		return rsHandler.getContextBundles();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#getBundlePaths
	 * (java.lang.String,
	 * net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler,
	 * java.util.Map)
	 */
	public ResourceBundlePathsIterator getBundlePaths(String bundleId,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {
		return rsHandler.getBundlePaths(bundleId, commentCallbackHandler,
				variants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.ResourceBundlesHandler#getBundlePaths(java
	 * .lang.String)
	 */
	public ResourceBundlePathsIterator getBundlePaths(boolean debugMode,
			String bundleId,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {
		return rsHandler.getBundlePaths(debugMode, bundleId,
				commentCallbackHandler, variants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.resource.bundle.ResourceBundlesHandler#getConfig()
	 */
	public JawrConfig getConfig() {
		return rsHandler.getConfig();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.resource.bundle.ResourceBundlesHandler#initAllBundles()
	 */
	public void initAllBundles() {
		rsHandler.initAllBundles();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.ResourceBundlesHandler#resolveBundleForPath
	 * (java.lang.String)
	 */
	public JoinableResourceBundle resolveBundleForPath(String path) {
		return rsHandler.resolveBundleForPath(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.ResourceBundlesHandler#streamBundleTo(java
	 * .lang.String, java.io.OutputStream)
	 */
	public void streamBundleTo(String bundlePath, OutputStream out)
			throws ResourceNotFoundException {

		try {
			ByteBuffer gzip = (ByteBuffer) gzipCache.get(bundlePath);

			// If it's not cached yet
			if (null == gzip) {
				// Stream the stored data
				ByteArrayOutputStream baOs = new ByteArrayOutputStream();
				BufferedOutputStream bfOs = new BufferedOutputStream(baOs);
				rsHandler.streamBundleTo(bundlePath, bfOs);

				// Copy the data into the ByteBuffer
				bfOs.close();
				gzip = ByteBuffer.wrap(baOs.toByteArray());

				// Cache the ByteBuffer
				gzipCache.put(bundlePath, gzip);
			}

			// Write bytes to the outputstream
			int max = gzip.capacity();
			for (int x = 0; x < max; x++)
				out.write(gzip.get(x)); // Use absolute get method

		} catch (IOException e) {
			throw new BundlingProcessException(
					"Unexpected IOException writing bundle[" + bundlePath + "]",
					e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.ResourceBundlesHandler#writeBundleTo(java
	 * .lang.String, java.io.Writer)
	 */
	public void writeBundleTo(String bundlePath, Writer writer)
			throws ResourceNotFoundException {
		String text = (String) textCache.get(bundlePath);
		try {
			// If it's not cached yet
			if (null == text) {
				String charsetName = rsHandler.getConfig().getResourceCharset()
						.name();
				ByteArrayOutputStream baOs = new ByteArrayOutputStream();
				WritableByteChannel wrChannel = Channels.newChannel(baOs);
				Writer tempWriter = Channels.newWriter(wrChannel, charsetName);
				rsHandler.writeBundleTo(bundlePath, tempWriter);
				text = baOs.toString(charsetName);
				textCache.put(bundlePath, text);
			}

			// Write the text to the outputstream
			writer.write(text);
			writer.flush();

		} catch (IOException e) {
			throw new BundlingProcessException(
					"Unexpected IOException writing bundle[" + bundlePath + "]",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * getClientSideHandler()
	 */
	public ClientSideHandlerGenerator getClientSideHandler() {
		return rsHandler.getClientSideHandler();
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#getGlobalResourceBundlePaths(java.lang.String, net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler, java.util.Map)
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(
			String bundleId,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {

		return rsHandler.getGlobalResourceBundlePaths(bundleId,
				commentCallbackHandler, variants);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#getGlobalResourceBundlePaths(boolean, net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler, java.util.Map)
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(
			boolean debugMode,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {

		return rsHandler.getGlobalResourceBundlePaths(debugMode,
				commentCallbackHandler, variants);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#getGlobalResourceBundlePaths(net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler, java.util.Map)
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {
		return rsHandler.getGlobalResourceBundlePaths(commentCallbackHandler,
				variants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * isGlobalResourceBundle(java.lang.String)
	 */
	public boolean isGlobalResourceBundle(String resourceBundleId) {
		return rsHandler.isGlobalResourceBundle(resourceBundleId);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#containsValidBundleHashcode(java.lang.String)
	 */
	public boolean containsValidBundleHashcode(String requestedPath) {
		return rsHandler.containsValidBundleHashcode(requestedPath);
	}

}
