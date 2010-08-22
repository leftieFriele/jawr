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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import net.jawr.web.JawrConstant;
import net.jawr.web.collections.ConcurrentCollectionsFactory;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.context.ThreadLocalJawrContext;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.ImageResourcesHandler;
import net.jawr.web.resource.bundle.CompositeResourceBundle;
import net.jawr.web.resource.bundle.IOUtils;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.JoinableResourceBundleContent;
import net.jawr.web.resource.bundle.JoinableResourceBundlePropertySerializer;
import net.jawr.web.resource.bundle.factory.util.PathNormalizer;
import net.jawr.web.resource.bundle.global.preprocessor.GlobalPreprocessingContext;
import net.jawr.web.resource.bundle.global.preprocessor.GlobalPreprocessor;
import net.jawr.web.resource.bundle.hashcode.BundleHashcodeGenerator;
import net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler;
import net.jawr.web.resource.bundle.iterator.DebugModePathsIteratorImpl;
import net.jawr.web.resource.bundle.iterator.PathsIteratorImpl;
import net.jawr.web.resource.bundle.iterator.ResourceBundlePathsIterator;
import net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus;
import net.jawr.web.resource.bundle.postprocess.ResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.sorting.GlobalResourceBundleComparator;
import net.jawr.web.resource.bundle.variant.VariantUtils;
import net.jawr.web.resource.handler.bundle.ResourceBundleHandler;
import net.jawr.web.resource.handler.reader.ResourceReaderHandler;
import net.jawr.web.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * Default implementation of ResourceBundlesHandler
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public class ResourceBundlesHandlerImpl implements ResourceBundlesHandler {

	/** The logger */
	private static final Logger LOGGER = Logger
			.getLogger(ResourceBundlesHandler.class);

	/**
	 * The bundles that this handler manages.
	 */
	private List bundles;

	/**
	 * Global bundles, to include in every page
	 */
	private List globalBundles;

	/**
	 * Bundles to include upon request
	 */
	private List contextBundles;

	/**
	 * The bundles that will be processed once when the server will be up and running.
	 */
	private List liveProcessBundles = new ArrayList();
	
	/** The resource handler */
	private ResourceReaderHandler resourceHandler;

	/** The resource handler */
	private ResourceBundleHandler resourceBundleHandler;

	/** The Jawr config */
	private JawrConfig config;

	/** The post processor */
	private ResourceBundlePostProcessor postProcessor;

	/** The unitary post processor */
	private ResourceBundlePostProcessor unitaryPostProcessor;

	/** The post processor for composite bundle */
	private ResourceBundlePostProcessor compositePostProcessor;

	/** The unitary post processor for composite bundle */
	private ResourceBundlePostProcessor unitaryCompositePostProcessor;

	/** The resourceTypeBundle processor */
	private GlobalPreprocessor resourceTypeProcessor;

	/** The client side handler generator */
	private ClientSideHandlerGenerator clientSideHandlerGenerator;

	/** The bundle hashcode generator */
	private BundleHashcodeGenerator bundleHashcodeGenerator;
	
	/** The bundle mapping */
	private Properties bundleMapping;

	/**
	 * Build a ResourceBundlesHandler.
	 * 
	 * @param bundles
	 *            List The JoinableResourceBundles to use for this handler.
	 * @param resourceHandler
	 *            The file system access handler.
	 * @param config
	 *            Configuration for this handler.
	 */
	public ResourceBundlesHandlerImpl(List bundles,
			ResourceReaderHandler resourceHandler,
			ResourceBundleHandler resourceBundleHandler, JawrConfig config) {
		this(bundles, resourceHandler, resourceBundleHandler, config, null,
				null, null, null, null);
	}

	/**
	 * Build a ResourceBundlesHandler which will use the specified
	 * postprocessor.
	 * 
	 * @param bundles
	 *            List The JoinableResourceBundles to use for this handler.
	 * @param resourceHandler
	 *            The file system access handler.
	 * @param config
	 *            Configuration for this handler.
	 * @param postProcessor
	 */
	public ResourceBundlesHandlerImpl(List bundles,
			ResourceReaderHandler resourceHandler,
			ResourceBundleHandler resourceBundleHandler, JawrConfig config,
			ResourceBundlePostProcessor postProcessor,
			ResourceBundlePostProcessor unitaryPostProcessor,
			ResourceBundlePostProcessor compositePostProcessor,
			ResourceBundlePostProcessor unitaryCompositePostProcessor,
			GlobalPreprocessor resourceTypeProcessor) {
		super();
		this.resourceHandler = resourceHandler;
		this.resourceBundleHandler = resourceBundleHandler;
		this.config = config;
		this.bundleHashcodeGenerator = config.getBundleHashcodeGenerator();
		this.postProcessor = postProcessor;
		this.unitaryPostProcessor = unitaryPostProcessor;
		this.compositePostProcessor = compositePostProcessor;
		this.unitaryCompositePostProcessor = unitaryCompositePostProcessor;
		this.resourceTypeProcessor = resourceTypeProcessor;
		this.bundles = ConcurrentCollectionsFactory.buildCopyOnWriteArrayList();
		this.bundles.addAll(bundles);
		splitBundlesByType(bundles);

		this.clientSideHandlerGenerator = new ClientSideHandlerGeneratorImpl(
				globalBundles, contextBundles, config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#getContextBundles
	 * ()
	 */
	public List getContextBundles() {
		return contextBundles;
	}

	/**
	 * Splits the bundles in two lists, one for global lists and other for the
	 * remaining bundles.
	 */
	private void splitBundlesByType(List bundles) {
		// Temporary lists (CopyOnWriteArrayList does not support sort())
		List tmpGlobal = new ArrayList();
		List tmpContext = new ArrayList();

		for (Iterator it = bundles.iterator(); it.hasNext();) {
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();

			// Exclude/include debug only scripts
			if (config.isDebugModeOn()
					&& bundle.getInclusionPattern().isExcludeOnDebug())
				continue;
			else if (!config.isDebugModeOn()
					&& bundle.getInclusionPattern().isIncludeOnDebug())
				continue;

			if (bundle.getInclusionPattern().isGlobal())
				tmpGlobal.add(bundle);
			else
				tmpContext.add(bundle);
		}

		// Sort the global bundles
		Collections.sort(tmpGlobal, new GlobalResourceBundleComparator());

		globalBundles = ConcurrentCollectionsFactory
				.buildCopyOnWriteArrayList();
		globalBundles.addAll(tmpGlobal);

		contextBundles = ConcurrentCollectionsFactory
				.buildCopyOnWriteArrayList();
		contextBundles.addAll(tmpContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * isGlobalResourceBundle(java.lang.String)
	 */
	public boolean isGlobalResourceBundle(String resourceBundleId) {

		boolean isGlobalResourceBundle = false;
		for (Iterator it = globalBundles.iterator(); it.hasNext();) {
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();
			if (bundle.getId().equals(resourceBundleId)) {
				isGlobalResourceBundle = true;
			}
		}

		return isGlobalResourceBundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * getGlobalResourceBundlePaths
	 * (net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler,
	 * java.lang.String)
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(
			boolean debugMode,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {

		return getBundleIterator(debugMode, globalBundles,
				commentCallbackHandler, variants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * getGlobalResourceBundlePaths
	 * (net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler,
	 * java.lang.String)
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {

		return getBundleIterator(getConfig().isDebugModeOn(), globalBundles,
				commentCallbackHandler, variants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * getGlobalResourceBundlePaths
	 * (net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler,
	 * java.lang.String)
	 */
	public ResourceBundlePathsIterator getGlobalResourceBundlePaths(
			String bundleId,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {

		List bundles = new ArrayList();
		for (Iterator it = globalBundles.iterator(); it.hasNext();) {
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();
			if (bundle.getId().equals(bundleId)) {
				bundles.add(bundle);
				break;
			}
		}
		return getBundleIterator(getConfig().isDebugModeOn(), bundles,
				commentCallbackHandler, variants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.ResourceCollector#getBundlePaths(java.lang
	 * .String)
	 */
	public ResourceBundlePathsIterator getBundlePaths(String bundleId,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {

		return getBundlePaths(getConfig().isDebugModeOn(), bundleId,
				commentCallbackHandler, variants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#getBundlePaths
	 * (boolean, java.lang.String,
	 * net.jawr.web.resource.bundle.iterator.ConditionalCommentCallbackHandler,
	 * java.lang.String)
	 */
	public ResourceBundlePathsIterator getBundlePaths(boolean debugMode,
			String bundleId,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {

		List bundles = new ArrayList();

		// if the path did not correspond to a global bundle, find the requested
		// one.
		if (!isGlobalResourceBundle(bundleId)) {
			for (Iterator it = contextBundles.iterator(); it.hasNext();) {
				JoinableResourceBundle bundle = (JoinableResourceBundle) it
						.next();
				if (bundle.getId().equals(bundleId)) {

					bundles.add(bundle);
					break;
				}
			}
		}

		return getBundleIterator(debugMode, bundles, commentCallbackHandler,
				variants);
	}

	/**
	 * Returns the bundle iterator
	 * 
	 * @param debugMode
	 *            the flag indicating if we are in debug mode or not
	 * @param commentCallbackHandler
	 *            the comment callback handler
	 * @param variants
	 *            the variant map
	 * @return the bundle iterator
	 */
	private ResourceBundlePathsIterator getBundleIterator(boolean debugMode,
			List bundles,
			ConditionalCommentCallbackHandler commentCallbackHandler,
			Map variants) {
		ResourceBundlePathsIterator bundlesIterator;
		if (debugMode) {
			bundlesIterator = new DebugModePathsIteratorImpl(bundles,
					commentCallbackHandler, variants);
		} else
			bundlesIterator = new PathsIteratorImpl(bundles,
					commentCallbackHandler, variants);
		return bundlesIterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.ResourceCollector#writeBundleTo(java.lang
	 * .String, java.io.Writer)
	 */
	public void writeBundleTo(String bundlePath, Writer writer)
			throws ResourceNotFoundException {

		Reader rd = null;

		try {
			
			// If debug mode is on, resources are retrieved one by one.
			if (config.isDebugModeOn()) {

				rd = resourceHandler.getResource(bundlePath);
			} else {
				// Prefixes are used only in production mode
				String path = PathNormalizer.removeVariantPrefixFromPath(bundlePath);
				rd = resourceBundleHandler.getResourceBundleReader(path);
				if(liveProcessBundles.contains(path)){
					rd = processInLive(rd);
				}
			}
			
			IOUtils.copy(rd, writer);
			writer.flush();
		} catch (IOException e) {
			throw new BundlingProcessException("Unexpected IOException writing bundle["
					+ bundlePath + "]", e);
		}finally{
			IOUtils.close(rd);
		}
	}

	/**
	 * Process the bundle content in live
	 * @param reader the reader
	 * @return the processed bundle content
	 * @throws IOException if an IOException occured
	 */
	private StringReader processInLive(Reader reader)
			throws IOException {
		
		String requestURL = ThreadLocalJawrContext.getRequestURL();
		StringWriter swriter = new StringWriter(); 
		IOUtils.copy(reader, swriter, true);
		String updatedContent = swriter.getBuffer().toString();
		if(requestURL != null){
			
			updatedContent = updatedContent.replaceAll(JawrConstant.JAWR_BUNDLE_PATH_PLACEHOLDER_PATTERN, requestURL);
		}
		return new StringReader(updatedContent);
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

		// Remove prefix, which are used only in production mode
		String path = PathNormalizer.removeVariantPrefixFromPath(bundlePath);
		ReadableByteChannel data = null;
		try {
			if(liveProcessBundles.contains(path)){
				
				Reader rd = resourceBundleHandler.getResourceBundleReader(path);
				StringReader strRd = processInLive(rd);
				StringWriter strWriter = new StringWriter();
				IOUtils.copy(strRd, strWriter);
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				GZIPOutputStream gzOut = new GZIPOutputStream(bos);
				byte[] byteData = strWriter.getBuffer().toString().getBytes(
						config.getResourceCharset().name());
				gzOut.write(byteData, 0, byteData.length);
				gzOut.close();
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				data = Channels.newChannel(bis);
			
			}else{
				data = resourceBundleHandler.getResourceBundleChannel(path);
			}
			
			
			WritableByteChannel outChannel = Channels.newChannel(out);
			IOUtils.copy(data, outChannel);
		
		} catch (IOException e) {
			throw new BundlingProcessException(
					"Unexpected IOException writing bundle [" + path
							+ "]", e);
		}finally{
			IOUtils.close(data);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.resource.bundle.ResourceCollector#getConfig()
	 */
	public JawrConfig getConfig() {
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.resource.bundle.ResourceCollector#initAllBundles()
	 */
	public void initAllBundles() {

		if (config.getUseBundleMapping()) {
			bundleMapping = resourceBundleHandler.getJawrBundleMapping();
		}

		// Run through every bundle
		boolean mappingFileExists = resourceBundleHandler
				.isExistingMappingFile();
		boolean processBundleFlag = !config.getUseBundleMapping()
				|| !mappingFileExists;

		if (resourceTypeProcessor != null) {
			GlobalPreprocessingContext ctx = new GlobalPreprocessingContext(
					config, resourceHandler, processBundleFlag);
			resourceTypeProcessor.processBundles(ctx, bundles);
		}

		for (Iterator itCol = bundles.iterator(); itCol.hasNext();) {
			JoinableResourceBundle bundle = (JoinableResourceBundle) itCol
					.next();

			boolean processBundle = processBundleFlag;
			if (!ThreadLocalJawrContext.isBundleProcessingAtBuildTime()
					&& null != bundle.getAlternateProductionURL()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER
							.debug("No bundle generated for '"
									+ bundle.getId()
									+ "' because a production URL is defined for this bundle.");
				}
				processBundle = false;
			}
			if (bundle instanceof CompositeResourceBundle)
				joinAndStoreCompositeResourcebundle(
						(CompositeResourceBundle) bundle, processBundle);
			else
				joinAndStoreBundle(bundle, processBundle);

			if (config.getUseBundleMapping() && !mappingFileExists) {
				JoinableResourceBundlePropertySerializer.serializeInProperties(
						bundle, resourceBundleHandler.getResourceType(),
						bundleMapping);
			}
		}

		if (config.getUseBundleMapping() && !mappingFileExists) {
			resourceBundleHandler.storeJawrBundleMapping(bundleMapping);

			if (resourceBundleHandler.getResourceType().equals(
					JawrConstant.CSS_TYPE)) {
				// Retrieve the image servlet mapping
				ImageResourcesHandler imgRsHandler = (ImageResourcesHandler) config
						.getContext().getAttribute(
								JawrConstant.IMG_CONTEXT_ATTRIBUTE);
				if (imgRsHandler != null) {
					// Here we update the image mapping if we are using the
					// build time bundle processor
					JawrConfig imgJawrConfig = imgRsHandler.getJawrConfig();

					// If we use the full image bundle mapping and the jawr
					// working directory is not located inside the web
					// application
					// We store the image bundle maping which now contains the
					// mapping for CSS images
					String jawrWorkingDirectory = imgJawrConfig
							.getJawrWorkingDirectory();
					if (imgJawrConfig.getUseBundleMapping()
							&& (jawrWorkingDirectory == null || !jawrWorkingDirectory
									.startsWith(JawrConstant.URL_SEPARATOR))) {

						// Store the bundle mapping
						Properties props = new Properties();
						props.putAll(imgRsHandler.getImageMap());
						imgRsHandler.getRsBundleHandler()
								.storeJawrBundleMapping(props);

					}
				}
			}
		}
	}

	/**
	 * Joins the members of a composite bundle in all its variants, storing in a
	 * separate file for each variant.
	 * 
	 * @param composite
	 *            the composite resource bundle
	 * @param processBundle
	 *            the flag indicating if we should process the bundle or not
	 */
	private void joinAndStoreCompositeResourcebundle(
			CompositeResourceBundle composite, boolean processBundle) {

		BundleProcessingStatus status = new BundleProcessingStatus(BundleProcessingStatus.FILE_PROCESSING_TYPE, composite,
				resourceHandler, config);
		
		// Collect all variant names from child bundles
		Map compositeBundleVariants = new HashMap();
		for (Iterator it = composite.getChildBundles().iterator(); it.hasNext();) {
			JoinableResourceBundle childbundle = (JoinableResourceBundle) it
					.next();
			if (null != childbundle.getVariants())
				compositeBundleVariants = VariantUtils.concatVariants(compositeBundleVariants, childbundle.getVariants());
		}
		composite.setVariants(compositeBundleVariants);
		status.setSearchingPostProcessorVariants(true);
		joinAndPostProcessBundle(composite, status, processBundle);

		Map postProcessVariants = status.getPostProcessVariants();
		if(!postProcessVariants.isEmpty()){
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Post process variants found for bundle "+composite.getId()+":"+ postProcessVariants);
			}
			Map newVariants =  VariantUtils.concatVariants(composite.getVariants(), postProcessVariants);
			composite.setVariants(newVariants);
			status.setSearchingPostProcessorVariants(false);
			joinAndPostProcessBundle(composite, status, processBundle);
		}
		
	}

	/**
	 * Joins and post process the variant composite bundle
	 * @param composite the composite bundle
	 * @param status the status
	 * @param compositeBundleVariants the variants
	 * @param processBundle the flag indicating if we must process the bundle or not.
	 */
	private void joinAndPostProcessBundle(CompositeResourceBundle composite,
			BundleProcessingStatus status, boolean processBundle) {
		JoinableResourceBundleContent store;
		
		List allVariants = VariantUtils.getAllVariants(composite.getVariants());
		// Add the default bundle variant (the non variant one)
		allVariants.add(null);
		// Process all variants
		for (Iterator vars = allVariants.iterator(); vars.hasNext();) {

			Map variants = (Map) vars.next();
			status.setBundleVariants(variants);
			store = new JoinableResourceBundleContent();
			for (Iterator it = composite.getChildBundles().iterator(); it
					.hasNext();) {
				JoinableResourceBundle childbundle = (JoinableResourceBundle) it
						.next();
				
				JoinableResourceBundleContent childContent = joinAndPostprocessBundle(childbundle, variants,
						status, processBundle);
				// Do unitary postprocessing.
				status.setProcessingType(BundleProcessingStatus.FILE_PROCESSING_TYPE);
				StringBuffer content = executeUnitaryPostProcessing(composite, status, childContent.getContent(), this.unitaryCompositePostProcessor);
				childContent.setContent(content);
				store.append(childContent);
			}
			
			// Post process composite bundle as needed
			store = postProcessJoinedCompositeBundle(composite, store.getContent(), status);
			
			if (processBundle) {
				
				String variantKey = VariantUtils.getVariantKey(variants);
				String name = VariantUtils.getVariantBundleName(composite
						.getId(), variantKey);
				storeBundle(name, store);
				initBundleDataHashcode(composite, store, variantKey);
			}
		}
	}

	/**
	 * Postprocess the composite bundle only if a composite bundle post processor is defined
	 * @param composite the composite bundle
	 * @param content the content
	 * @param status the status
	 * @return the content
	 */
	private JoinableResourceBundleContent postProcessJoinedCompositeBundle(CompositeResourceBundle composite,
			StringBuffer content, BundleProcessingStatus status) {
		
		JoinableResourceBundleContent store = new JoinableResourceBundleContent();
		StringBuffer processedContent = null;
		status.setProcessingType(BundleProcessingStatus.BUNDLE_PROCESSING_TYPE);
		ResourceBundlePostProcessor bundlePostProcessor = composite.getBundlePostProcessor();
		if (null != bundlePostProcessor){
			
			processedContent = bundlePostProcessor.postProcessBundle(
					status, content);
		}
		else if (null != this.compositePostProcessor){
			
			processedContent = this.compositePostProcessor.postProcessBundle(status, content);
		}
		else{
			processedContent = content;
		}
		
		store.setContent(processedContent);
		
		return store;
	}

	/**
	 * Initialize the bundle data hashcode and initialize the bundle mapping if
	 * needed
	 * 
	 * @param bundle
	 *            the bundle
	 * @param store
	 *            the data to store
	 */
	private void initBundleDataHashcode(JoinableResourceBundle bundle,
			JoinableResourceBundleContent store, String variant) {
		
		String bundleHashcode = bundleHashcodeGenerator.generateHashCode(config, store.getContent().toString());
		bundle.setBundleDataHashCode(variant, bundleHashcode);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ResourceBundlesHandler#containsValidBundleHashcode(java.lang.String)
	 */
	public boolean containsValidBundleHashcode(String requestedPath) {
		
		boolean validHashcode = false;
		
		String[] pathInfos = PathNormalizer.extractBundleInfoFromPath(requestedPath);
		String bundleId = pathInfos[0];
		String variantKey = pathInfos[1];
		String hashcode = pathInfos[2];
		
		JoinableResourceBundle bundle = resolveBundleForPath(bundleId);
		if(bundle != null){
			String bundleHashcode = bundle.getBundleDataHashCode(variantKey);
			if(hashcode == null && bundleHashcode == null ||
					hashcode!= null && hashcode.equals(bundleHashcode)){
				validHashcode = true;
			}
		}
		return validHashcode;
	}
	
	/**
	 * Joins the members of a bundle and stores it
	 * 
	 * @param bundle
	 *            the bundle
	 * @param the
	 *            flag indicating if we should process the bundle or not
	 */
	private void joinAndStoreBundle(JoinableResourceBundle bundle,
			boolean processBundle) {
		
		if(processBundle){
			
			BundleProcessingStatus status = new BundleProcessingStatus(BundleProcessingStatus.FILE_PROCESSING_TYPE, bundle,
					resourceHandler, config);
			JoinableResourceBundleContent store = null;
			
			// Process the bundle
			status.setSearchingPostProcessorVariants(true);
			joinAndPostProcessBundle(bundle, status, processBundle);
			
			Map postProcessVariants = status.getPostProcessVariants();
			if(!postProcessVariants.isEmpty()){
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Post process variants found for bundle "+bundle.getId()+":"+ postProcessVariants);
				}
				Map newVariants =  VariantUtils.concatVariants(bundle.getVariants(), postProcessVariants);
				bundle.setVariants(newVariants);
				status.setSearchingPostProcessorVariants(false);
				joinAndPostProcessBundle(bundle, status, processBundle);
			}
			
			// Store the collected resources as a single file, both in text and gzip
			// formats.
			store = joinAndPostprocessBundle(bundle, null, status, processBundle);
			storeBundle(bundle.getId(), store);
			// Set the data hascode in the bundle, in case the prefix needs to
			// be generated
			initBundleDataHashcode(bundle, store, null);
		
		}
	}

	/**
	 * Store the bundle
	 * @param bundleId the bundle Id to store
	 * @param store the bundle
	 */
	private void storeBundle(String bundleId,
			JoinableResourceBundleContent store) {
		
		if(bundleMustBeProcessedInLive(store.getContent().toString())){
			liveProcessBundles.add(bundleId);
		}
		resourceBundleHandler.storeBundle(bundleId, store);
	}

	/**
	 * Checks if the bundle must be processed in live
	 * @param the bundle content 
	 * @return true if the bundle must be processed in live
	 */
	private boolean bundleMustBeProcessedInLive(
			String content) {
		return content.indexOf(JawrConstant.JAWR_BUNDLE_PATH_PLACEHOLDER) != -1;
	}

	/**
	 * Join and post process the bundle taking in account all its variants.
	 * @param bundle the bundle
	 * @param status the bundle processing status
	 * @param processBundle the flag indicating if we must process the bundle or not
	 */
	private void joinAndPostProcessBundle(JoinableResourceBundle bundle,
			BundleProcessingStatus status, boolean processBundle) {
		
		JoinableResourceBundleContent store;
		List allVariants = VariantUtils.getAllVariants(bundle.getVariants());
		// Add the default bundle variant (the non variant one)
		allVariants.add(null);
		
		for (Iterator it = allVariants.iterator(); it
				.hasNext();) {
			Map variantMap = (Map) it.next();
			status.setBundleVariants(variantMap);
			String variantKey = VariantUtils.getVariantKey(variantMap);
			String name = VariantUtils.getVariantBundleName(bundle
					.getId(), variantKey);
			store = joinAndPostprocessBundle(bundle, variantMap,
					status, processBundle);
			storeBundle(name, store);
			initBundleDataHashcode(bundle, store, variantKey);
		}
	}
	
	/**
	 * Reads all the members of a bundle and executes all associated
	 * postprocessors.
	 * 
	 * @param bundle
	 *            the bundle
	 * @param variants
	 *            the variant map
	 * @param the bundling processing status           
	 * @param the
	 *            flag indicating if we should process the bundle or not
	 * @return the resource bundle content, where all postprocessors have been
	 *         executed
	 */
	private JoinableResourceBundleContent joinAndPostprocessBundle(
			JoinableResourceBundle bundle, Map variants,
			BundleProcessingStatus status, boolean processBundle) {

		JoinableResourceBundleContent bundleContent = new JoinableResourceBundleContent();

		// Don't bother with the bundle if it is excluded because of the
		// inclusion pattern
		// or if we don't process the bundle at start up
		if ((bundle.getInclusionPattern().isExcludeOnDebug() && config
				.isDebugModeOn())
				|| (bundle.getInclusionPattern().isIncludeOnDebug() && !config
						.isDebugModeOn()) || !processBundle)
			return bundleContent;

		StringBuffer bundleData = new StringBuffer();
		StringBuffer store = null;

		try {
			// Run through all the files belonging to the bundle
			for (Iterator it = bundle.getItemPathList(variants).iterator(); it
					.hasNext();) {

				// File is first created in memory using a stringwriter.
				StringWriter writer = new StringWriter();
				BufferedWriter bwriter = new BufferedWriter(writer);

				String path = (String) it.next();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Adding file [" + path + "] to bundle "
							+ bundle.getId());

				// Get a reader on the resource, with appropiate encoding
				Reader rd = null;

				try {
					rd = resourceHandler.getResource(path, true);
				} catch (ResourceNotFoundException e) {
					// If a mapped file does not exist, a warning is issued and
					// process continues normally.
					LOGGER.warn("A mapped resource was not found: [" + path
							+ "]. Please check your configuration");
					continue;
				}

				// Update the status.
				status.setLastPathAdded(path);

				// Copy the content
				IOUtils.copy(rd, bwriter, true);
				
				// Add new line at the end if it doesn't exist
				if(!writer.getBuffer().toString().endsWith(StringUtils.LINE_SEPARATOR)){
					writer.getBuffer().append(StringUtils.LINE_SEPARATOR);
				}
				
				// Do unitary postprocessing.
				status.setProcessingType(BundleProcessingStatus.FILE_PROCESSING_TYPE);
				bundleData.append(executeUnitaryPostProcessing(bundle, status, writer.getBuffer(), this.unitaryPostProcessor));
			}

			// Post process bundle as needed
			store = executeBundlePostProcessing(bundle, status, bundleData);

		} catch (IOException e) {
			throw new BundlingProcessException(
					"Unexpected IOException generating collected file ["
							+ bundle.getId() + "].", e);
		}

		bundleContent.setContent(store);
		return bundleContent;
	}

	/**
	 * Executes the unitary resource post processing
	 * @param bundle the bundle
	 * @param status the bundle processing status
	 * @param content the content to process
	 * @return the processed content
	 */
	private StringBuffer executeUnitaryPostProcessing(JoinableResourceBundle bundle,
			BundleProcessingStatus status, StringBuffer content, ResourceBundlePostProcessor defaultPostProcessor) {
		
		StringBuffer bundleData = new StringBuffer();
		status.setProcessingType(BundleProcessingStatus.FILE_PROCESSING_TYPE);
		if (null != bundle.getUnitaryPostProcessor()) {
			StringBuffer resourceData = bundle
					.getUnitaryPostProcessor().postProcessBundle(
							status, content);

			bundleData.append(resourceData);
		} else if (null != defaultPostProcessor) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("POSTPROCESSING UNIT:"
						+ status.getLastPathAdded());
			StringBuffer resourceData = defaultPostProcessor
					.postProcessBundle(status, content);
			bundleData.append(resourceData);
		} else{
			bundleData = content;
		}
		
		return bundleData;
	}

	/**
	 * Execute the bundle post processing
	 * @param bundle the bundle
	 * @param status the status
	 * @param bundleData the bundle data
	 * @return the processed content
	 */
	private StringBuffer executeBundlePostProcessing(
			JoinableResourceBundle bundle, BundleProcessingStatus status,
			StringBuffer bundleData) {
		
		StringBuffer store;
		status.setProcessingType(BundleProcessingStatus.BUNDLE_PROCESSING_TYPE);
		if (null != bundle.getBundlePostProcessor())
			store = bundle.getBundlePostProcessor().postProcessBundle(
					status, bundleData);
		else if (null != this.postProcessor)
			store = this.postProcessor
					.postProcessBundle(status, bundleData);
		else
			store = bundleData;
		return store;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * resolveBundleForPath(java.lang.String)
	 */
	public JoinableResourceBundle resolveBundleForPath(String path) {

		JoinableResourceBundle theBundle = null;
		for (Iterator it = bundles.iterator(); it.hasNext();) {
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();
			if (bundle.getId().equals(path) || bundle.belongsToBundle(path)) {
				theBundle = bundle;
				break;
			}
		}
		return theBundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.handler.ResourceBundlesHandler#
	 * getClientSideHandler()
	 */
	public ClientSideHandlerGenerator getClientSideHandler() {
		return this.clientSideHandlerGenerator;
	}
	
}
