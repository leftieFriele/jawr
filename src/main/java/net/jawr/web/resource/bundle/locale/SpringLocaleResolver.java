/**
 * Copyright 2008 Jordi Hern�ndez Sell�s
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
package net.jawr.web.resource.bundle.locale;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Implementation of Jawr's LocaleResolver which in turn uses Spring's LocaleResolver to 
 * do its job. 
 * 
 * @author Jordi Hern�ndez Sell�s
 *
 */
public class SpringLocaleResolver implements LocaleResolver {

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.locale.LocaleResolver#resolveLocaleCode(javax.servlet.http.HttpServletRequest)
	 */
	public String resolveLocaleCode(HttpServletRequest request) {
		org.springframework.web.servlet.LocaleResolver resolver = RequestContextUtils.getLocaleResolver(request);
		Locale resolved = resolver.resolveLocale(request);
		if(resolved != Locale.getDefault())
			return resolved.toString();
		else return null;
	}

}
