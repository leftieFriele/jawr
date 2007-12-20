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
package net.java.jawr.web.resource.bundle.renderer;

import net.java.jawr.web.resource.bundle.factory.util.PathNormalizer;
import net.java.jawr.web.resource.bundle.handler.ResourceBundlesHandler;

/**
 * Renderer that creates javascript link tags. 
 * 
 * @author Jordi Hern�ndez Sell�s
 */
public class JavascriptHTMLBundleLinkRenderer extends AbstractBundleLinkRenderer implements BundleRenderer{
    
    private static final String PRE_TAG = "<script type=\"text/javascript\" src=\"";
    private static final String POST_TAG = "\" ></script>\n";
    
    /** Creates a new instance of JavascriptHTMLBundleLinkRenderer */
    public JavascriptHTMLBundleLinkRenderer(ResourceBundlesHandler bundler) {
        super(bundler);
    }

    /* (non-Javadoc)
     * @see net.java.jawr.web.resource.bundle.renderer.AbstractBundleLinkRenderer#createBundleLink(java.lang.String, java.lang.String)
     */
    protected String createBundleLink(String bundleId, String contextPath) {
    	StringBuilder sb = new StringBuilder(PRE_TAG);
        String fullPath = PathNormalizer.joinPaths(getBundler().getConfig().getServletMapping(), bundleId);
        fullPath = PathNormalizer.joinPaths(contextPath,fullPath);
		sb.append(fullPath)
			.append(POST_TAG); 
        return sb.toString();
    }
    
}
