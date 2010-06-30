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
package net.jawr.web.resource.bundle.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.resource.bundle.IOUtils;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;

import org.apache.log4j.Logger;

/**
 * This generator defines the generator which generates the themeSwicther javascript file.
 * 
 * @author Ibrahim Chaehoi
 *
 */
public class SkinSwitcherJsGenerator extends AbstractJavascriptGenerator {

	/** The logger */
	private static final Logger LOGGER = Logger.getLogger(SkinSwitcherJsGenerator.class);
	
	/** The script template */
	private static final String SCRIPT_TEMPLATE = "/net/jawr/web/resource/bundle/skin/skinSwitcher.js";
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#createResource(net.jawr.web.resource.bundle.generator.GeneratorContext)
	 */
	public Reader createResource(GeneratorContext context) {
		JawrConfig config = context.getConfig();
		String skinCookieName = config.getSkinCookieName();
		String script = createScript(skinCookieName);
		return new StringReader(script);
	}
	
	/**
	 * Loads a template containing the functions which convert properties into methods. 
	 * @return
	 */
	private String createScript(String skinCookieName) {
		StringWriter sw = new StringWriter();
		InputStream is = null;
		try {
			is = ClassLoaderResourceUtils.getResourceAsStream(SCRIPT_TEMPLATE,this);
            IOUtils.copy(is, sw);
        } catch (IOException e) {
			LOGGER.fatal("a serious error occurred when initializing ThemeSwitcherJsGenerator");
			throw new BundlingProcessException("Classloading issues prevent loading the themeSwitcher template to be loaded. ",e);
		}finally{
			IOUtils.close(is);
		}
		
		return sw.getBuffer().toString().replaceAll("\\{JAWR_SKIN_COOKIE_NAME\\}", skinCookieName);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.PrefixedResourceGenerator#getMappingPrefix()
	 */
	public String getMappingPrefix() {
		return GeneratorRegistry.SKIN_SWTICHER_GENERATOR_PREFIX;
	}

}
