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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.servlet.RendererRequestUtils;

/**
 * Abstract implementation of a tag lib component which will retrieve a Jawr config
 * object from the servlet context and use it to render bundles of resources according 
 * to its src attribute.  
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public abstract class AbstractResourceBundleTag extends TagSupport {
	
	//private static final Logger log = Logger.getLogger(AbstractResourceBundleTag.class.getName());
	
	
	private static final long serialVersionUID = -9114179136913388470L;
	
	private String src;
	protected BundleRenderer renderer;
	protected boolean useRandomParam = true;    


	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {		
            
           // Renderer istance which takes care of generating the response
		   this.renderer = createRenderer();
           
           HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
           
           // set the debug override
          // renderer.getBundler().getConfig().setDebugOverride(true);
           
           String localeKey = this.renderer.getBundler().getConfig().getLocaleResolver().resolveLocaleCode(request);
           boolean isGzippable = RendererRequestUtils.isRequestGzippable(request,renderer.getBundler().getConfig());
           
           RendererRequestUtils.setRequestDebuggable(request,renderer.getBundler().getConfig());
           
           try {
                renderer.renderBundleLinks( src,
                                            request.getContextPath(),
                                            localeKey,
                                            RendererRequestUtils.getAddedBundlesLog(request),
                                            isGzippable,
                                            pageContext.getOut());
            } catch (IOException ex) {
                throw new JspException("Unexpected IOException when writing script tags for path " + src,ex);
            }

            return super.doStartTag();
	}
        

	
	/**
	 * Set the source of the resource or bundle to retrieve. 
	 * @param src
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * Set wether random param will be added in development mode to generated urls. 
	 * @param useRandomParam
	 */
	public void setUseRandomParam(boolean useRandomParam) {
		this.useRandomParam = useRandomParam;
	}
	
	/**
	 * Retrieve the ResourceCollector from context. Each implementation will use a different key
	 * to retrieve it. 
	 * @return
	 */
	protected abstract BundleRenderer createRenderer();

}
