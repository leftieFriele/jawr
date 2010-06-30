/**
 * Copyright 2007-2009 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.postprocess;

import java.io.IOException;

import net.jawr.web.exception.BundlingProcessException;

import org.apache.log4j.Logger;


/**
 * Chained abstract implementation of ResourceBundlePostProcessor. Implementations
 * can be used as a processing chain. 
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */ 
public abstract class AbstractChainedResourceBundlePostProcessor implements
		ChainedResourceBundlePostProcessor {
	
	/** The logger */
	private static final Logger LOGGER = Logger.getLogger(AbstractChainedResourceBundlePostProcessor.class);
	
	/** The next post processor */
	private ChainedResourceBundlePostProcessor nextProcessor;
	
	/** The ID of the chained bundle post processor */
	private String id;
	
	/**
	 * Constructor
	 * @param id the id of the post processor
	 */
	public AbstractChainedResourceBundlePostProcessor(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the ID of the ChainedResourceBundlePostProcessor
	 * @return the ID of the ChainedResourceBundlePostProcessor
	 */
	public String getId(){
		StringBuffer strId = new StringBuffer();
		strId.append(id);
		if(nextProcessor != null){
			strId.append(","+nextProcessor.getId());
		}
		return strId.toString();
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.postprocess.ResourceBundlePostProcessor#postProcessBundle(java.lang.StringBuffer)
	 */
	public StringBuffer postProcessBundle(BundleProcessingStatus status, StringBuffer bundleData) {
		StringBuffer processedBundle = null;
		try {
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("postprocessing bundle:" + status.getCurrentBundle().getId());
			processedBundle = doPostProcessBundle(status,bundleData);
		} catch (IOException e) {
			throw new BundlingProcessException("Unexpected IOException during execution of a postprocessor.",e);
		}
		if(null != nextProcessor) {
			processedBundle = nextProcessor.postProcessBundle(status,processedBundle);
		}		
		return processedBundle;
	}
	
	/**
	 * Set the next post processor in the chain. 
	 * @param nextProcessor the post processor to set
	 */
	public void addNextProcessor(ChainedResourceBundlePostProcessor nextProcessor) {
		if(this.nextProcessor == null){
			this.nextProcessor = nextProcessor;
		}else{
			this.nextProcessor.addNextProcessor(nextProcessor);
		}
	}
	
	/**
	 * Postprocess a bundle of resources in the context of this chain of processors. 
	 * @param bundleData the bundle data
	 * @return the processed content
	 * @throws IOException if an IOException occurs
	 */
	protected abstract StringBuffer doPostProcessBundle(BundleProcessingStatus status, StringBuffer bundleData) throws IOException;
}
