/**
 * Copyright 2008-2010 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jawr.web.resource.bundle.JoinableResourceBundle;

/**
 * Debug mode implementation of ResourceBundlePathsIterator. Uses a ConditionalCommentCallbackHandler
 * to signal the use of conditional comments. The paths returned are those of the individual 
 * members of the bundle. 
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public class DebugModePathsIteratorImpl extends AbstractPathsIterator implements ResourceBundlePathsIterator {

	/** The bundle iterator */
	private Iterator bundlesIterator;
	
	/** The path iterator */
	private Iterator pathsIterator;
	
	/** The current bundle */
	private JoinableResourceBundle currentBundle;
	
	/**
	 * Constructor
	 * @param bundles the list of bundle
	 * @param callbackHandler the comment callback handler
	 * @param variants the variants
	 */
	public DebugModePathsIteratorImpl(List bundles,ConditionalCommentCallbackHandler callbackHandler,Map variants) {
		super(callbackHandler,variants);
		this.bundlesIterator = bundles.iterator();
	}
	

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.iterator.ResourceBundlePathsIterator#nextPath()
	 */
	public String nextPath() {
		
		String path = null;
		if(null == pathsIterator || !pathsIterator.hasNext()) {
			currentBundle = (JoinableResourceBundle) bundlesIterator.next();
			
			if(null != currentBundle.getExplorerConditionalExpression())
				commentCallbackHandler.openConditionalComment(currentBundle.getExplorerConditionalExpression());

			pathsIterator = currentBundle.getItemPathList(variants).iterator();
		}
		
		
		if(pathsIterator != null && pathsIterator.hasNext()){
			path = pathsIterator.next().toString();
		}
		
		return path;
	}


	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if(null != pathsIterator && !pathsIterator.hasNext()) {
			if(null != currentBundle && null != currentBundle.getExplorerConditionalExpression())
				commentCallbackHandler.closeConditionalComment();
		}
		boolean rets = false;
		if(null != pathsIterator) {
			rets = pathsIterator.hasNext() || bundlesIterator.hasNext();
		}
		else{
			rets = bundlesIterator.hasNext();
		}
			
		return rets;
	}

}
