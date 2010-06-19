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
package net.jawr.web.resource.bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import net.jawr.web.resource.bundle.factory.PropertiesBundleConstant;
import net.jawr.web.resource.bundle.postprocess.ChainedResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.variant.VariantSet;
import net.jawr.web.util.StringUtils;

/**
 * This class will manage the serialization of the joinable resource bundle.
 * 
 * @author Ibrahim Chaehoi
 * 
 */
public class JoinableResourceBundlePropertySerializer {

	/**
	 * This method will serialize the properties of the bundle in the Properties object
	 * 
	 * This method will serialize all bundle except the childs of composite bundle,
	 * for which the mappings will be part of their parent bundles.  
	 * The properties associated to a bundle are the same as the one define in the jawr configuration file.
	 * Only the following properties are different from the standard configuration file :
	 *   - The mapping, which will contains path to each resources of the bundle ( no wildcard like : myfolder/** ) 
	 *   - The variants which will be explicitly specified.
	 *   - The bundle hash codes will be define as properties, so we will not have to compute them.
	 *   For a bundle with local variants, there will be an hash code for each variant + one, which is the hash 
	 *   code of the default bundle.
	 *   - The licence path list.
	 *   
	 * @param bundle the bundle to serialize
	 * @param props the properties to update
	 */
	public static void serializeInProperties(JoinableResourceBundle bundle,
			String type, Properties props) {

		// If the bundle is a child of a composite bundle, 
		// no need to serialize it, it will be integrated with the composite bundle
		if (StringUtils.isEmpty(bundle.getId())) {
			return;
		}

		String bundleName = bundle.getName();
		String prefix = PropertiesBundleConstant.PROPS_PREFIX + type + "."+ PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_PROPERTY + bundleName;
		InclusionPattern inclusion = bundle.getInclusionPattern();

		// Set the ID
		props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_ID, bundle.getId());

		if (inclusion.isGlobal()) {
			props
					.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_GLOBAL_FLAG, Boolean.toString(inclusion
							.isGlobal()));
		}
		if (inclusion.getInclusionOrder() != 0) {
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_ORDER, Integer.toString(inclusion
					.getInclusionOrder()));
		}

		if (inclusion.isIncludeOnDebug()) {
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_DEBUGONLY, Boolean.toString(inclusion
					.isIncludeOnDebug()));
		}
		if (inclusion.isExcludeOnDebug()) {
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_DEBUGNEVER, Boolean.toString(inclusion
					.isExcludeOnDebug()));
		}
		if (StringUtils.isNotEmpty(bundle.getExplorerConditionalExpression())) {
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_IE_CONDITIONAL_EXPRESSION, bundle
					.getExplorerConditionalExpression());
		}
		if (StringUtils.isNotEmpty(bundle.getAlternateProductionURL())) {
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_PRODUCTION_ALT_URL, bundle
					.getAlternateProductionURL());
		}
		
		if (bundle.getBundlePostProcessor() != null) {
			props
					.put(prefix +PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_POSTPROCESSOR,
							getBundlePostProcessorsName((ChainedResourceBundlePostProcessor) bundle
									.getBundlePostProcessor()));
		}

		if (bundle.getUnitaryPostProcessor() != null) {
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_FILE_POSTPROCESSOR,
					getBundlePostProcessorsName((ChainedResourceBundlePostProcessor) bundle
							.getUnitaryPostProcessor()));
		}
		
		// Add variants and the bundle hashcode
		Map variants = bundle.getVariants();
		if (variants != null && !variants.isEmpty()) {
			String serializedVariants = serializeVariantSets(variants);
			if (StringUtils.isNotEmpty(serializedVariants)) {
				props.put(prefix +  PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_VARIANTS, serializedVariants);
			}

			List variantKeys = bundle.getVariantKeys();
			for (Iterator iterator = variantKeys.iterator(); iterator
					.hasNext();) {
				String variantKey = (String) iterator.next();
				if (StringUtils.isNotEmpty(variantKey)) {
					props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_HASHCODE_VARIANT + variantKey,
							bundle.getBundleDataHashCode(variantKey));
				}
			}
		} 
			
		props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_HASHCODE, bundle
					.getBundleDataHashCode(null));
		

		// mapping
		List itemPathList = bundle.getItemPathList();
		if (itemPathList != null && !itemPathList.isEmpty()) {
			props
					.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_MAPPINGS,
							getCommaSeparatedString(itemPathList));
		}
		List dependencies = bundle.getDependencies();
		if (dependencies != null && !dependencies.isEmpty()) {
			List dependenciesBundleName = getBundleNames(bundle.getDependencies());
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_DEPENDENCIES, getCommaSeparatedString(dependenciesBundleName));
		}
		
		Set licensesPathList = bundle.getLicensesPathList();
		if (licensesPathList != null && !licensesPathList.isEmpty()) {
			props.put(prefix + PropertiesBundleConstant.BUNDLE_FACTORY_CUSTOM_LICENCE_PATH_LIST,
					getCommaSeparatedString(licensesPathList));
		}
	}

	/**
	 * Serialize the variant sets.
	 * 
	 * @param map the map to serialize
	 * @return the serialized variant sets
	 */
	private static String serializeVariantSets(Map map) {
		StringBuffer result = new StringBuffer();
		
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			result.append(entry.getKey()+":");
			VariantSet variantSet = (VariantSet) entry.getValue();
			result.append(variantSet.getDefaultVariant()+":");
			result.append(getCommaSeparatedString((Collection) variantSet));
			result.append(";");
		}
		
		return result.toString();
	}

	/**
	 * Returns the list of bundle names
	 * @param bundles the bundles
	 * @return the list of bundle names
	 */
	private static List getBundleNames(List bundles) {
		
		List bundleNames = new ArrayList();
		for (Iterator iterator = bundles.iterator(); iterator
				.hasNext();) {
			bundleNames.add(((JoinableResourceBundle) iterator.next()).getName());
		}
		return bundleNames;
	}

	/**
	 * Returns the mapping list
	 * 
	 * @param itemPathList the item path list
	 * @return the item path list
	 */
	private static String getCommaSeparatedString(Collection coll) {

		StringBuffer buffer = new StringBuffer();
		for (Iterator eltIterator = coll.iterator(); eltIterator.hasNext();) {
			String elt = (String) eltIterator.next();
			buffer.append(elt);
			if(eltIterator.hasNext()){
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

	/**
	 * Returns the bundle post processor name separated by a comma character
	 * 
	 * @param processor the post processor
	 * @return the bundle post processor name separated by a comma character
	 */
	private static String getBundlePostProcessorsName(
			ChainedResourceBundlePostProcessor processor) {

		String bundlePostProcessor = "";
		if (processor != null) {
			bundlePostProcessor = processor.getId();
		}

		return bundlePostProcessor;
	}
}
