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
package net.jawr.web.resource.bundle.factory;

import java.util.Properties;

import net.jawr.web.resource.bundle.factory.util.ConfigPropertiesSource;
import net.jawr.web.resource.bundle.factory.util.PropsFilePropertiesSource;

import org.apache.log4j.Logger;

/**
 * ConfigPropertiesSource implementation that reads its values from a Properties object.
 * 
 * @author Ibrahim Chaehoi
 */
public class PropsConfigPropertiesSource implements ConfigPropertiesSource {

	/** The logger */
	private static final Logger LOGGER = Logger.getLogger(PropsFilePropertiesSource.class.getName());
	
	/** The configuration properties */
	private Properties configProps;
	
	/** The properties hashcode */
	protected int propsHashCode;
	
	/**
	 * Constructor
	 * @param props the properties
	 */
	public PropsConfigPropertiesSource(Properties props) {
		this.configProps = props;
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.util.ConfigPropertiesSource#configChanged()
	 */
	public boolean configChanged() {
		int currentConfigHash = this.configProps.hashCode();
		boolean configChanged = this.propsHashCode != currentConfigHash;
		
		if(configChanged && LOGGER.isDebugEnabled())
			LOGGER.debug("Changes in configuration properties file detected.");
			
		this.propsHashCode = currentConfigHash;
		
		return configChanged;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.factory.util.ConfigPropertiesSource#getConfigProperties()
	 */
	public Properties getConfigProperties() {
		return configProps;
	}

}
