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
package net.jawr.web.servlet.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.resource.bundle.IOUtils;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;

/**
 * Holds a reference to the supported media types paired with their corresponding MIME types. 
 * 
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class MIMETypesSupport {
	
	private static Properties supportedMIMETypes;
	private static final String MIME_PROPS_LOCATION = "/net/jawr/web/resource/image/mimetypes.properties";
	
	/**
	 * Returns a Map object containing all the supopported media 
	 * extensions, paired to their MIME type.  
	 * @param ref An object reference to anchor the classpath (any 'this' reference does). 
	 * @return
	 */
	public static Map getSupportedProperties(Object ref) {
		
		if(null == supportedMIMETypes) {
			synchronized (MIMETypesSupport.class) {
				if(null == supportedMIMETypes) {
					// Load the supported MIME types out of a properties file
					InputStream is = null;
					try {
						is = ClassLoaderResourceUtils.getResourceAsStream(MIME_PROPS_LOCATION, ref);
						supportedMIMETypes = new Properties();
						supportedMIMETypes.load(is);
					} catch (FileNotFoundException e) {
						throw new BundlingProcessException("Error retrieving " + MIME_PROPS_LOCATION 
													+ ".Please check your classloader settings");
					} catch (IOException e) {
						throw new BundlingProcessException("Error retrieving " + MIME_PROPS_LOCATION 
													+ ".Please check your classloader settings");
					}finally{
						IOUtils.close(is);
					}
				}
			}
		}
		
		return supportedMIMETypes;
	}

}
