/**
 * 
 */
package test.net.java.jawr.web.resource.bundle.factory.mapper;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.java.jawr.web.exception.DuplicateBundlePathException;
import net.java.jawr.web.resource.ResourceHandler;
import net.java.jawr.web.resource.bundle.factory.mapper.ResourceBundleDirMapper;

import test.net.java.jawr.web.resource.bundle.handler.ResourceHandlerBasedTest;


/**
 * @author jordi
 *
 */
public class ResourceBundleDirMapperTest extends  ResourceHandlerBasedTest {
	private static final String ROOT_TESTDIR = "/resourcebundledirmapper/";
	ResourceBundleDirMapper factory;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		try {			
			Charset charsetUtf = Charset.forName("UTF-8"); 
			
			ResourceHandler rdHandler = createResourceHandler(ROOT_TESTDIR,charsetUtf);
			Set exclude = new HashSet();
			exclude.add("/js/global");
			exclude.add("/js/lib//");
			exclude.add("/js//debug");
			exclude.add("/js/excluded.js");
			factory = new ResourceBundleDirMapper("/js/",rdHandler,null,".js",exclude);
		} catch (Exception e) {
			System.out.println("Error in test constructor");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link net.java.jawr.web.resource.bundle.factory.mapper.ResourceBundleDirMapper#getMappings()}.
	 */
	public void testGetMappings() {
		Map mappings = null;
		try {
			mappings = factory.getBundleMapping();
		} catch (DuplicateBundlePathException e) {
			fail("DuplicateBundlePathException : "+ e.getBundlePath());
		}
		assertTrue("Expected mapping /js/one.js not included",mappings.containsKey("/js/one.js"));
		assertTrue("Expected folder not included",mappings.containsValue("/js/one/**"));
		assertTrue("Expected mapping not included",mappings.containsKey("/js/two.js"));
		assertTrue("Expected folder not included",mappings.containsValue("/js/two/**"));
		assertTrue("Expected mapping not included",mappings.containsKey("/js/three.js"));
		assertTrue("Expected folder not included",mappings.containsValue("/js/three/**"));
		//assertTrue("Expected mapping not included",mappings.containsKey("/js/included.js"));
		//assertTrue("Expected file not included",mappings.containsValue("/js/included.js"));
		assertFalse("Unexpected file included: /js/excluded.js",mappings.containsValue("/js/excluded.js"));
		assertFalse("Unexpected folder 'lib' included",mappings.containsKey("/js/lib.js"));
		assertFalse("Unexpected folder 'global' included",mappings.containsKey("/js/global.js"));
		assertFalse("Unexpected folder 'debug' included",mappings.containsKey("/js/debug.js"));
	}

}
