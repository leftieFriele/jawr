/**
 * Copyright 2010 Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.variant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.jawr.web.JawrConstant;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.util.StringUtils;

/**
 * Utility class for variants
 * 
 * @author Ibrahim Chaehoi
 */
public class VariantUtils {

	/**
	 * Returns all variant keys from the map of variant set
	 * 
	 * @param variants
	 *            the map of variant set
	 * @return all variant keys
	 */
	public static List<Map<String, String>> getAllVariants(
			Map<String, VariantSet> variantSets) {

		List<Map<String, String>> variantKeys = new ArrayList<Map<String, String>>();
		if (variantSets != null) {
			for (Iterator<Entry<String, Collection<String>>> itVariantSets = new TreeMap<String, Collection<String>>(
					variantSets).entrySet().iterator(); itVariantSets.hasNext();) {

				Entry<String, Collection<String>> variantEntry = itVariantSets
						.next();
				String variantType = variantEntry.getKey();
				Collection<String> variantList = variantEntry.getValue();
				if (variantKeys.isEmpty()) {
					variantKeys = getVariants(null, variantType, variantList);
				} else {

					List<Map<String, String>> tmpResult = new ArrayList<Map<String, String>>();
					for (Iterator<Map<String, String>> itCurVariantKeys = variantKeys
							.iterator(); itCurVariantKeys.hasNext();) {
						Map<String, String> curVariant = itCurVariantKeys
								.next();
						tmpResult.addAll(getVariants(curVariant, variantType,
								variantList));
					}
					variantKeys = tmpResult;
				}
			}
		}

		return variantKeys;
	}

	/**
	 * Returns the list of variant maps, which are initialize with the current
	 * map values and each element of the list contains a element of the variant
	 * list with the variant type as key
	 * 
	 * @param curVariant
	 *            the current variant map
	 * @param variantType
	 *            the variant type
	 * @param variantList
	 *            the variant list
	 * @return the list of variant maps
	 */
	private static List<Map<String, String>> getVariants(
			Map<String, String> curVariant, String variantType,
			Collection<String> variantList) {

		List<Map<String, String>> variants = new ArrayList<Map<String, String>>();
		for (Iterator<String> itCurVariant = variantList.iterator(); itCurVariant
				.hasNext();) {

			String variant = (String) itCurVariant.next();

			Map<String, String> map = new HashMap<String, String>();
			if (curVariant != null) {
				map.putAll(curVariant);
			}
			map.put(variantType, variant);
			variants.add(map);
		}
		return variants;
	}

	/**
	 * Returns all variant keys from the map of variant set
	 * 
	 * @param variants
	 *            the map of variant set
	 * @return all variant keys
	 */
	public static List<String> getAllVariantKeys(
			Map<String, ? extends Collection<String>> variants) {

		List<String> variantKeys = new ArrayList<String>();
		for (Iterator<Collection<String>> itVariantSets = new TreeMap<String, Collection<String>>(
				variants).values().iterator(); itVariantSets.hasNext();) {
			Collection<String> variantList = itVariantSets.next();
			if (variantKeys.isEmpty()) {
				variantKeys = getVariantKeys(null, variantList);
			} else {

				List<String> tmpResult = new ArrayList<String>();
				for (Iterator<String> itCurVariantKeys = variantKeys.iterator(); itCurVariantKeys
						.hasNext();) {
					String curVariantKey = itCurVariantKeys.next();
					tmpResult
							.addAll(getVariantKeys(curVariantKey
									+ JawrConstant.VARIANT_SEPARATOR_CHAR,
									variantList));
				}
				variantKeys = tmpResult;
			}
		}

		return variantKeys;
	}

	/**
	 * Returns all variant keys from the map of variant set
	 * 
	 * @param variants
	 *            the map of variant set
	 * @return all variant keys
	 */
	public static List<String> getAllVariantKeysFromFixedVariants(
			Map<String, VariantSet> variants, Map<String, String> fixedVariants) {

		Map<String, VariantSet> tempVariants = new HashMap<String, VariantSet>(
				variants);

		if (fixedVariants != null) {

			for (Iterator<Entry<String, String>> iterator = fixedVariants
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				VariantSet variantSet = tempVariants.get(entry.getKey());
				if (variantSet != null) {
					String variantValue = variantSet.getDefaultVariant();
					if (variantSet.contains(entry.getValue())) {
						variantValue = entry.getValue();
					}

					VariantSet newVariantSet = new VariantSet(variantSet.getType(), variantValue, Arrays.asList(new String[]{variantValue})); 
					tempVariants.put(variantSet.getType(), newVariantSet);
				}
			}
		}

		return getAllVariantKeys(tempVariants);
	}

	/**
	 * Returns the variant keys
	 * 
	 * @param variantKeyPrefix
	 *            the variant key prefix
	 * @param variants
	 *            The variants
	 */
	private static List<String> getVariantKeys(String variantKeyPrefix,
			Collection<String> variants) {

		List<String> variantKeys = new ArrayList<String>();
		for (Iterator<String> itCurVariant = variants.iterator(); itCurVariant
				.hasNext();) {

			String variant = (String) itCurVariant.next();
			if (variant == null) {
				variant = "";
			}
			if (variantKeyPrefix == null) {
				variantKeys.add(variant);
			} else {
				variantKeys.add(variantKeyPrefix + variant);
			}
		}
		return variantKeys;
	}

	/**
	 * Returns the variant key from the variants given in parameter
	 * 
	 * @param variants
	 *            the variants
	 * @return the variant key
	 */
	public static String getVariantKey(Map<String, String> variants) {

		String variantKey = "";
		if (variants != null) {
			variantKey = getVariantKey(variants, variants.keySet());
		}

		return variantKey;
	}

	/**
	 * Resolves a registered path from a locale key, using the same algorithm
	 * used to locate ResourceBundles.
	 * 
	 * @param variantKey
	 *            the requested variant key
	 * @return the variant key to use
	 */
	public static String getVariantKey(Map<String, String> curVariants,
			Set<String> variantTypes) {

		String variantKey = "";
		if (curVariants != null && variantTypes != null) {

			Map<String, String> tempVariants = new TreeMap<String, String>(
					curVariants);
			StringBuffer variantKeyBuf = new StringBuffer();
			for (Iterator<Entry<String, String>> it = tempVariants.entrySet()
					.iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				if (variantTypes.contains(entry.getKey())) {
					String value = entry.getValue();
					if (value == null) {
						value = "";
					}
					variantKeyBuf.append(value
							+ JawrConstant.VARIANT_SEPARATOR_CHAR);
				}
			}

			variantKey = variantKeyBuf.toString();
			if (StringUtils.isNotEmpty(variantKey)
					&& variantKey.charAt(variantKey.length() - 1) == JawrConstant.VARIANT_SEPARATOR_CHAR) {
				variantKey = variantKey.substring(0, variantKey.length() - 1);
			}
		}

		return variantKey;
	}

	/**
	 * Get the bundle name taking in account the variant key
	 * 
	 * @param bundleName
	 *            the bundle name
	 * @param variantKey
	 *            the variant key
	 * @return the variant bundle name
	 */
	public static String getVariantBundleName(String bundleName,
			String variantKey) {

		String newName = bundleName;
		if (StringUtils.isNotEmpty(variantKey)) {
			int idxSeparator = bundleName.lastIndexOf('.');
			if (idxSeparator != -1) {
				newName = bundleName.substring(0, idxSeparator);
				newName += JawrConstant.VARIANT_SEPARATOR_CHAR + variantKey;
				newName += bundleName.substring(idxSeparator);
			} else {
				newName += JawrConstant.VARIANT_SEPARATOR_CHAR + variantKey;
			}
		}

		return newName;
	}

	/**
	 * Returns the bundle name from the variants given in parameter
	 * 
	 * @param bundleName
	 *            the original bundle name
	 * @param variants
	 *            the map of variant
	 * @return the variant bundle name
	 */
	public static String getVariantBundleName(String bundleName,
			Map<String, String> variants) {

		String variantKey = getVariantKey(variants);
		return getVariantBundleName(bundleName, variantKey);
	}

	/**
	 * Concatenates 2 map of variant sets.
	 * 
	 * @param variantSet1
	 *            the first map
	 * @param variantSet2
	 *            the second map
	 * @return the concatenated variant map
	 */
	public static Map<String, VariantSet> concatVariants(
			Map<String, VariantSet> variantSet1,
			Map<String, VariantSet> variantSet2) {

		Map<String, VariantSet> result = new HashMap<String, VariantSet>();
		if (!isEmpty(variantSet1) && isEmpty(variantSet2)) {
			result.putAll(variantSet1);
		} else if (isEmpty(variantSet1) && !isEmpty(variantSet2)) {
			result.putAll(variantSet2);
		} else if (!isEmpty(variantSet1) && !isEmpty(variantSet2)) {

			Set<String> keySet = new HashSet<String>();
			keySet.addAll(variantSet1.keySet());
			keySet.addAll(variantSet2.keySet());
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String variantType = iterator.next();
				VariantSet variants1 = variantSet1.get(variantType);
				VariantSet variants2 = variantSet2.get(variantType);
				Set<String> variants = new HashSet<String>();
				String defaultVariant = null;

				if (variants1 != null && variants2 != null
						&& !variants1.hasSameDefaultVariant(variants2)) {
					throw new BundlingProcessException(
							"For the variant type '"
									+ variantType
									+ "', the variant sets defined in your bundles don't have the same default value.");
				}

				if (variants1 != null) {
					variants.addAll(variants1);
					defaultVariant = variants1.getDefaultVariant();
				}
				if (variants2 != null) {
					variants.addAll(variants2);
					defaultVariant = variants2.getDefaultVariant();
				}

				VariantSet variantSet = new VariantSet(variantType,
						defaultVariant, variants);
				result.put(variantType, variantSet);
			}
		}

		return result;
	}

	/**
	 * Returns true if the map is null or empty
	 * 
	 * @param map
	 *            the map
	 * @return true if the map is null or empty
	 */
	private static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

}
