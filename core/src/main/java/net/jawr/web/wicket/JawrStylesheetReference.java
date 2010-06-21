/**
 * Copyright 2009-2010 Ibrahim Chaehoi
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
package net.jawr.web.wicket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.jawr.web.JawrConstant;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.resource.bundle.renderer.BundleRendererContext;
import net.jawr.web.resource.bundle.renderer.CSSHTMLBundleLinkRenderer;
import net.jawr.web.servlet.RendererRequestUtils;
import net.jawr.web.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.value.IValueMap;


/**
 * The abstract class for the CSS and Stylesheet reference for Wicket.
 * 
 * @autor Robert Kopaczewski (Original author) 
 * @author Ibrahim Chaehoi
 */
public class JawrStylesheetReference extends AbstractJawrReference {

    /** The serial version UID */
	private static final long serialVersionUID = -8704775670669437484L;

	/** The logger */
	private static final Logger LOGGER = Logger
			.getLogger(JawrStylesheetReference.class);
	/**
	 * Constructor
	 * @param id the ID
	 */
	public JawrStylesheetReference(String id) {
        super(id);
    }

	/* (non-Javadoc)
	 * @see net.jawr.web.wicket.JawrAbstractReference#getReferencePath(org.apache.wicket.util.value.IValueMap)
	 */
	protected String getReferencePath(final IValueMap attributes) {
		
		String refPath = (String) attributes.get(JawrConstant.HREF_ATTR);
		if(StringUtils.isEmpty(refPath)){
			refPath = (String) attributes.get(JawrConstant.SRC_ATTR);
		}
		
		return refPath;
	}
	
	/* (non-Javadoc)
     * @see net.jawr.web.wicket.JawrAbstractReference#createRenderer(org.apache.wicket.markup.ComponentTag)
     */
    protected BundleRenderer createRenderer(ComponentTag tag) {
        final IValueMap attributes = tag.getAttributes();

        Object handler = WebApplication.get().getServletContext().getAttribute(JawrConstant.CSS_CONTEXT_ATTRIBUTE);
        if (null == handler) {
            throw new IllegalStateException("ResourceBundlesHandler not present in servlet context. Initialization of Jawr either failed or never occurred.");
        }

        ResourceBundlesHandler rsHandler = (ResourceBundlesHandler) handler;
        String media = attributes.getString(JawrConstant.MEDIA_ATTR);
        String title = attributes.getString(JawrConstant.TITLE_ATTR);
        boolean alternate = attributes.getBoolean(JawrConstant.ALTERNATE_ATTR);
        boolean displayAlternateStyles = attributes.getBoolean(JawrConstant.DISPLAY_ALTERNATE_ATTR);
        
        
        return new CSSHTMLBundleLinkRenderer(rsHandler, this.useRandomParam, media, alternate, displayAlternateStyles, title);
    }
    
}
