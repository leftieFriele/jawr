/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.net.jawr.web.resource.bundle.renderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundleDependencyException;
import net.jawr.web.exception.DuplicateBundlePathException;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.resource.bundle.renderer.BundleRendererContext;
import net.jawr.web.resource.bundle.renderer.CSSHTMLBundleLinkRenderer;
import net.jawr.web.resource.bundle.renderer.JavascriptHTMLBundleLinkRenderer;
import net.jawr.web.resource.handler.bundle.ResourceBundleHandler;
import net.jawr.web.resource.handler.reader.ResourceReaderHandler;
import net.jawr.web.util.StringUtils;
import test.net.jawr.web.resource.bundle.PredefinedBundlesHandlerUtil;
import test.net.jawr.web.resource.bundle.handler.ResourceHandlerBasedTest;
/**
 *
 * @author jhernandez
 */
public class BundleLinkRendererTest  extends ResourceHandlerBasedTest{
	private static final String ROOT_TESTDIR = "/bundleLinkRenderer/";
	private static final String JS_BASEDIR = "js";
	private static final String JS_CTX_PATH = "/ctxPathJs";
	
	private static final String JS_PRE_TAG = "<script type=\"text/javascript\" src=\"";
    private static final String JS_POST_TAG = "\" ></script>";
	
	private JavascriptHTMLBundleLinkRenderer jsRenderer;
	private JawrConfig jawrConfig;
	
	private BundleRendererContext bundleRendererCtx = null;
	
	public BundleLinkRendererTest() {
	     
	    Charset charsetUtf = Charset.forName("UTF-8"); 
			
	    ResourceReaderHandler rsHandler = createResourceReaderHandler(ROOT_TESTDIR,charsetUtf);
	    ResourceBundleHandler rsBundleHandler = createResourceBundleHandler(ROOT_TESTDIR,charsetUtf);
	    jawrConfig = new JawrConfig(new Properties());
	    jawrConfig.setCharsetName("UTF-8");
	    jawrConfig.setServletMapping("/srvMapping");
	    jawrConfig.setCssLinkFlavor(CSSHTMLBundleLinkRenderer.FLAVORS_XHTML);
	    ResourceBundlesHandler jsHandler = null;
	    try {
	    	jsHandler = PredefinedBundlesHandlerUtil.buildSimpleBundles(rsHandler,rsBundleHandler,JS_BASEDIR,"js", jawrConfig);
	    } catch (DuplicateBundlePathException e) {
			// 
			throw new RuntimeException(e);
		} catch (BundleDependencyException e) {
			throw new RuntimeException(e);
		}
	    jsRenderer = new JavascriptHTMLBundleLinkRenderer(jsHandler,true);
	}
	
	private String renderToString(BundleRenderer renderer, String path, BundleRendererContext ctx){
		ByteArrayOutputStream baOs = new ByteArrayOutputStream();
	    WritableByteChannel wrChannel = Channels.newChannel(baOs);
	    Writer writer = Channels.newWriter(wrChannel, jawrConfig.getResourceCharset().name());
	    String ret = null;
	    try {
			renderer.renderBundleLinks(path, ctx, writer);
		    writer.close();
		    ret = baOs.toString(jawrConfig.getResourceCharset().name());
		} catch (IOException e) {
			fail("Exception rendering tags:" + e.getMessage());
		}
	    return ret;
	}
	
	public void testWriteJSBundleLinks()
	{
		jawrConfig.setDebugModeOn(false);
		
		// Test regular link creation
	    bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, false, false);
	    String result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		
		assertNotSame("No script tag written ", "", result.trim());
			
		String libTag = JS_PRE_TAG + "/ctxPathJs/srvMapping/libPfx/library.js" + JS_POST_TAG;
		String globalTag = JS_PRE_TAG + "/ctxPathJs/srvMapping/globalPfx/global.js" + JS_POST_TAG;
		String debOffTag = JS_PRE_TAG + "/ctxPathJs/srvMapping/pfx/debugOff.js" + JS_POST_TAG;
		String oneTag = JS_PRE_TAG + "/ctxPathJs/srvMapping/pfx/js/one.js" + JS_POST_TAG;
		StringTokenizer tk = new StringTokenizer(result,"\n");
		
		
		assertEquals("Invalid number of tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1", assertStartEndSimmilarity(globalTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3", assertStartEndSimmilarity(oneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));
		

		// Test gzipped link creation
		String libZTag = JS_PRE_TAG+ "/ctxPathJs/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "libPfx/library.js" + JS_POST_TAG;
		String globalZTag = JS_PRE_TAG + "/ctxPathJs/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "globalPfx/global.js" + JS_POST_TAG;
		String debOffZTag = JS_PRE_TAG + "/ctxPathJs/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/debugOff.js" + JS_POST_TAG;
		String debOffoneTag = JS_PRE_TAG + "/ctxPathJs/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/js/one.js" + JS_POST_TAG;
		bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, true, false);
	    //globalBundleAdded = false;
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertNotSame("No gzip script tags written ", "", result.trim());
		tk = new StringTokenizer(result,"\n");
		assertEquals("Invalid number of gzip script tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libZTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1",assertStartEndSimmilarity(globalZTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffZTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3",assertStartEndSimmilarity(debOffoneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));
		
	}
	
	private boolean assertStartEndSimmilarity(String toCompare, String splitter, String toMatch) {
		String[] parts = toCompare.split(splitter);
		return toMatch.startsWith(parts[0]) && toMatch.endsWith(parts[1]);
	}
	
	public void testWriteJSDebugLinks() 
	{
		jawrConfig.setDebugModeOn(true);
		
		// Test regular link creation
	    bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, false, false);
	    
	    String result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
	    // Reusing the set, we test that no repeats are allowed. 
	    String repeated = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
	    
	    assertNotSame("No script tag written ", "", result.trim());

	    Pattern comment = Pattern.compile("<script type=\"text/javascript\">/.*/</script>");
	   
		StringTokenizer tk = new StringTokenizer(result,"\n");
		
		// First item should be a comment		
		assertTrue("comment expected",comment.matcher(tk.nextToken()).matches());
		
	    String regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/lib/prototype/protoype.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /lib/protoype.js", Pattern.matches(regX, tk.nextToken()));
		assertFalse("Repeated already included tag ", Pattern.matches(regX, repeated));
		
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/lib/lib2.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /lib/lib2.js", Pattern.matches(regX, tk.nextToken()));
		assertFalse("Repeated already included tag ", Pattern.matches(regX, repeated));
		
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/lib/scriptaculous/scriptaculous.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /lib/scriptaculous/scriptaculous.js", Pattern.matches(regX, tk.nextToken()));
		assertFalse("Repeated already included tag ", Pattern.matches(regX, repeated));
		
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/global/global2.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /global/global2.js", Pattern.matches(regX, tk.nextToken()));
		assertFalse("Repeated already included tag ", Pattern.matches(regX, repeated));
		
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/global/global.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /global/global.js", Pattern.matches(regX, tk.nextToken()));
		assertFalse("Repeated already included tag ", Pattern.matches(regX, repeated));
		
		// Even though we set dbug to off, the handler was initialized in non-debug mode, thus we still get the debugoff.js file. 
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/debug/off/debugOff.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /debug/off/debugOff.js", Pattern.matches(regX, tk.nextToken()));
		
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/one/one.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("comment expected",comment.matcher(tk.nextToken()).matches());
		assertTrue("comment expected",comment.matcher(tk.nextToken()).matches());
		assertTrue("No match for /js/one/one.js", Pattern.matches(regX, tk.nextToken()));
		
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/one/one2.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /js/one/one2.js", Pattern.matches(regX, tk.nextToken()));
		
		regX = JS_PRE_TAG + "/ctxPathJs/srvMapping/js/one/sub/one3.js\\?d=\\d*" + JS_POST_TAG;
		assertTrue("No match for /js/one/sub/one3.js", Pattern.matches(regX, tk.nextToken()));
		
		assertTrue("comment expected",comment.matcher(tk.nextToken()).matches());
		assertFalse("Repeated already included tag ", Pattern.matches(regX, repeated));
		
	}

	public void testWriteJSBundleLinksWithHttpCdn()
	{
		jawrConfig.setDebugModeOn(false);
		
		String contextPathOverride = "http://mydomain.com/aPath";
		String contextPathSslOverride = "http://mydomain.com/aPath";
		jawrConfig.setContextPathOverride(contextPathOverride+"/");
		jawrConfig.setContextPathSslOverride(contextPathSslOverride+"/");
		
		//boolean isSslRequest = false;
		
		// Test regular link creation
	    bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, false, false);
	    
	    String result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		
		assertNotSame("No script tag written ", "", result.trim());
			
		String libTag = JS_PRE_TAG + contextPathOverride+"/srvMapping/libPfx/library.js" + JS_POST_TAG;
		String globalTag = JS_PRE_TAG + contextPathOverride+"/srvMapping/globalPfx/global.js" + JS_POST_TAG;
		String debOffTag = JS_PRE_TAG + contextPathOverride+"/srvMapping/pfx/debugOff.js" + JS_POST_TAG;
		String oneTag = JS_PRE_TAG + contextPathOverride+"/srvMapping/pfx/js/one.js" + JS_POST_TAG;
		StringTokenizer tk = new StringTokenizer(result,"\n");
		
		
		assertEquals("Invalid number of tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1", assertStartEndSimmilarity(globalTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3", assertStartEndSimmilarity(oneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));
		

		// Test gzipped link creation
		String libZTag = JS_PRE_TAG+ contextPathOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "libPfx/library.js" + JS_POST_TAG;
		String globalZTag = JS_PRE_TAG + contextPathOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "globalPfx/global.js" + JS_POST_TAG;
		String debOffZTag = JS_PRE_TAG + contextPathOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/debugOff.js" + JS_POST_TAG;
		String debOffoneTag = JS_PRE_TAG + contextPathOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/js/one.js" + JS_POST_TAG;
		bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, true, false);
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertNotSame("No gzip script tags written ", "", result.trim());
		tk = new StringTokenizer(result,"\n");
		assertEquals("Invalid number of gzip script tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libZTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1",assertStartEndSimmilarity(globalZTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffZTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3",assertStartEndSimmilarity(debOffoneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));
		
	}
	
	public void testWriteJSBundleLinksWithHttpsCdn()
	{
		jawrConfig.setDebugModeOn(false);
		
		String contextPathOverride = "http://mydomain.com/aPath";
		String contextPathSslOverride = "https://mydomain.com/aPath";
		jawrConfig.setContextPathOverride(contextPathOverride+"/");
		jawrConfig.setContextPathSslOverride(contextPathSslOverride+"/");
		
		// Test regular link creation
	    bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, false, true);
		
	    String result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		
		assertNotSame("No script tag written ", "", result.trim());
			
		String libTag = JS_PRE_TAG + contextPathSslOverride+"/srvMapping/libPfx/library.js" + JS_POST_TAG;
		String globalTag = JS_PRE_TAG + contextPathSslOverride+"/srvMapping/globalPfx/global.js" + JS_POST_TAG;
		String debOffTag = JS_PRE_TAG + contextPathSslOverride+"/srvMapping/pfx/debugOff.js" + JS_POST_TAG;
		String oneTag = JS_PRE_TAG + contextPathSslOverride+"/srvMapping/pfx/js/one.js" + JS_POST_TAG;
		StringTokenizer tk = new StringTokenizer(result,"\n");
		
		
		assertEquals("Invalid number of tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1", assertStartEndSimmilarity(globalTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3", assertStartEndSimmilarity(oneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));
		
		// Test gzipped link creation
		String libZTag = JS_PRE_TAG+ contextPathSslOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "libPfx/library.js" + JS_POST_TAG;
		String globalZTag = JS_PRE_TAG + contextPathSslOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "globalPfx/global.js" + JS_POST_TAG;
		String debOffZTag = JS_PRE_TAG + contextPathSslOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/debugOff.js" + JS_POST_TAG;
		String debOffoneTag = JS_PRE_TAG + contextPathSslOverride+"/srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/js/one.js" + JS_POST_TAG;
		bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, true, true);
		
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertNotSame("No gzip script tags written ", "", result.trim());
		tk = new StringTokenizer(result,"\n");
		assertEquals("Invalid number of gzip script tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libZTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1",assertStartEndSimmilarity(globalZTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffZTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3",assertStartEndSimmilarity(debOffoneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Gzip tags were repeated", StringUtils.isEmpty(result));
	}
	
	public void testWriteJSBundleLinksWithHttpsRelativePath()
	{
		jawrConfig.setDebugModeOn(false);
		
		String contextPathOverride = "http://mydomain.com/aPath";
		String contextPathSslOverride = "";
		jawrConfig.setContextPathOverride(contextPathOverride+"/");
		jawrConfig.setContextPathSslOverride(contextPathSslOverride);
		
		// Test regular link creation
	    bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, false, true);
		
	    String result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		
		assertNotSame("No script tag written ", "", result.trim());
			
		String libTag = JS_PRE_TAG + "srvMapping/libPfx/library.js" + JS_POST_TAG;
		String globalTag = JS_PRE_TAG + "srvMapping/globalPfx/global.js" + JS_POST_TAG;
		String debOffTag = JS_PRE_TAG + "srvMapping/pfx/debugOff.js" + JS_POST_TAG;
		String oneTag = JS_PRE_TAG + "srvMapping/pfx/js/one.js" + JS_POST_TAG;
		StringTokenizer tk = new StringTokenizer(result,"\n");
		
		
		assertEquals("Invalid number of tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1", assertStartEndSimmilarity(globalTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3", assertStartEndSimmilarity(oneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));
		

		// Test gzipped link creation
		String libZTag = JS_PRE_TAG+ "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "libPfx/library.js" + JS_POST_TAG;
		String globalZTag = JS_PRE_TAG + "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "globalPfx/global.js" + JS_POST_TAG;
		String debOffZTag = JS_PRE_TAG + "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/debugOff.js" + JS_POST_TAG;
		String debOffoneTag = JS_PRE_TAG + "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/js/one.js" + JS_POST_TAG;
		bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, true, true);
		
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertNotSame("No gzip script tags written ", "", result.trim());
		tk = new StringTokenizer(result,"\n");
		assertEquals("Invalid number of gzip script tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libZTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1",assertStartEndSimmilarity(globalZTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffZTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3",assertStartEndSimmilarity(debOffoneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Gzip tags were repeated", StringUtils.isEmpty(result));
	}
	
	public void testWriteJSBundleLinksWithRelativePath()
	{
		jawrConfig.setDebugModeOn(false);
		jawrConfig.setContextPathOverride("");
		jawrConfig.setContextPathSslOverride("https://mydomain.com/aPath/");
		
		// Test regular link creation
	    bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, false, false);
		
	    String result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		
		assertNotSame("No script tag written ", "", result.trim());
			
		String libTag = JS_PRE_TAG + "srvMapping/libPfx/library.js" + JS_POST_TAG;
		String globalTag = JS_PRE_TAG + "srvMapping/globalPfx/global.js" + JS_POST_TAG;
		String debOffTag = JS_PRE_TAG + "srvMapping/pfx/debugOff.js" + JS_POST_TAG;
		String oneTag = JS_PRE_TAG + "srvMapping/pfx/js/one.js" + JS_POST_TAG;
		StringTokenizer tk = new StringTokenizer(result,"\n");
		
		
		assertEquals("Invalid number of tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1", assertStartEndSimmilarity(globalTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3", assertStartEndSimmilarity(oneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));

		// Test gzipped link creation
		String libZTag = JS_PRE_TAG+ "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "libPfx/library.js" + JS_POST_TAG;
		String globalZTag = JS_PRE_TAG + "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "globalPfx/global.js" + JS_POST_TAG;
		String debOffZTag = JS_PRE_TAG + "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/debugOff.js" + JS_POST_TAG;
		String debOffoneTag = JS_PRE_TAG + "srvMapping" + BundleRenderer.GZIP_PATH_PREFIX + "pfx/js/one.js" + JS_POST_TAG;
		bundleRendererCtx = new BundleRendererContext(JS_CTX_PATH, null, true, false);
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertNotSame("No gzip script tags written ", "", result.trim());
		tk = new StringTokenizer(result,"\n");
		assertEquals("Invalid number of gzip script tags written. ",4, tk.countTokens());
		assertTrue("Unexpected tag added at position 0", assertStartEndSimmilarity(libZTag,"libPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 1",assertStartEndSimmilarity(globalZTag,"globalPfx",tk.nextToken()));
		assertTrue("Unexpected tag added at position 2",assertStartEndSimmilarity(debOffZTag,"pfx",tk.nextToken()) );
		assertTrue("Unexpected tag added at position 3",assertStartEndSimmilarity(debOffoneTag,"pfx",tk.nextToken()) );
		
		// Reusing the set, we test that no repeats are allowed. 
		result = renderToString(jsRenderer,"/js/one/one2.js", bundleRendererCtx);
		assertTrue("Tags were repeated", StringUtils.isEmpty(result));

	}
	
	
}
