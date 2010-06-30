/**
 * Copyright 2007-2010 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletContext;

import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.util.StringUtils;

/**
 * Utility class for locale.
 * 
 * are allowed within the url.
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public class LocaleUtils {

	/** The message resource bundle suffix */
	private static final String MSG_RESOURCE_BUNDLE_SUFFIX = ".properties";

	/** The available locale suffixes */
	public static final Set LOCALE_SUFFIXES = LocaleUtils.getAvailableLocaleSuffixes();
	
	/**
	 * Returns the localized bundle name
	 * 
	 * @param bundleName
	 * @param localeKey
	 * @return the localized bundle name
	 */
	public static String getLocalizedBundleName(String bundleName, String localeKey) {

		String newName = bundleName;
		int idxSeparator = bundleName.lastIndexOf('.');
		if (StringUtils.isNotEmpty(localeKey) && idxSeparator != -1) {
			newName = bundleName.substring(0, idxSeparator);
			newName += '_' + localeKey;
			newName += bundleName.substring(idxSeparator);
		}

		return newName;
	}

	/**
	 * Returns the list of available locale suffixes for a message resource bundle
	 * 
	 * @param messageBundlePath the resource bundle path
	 * @return the list of available locale suffixes for a message resource bundle
	 */
	public static List getAvailableLocaleSuffixesForBundle(String messageBundlePath) {

		return getAvailableLocaleSuffixesForBundle(messageBundlePath, MSG_RESOURCE_BUNDLE_SUFFIX, null);
	}
	
	/**
	 * Returns the list of available locale suffixes for a message resource bundle
	 * 
	 * @param messageBundlePath the resource bundle path
	 * @param servletContext the servlet context
	 * @return the list of available locale suffixes for a message resource bundle
	 */
	public static List getAvailableLocaleSuffixesForBundle(String messageBundlePath, ServletContext servletContext) {

		return getAvailableLocaleSuffixesForBundle(messageBundlePath, MSG_RESOURCE_BUNDLE_SUFFIX, servletContext);
	}
	
	/**
	 * Returns the list of available locale suffixes for a message resource bundle
	 * 
	 * @param messageBundlePath the resource bundle path
	 * @param fileSuffix the file suffix
	 * @return the list of available locale suffixes for a message resource bundle
	 */
	public static List getAvailableLocaleSuffixesForBundle(String messageBundlePath, String fileSuffix) {
		return getAvailableLocaleSuffixesForBundle(messageBundlePath, fileSuffix, null);
	}
	
	/**
	 * Returns the list of available locale suffixes for a message resource bundle
	 * 
	 * @param messageBundlePath the resource bundle path
	 * @param fileSuffix the file suffix
	 * @param servletContext the servlet context
	 * @return the list of available locale suffixes for a message resource bundle
	 */
	public static List getAvailableLocaleSuffixesForBundle(String messageBundlePath, String fileSuffix, ServletContext servletContext) {

		int idxNameSpace = messageBundlePath.indexOf("(");
		int idxFilter = messageBundlePath.indexOf("[");
		int idx = -1;
		if(idxNameSpace != -1 && idxFilter != -1){
			idx = Math.min(idxNameSpace, idxFilter);
		}else if(idxNameSpace != -1 && idxFilter == -1){
			idx = idxNameSpace;
		}else if(idxNameSpace == -1 && idxFilter != -1){
			idx = idxFilter;
		}
		
		String messageBundle = null;
		if(idx > 0){
			messageBundle = messageBundlePath.substring(0, idx);  
		}else{
			messageBundle = messageBundlePath;
		}
		return getAvailableLocaleSuffixes(messageBundle, fileSuffix, servletContext);
	}

	/**
	 * Returns the list of available locale suffixes for a message resource bundle
	 * 
	 * @param messageBundle the resource bundle path
	 * @return the list of available locale suffixes for a message resource bundle
	 */
	public static List getAvailableLocaleSuffixes(String messageBundle, ServletContext servletContext) {
		
		return getAvailableLocaleSuffixes(messageBundle, MSG_RESOURCE_BUNDLE_SUFFIX, servletContext);
	}
	
	/**
	 * Returns the list of available locale suffixes for a message resource bundle
	 * 
	 * @param messageBundle the resource bundle path
	 * @param fileSuffix the file suffix
	 * @param servletContext the servlet context 
	 * @return the list of available locale suffixes for a message resource bundle
	 */
	public static List getAvailableLocaleSuffixes(String messageBundle, String fileSuffix, ServletContext servletContext) {
		List availableLocaleSuffixes = new ArrayList();
		Locale[] availableLocales = Locale.getAvailableLocales();
	
		addSuffixIfAvailable(messageBundle, availableLocaleSuffixes, null, fileSuffix, servletContext);
	
		for (int i = 0; i < availableLocales.length; i++) {
			Locale locale = availableLocales[i];
			addSuffixIfAvailable(messageBundle, availableLocaleSuffixes, locale, fileSuffix, servletContext);
		}
	
		return availableLocaleSuffixes;
	}
	
	/**
	 * Add the locale suffix if the message resource bundle file exists.
	 * 
	 * @param messageBundlePath the message resource bundle path
	 * @param availableLocaleSuffixes the list of available locale suffix to update
	 * @param locale the locale to check.
	 * @param fileSuffix the file suffix
	 */
	private static void addSuffixIfAvailable(String messageBundlePath, List availableLocaleSuffixes, Locale locale, String fileSuffix, ServletContext servletContext) {
		String localMsgResourcePath = toBundleName(messageBundlePath, locale) + fileSuffix;
		URL resourceUrl = null;
		try {
			resourceUrl = ClassLoaderResourceUtils.getResourceURL(localMsgResourcePath, LocaleUtils.class);
		} catch (Exception e) {
			// Nothing to do
		}
		
		if(resourceUrl == null && servletContext != null && localMsgResourcePath.startsWith("grails-app/")){
			try {
				resourceUrl = servletContext.getResource("/WEB-INF/"+localMsgResourcePath);
			} catch (MalformedURLException e) {
				// Nothing to do
			}
		}

		if (resourceUrl != null) {

			String suffix = localMsgResourcePath.substring(messageBundlePath.length());
			if (suffix.length() > 0) {

				if (suffix.length() == fileSuffix.length()) {
					suffix = "";
				} else {
					// remove the "_" before the suffix "_en_US" => "en_US"
					suffix = suffix.substring(1, suffix.length() - fileSuffix.length());
				}
			}
			availableLocaleSuffixes.add(suffix);
		}
	}

	/**
	 * Converts the given <code>baseName</code> and <code>locale</code> to the bundle name. This method is called from the default implementation of
	 * the {@link #newBundle(String, Locale, String, ClassLoader, boolean) newBundle} and
	 * {@link #needsReload(String, Locale, String, ClassLoader, ResourceBundle, long) needsReload} methods.
	 * 
	 * <p>
	 * This implementation returns the following value:
	 * 
	 * <pre>
	 * baseName + &quot;_&quot; + language + &quot;_&quot; + country + &quot;_&quot; + variant
	 * </pre>
	 * 
	 * where <code>language</code>, <code>country</code> and <code>variant</code> are the language, country and variant values of <code>locale</code>,
	 * respectively. Final component values that are empty Strings are omitted along with the preceding '_'. If all of the values are empty strings,
	 * then <code>baseName</code> is returned.
	 * 
	 * <p>
	 * For example, if <code>baseName</code> is <code>"baseName"</code> and <code>locale</code> is <code>Locale("ja",&nbsp;"",&nbsp;"XX")</code>, then
	 * <code>"baseName_ja_&thinsp;_XX"</code> is returned. If the given locale is <code>Locale("en")</code>, then <code>"baseName_en"</code> is
	 * returned.
	 * 
	 * <p>
	 * Overriding this method allows applications to use different conventions in the organization and packaging of localized resources.
	 * 
	 * @param baseName the base name of the resource bundle, a fully qualified class name
	 * @param locale the locale for which a resource bundle should be loaded
	 * @return the bundle name for the resource bundle
	 * @exception NullPointerException if <code>baseName</code> or <code>locale</code> is <code>null</code>
	 */
	public static String toBundleName(String bundleBaseName, Locale locale) {
		
		String baseName = bundleBaseName.replace('.', '/');
		if (locale == null) {
			return baseName;
		}

		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if (language == "" && country == "" && variant == "") {
			return baseName;
		}

		StringBuffer sb = new StringBuffer(baseName);
		sb.append('_');
		if (variant != "") {
			sb.append(language).append('_').append(country).append('_').append(variant);
		} else if (country != "") {
			sb.append(language).append('_').append(country);
		} else {
			sb.append(language);
		}
		return sb.toString();
	}
	
	/**
	 * Returns the set of available locale suffixes
	 * @return the set of available locale suffixes
	 */
	public static Set getAvailableLocaleSuffixes(){
	
		Set availableLocaleSuffixes = new HashSet(); 
		Locale[] availableLocales = Locale.getAvailableLocales();
		for (int i = 0; i < availableLocales.length; i++) {
			Locale locale = availableLocales[i];
			
			StringBuffer sb = new StringBuffer();
			if (locale != null) {
				
				String language = locale.getLanguage();
				String country = locale.getCountry();
				String variant = locale.getVariant();

				if (variant != "") {
					sb.append(language).append('_').append(country).append('_').append(variant);
				} else if (country != "") {
					sb.append(language).append('_').append(country);
				} else {
					sb.append(language);
				}
			}
			availableLocaleSuffixes.add(sb.toString());
		}
		return availableLocaleSuffixes;
	}
}
