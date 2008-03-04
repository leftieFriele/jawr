/**
 * Copyright 2007 Jordi Hern�ndez Sell�s
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
package net.jawr.web.resource.bundle.postprocess.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

import net.jawr.web.minification.JSMin;
import net.jawr.web.minification.JSMin.UnterminatedCommentException;
import net.jawr.web.minification.JSMin.UnterminatedRegExpLiteralException;
import net.jawr.web.minification.JSMin.UnterminatedStringLiteralException;
import net.jawr.web.resource.bundle.postprocess.AbstractChainedResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus;

/**
 * This postprocessor will minify a javascript bundle using Douglas Crockford's JSMin,
 * in its java implementation (see www.crockford.com and www.inconspicuous.org). 
 * 
 * @author Jordi Hern�ndez Sell�s
 */
public class JSMinPostProcessor extends
		AbstractChainedResourceBundlePostProcessor {
	

	/**
	 * Constructor for a compressor.  
	 * @param charset
	 */
	public JSMinPostProcessor() {
		super();
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.postprocess.impl.AbstractChainedResourceBundlePostProcessor#doPostProcessBundle(java.lang.StringBuffer)
	 */
	protected StringBuffer doPostProcessBundle(BundleProcessingStatus status,StringBuffer bundleString)
			throws IOException {
		Charset charset = status.getJeesConfig().getResourceCharset();
		ByteArrayInputStream bIs = new ByteArrayInputStream(bundleString.toString().getBytes(charset.name()));
		ByteArrayOutputStream bOs = new ByteArrayOutputStream();
		
		// Compress data and recover it as a byte array. 
		JSMin minifier = new JSMin(bIs,bOs);
		try {
			minifier.jsmin();
		} catch (UnterminatedRegExpLiteralException e) {			
			formatAndThrowJSLintError(status, e);				
		} catch (UnterminatedCommentException e) {
			formatAndThrowJSLintError(status, e);		
		} catch (UnterminatedStringLiteralException e) {
			formatAndThrowJSLintError(status, e);		
		}
		byte[] minified = bOs.toByteArray();

		// Write the data into a string
		ReadableByteChannel chan = Channels.newChannel(new ByteArrayInputStream(minified));
        Reader rd = Channels.newReader(chan,charset.newDecoder(),-1);
        StringWriter writer = new StringWriter();
		int i;
		while((i = rd.read()) != -1)
			writer.write(i);
		
		return writer.getBuffer();
	}

	/**
	 * Upon an exception thrown during minification, this method will throw an error with detailed information. 
	 * @param status
	 * @param e
	 */
	private void formatAndThrowJSLintError(BundleProcessingStatus status, Exception e) {
		String errorMsg = "JSMin failed to minify the bundle with id: '" + status.getCurrentBundle().getName() + "'.\n";
		errorMsg += "The exception thrown is of type:" + e.getClass().getName() + "'.\n";
		errorMsg += "If you can't find the error, try to check the scripts using JSLint (http://www.jslint.com/) to find the conflicting part of the code. ";
		throw new RuntimeException(errorMsg,e);
	}

}
