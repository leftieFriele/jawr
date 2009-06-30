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
package net.jawr.web.taglib;

import net.jawr.web.JawrConstant;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.resource.bundle.renderer.JavascriptHTMLBundleLinkRenderer;

/**
 * Implementation of a jsp taglib AbstractResourceBundleTag used to render javascript bundles. 
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class JavascriptBundleTag extends AbstractResourceBundleTag {

	/** The serial version UID */
	private static final long serialVersionUID = 5087323727715427593L;

	/* (non-Javadoc)
	 * @see net.jawr.web.taglib.AbstractResourceBundleTag#createRenderer()
	 */
	protected BundleRenderer createRenderer() {
		if(null == pageContext.getServletContext().getAttribute(JawrConstant.JS_CONTEXT_ATTRIBUTE))
			throw new IllegalStateException("ResourceBundlesHandler not present in servlet context. Initialization of Jawr either failed or never occurred.");

		ResourceBundlesHandler rsHandler = (ResourceBundlesHandler) pageContext.getServletContext().getAttribute(JawrConstant.JS_CONTEXT_ATTRIBUTE);
		return  new JavascriptHTMLBundleLinkRenderer(rsHandler, this.useRandomParam);
	}


}
