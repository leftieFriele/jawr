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
package net.jawr.web.resource.bundle.generated;

import java.io.Reader;
import java.nio.charset.Charset;

import net.jawr.web.resource.bundle.message.MessageBundleScriptCreator;

/**
 * A generator that creates a script from message bundles.
 * The generated script can be used to reference the message literals easily from javascript.  
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class ResourceBundleMessagesGenerator implements ResourceGenerator {

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generated.ResourceGenerator#createResource(java.lang.String, java.nio.charset.Charset)
	 */
	public Reader createResource(String path,Charset charset) {
		MessageBundleScriptCreator creator = new MessageBundleScriptCreator(path);
		return creator.createScript();
	}

}
