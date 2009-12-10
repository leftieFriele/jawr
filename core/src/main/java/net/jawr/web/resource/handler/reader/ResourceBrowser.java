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
package net.jawr.web.resource.handler.reader;

import java.util.Set;

/**
 * The interface of Objects, which can handle resource browsing.
 * 
 * @author Ibrahim Chaehoi
 */
public interface ResourceBrowser {

	/**
	 * Returns a list of resources at a specified path within the resources directory
	 * (normally in the war). 
	 * @param path
	 * @return a list of resources at the specified path
	 */
	public Set getResourceNames(String path);
	
	/**
	 * Determines wether a given path is a directory. 
	 * @param path the path to check
	 * @return true if the path is a directory
	 */
	public boolean isDirectory(String path);
	
	
}
