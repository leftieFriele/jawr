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
package net.jawr.web.resource.bundle.generator;

import java.io.InputStream;

/**
 * This interface is implemented by all class which generates image resources.
 *  
 * @author Ibrahim Chaehoi
 *
 */
public interface StreamResourceGenerator extends PrefixedResourceGenerator{

	/**
	 * Created an inputStream on a generated image resource (any image not read from the war file 
	 * structure). 
	 * 
	 * @param context the generator context
	 * @return an inputStream
	 */
	public InputStream createResourceAsStream(GeneratorContext context);
}
