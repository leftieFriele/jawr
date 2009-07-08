/**
 * Copyright 2008-2009 Jordi Hern�ndez Sell�s
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

import net.jawr.web.JawrConstant;

/**
 * Abstract implementation of ResourceGenerator with a default return value for the 
 * getMappingPrefix method. 
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public abstract class AbstractJavascriptGenerator implements SpecificCDNDebugPathResourceGenerator{

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#getMappingPrefix()
	 */
	public String getDebugModeRequestPath() {		
		return ResourceGenerator.JAVASCRIPT_DEBUGPATH;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#getDebugModeBuildTimeGenerationPath(java.lang.String)
	 */
	public String getDebugModeBuildTimeGenerationPath(String path) {
		
		return path.replaceFirst(GeneratorRegistry.PREFIX_SEPARATOR, JawrConstant.URL_SEPARATOR)+"."+JawrConstant.JS_TYPE;
	}
}
