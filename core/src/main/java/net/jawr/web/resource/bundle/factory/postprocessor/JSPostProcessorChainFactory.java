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
package net.jawr.web.resource.bundle.factory.postprocessor;

import net.jawr.web.resource.bundle.postprocess.AbstractChainedResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.postprocess.PostProcessFactoryConstant;
import net.jawr.web.resource.bundle.postprocess.ResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.postprocess.impl.JSMinPostProcessor;
import net.jawr.web.resource.bundle.postprocess.impl.yui.YUIJSCompressor;

/**
 * PostProcessorChainFactory for javascript resources. 
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 *
 */
public class JSPostProcessorChainFactory extends AbstractPostProcessorChainFactory implements PostProcessorChainFactory {
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.processor.PostProcessorChainFactory#buildDefaultProcessor()
	 */
	public ResourceBundlePostProcessor buildDefaultProcessorChain() {
		JSMinPostProcessor processor = buildJSMinPostProcessor();
		processor.addNextProcessor(buildLicensesProcessor());
		return processor;
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.processor.PostProcessorChainFactory#buildDefaultUnitProcessor()
	 */
	public ResourceBundlePostProcessor buildDefaultUnitProcessorChain() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.processor.PostProcessorChainFactory#getPostProcessor(java.lang.String)
	 */
	protected AbstractChainedResourceBundlePostProcessor buildProcessorByKey(String procesorKey){
		if(PostProcessFactoryConstant.JSMIN.equals(procesorKey))
			return buildJSMinPostProcessor();
		else if (PostProcessFactoryConstant.LICENSE_INCLUDER.equals(procesorKey))
			return buildLicensesProcessor();
		else if (PostProcessFactoryConstant.YUI_COMPRESSOR.equals(procesorKey))
			return new YUIJSCompressor(false);
		else if (PostProcessFactoryConstant.YUI_COMPRESSOR_OBFUSCATOR.equals(procesorKey))
			return new YUIJSCompressor(true);
		else throw new IllegalArgumentException("The supplied key [" + procesorKey + "] is not bound to any ResourceBundlePostProcessor. Please check the documentation for valid keys. ");
	}
	
	private JSMinPostProcessor buildJSMinPostProcessor() {
		return new JSMinPostProcessor();
	}
	
}
