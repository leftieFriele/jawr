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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class defines the variant set for a variant type.
 * A variant set defines for a type of variant what are the available variant values,
 * and which one is used by default.
 * 
 * @author Ibrahim Chaehoi
 *
 */
public class VariantSet implements Set {

	/** The variant set type */
	private String type;
	
	/** The default variant */
	private String defaultVariant;
	
	/** The variant set */
	private Set variants;
	
	/**
	 * Constructor
	 * @param type the variant type
	 * @param defaultVariant the default variant
	 * @param variants the variant set
	 */
	public VariantSet(String type, String defaultVariant, String[] variants){
		
		this(type, defaultVariant, Arrays.asList(variants));
	}
	
	/**
	 * Constructor
	 * @param type the variant type
	 * @param defaultVariant the default variant
	 * @param variants the variant set
	 */
	public VariantSet(String type, String defaultVariant, Collection variants){
		
		if(!variants.contains(defaultVariant)){
			throw new IllegalArgumentException("For the variant type '"+type+"', the default variant '"+defaultVariant+"' doesn't exist in the variant set "+variants+".");
		}
		
		this.type = type;
		this.defaultVariant = defaultVariant;
		this.variants = new HashSet(variants);
	}
	
	/**
	 * Constructor
	 * @param type the variant type
	 * @param defaultVariant the default variant
	 * @param variants the variant set
	 */
	public VariantSet(String type, String defaultVariant, Set variants){
		
		if(!variants.contains(defaultVariant)){
			throw new IllegalArgumentException("For the variant type '"+type+"', the default variant '"+defaultVariant+"' doesn't exist in the variant set "+variants+".");
		}
		
		this.type = type;
		this.defaultVariant = defaultVariant;
		this.variants = variants;
	}

	/**
	 * Returns the variant type
	 * @return the variant type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the default variant
	 * @return the defaultVariant
	 */
	public String getDefaultVariant() {
		return defaultVariant;
	}

	/**
	 * Returns the variant set
	 * @return the variants
	 */
	public Set getVariants() {
		return variants;
	}

	/**
	 * Returns true of the variantSet passed in parameter has the same default value
	 * @param obj the variantSet to test
	 * @return  true of the variantSet passed in parameter has the same default value
	 */
	public boolean hasSameDefaultVariant(VariantSet obj){
		
		return (this.defaultVariant == null && obj.defaultVariant == null)
				|| (this.defaultVariant != null &&
						this.defaultVariant.equals(obj.defaultVariant));
	}
	
	/* (non-Javadoc)
	 * @see java.util.Set#add(E)
	 */
	public boolean add(Object arg0) {
		return variants.add(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection arg0) {
		return variants.addAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		variants.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return variants.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection coll) {
		return variants.containsAll(coll);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return variants.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	public Iterator iterator() {
		return variants.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return variants.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection coll) {
		return variants.removeAll(coll);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection coll) {
		return variants.retainAll(coll);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	public int size() {
		return variants.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		return variants.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray(T[])
	 */
	public Object[] toArray(Object[] arg0) {
		return variants.toArray(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultVariant == null) ? 0 : defaultVariant.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((variants == null) ? 0 : variants.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariantSet other = (VariantSet) obj;
		if (defaultVariant == null) {
			if (other.defaultVariant != null)
				return false;
		} else if (!defaultVariant.equals(other.defaultVariant))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (variants == null) {
			if (other.variants != null)
				return false;
		} else if (!variants.equals(other.variants))
			return false;
		return true;
	}
	
	
}
