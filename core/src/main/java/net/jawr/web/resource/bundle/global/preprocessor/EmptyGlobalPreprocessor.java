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
package net.jawr.web.resource.bundle.global.preprocessor;

import java.util.List;

import net.jawr.web.JawrConstant;


/**
 * This class defines the empty global preprocessor, which do nothing. 
 * 
 * @author Ibrahim Chaehoi
 *
 */
public class EmptyGlobalPreprocessor extends
		AbstractChainedGlobalPreprocessor {

	/**
	 * Construtor
	 */
	public EmptyGlobalPreprocessor() {
		super(JawrConstant.EMPTY_GLOBAL_PREPROCESSOR_ID);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.global.preprocessor.GlobalPreprocessor#processBundles(net.jawr.web.resource.bundle.global.preprocessor.GlobalPreprocessingContext, java.util.List)
	 */
	public void processBundles(GlobalPreprocessingContext ctx,
			List bundles) {

		// Nothing to do
		
	}

}
