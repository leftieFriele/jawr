/**
 * Copyright 2010 Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.hashcode;

import net.jawr.web.config.JawrConfig;

/**
 * This interfaces defines the methods for the bundle hashcode generator
 * 
 * @author Ibrahim Chaehoi
 */
public interface BundleHashcodeGenerator {

	/**
	 * Returns the hashcode for the content given in parameter
	 * @param content the content to hash
	 * @return the hashcode for the content given in parameter
	 */
	String generateHashCode(JawrConfig config, String content);
	
}
