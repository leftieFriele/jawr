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
package net.jawr.web.resource.bundle.sorting;

import java.io.Serializable;
import java.util.Comparator;

import net.jawr.web.resource.bundle.JoinableResourceBundle;

/**
 * Implementation of Comparator interface used to determine the inclusion order
 * of global bundles. Compares the inclusion order attribute of each bundle's 
 * InclusionPattern attribute.  
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class GlobalResourceBundleComparator implements Comparator, Serializable {

	/** The serial version UID */
	private static final long serialVersionUID = -277897413409167116L;

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(Object bundleA, Object bundleB) {
		JoinableResourceBundle colA = (JoinableResourceBundle) bundleA;
		JoinableResourceBundle colB = (JoinableResourceBundle) bundleB;
		Integer a = new Integer(colA.getInclusionPattern().getInclusionOrder());
		Integer b = new Integer(colB.getInclusionPattern().getInclusionOrder());
		return a.compareTo(b);
	}

}
