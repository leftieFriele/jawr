package net.jawr.web.test.servlet.mapping.debug;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import net.jawr.web.test.JawrTestConfigFiles;
import net.jawr.web.test.servlet.mapping.standard.MainPageJsCssServletMappingTest;
import net.jawr.web.test.utils.Utils;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlScript;

/**
 * Test case for page using JS and CSS servlet mapping in debug mode.
 * 
 * @author ibrahim Chaehoi
 */
@JawrTestConfigFiles(webXml = "net/jawr/web/servlet/mapping/config/web-js-css-servlet-mapping.xml", jawrConfig = "net/jawr/web/servlet/mapping/config/jawr-debug.properties")
public class MainPageJsCssServletMappingDebugTest extends MainPageJsCssServletMappingTest {

	@Test
	public void testPageLoad() throws Exception {

		final List<String> expectedAlerts = Collections
				.singletonList("A little message retrieved from the message bundle : Hello $ world!");
		assertEquals(expectedAlerts, collectedAlerts);

		assertContentEquals("/net/jawr/web/servlet/mapping/resources/debug/index-jsp-result-debug-js-css-servlet-mapping-expected.txt", page);
		
	}

	@Test
	public void checkGeneratedJsLinks() {
		// Test generated Script link
		final List<?> scripts = page.getByXPath("html/head/script");
		assertEquals(10, scripts.size());
		HtmlScript script = (HtmlScript) scripts.get(7);
		assertEquals(
				CONTEXT_PATH+"/jsJawr/jawr_generator.js?generationConfigParam=messages%3Amessages%40en_US",
				script.getSrcAttribute());
		script = (HtmlScript) scripts.get(8);
		assertEquals(
				CONTEXT_PATH+"/jsJawr/jawr_generator.js?generationConfigParam=testJs%3AgeneratedContent.js%40en_US",
				script.getSrcAttribute());
	}

	@Test
	public void testJsBundleContent() throws Exception {

		final List<?> scripts = page.getByXPath("html/head/script");
		HtmlScript script = (HtmlScript) scripts.get(7);
		JavaScriptPage page = getJavascriptPage(script);
		assertContentEquals("/net/jawr/web/servlet/mapping/resources/debug/msg-debug.js", page);
		
		script = (HtmlScript) scripts.get(8);
		page = getJavascriptPage(script);
		assertContentEquals("/net/jawr/web/servlet/mapping/resources/debug/generatedContent.js", page);
	}

	@Test
	public void checkGeneratedCssLinks() {
		// Test generated Css link
		final List<?> styleSheets = page.getByXPath("html/head/link");
		assertEquals(2, styleSheets.size());
		HtmlLink css = (HtmlLink) styleSheets.get(0);
		Utils.assertGeneratedLinkEquals(
				CONTEXT_PATH+"/cssJawr/jawr_generator.css?generationConfigParam=jar%3Afwk%2Fcss%2Ftemp.css%40en_US",
				css.getHrefAttribute());
		
		css = (HtmlLink) styleSheets.get(1);
		Utils.assertGeneratedLinkEquals(
				CONTEXT_PATH+"/cssJawr/css/one.css?d=11111",css.getHrefAttribute());
	}
	
	@Test
	public void testCssBundleContent() throws Exception {

		final List<?> styleSheets = page.getByXPath("html/head/link");
		HtmlLink css = (HtmlLink) styleSheets.get(0);
		TextPage page = getCssPage(css);
		assertContentEquals("/net/jawr/web/servlet/mapping/resources/debug/jar_temp-js-css-servlet-mapping.css", page);
		
		css = (HtmlLink) styleSheets.get(1);
		page = getCssPage(css);
		assertContentEquals("/net/jawr/web/servlet/mapping/resources/debug/one-js-css-servlet-mapping.css", page);
	}

}
