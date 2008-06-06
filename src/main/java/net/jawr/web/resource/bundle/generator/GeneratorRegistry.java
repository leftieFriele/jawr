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
package net.jawr.web.resource.bundle.generator;

import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.jawr.web.resource.bundle.generator.classpath.ClasspathResourceGenerator;
import net.jawr.web.resource.bundle.generator.dwr.DWRBeanGenerator;
import net.jawr.web.resource.bundle.generator.validator.CommonsValidatorGenerator;
import net.jawr.web.resource.bundle.locale.ResourceBundleMessagesGenerator;

/**
 * Registry for resource generators, which create scripts or CSS data dynamically, 
 * as opposed to the usual behavior of reading a resource from the war file. 
 * It provides methods to determine if a path mapping should be handled by a generator, 
 * and to actually render the resource using the appropiate generator. 
 * Path mappings which require generation will use a prefix (preferrably one which ends with 
 * a colon, such as 'messages:'). 
 * Generators provided with Jawr will be automatically mapped. 
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class GeneratorRegistry {
	
	public static final String MESSAGE_BUNDLE_PREFIX = "messages:";
	public static final String CLASSPATH_BUNDLE_PREFIX = "jar:";
	public static final String DWR_BUNDLE_PREFIX = "dwr:";
	public static final String COMMONS_VALIDATOR_PREFIX = "acv:";
	
	private static final Map registry = new HashMap();
	private static final List prefixRegistry = new ArrayList();
	private ServletContext servletContext;
	
	static
	{
		prefixRegistry.add(MESSAGE_BUNDLE_PREFIX);
		prefixRegistry.add(CLASSPATH_BUNDLE_PREFIX);
		prefixRegistry.add(DWR_BUNDLE_PREFIX);
		prefixRegistry.add(COMMONS_VALIDATOR_PREFIX);
	}
	

	public GeneratorRegistry(ServletContext servletContext) {
		super();
		this.servletContext = servletContext;
	}
	
	/**
	 * Use only for testing purposes.
	 */
	public GeneratorRegistry(){
		super();
	}
	
	/**
	 * Lazy loads generators, to avoid the need for undesired dependencies. 
	 * 
	 * @param generatorKey
	 * @return
	 */
	private void loadGenerator(String generatorKey) {
		if(MESSAGE_BUNDLE_PREFIX.equals(generatorKey)){
			registry.put(generatorKey, new ResourceBundleMessagesGenerator());
		}
		else if(CLASSPATH_BUNDLE_PREFIX.equals(generatorKey)){
			registry.put(generatorKey, new ClasspathResourceGenerator());
		}
		else if(DWR_BUNDLE_PREFIX.equals(generatorKey)){
			registry.put(generatorKey, new DWRBeanGenerator());
		}
		else if(COMMONS_VALIDATOR_PREFIX.equals(generatorKey)){
			registry.put(generatorKey, new CommonsValidatorGenerator());
		}
	}
	
	/**
	 * Register a generator mapping it to the specified prefix. 
	 * 
	 * @param prefix
	 * @param clazz
	
	public void registerGenerator(String prefix, Class clazz){
		
	} */
	
	/**
	 * Determines wether a path is to be handled by a generator. 
	 * @param path
	 * @return
	 */
	public boolean isPathGenerated(String path) {
		return null != matchPath(path);
	}
	
	/**
	 * Creates the contents corresponding to a path, by using the appropiate generator. 
	 * @param path
	 * @param charset
	 * @return
	 */
	public Reader createResource(String path,Charset charset) {
		String key = matchPath(path);
		return ((ResourceGenerator)registry.get(key)).createResource(path.substring(key.length()),servletContext,charset);
	}
	
	/**
	 * Get the key from the mappings that corresponds to the specified path. 
	 * @param path
	 * @return
	 */
	private String matchPath(String path) {
		String rets = null;
		for(Iterator it = prefixRegistry.iterator();it.hasNext() && rets == null;) {
			String prefix = (String) it.next();
			if(path.startsWith(prefix))
				rets = prefix;			
		}	
		// Lazy load generator
		if(null != rets && !registry.containsKey(rets))
			loadGenerator(rets);
		
		return rets;
	}

}
