package test.net.jawr.web.resource.bundle;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.resource.bundle.CompositeResourceBundle;
import net.jawr.web.resource.bundle.InclusionPattern;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.JoinableResourceBundleImpl;
import net.jawr.web.resource.handler.reader.ResourceReaderHandler;
import test.net.jawr.web.resource.bundle.handler.ResourceHandlerBasedTest;

public class CompositeResourceBundleTest extends ResourceHandlerBasedTest {
	private static final String ROOT_TESTDIR = "/compositeresourcebundle/";
	private JoinableResourceBundle compositeCollectionNoDebug;
	private JoinableResourceBundle compositeCollectionDebug;
	
	public CompositeResourceBundleTest() {
		ResourceReaderHandler rsHandler = null;
		try {
			rsHandler = createResourceReaderHandler(ROOT_TESTDIR, Charset.forName("UTF-8"));
		} catch (Exception e) {
			System.out.println("Error in test constructor");
			e.printStackTrace();
		}
		
		String COMPOSITE_ID = "/bundles/composite.js";

		List mappingA = new ArrayList();
		mappingA.add("/js/subfolder/");
		mappingA.add("/outsider.js");
		
		List mappingB = Collections.singletonList("/js/subfolder2/"); 
		
		InclusionPattern onDebug = new InclusionPattern(false,0,true,false);
		InclusionPattern excludedOnDebug = new InclusionPattern(false,0,false,true);
		
		JoinableResourceBundleImpl bundleA = new JoinableResourceBundleImpl(COMPOSITE_ID,"composite", ".js", onDebug,mappingA,rsHandler,  config.getGeneratorRegistry());
		JoinableResourceBundleImpl bundleB = new JoinableResourceBundleImpl(COMPOSITE_ID,"composite", ".js", excludedOnDebug,mappingB,rsHandler, config.getGeneratorRegistry());
		List bundles = new ArrayList();
		bundles.add(bundleA);
		bundles.add(bundleB);
		
		Properties props = new Properties();
		JawrConfig config = new JawrConfig(props);
		config.setDebugModeOn(false);
		compositeCollectionNoDebug = new CompositeResourceBundle(COMPOSITE_ID,"composite",bundles,new InclusionPattern(),rsHandler,".js",config);
		config.setDebugModeOn(true);
		compositeCollectionDebug = new CompositeResourceBundle(COMPOSITE_ID,"composite",bundles,new InclusionPattern(),rsHandler,".js",config);
	}
	
	
	public void testDebugModeInclusion() {
		
		assertTrue("/outsider.js should be added in debug mode",
				compositeCollectionDebug.belongsToBundle("/outsider.js"));
		assertFalse("/outsider.js should not be added in production mode",
				compositeCollectionNoDebug.belongsToBundle("/outsider.js"));
		
		assertTrue("/js/subfolder/subfolderscript.js should be added in debug mode",
				compositeCollectionDebug.belongsToBundle("/js/subfolder/subfolderscript.js"));
		assertFalse("/js/subfolder/subfolderscript.js should not be added in production mode",
				compositeCollectionNoDebug.belongsToBundle("/js/subfolder/subfolderscript.js"));
		
		assertFalse("/js/subfolder2/subfolderscript2.js should not be added in debug mode",
				compositeCollectionDebug.belongsToBundle("/js/subfolder2/subfolderscript2.js"));
		assertTrue("/js/subfolder2/subfolderscript2.js should be added in production mode",
				compositeCollectionNoDebug.belongsToBundle("/js/subfolder2/subfolderscript2.js"));
		
		
	}
	// Test: debugonly is not added on prod. mode, and is added in debug mode. 
	// Test: debug never is not added in debug mode, and is not added in prod. mode. 
	// Test: different postprocessors execute right. 
}
