/**
 * 
 */
package test.net.jawr.web.resource.bundle;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jawr.web.resource.bundle.InclusionPattern;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.JoinableResourceBundleImpl;
import net.jawr.web.resource.handler.reader.ResourceReaderHandler;
import test.net.jawr.web.resource.bundle.handler.ResourceHandlerBasedTest;


/**
 * 
 * @author jordi
 */
public class JoinableResourceBundleImplTest extends  ResourceHandlerBasedTest  {
	
	private static final String ROOT_TESTDIR = "/joinableresourcebundle/";
	private JoinableResourceBundle fullCollection;
	private JoinableResourceBundle partialCollection;

	/**
	 * 
	 */
	public JoinableResourceBundleImplTest() {
		InclusionPattern pattern = new InclusionPattern(true,0,true,false);
		List fullMapping = Collections.singletonList("js/**");
		
		List partialMapping = new ArrayList();
		partialMapping.add("/js/subfolder/");
		partialMapping.add("/outsider.js");
		
		ResourceReaderHandler rsHandler = null;
		try {
			rsHandler = createResourceReaderHandler(ROOT_TESTDIR, Charset.forName("UTF-8"));
		} catch (Exception e) {
			System.out.println("Error in test constructor");
			e.printStackTrace();
		}
		fullCollection = new JoinableResourceBundleImpl("full.js","full",".js",pattern,fullMapping,rsHandler);
		partialCollection = new JoinableResourceBundleImpl("partial.js","partial",".js",pattern,partialMapping,rsHandler);
	}
 
	/**
	 * Test method for {@link net.jawr.web.resource.bundle.JoinableResourceBundleImpl#belongsToBundle(java.lang.String)}.
	 * Test if the bundle recognizzes which items belong to it. 
	 */
	public void testBelongsToBundle() {
		// Full collection
		assertTrue("/js/script1.js should belong to the collection",fullCollection.belongsToBundle("/js/script1.js"));
		assertTrue("/js/script2.js should belong to the collection",fullCollection.belongsToBundle("/js/script2.js"));
		assertTrue("/js/subfolder/subfolderscript.js should belong to the collection",fullCollection.belongsToBundle("/js/subfolder/subfolderscript.js"));
		assertTrue("/js/subfolder2/subfolderscript2.js should belong to the collection",fullCollection.belongsToBundle("/js/subfolder2/subfolderscript2.js"));
		assertFalse("/outsider.js should not belong to the collection",fullCollection.belongsToBundle("/outsider.js"));
		
		// Partial collection
		assertTrue("[partialMapping] /js/subfolder/subfolderscript.js should belong to the collection",partialCollection.belongsToBundle("/js/subfolder/subfolderscript.js"));
		assertTrue("[partialMapping] /outsider.js should belong to the collection",partialCollection.belongsToBundle("/outsider.js"));
		assertFalse("[partialMapping] /js/script1.js should not belong to the collection",partialCollection.belongsToBundle("/js/script1.js"));
		
	}


	/**
	 * Test method for {@link net.jawr.web.resource.bundle.JoinableResourceBundleImpl#getItemPathList()}.
	 */
	public void testGetItemPathList() {
		// Full collection
		List expectedInFullCol = new ArrayList();
		expectedInFullCol.add("/js/script2.js");
		expectedInFullCol.add("/js/subfolder/subfolderscript.js");
		expectedInFullCol.add("/js/subfolder2/subfolderscript2.js");
		expectedInFullCol.add("/js/script1.js");
		assertEquals("Order of inclusion does not match the expected. ",expectedInFullCol, fullCollection.getItemPathList());

		// Partial collection
		List expectedInPartCol = new ArrayList();
		expectedInPartCol.add("/js/subfolder/subfolderscript.js");
		expectedInPartCol.add("/outsider.js");
		assertEquals("[partialMapping] Order of inclusion does not match the expected. ",expectedInPartCol, partialCollection.getItemPathList());
	}


}
