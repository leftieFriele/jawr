package net.jawr.web.test.sprite.smartsprites;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.jawr.web.test.JawrTestConfigFiles;
import net.jawr.web.test.utils.Utils;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlScript;

/**
 * Test case for page using inclusion features in debug mode.
 * 
 * @author ibrahim Chaehoi
 */
@JawrTestConfigFiles(webXml = "net/jawr/web/sprite/smartsprites/debug/config/web.xml", jawrConfig = "net/jawr/web/sprite/smartsprites/debug/config/jawr.properties")
public class MainPageSmartSpritesDebugTest extends MainPageSmartSpritesTest {

	/* (non-Javadoc)
	 * @see net.jawr.web.AbstractPageTest#createWebClient()
	 */
	@Override
	protected WebClient createWebClient() {
		WebClient webClient = super.createWebClient();
		
		// Update the webClient so it will not throw an exception when it will try to load the external JS file which doesn't exists
		webClient.setThrowExceptionOnFailingStatusCode(false);
		return webClient;
	}

	@Override
	protected String getPageUrl() {
		return SERVER_URL + CONTEXT_PATH+"/sprite/index.jsp";
	}

	@Test
	public void testPageLoad() throws Exception {
		
		assertContentEquals("/net/jawr/web/sprite/smartsprites/debug/resources/index-jsp-result-debug-mode-expected.txt", page);
	}

	@Test
	public void checkGeneratedJsLinks() {
		// Test generated Script link
		final List<?> scripts = getJsScriptTags();
		assertEquals(2, scripts.size());
		HtmlScript script = (HtmlScript) scripts.get(0);
		assertEquals(
				CONTEXT_PATH+"/jawr_generator.js?generationConfigParam=messages%3Amessages%40en_US",
				script.getSrcAttribute());
		script = (HtmlScript) scripts.get(1);
		assertEquals(
				CONTEXT_PATH+"/jawr_generator.js?generationConfigParam=testJs%3AgeneratedContent.js",
				script.getSrcAttribute());
	}

	@Test
	public void testJsBundleContent() throws Exception {

		final List<?> scripts = getJsScriptTags();
		HtmlScript script = (HtmlScript) scripts.get(0);
		JavaScriptPage page = getJavascriptPage(script);
		assertContentEquals("/net/jawr/web/debug/resources/msg-debug.js", page);
		
		script = (HtmlScript) scripts.get(1);
		page = getJavascriptPage(script);
		assertContentEquals("/net/jawr/web/debug/resources/generatedContent.js", page);
	}

	@Test
	public void checkGeneratedCssLinks() {
		// Test generated Css link
		final List<?> styleSheets = getHtmlLinkTags();
		assertEquals(2, styleSheets.size());
		HtmlLink css = (HtmlLink) styleSheets.get(0);
		Utils.assertGeneratedLinkEquals(
				CONTEXT_PATH+"/jawr_generator.css?generationConfigParam=jar%3Afwk%2Fcss%2FtempCssWithSprite.css",
				css.getHrefAttribute());
		
		css = (HtmlLink) styleSheets.get(1);
		Utils.assertGeneratedLinkEquals(
				CONTEXT_PATH+"/css/sprite/one.css?d=11111",css.getHrefAttribute());
	}

	@Test
	public void testCssBundleContent() throws Exception {

		final List<?> styleSheets = getHtmlLinkTags();
		HtmlLink css = (HtmlLink) styleSheets.get(0);
		TextPage page = getCssPage(css);
		assertContentEquals("/net/jawr/web/sprite/smartsprites/debug/resources/jar_temp.css", page);
		
		css = (HtmlLink) styleSheets.get(1);
		page = getCssPage(css);
		assertContentEquals("/net/jawr/web/sprite/smartsprites/debug/resources/one.css", page);
	}
}
