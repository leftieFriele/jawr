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
package net.jawr.web.resource.bundle.global.preprocessor;

/**
 * This class defines the abstract class for the preprocessor, which handle one type of resource bundle.
 * 
 * @author Ibrahim Chaehoi
 */
public abstract class AbstractChainedGlobalPreprocessor implements
		ChainedGlobalPreprocessor {

	/** The ID of the resource type bundle processor */
	private String id;
	
	/** The next processor */
	private ChainedGlobalPreprocessor nextProcessor;
	
	/**
	 * Constructor
	 * @param id the ID of the processor
	 */
	public AbstractChainedGlobalPreprocessor(String id) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.global.processor.ChainedResourceTypeBundleProcessor#addNextProcessor(net.jawr.web.resource.bundle.global.processor.ChainedResourceTypeBundleProcessor)
	 */
	public void addNextProcessor(
			ChainedGlobalPreprocessor nextProcessor) {
		
		if(this.nextProcessor == null){
			this.nextProcessor = nextProcessor;
		}else{
			this.nextProcessor.addNextProcessor(nextProcessor);
		}
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.global.processor.ChainedResourceTypeBundleProcessor#getId()
	 */
	public String getId() {
		
		return this.id;
	}

}
