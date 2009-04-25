/**
 * Copyright 2009 Ibrahim Chaehoi
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.jawr.web.JawrConstant;
import net.jawr.web.resource.ImageResourcesHandler;
import net.jawr.web.resource.bundle.factory.util.PathNormalizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class defines the image tag.
 * 
 * This implementation is largely based on the Struts image tag.
 *  
 * @author Ibrahim Chaehoi
 */
public class ImgHtmlTag extends TagSupport {
	
		// -------------------------------------------------------------
	
		/** The logger */
		private static Log logger = LogFactory.getLog(ImgHtmlTag.class);
    
	    /**
	     * The property to specify where to align the image.
	     */
	    protected String align = null;

	    /**
	     * The border size around the image.
	     */
	    protected String border = null;

	    /**
	     * The image height.
	     */
	    protected String height = null;

	    /**
	     * The horizontal spacing around the image.
	     */
	    protected String hspace = null;

	    /**
	     * The image name for named images.
	     */
	    protected String name = null;

	    /**
	     * Server-side image map declaration.
	     */
	    protected String ismap = null;

	    /**
	     * The image source URI.
	     */
	    protected String src = null;

	    /**
	     * Client-side image map declaration.
	     */
	    protected String usemap = null;

	    /**
	     * The vertical spacing around the image.
	     */
	    protected String vspace = null;

	    /**
	     * The image width.
	     */
	    protected String width = null;
	    
	    // CSS Style Support

	    /**
	     * Style attribute associated with component.
	     */
	    private String style = null;

	    /**
	     * Named Style class associated with component.
	     */
	    private String styleClass = null;

	    /**
	     * Identifier associated with component.
	     */
	    private String styleId = null;

	    /**
	     * The alternate text of this element.
	     */
	    private String alt = null;

	    /**
	     * The advisory title of this element.
	     */
	    private String title = null;

	    /**
	     * The language code of this element.
	     */
	    private String lang = null;

	    /**
	     * The direction for weak/neutral text of this element.
	     */
	    private String dir = null;

	    //  Mouse Events
	    /**
	     * Mouse click event.
	     */
	    private String onclick = null;

	    /**
	     * Mouse double click event.
	     */
	    private String ondblclick = null;

	    /**
	     * Mouse over component event.
	     */
	    private String onmouseover = null;

	    /**
	     * Mouse exit component event.
	     */
	    private String onmouseout = null;

	    /**
	     * Mouse moved over component event.
	     */
	    private String onmousemove = null;

	    /**
	     * Mouse pressed on component event.
	     */
	    private String onmousedown = null;

	    /**
	     * Mouse released on component event.
	     */
	    private String onmouseup = null;

	    //  Keyboard Events

	    /**
	     * Key down in component event.
	     */
	    private String onkeydown = null;

	    /**
	     * Key released in component event.
	     */
	    private String onkeyup = null;

	    /**
	     * Key down and up together in component event.
	     */
	    private String onkeypress = null;

	    // ----------------------------------------------------- Constructor
	    public ImgHtmlTag() {
	        
	    }

	    public String getAlign() {
	        return (this.align);
	    }

	    public void setAlign(String align) {
	        this.align = align;
	    }

	    public String getBorder() {
	        return (this.border);
	    }

	    public void setBorder(String border) {
	        this.border = border;
	    }

	    public String getHeight() {
	        return (this.height);
	    }

	    public void setHeight(String height) {
	        this.height = height;
	    }

	    public String getHspace() {
	        return (this.hspace);
	    }

	    public void setHspace(String hspace) {
	        this.hspace = hspace;
	    }

	    public String getName() {
	        return (this.name);
	    }

	    public void setName(String imageName) {
	        this.name = imageName;
	    }

	    public String getIsmap() {
	        return (this.ismap);
	    }

	    public void setIsmap(String ismap) {
	        this.ismap = ismap;
	    }

	    public String getSrc() {
	        return (this.src);
	    }

	    public void setSrc(String src) {
	        this.src = src;
	    }

	    public String getUsemap() {
	        return (this.usemap);
	    }

	    public void setUsemap(String usemap) {
	        this.usemap = usemap;
	    }

	    public String getVspace() {
	        return (this.vspace);
	    }

	    public void setVspace(String vspace) {
	        this.vspace = vspace;
	    }

	    public String getWidth() {
	        return (this.width);
	    }

	    public void setWidth(String width) {
	        this.width = width;
	    }

	    

	    // --------------------------------------------------------- Public Methods

	    /**
		 * @return the style
		 */
		public String getStyle() {
			return style;
		}

		/**
		 * @param style the style to set
		 */
		public void setStyle(String style) {
			this.style = style;
		}

		/**
		 * @return the styleClass
		 */
		public String getStyleClass() {
			return styleClass;
		}

		/**
		 * @param styleClass the styleClass to set
		 */
		public void setStyleClass(String styleClass) {
			this.styleClass = styleClass;
		}

		/**
		 * @return the styleId
		 */
		public String getStyleId() {
			return styleId;
		}

		/**
		 * @param styleId the styleId to set
		 */
		public void setStyleId(String styleId) {
			this.styleId = styleId;
		}

		/**
		 * @return the alt
		 */
		public String getAlt() {
			return alt;
		}

		/**
		 * @param alt the alt to set
		 */
		public void setAlt(String alt) {
			this.alt = alt;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @param title the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * @return the lang
		 */
		public String getLang() {
			return lang;
		}

		/**
		 * @param lang the lang to set
		 */
		public void setLang(String lang) {
			this.lang = lang;
		}

		/**
		 * @return the dir
		 */
		public String getDir() {
			return dir;
		}

		/**
		 * @param dir the dir to set
		 */
		public void setDir(String dir) {
			this.dir = dir;
		}

		/**
		 * @return the onclick
		 */
		public String getOnclick() {
			return onclick;
		}

		/**
		 * @param onclick the onclick to set
		 */
		public void setOnclick(String onclick) {
			this.onclick = onclick;
		}

		/**
		 * @return the ondblclick
		 */
		public String getOndblclick() {
			return ondblclick;
		}

		/**
		 * @param ondblclick the ondblclick to set
		 */
		public void setOndblclick(String ondblclick) {
			this.ondblclick = ondblclick;
		}

		/**
		 * @return the onmouseover
		 */
		public String getOnmouseover() {
			return onmouseover;
		}

		/**
		 * @param onmouseover the onmouseover to set
		 */
		public void setOnmouseover(String onmouseover) {
			this.onmouseover = onmouseover;
		}

		/**
		 * @return the onmouseout
		 */
		public String getOnmouseout() {
			return onmouseout;
		}

		/**
		 * @param onmouseout the onmouseout to set
		 */
		public void setOnmouseout(String onmouseout) {
			this.onmouseout = onmouseout;
		}

		/**
		 * @return the onmousemove
		 */
		public String getOnmousemove() {
			return onmousemove;
		}

		/**
		 * @param onmousemove the onmousemove to set
		 */
		public void setOnmousemove(String onmousemove) {
			this.onmousemove = onmousemove;
		}

		/**
		 * @return the onmousedown
		 */
		public String getOnmousedown() {
			return onmousedown;
		}

		/**
		 * @param onmousedown the onmousedown to set
		 */
		public void setOnmousedown(String onmousedown) {
			this.onmousedown = onmousedown;
		}

		/**
		 * @return the onmouseup
		 */
		public String getOnmouseup() {
			return onmouseup;
		}

		/**
		 * @param onmouseup the onmouseup to set
		 */
		public void setOnmouseup(String onmouseup) {
			this.onmouseup = onmouseup;
		}

		/**
		 * @return the onkeydown
		 */
		public String getOnkeydown() {
			return onkeydown;
		}

		/**
		 * @param onkeydown the onkeydown to set
		 */
		public void setOnkeydown(String onkeydown) {
			this.onkeydown = onkeydown;
		}

		/**
		 * @return the onkeyup
		 */
		public String getOnkeyup() {
			return onkeyup;
		}

		/**
		 * @param onkeyup the onkeyup to set
		 */
		public void setOnkeyup(String onkeyup) {
			this.onkeyup = onkeyup;
		}

		/**
		 * @return the onkeypress
		 */
		public String getOnkeypress() {
			return onkeypress;
		}

		/**
		 * @param onkeypress the onkeypress to set
		 */
		public void setOnkeypress(String onkeypress) {
			this.onkeypress = onkeypress;
		}

		/**
	     * Render the beginning of the IMG tag.
	     *
	     * @throws JspException if a JSP exception has occurred
	     */
	    public int doStartTag() throws JspException {
	        
	    	return super.doStartTag();
	    }

	    /**
	     * Render the end of the IMG tag.
	     *
	     * @throws JspException if a JSP exception has occurred
	     */
	    public int doEndTag() throws JspException {
	        // Generate the name definition or image element
	        HttpServletResponse response =
	            (HttpServletResponse) pageContext.getResponse();
	        StringBuffer results = new StringBuffer("<img");
	        
	        ImageResourcesHandler imgRsHandler = (ImageResourcesHandler) pageContext.getServletContext().getAttribute(JawrConstant.IMG_CONTEXT_ATTRIBUTE);
	        String newUrl = (String) imgRsHandler.getCacheUrl(getSrc());

	        if(newUrl == null){
	        	newUrl = getSrc();
	        	logger.debug("No mapping found for the image : "+getSrc());
	        }
	        
	        String imageServletMapping = imgRsHandler.getJawrConfig().getServletMapping();
			if(imageServletMapping != null){
				newUrl = PathNormalizer.joinDomainToPath(imageServletMapping, newUrl);
			}else{
				if(newUrl.startsWith("/")){
					newUrl = newUrl.substring(1);
				}
			}
	        
	        prepareAttribute(results, "src", response.encodeURL(newUrl));
	        
	        prepareAttribute(results, "name", getName());
	        prepareAttribute(results, "height", getHeight());
	        prepareAttribute(results, "width", getWidth());
	        prepareAttribute(results, "align", getAlign());
	        prepareAttribute(results, "border", getBorder());
	        prepareAttribute(results, "hspace", getHspace());
	        prepareAttribute(results, "vspace", getVspace());
	        prepareAttribute(results, "ismap", getIsmap());
	        prepareAttribute(results, "usemap", getUsemap());
	        results.append(prepareStyles());
	        results.append(prepareEventHandlers());
	        results.append(" />");

	        try {
				pageContext.getOut().write(results.toString());
			} catch (IOException e) {
				throw new JspException(e);
			}

	        return (EVAL_PAGE);
	    }
	    
	    /**
	     * Prepares the style attributes for inclusion in the component's HTML
	     * tag.
	     *
	     * @return The prepared String for inclusion in the HTML tag.
	     * @throws JspException if invalid attributes are specified
	     */
	    protected String prepareStyles()
	        throws JspException {
	        StringBuffer styles = new StringBuffer();

	        prepareAttribute(styles, "id", getStyleId());
	        prepareAttribute(styles, "style", getStyle());
	        prepareAttribute(styles, "class", getStyleClass());
	        prepareAttribute(styles, "title", getTitle());
	        prepareAttribute(styles, "alt", getAlt());
	        prepareInternationalization(styles);

	        return styles.toString();
	    }

	    /**
	     * Prepares the internationalization attributes, appending them to the the given
	     * StringBuffer.
	     *
	     * @param handlers The StringBuffer that output will be appended to.
	     * @since Struts 1.3.6
	     */
	    protected void prepareInternationalization(StringBuffer handlers) {
	        prepareAttribute(handlers, "lang", getLang());
	        prepareAttribute(handlers, "dir", getDir());
	    }

	    /**
	     * Prepares the event handlers for inclusion in the component's HTML tag.
	     *
	     * @return The prepared String for inclusion in the HTML tag.
	     */
	    protected String prepareEventHandlers() {
	        StringBuffer handlers = new StringBuffer();

	        prepareMouseEvents(handlers);
	        prepareKeyEvents(handlers);
	    
	        return handlers.toString();
	    }

	    /**
	     * Prepares the mouse event handlers, appending them to the the given
	     * StringBuffer.
	     *
	     * @param handlers The StringBuffer that output will be appended to.
	     */
	    protected void prepareMouseEvents(StringBuffer handlers) {
	        prepareAttribute(handlers, "onclick", getOnclick());
	        prepareAttribute(handlers, "ondblclick", getOndblclick());
	        prepareAttribute(handlers, "onmouseover", getOnmouseover());
	        prepareAttribute(handlers, "onmouseout", getOnmouseout());
	        prepareAttribute(handlers, "onmousemove", getOnmousemove());
	        prepareAttribute(handlers, "onmousedown", getOnmousedown());
	        prepareAttribute(handlers, "onmouseup", getOnmouseup());
	    }

	    /**
	     * Prepares the keyboard event handlers, appending them to the the given
	     * StringBuffer.
	     *
	     * @param handlers The StringBuffer that output will be appended to.
	     */
	    protected void prepareKeyEvents(StringBuffer handlers) {
	        prepareAttribute(handlers, "onkeydown", getOnkeydown());
	        prepareAttribute(handlers, "onkeyup", getOnkeyup());
	        prepareAttribute(handlers, "onkeypress", getOnkeypress());
	    }

	    /**
	     * Prepares an attribute if the value is not null, appending it to the the
	     * given StringBuffer.
	     *
	     * @param handlers The StringBuffer that output will be appended to.
	     */
	    protected void prepareAttribute(StringBuffer handlers, String name,
	        Object value) {
	        if (value != null) {
	            handlers.append(" ");
	            handlers.append(name);
	            handlers.append("=\"");
	            handlers.append(value);
	            handlers.append("\"");
	        }
	    }

	    /**
	     * Release any acquired resources.
	     */
	    public void release() {
	        super.release();

	        border = null;
	        height = null;
	        hspace = null;
	        name = null;
	        ismap = null;
	        src = null;
	        usemap = null;
	        vspace = null;
	        width = null;
	    }

	    // ------------------------------------------------------ Protected Methods

	    /**
	     * Return the specified src URL, modified as necessary with optional
	     * request parameters.
	     *
	     * @param url The URL to be modified (or null if this url will not be
	     *            used)
	     * @throws JspException if an error occurs preparing the URL
	     */
	    protected String url(String url)
	        throws JspException {
	        if (url == null) {
	            return (url);
	        }

	        // Start with an unadorned URL as specified
	        StringBuffer src = new StringBuffer(url);


	        // Return the final result
	        return (src.toString());
	    }
	}
