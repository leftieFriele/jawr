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
package net.jawr.web.resource.bundle.postprocess.impl.css.base64;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jawr.web.JawrConstant;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.FileNameUtils;
import net.jawr.web.resource.ImageResourcesHandler;
import net.jawr.web.resource.bundle.IOUtils;
import net.jawr.web.resource.bundle.factory.util.RegexUtil;
import net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus;
import net.jawr.web.resource.bundle.postprocess.impl.PostProcessorCssImageUrlRewriter;
import net.jawr.web.servlet.util.MIMETypesSupport;
import net.jawr.web.util.Base64Encoder;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class defines the image URL rewriter for the base64 image post processor.
 * This postprocessor will apply the standard URL rewriting process if the image URL
 * is annotated as "jawr:base64-skip" or if the brwoser is IE7, because
 * the MHTML is not properly supported with IE7 on Windows Vista and Windows 7
 * Please check the following link for more info.
 * 
 * http://www.phpied.com/data-uris-mhtml-ie7-win7-vista-blues/#vista
 * 
 * @author Ibrahim Chaehoi
 */
public class Base64PostProcessorCssImageUrlRewriter extends
		PostProcessorCssImageUrlRewriter {

	/** The logger */
	private Logger LOGGER = Logger
			.getLogger(Base64PostProcessorCssImageUrlRewriter.class);

	/** The data prefix */
	private static final String DATA_PREFIX = "data:";

	/** The mhtml prefix */
	private static final String MHTML_PREFIX = "mhtml:";

	/** The annotation to skip the base64 encoding */
	private static final String ANNOTATION_BASE64_SKIP = "jawr:base64-skip";

	/** The annotation group in the URL pattern */
	private static final int ANNOTATION_GROUP = 10;

	/** The url pattern */
	private static final Pattern URL_WITH_ANNOTATION_PATTERN = Pattern.compile(
			"((" + URL_REGEXP + "\\s*)+)" + "([^;]*);?"
					+ "\\s*(/\\*\\*+((.|[\\r\\n])*?)\\*/)?", // Any number of
			// whitespaces and then
			// an annotation

			Pattern.CASE_INSENSITIVE); // works with 'URL('

	/** The default max file size */
	private static final int MAX_LENGTH_FILE = 40000;

	/** The maximum image file size authorized to be encoded in base64 */
	private int maxFileSize;

	private Map encodedResources = null;

	/** The flag indicating if we must skip the base64 encoding */
	private boolean skipBase64Encoding;

	/**
	 * Constructor
	 * 
	 * @param status
	 *            the bundle processing status
	 */
	public Base64PostProcessorCssImageUrlRewriter(BundleProcessingStatus status) {
		super(status);
		encodedResources = (Map) status
				.getData(JawrConstant.BASE64_ENCODED_RESOURCES);
		maxFileSize = MAX_LENGTH_FILE;
		String maxLengthProperty = (String) status.getJawrConfig()
				.getConfigProperties().get(
						JawrConstant.BASE64_MAX_IMG_FILE_SIZE);
		if (StringUtils.isNotEmpty(maxLengthProperty)) {
			maxFileSize = Integer.parseInt(maxLengthProperty);
		}
		LOGGER.debug("max file length: " + maxFileSize);
	}

	/**
	 * Rewrites the image URL
	 * 
	 * @param originalCssPath
	 *            the original CSS path
	 * @param newCssPath
	 *            the new CSS path
	 * @param originalCssContent
	 *            the original CSS content
	 * @return the new CSS content with image path rewritten
	 * @throws IOException
	 */
	public StringBuffer rewriteUrl(String originalCssPath, String newCssPath,
			String originalCssContent) throws IOException {

		// Rewrite each css image url path
		Matcher matcher = URL_WITH_ANNOTATION_PATTERN
				.matcher(originalCssContent);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {

			String annotation = matcher.group(ANNOTATION_GROUP);

			skipBase64Encoding = StringUtils.isNotEmpty(annotation)
					&& annotation.indexOf(ANNOTATION_BASE64_SKIP) != -1;
			
			StringBuffer sbUrl = new StringBuffer();
			Matcher urlMatcher = URL_PATTERN.matcher(matcher.group());
			while (urlMatcher.find()) {
				
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Skip encoding image resource : "+urlMatcher.group());
				}
				
				String url = getUrlPath(urlMatcher.group(), originalCssPath,
						newCssPath);

				urlMatcher.appendReplacement(sbUrl, RegexUtil
						.adaptReplacementToMatcher(url));
			}
			urlMatcher.appendTail(sbUrl);
			matcher.appendReplacement(sb, RegexUtil
					.adaptReplacementToMatcher(sbUrl.toString()));

		}
		matcher.appendTail(sb);

		return sb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.postprocess.impl.
	 * PostProcessorCssImageUrlRewriter
	 * #rewriteURL(net.jawr.web.resource.bundle.postprocess
	 * .BundleProcessingStatus, java.lang.String, java.lang.String,
	 * java.lang.String, net.jawr.web.resource.ImageResourcesHandler)
	 */
	protected String rewriteURL(BundleProcessingStatus status, String url,
			String imgServletPath, String newCssPath,
			ImageResourcesHandler imgRsHandler) throws IOException {

		String imgUrl = url;
		String browser = status.getVariant(JawrConstant.BROWSER_VARIANT_TYPE);

		// Skip base64 encoding if a annotation has been set
		if (skipBase64Encoding) {
			imgUrl = super.rewriteURL(status, imgUrl, imgServletPath,
					newCssPath, imgRsHandler);
		} else {

			LOGGER.info("Encoding resource: " + url);
			try {
				InputStream is = imgRsHandler.getRsReaderHandler()
						.getResourceAsStream(url);

				String fileExtension = FileNameUtils.getExtension(url);
				String fileMimeType = (String) MIMETypesSupport
						.getSupportedProperties(this).get(fileExtension);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				IOUtils.copy(is, out, true);

				int size = out.size();
				if (size > maxFileSize) { // Check file size 

					LOGGER.warn("File content length of '"+url+"' exceeds maximum file length: "
							+ size + " > " + maxFileSize);
				} else {

					byte[] data = out.toByteArray();
					StringBuffer s = new StringBuffer(encodeInBase64(data));

					Base64EncodedResource encodedImage = new Base64EncodedResource();
					encodedImage.setId(url.hashCode());
					encodedImage.setType(fileMimeType);
					encodedImage.setBase64Encoding(s);

					encodedResources.put(encodedImage.getId(), encodedImage);

					// For IE under IE8, use MHTML
					if (JawrConstant.BROWSER_IE6.equals(browser) || JawrConstant.BROWSER_IE7.equals(browser)) {

						/**
						 * For Internet Explorer 6 and 7, the url must be mhtml: followed
						 * by an absolute url. However, this URL is not known at
						 * post process time. So we make add a place holder which will
						 * be resolved at runtime.
						 */
						imgUrl = MHTML_PREFIX+JawrConstant.JAWR_BUNDLE_PATH_PLACEHOLDER+"!"+encodedImage.getId();

					} else {
						imgUrl = DATA_PREFIX + fileMimeType + ";base64," + s;
					}
				}
			} catch (IOException e) {
				LOGGER.error(e);

			} catch (ResourceNotFoundException e) {
				LOGGER.error("The resource '" + e.getRequestedPath()
						+ "' has not been found.");
			} catch (Throwable e) {
				LOGGER.error(e);
			}
		}

		return imgUrl;
	}

	/**
	 * Encodes the data in base64 
	 * 
	 * @param data the byte array of data to encode
	 * @return the base64 encoded string of the data
	 */
	private String encodeInBase64(byte[] data) {
		return new String(Base64Encoder.encode(data));
	}
}