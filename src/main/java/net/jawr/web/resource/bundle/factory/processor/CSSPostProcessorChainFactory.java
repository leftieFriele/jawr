/**
 * Copyright 2007 Jordi Hern�ndez Sell�s
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
package net.jawr.web.resource.bundle.factory.processor;

import net.jawr.web.resource.bundle.postprocess.AbstractChainedResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.postprocess.ResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.postprocess.impl.CSSMinPostProcessor;
import net.jawr.web.resource.bundle.postprocess.impl.CSSURLPathRewriterPostProcessor;
import net.jawr.web.resource.bundle.postprocess.impl.yui.YUICSSCompressor;

/**
 * PostProcessorChainFactory for css resources. 
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class CSSPostProcessorChainFactory extends
		AbstractPostProcessorChainFactory implements PostProcessorChainFactory {

	private static final String CSS_MINIFIER = "cssminify";
	private static final String URL_PATH_REWRITER = "csspathrewriter";
	private static final String YUI_COMPRESSOR = "YUI";
		
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.processor.PostProcessorChainFactory#buildDefaultProcessorChain()
	 */
	public ResourceBundlePostProcessor buildDefaultProcessorChain() {
		YUICSSCompressor processor = new YUICSSCompressor();
		processor.setNextProcessor(buildLicensesProcessor());
		return processor;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.processor.PostProcessorChainFactory#buildDefaultUnitProcessorChain()
	 */
	public ResourceBundlePostProcessor buildDefaultUnitProcessorChain() {
		return new CSSURLPathRewriterPostProcessor();
	}
	
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.processor.PostProcessorChainFactory#getPostProcessor(java.lang.String)
	 */
	protected AbstractChainedResourceBundlePostProcessor buildProcessorByKey(String processorKey){
		
		if (LICENSE_INCLUDER.equals(processorKey))
			return buildLicensesProcessor();
		else if(CSS_MINIFIER.equals(processorKey))
			return new CSSMinPostProcessor();
		else if (URL_PATH_REWRITER.equals(processorKey))
			return new CSSURLPathRewriterPostProcessor();
		else if (YUI_COMPRESSOR.equals(processorKey))
			return new YUICSSCompressor();
		
		else throw new IllegalArgumentException("The supplied key [" + processorKey + "] is not bound to any ResourceBundlePostProcessor. Please check the documentation for valid keys. ");
	}
}
