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
package test.net.jawr.web.resource.bundle.generator.variant.css;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;
import net.jawr.web.JawrConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.resource.bundle.generator.GeneratorContext;
import net.jawr.web.resource.bundle.generator.GeneratorRegistry;
import net.jawr.web.resource.bundle.generator.variant.css.CssSkinGenerator;
import net.jawr.web.resource.bundle.variant.VariantSet;
import net.jawr.web.resource.handler.reader.FileSystemResourceReader;
import net.jawr.web.resource.handler.reader.ResourceBrowser;
import net.jawr.web.resource.handler.reader.ServletContextResourceReaderHandler;

import org.apache.wicket.util.io.IOUtils;

import test.net.jawr.web.FileUtils;
import test.net.jawr.web.servlet.mock.MockServletContext;

/**
 * @author Ibrahim Chaehoi
 *
 */
public class CssSkinGeneratorTestCase extends TestCase {

	public void testCssSkinGeneratorBasic() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/basic/default/");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ResourceBrowser rsBrowser = new FileSystemResourceReader(baseDir, config);
		CssSkinGenerator generator = new CssSkinGenerator(rsBrowser, config);
		Map variantMap = generator.getAvailableVariants("/css/panel/basic/default/temp1.css");
		assertFalse(variantMap.isEmpty());
		Set skinVariants = (Set) variantMap.get(JawrConstant.SKIN_VARIANT_TYPE);
		assertNotNull(skinVariants);
		assertEquals(3, skinVariants.size());
		assertTrue(skinVariants.contains("default"));
		assertTrue(skinVariants.contains("winter"));
		assertTrue(skinVariants.contains("summer"));
	}
	
	public void testCssSkinGeneratorBasicWithFolderHierarchy() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/basic/default/");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ResourceBrowser rsBrowser = new FileSystemResourceReader(baseDir, config);
		CssSkinGenerator generator = new CssSkinGenerator(rsBrowser, config);
		Map variantMap = generator.getAvailableVariants("/css/panel/basic/default/border/squareBorder/borderSquare1.css");
		assertFalse(variantMap.isEmpty());
		Set skinVariants = (Set) variantMap.get(JawrConstant.SKIN_VARIANT_TYPE);
		assertNotNull(skinVariants);
		assertEquals(3, skinVariants.size());
		assertTrue(skinVariants.contains("default"));
		assertTrue(skinVariants.contains("winter"));
		assertTrue(skinVariants.contains("summer"));
	}
	
	public void testCssSkinGeneratorI18n() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/skin_locale/default/en_US");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ResourceBrowser rsBrowser = new FileSystemResourceReader(baseDir, config);
		CssSkinGenerator generator = new CssSkinGenerator(rsBrowser, config);
		Map variantMap = generator.getAvailableVariants("/css/panel/skin_locale/default/en_US/temp1.css");
		assertFalse(variantMap.isEmpty());
		
		// check skin variants 
		Set skinVariants = (Set) variantMap.get(JawrConstant.SKIN_VARIANT_TYPE);
		assertNotNull(skinVariants);
		assertEquals(3, skinVariants.size());
		assertTrue(skinVariants.contains("default"));
		assertTrue(skinVariants.contains("winter"));
		assertTrue(skinVariants.contains("summer"));
		
		// check locale variants
		Set localeVariants = (Set) variantMap.get(JawrConstant.LOCALE_VARIANT_TYPE);
		assertNotNull(localeVariants);
		assertEquals(3, localeVariants.size());
		assertTrue(localeVariants.contains("en_US"));
		assertTrue(localeVariants.contains("fr_FR"));
		assertTrue(localeVariants.contains("es_ES"));
	}
	
	public void testCssSkinGeneratorI18nLocaleSkinMapping() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/locale_skin/en_US/default/");
		props.setProperty("jawr.css.skin.type.mapping", "locale_skin");
		
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ResourceBrowser rsBrowser = new FileSystemResourceReader(baseDir, config);
		CssSkinGenerator generator = new CssSkinGenerator(rsBrowser, config);
		Map variantMap = generator.getAvailableVariants("/css/panel/locale_skin/en_US/default/temp1.css");
		assertFalse(variantMap.isEmpty());
		
		// check skin variants 
		Set skinVariants = (Set) variantMap.get(JawrConstant.SKIN_VARIANT_TYPE);
		assertNotNull(skinVariants);
		assertEquals(3, skinVariants.size());
		assertTrue(skinVariants.contains("default"));
		assertTrue(skinVariants.contains("winter"));
		assertTrue(skinVariants.contains("summer"));
		
		// check locale variants
		Set localeVariants = (Set) variantMap.get(JawrConstant.LOCALE_VARIANT_TYPE);
		assertNotNull(localeVariants);
		assertEquals(3, localeVariants.size());
		assertTrue(localeVariants.contains("en_US"));
		assertTrue(localeVariants.contains("fr_FR"));
		assertTrue(localeVariants.contains("es_ES"));
	}
	
	public void testCssSkinGeneratorSkinDirsOverride() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/basic/default/,/css/panel/basic/default/border/");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ResourceBrowser rsBrowser = new FileSystemResourceReader(baseDir, config);
		try{
			new CssSkinGenerator(rsBrowser, config);
			fail("No exception has been thrown");
		}catch(Exception e){
			assertTrue(e instanceof BundlingProcessException);
		}
	}
	
	public void testCssSkinGeneratorSkinI18nDirsOverride() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/skin_locale/default/en_US,/css/panel/skin_locale/default/fr_FR");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ResourceBrowser rsBrowser = new FileSystemResourceReader(baseDir, config);
		try{
			new CssSkinGenerator(rsBrowser, config);
			fail("No exception has been thrown");
		}catch(Exception e){
			assertTrue(e instanceof BundlingProcessException);
		}
	}
	
	public void testCssSkinReaderProviderBasicDefaultSkin() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/basic/default/");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		
		GeneratorContext context = new GeneratorContext(config, "/css/panel/basic/default/temp1.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "default");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/basic/default/temp1.css")), writer.getBuffer().toString());
	}
	
	public void testCssSkinReaderProviderBasicSkin() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/basic/default/");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		GeneratorContext context = new GeneratorContext(config, "/css/panel/basic/default/temp2.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "winter");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/basic/winter/temp2.css")), writer.getBuffer().toString());
	}
	
	public void testCssSkinReaderProviderBasicSkinFileExistInDefault() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/basic/default/");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		
		GeneratorContext context = new GeneratorContext(config, "/css/panel/basic/default/temp1.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "winter");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/basic/default/temp1.css")), writer.getBuffer().toString());
	}
	
	public void testCssSkinReaderProviderBasicSkinWithLocale() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/skin_locale/default/en_US");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		
		GeneratorContext context = new GeneratorContext(config, "/css/panel/skin_locale/default/en_US/temp1.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "default");
		variantMap.put(JawrConstant.LOCALE_VARIANT_TYPE, "en_US");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		variantSets.put(JawrConstant.LOCALE_VARIANT_TYPE, new VariantSet(JawrConstant.LOCALE_VARIANT_TYPE, "en_US", new HashSet(Arrays.asList(new String[]{"en_US","fr_FR","es_ES"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/skin_locale/default/en_US/temp1.css")), writer.getBuffer().toString());
	}

	public void testCssSkinReaderProviderWinterSkinWithLocale() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/skin_locale/default/en_US");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		
		GeneratorContext context = new GeneratorContext(config, "/css/panel/skin_locale/default/en_US/temp2.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "winter");
		variantMap.put(JawrConstant.LOCALE_VARIANT_TYPE, "en_US");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		variantSets.put(JawrConstant.LOCALE_VARIANT_TYPE, new VariantSet(JawrConstant.LOCALE_VARIANT_TYPE, "en_US", new HashSet(Arrays.asList(new String[]{"en_US","fr_FR","es_ES"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/skin_locale/winter/en_US/temp2.css")), writer.getBuffer().toString());
	}

	public void testCssSkinReaderProviderWinterSkinWithLocaleFileInDefault() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/skin_locale/default/en_US");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		
		GeneratorContext context = new GeneratorContext(config, "/css/panel/skin_locale/default/en_US/temp1.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "winter");
		variantMap.put(JawrConstant.LOCALE_VARIANT_TYPE, "en_US");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		variantSets.put(JawrConstant.LOCALE_VARIANT_TYPE, new VariantSet(JawrConstant.LOCALE_VARIANT_TYPE, "en_US", new HashSet(Arrays.asList(new String[]{"en_US","fr_FR","es_ES"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/skin_locale/default/en_US/temp1.css")), writer.getBuffer().toString());
	}

	public void testCssSkinReaderProviderWinterSkinWithNonDefaultLocale() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/skin_locale/default/en_US");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		
		GeneratorContext context = new GeneratorContext(config, "/css/panel/skin_locale/default/en_US/temp2.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "winter");
		variantMap.put(JawrConstant.LOCALE_VARIANT_TYPE, "es_ES");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		variantSets.put(JawrConstant.LOCALE_VARIANT_TYPE, new VariantSet(JawrConstant.LOCALE_VARIANT_TYPE, "en_US", new HashSet(Arrays.asList(new String[]{"en_US","fr_FR","es_ES"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/skin_locale/winter/es_ES/temp2.css")), writer.getBuffer().toString());
	}
	
	public void testCssSkinReaderProviderWinterSkinWithNonExistingLocale() throws Exception {
		
		Properties props = new Properties();
		props.setProperty("jawr.css.skin.default.root.dirs", "/css/panel/skin_locale/default/en_US");
		JawrConfig config = new JawrConfig(props);
		GeneratorRegistry generatorRegistry = new GeneratorRegistry(JawrConstant.CSS_TYPE);
		generatorRegistry.setConfig(config);
		config.setGeneratorRegistry(generatorRegistry);
		
		String baseDir = FileUtils.getClasspathRootDir()+"/generator/variant";
		ServletContextResourceReaderHandler rsHandler = createResourceReaderHandler(baseDir, config);
		
		CssSkinGenerator generator = new CssSkinGenerator(rsHandler, config);
		
		GeneratorContext context = new GeneratorContext(config, "/css/panel/skin_locale/default/en_US/temp2.css");
		Map variantMap = new HashMap();
		variantMap.put(JawrConstant.SKIN_VARIANT_TYPE, "winter");
		variantMap.put(JawrConstant.LOCALE_VARIANT_TYPE, "fr_FR");
		context.setVariantMap(variantMap);
		Map variantSets = new HashMap();
		variantSets.put(JawrConstant.SKIN_VARIANT_TYPE, new VariantSet(JawrConstant.SKIN_VARIANT_TYPE, "default", new HashSet(Arrays.asList(new String[]{"default","winter","summer"}))));
		variantSets.put(JawrConstant.LOCALE_VARIANT_TYPE, new VariantSet(JawrConstant.LOCALE_VARIANT_TYPE, "en_US", new HashSet(Arrays.asList(new String[]{"en_US","fr_FR","es_ES"}))));
		context.setVariantSets(variantSets);
		context.setResourceReaderHandler(rsHandler);
		Reader rd = generator.createResource(context);
		assertNotNull(rd);
		StringWriter writer = new StringWriter();
		IOUtils.copy(rd, writer);
		assertEquals(FileUtils.readFile(new File(baseDir, "/css/panel/skin_locale/winter/en_US/temp2.css")), writer.getBuffer().toString());
	}
	
	protected ServletContextResourceReaderHandler createResourceReaderHandler(String rootDir, JawrConfig config) {
		try {
		    
		    MockServletContext ctx = new MockServletContext(rootDir, rootDir + "/temp/");
		    GeneratorRegistry generatorRegistry = new GeneratorRegistry();
		    return new ServletContextResourceReaderHandler(ctx, config, generatorRegistry);
		} catch (Exception ex) {
		     ex.printStackTrace();
		   throw new RuntimeException(ex);
		}
	}
}
