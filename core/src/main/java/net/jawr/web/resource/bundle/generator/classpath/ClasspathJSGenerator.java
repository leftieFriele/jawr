/**
 * Copyright 2008 Jordi Hern�ndez Sell�s
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
package net.jawr.web.resource.bundle.generator.classpath;

import java.io.Reader;

import net.jawr.web.JawrConstant;
import net.jawr.web.resource.FileNameUtils;
import net.jawr.web.resource.bundle.generator.AbstractJavascriptGenerator;
import net.jawr.web.resource.bundle.generator.GeneratorContext;
import net.jawr.web.resource.bundle.generator.GeneratorRegistry;
import net.jawr.web.resource.bundle.generator.ResourceGenerator;

/**
 * This class defines the resource generator, which loads Javascript resources from the classpath.  
 * 
 * @author Jordi Hern�ndez Sell�s
 */
public class ClasspathJSGenerator extends AbstractJavascriptGenerator implements ResourceGenerator {

	/** The classpath generator helper */
	private ClassPathGeneratorHelper helper;
	
	/**
	 * Constructor
	 */
	public ClasspathJSGenerator() {
		helper = new ClassPathGeneratorHelper();
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#createResource(java.lang.String, java.nio.charset.Charset)
	 */
	public Reader createResource(GeneratorContext context) {
		
		Reader rd = null;
		if(FileNameUtils.isExtension(context.getPath(), JawrConstant.JS_TYPE)){
			rd = helper.createResource(context);
		}
		return rd;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#getMappingPrefix()
	 */
	public String getMappingPrefix() {
		return GeneratorRegistry.CLASSPATH_RESOURCE_BUNDLE_PREFIX;
	}

}
