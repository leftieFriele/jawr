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
package net.jawr.web.resource.bundle.renderer;

import java.io.IOException;
import java.io.Writer;

import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;

/**
 * This interface defines operations to generate import tags for bundled resources. 
 * 
 * @author Jordi Hern�ndez Sell�s
 */
public interface BundleRenderer {
    
    /**
     * Prefix to append to every URL that points to a gzipped version of a resource. 
     */
    public static final String GZIP_PATH_PREFIX = "/gzip_";
            
    /**
     * Render a link to the specified resource. 
     * @param requestedPath String Path that identifies a resource bundle id or one of its members. 
     * @param ctx the bundle renderer context
	 * @param out Writer Writer to output the tags, typically a JSPWriter.
	 * @throws IOException if an IO exception occurs
     */
	public void renderBundleLinks(String requestedPath, BundleRendererContext ctx,
			Writer out) throws IOException;
	
    /**
     * @return ResourceBundlesHandler The resources handler used by this renderer.
     */
    public ResourceBundlesHandler getBundler();
    
    /**
     * Returns the resource type 
     * 
     * @return the resource type
     */
    public String getResourceType();

}
