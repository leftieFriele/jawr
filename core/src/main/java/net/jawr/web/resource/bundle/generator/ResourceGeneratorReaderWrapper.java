/**
 * Copyright 2009-2010 Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.generator;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.jawr.web.JawrConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.resource.FileNameUtils;
import net.jawr.web.resource.bundle.generator.variant.VariantResourceGenerator;
import net.jawr.web.resource.handler.reader.ResourceReaderHandler;
import net.jawr.web.resource.handler.reader.TextResourceReader;
import net.jawr.web.util.StringUtils;

/**
 * This class defines the wrapper class for resource generator in text resource reader.
 * 
 * @author Ibrahim Chaehoi
 */
public class ResourceGeneratorReaderWrapper implements TextResourceReader {

	/** The resource generator wrapped */
	private ResourceGenerator generator;
	
	/** The resource handler */
	private ResourceReaderHandler rsHandler;
	
	/** The Jawr config */
	private JawrConfig config;
	
	/**
	 * Constructor
	 * @param generator the generator
	 */
	public ResourceGeneratorReaderWrapper(ResourceGenerator generator, ResourceReaderHandler rsHandler, JawrConfig config) {
		this.generator = generator;
		this.config = config;
		this.rsHandler = rsHandler;
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.ResourceReader#getResource(java.lang.String)
	 */
	public Reader getResource(String resourceName) {
		
		return getResource(resourceName, false);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.ResourceReader#getResource(java.lang.String, boolean)
	 */
	public Reader getResource(String resourceName, boolean processingBundle) {
		
		Locale locale = null;
		String path = resourceName;
		// Remove generator prefix
		path = path.substring((generator.getMappingPrefix()+GeneratorRegistry.PREFIX_SEPARATOR).length());
		
		Map contextVariants = new HashMap();
		Map variantSets = new HashMap();
		if(generator instanceof VariantResourceGenerator || generator instanceof LocaleAwareResourceGenerator){
			
			int variantSuffixIdx = path.indexOf("@");
			if(variantSuffixIdx != -1){
				String variantKey = null;

				variantKey = FileNameUtils.getBaseName(path.substring(path.indexOf('@')+1));
					// Remove variant suffix
					String extension = FileNameUtils.getExtension(path);
					path = path.substring(0, variantSuffixIdx);
					if(StringUtils.isNotEmpty(extension)){
						path = path+"."+extension;
					}
				String[] variants = variantKey.split("@");
				if(generator instanceof VariantResourceGenerator){
					variantSets = ((VariantResourceGenerator)generator).getAvailableVariants(path);
				}else{ // instanceof LocaleAwareResourceGenerator
					List availableLocales = ((LocaleAwareResourceGenerator)generator).getAvailableLocales(path);
					if(availableLocales != null){
						variantSets.put(JawrConstant.LOCALE_VARIANT_TYPE, availableLocales);
					}
				}
				
				ArrayList variantTypes = new ArrayList(variantSets.keySet());
				Collections.sort(variantTypes);
				int nbVariants = variants.length;
				
				for(int i = 0; i < nbVariants; i++){
					String variantType = (String) variantTypes.get(i);
					String variantValue = variants[i];
					contextVariants.put(variantType, variantValue);
					if(variantType == JawrConstant.LOCALE_VARIANT_TYPE){
						// Resourcebundle should be doing this for me...
						String[] params = variantValue.split("_");			
						switch(params.length) {
							case 3:
								locale = new Locale(params[0],params[1],params[2]);
								break;
							case 2: 
								locale = new Locale(params[0],params[1]);
								break;
							default:
								locale = new Locale(variantValue);
						}
					}
				}
			}
		}
		
		GeneratorContext context = new GeneratorContext(config, path);
		context.setVariantMap(contextVariants);
		context.setVariantSets(variantSets);
		
		context.setLocale(locale);
		context.setResourceReaderHandler(rsHandler);
		context.setProcessingBundle(processingBundle);
		
		return generator.createResource(context);
	}
	
}
