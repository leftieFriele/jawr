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
package net.jawr.web.resource.bundle.generator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.resource.handler.reader.ResourceReader;
import net.jawr.web.resource.handler.reader.ResourceReaderHandler;
import net.jawr.web.resource.handler.reader.StreamResourceReader;
import net.jawr.web.resource.handler.reader.TextResourceReader;

/**
 * This class defines the Factory which creates a proxy to ResourceReader objects from a Resource generator.
 *  
 * @author Ibrahim Chaehoi
 *
 */
public class ResourceGeneratorReaderProxyFactory {

	/**
	 * Creates a Resource reader from a resource generator.
	 * 
	 * @param generator the resource generator, which can be a text or an image resource generator.
	 * @param rsReaderHandler the resourceReaderHandler
	 * @param config the jawr config
	 * @return
	 */
	public static ResourceReader getResourceReaderProxy(PrefixedResourceGenerator generator, ResourceReaderHandler rsReaderHandler, JawrConfig config){
		
		ResourceReader proxy = null;
		
		// Defines the interfaces to be implemented by the ResourceReader 
		int nbExtraInterface = 0;
		Class[] extraInterfaces = new Class[2];
		boolean isResourceGenerator = generator instanceof ResourceGenerator;
		boolean isStreamResourceGenerator= generator instanceof StreamResourceGenerator;
		
		if(isResourceGenerator){
			extraInterfaces[nbExtraInterface++] = TextResourceReader.class;
		}
		if(isStreamResourceGenerator){
			extraInterfaces[nbExtraInterface++] = StreamResourceReader.class;
		}
		
		Class[] generatorInterfaces = getGeneratorInterfaces(generator);
		Class[] implementedInterfaces = new Class[generatorInterfaces.length+nbExtraInterface];
		for (int i = 0; i < generatorInterfaces.length; i++) {
			implementedInterfaces[i] = generatorInterfaces[i];
		}
		for (int i = 0; i < nbExtraInterface; i++) {
			implementedInterfaces[generatorInterfaces.length+i] = extraInterfaces[i];
		}
		
		// Creates the proxy
		InvocationHandler handler = new ResourceGeneratorReaderWrapperInvocationHandler(generator, rsReaderHandler, config);	
		proxy = (ResourceReader) Proxy.newProxyInstance(ResourceGeneratorReaderProxyFactory.class.getClassLoader(),
					implementedInterfaces,
	                handler);
		
		
		return proxy;
	}

	/**
	 * Returns the array of interfaces implemented by the PrefixedResourceGenerator
	 * @param generator the generator
	 * @return the array of interfaces implemented by the PrefixedResourceGenerator
	 */
	private static Class[] getGeneratorInterfaces(
			PrefixedResourceGenerator generator) {
		
		Set interfaces = new HashSet();
		addInterfaces(generator, interfaces);
		return (Class[]) interfaces.toArray(new Class[]{});
	}

	/**
	 * Adds all the interfaces of the object passed in parameter in the set of interfaces.
	 * @param obj the object
	 * @param interfaces the set of interfaces to update
	 */
	private static void addInterfaces(Object obj,
			Set interfaces) {
		
		Class[] generatorInterfaces = null;
		Class superClass = null;
		if(obj instanceof Class){
			generatorInterfaces = ((Class)obj).getInterfaces();
			superClass = ((Class)obj).getSuperclass();
		}else{
			generatorInterfaces = obj.getClass().getInterfaces();
			superClass = obj.getClass().getSuperclass();
		}
		for (int i = 0; i < generatorInterfaces.length; i++) {
			interfaces.add(generatorInterfaces[i]);
			addInterfaces(generatorInterfaces[i], interfaces);
		}

		if(superClass != null && superClass != Object.class){
			addInterfaces(superClass, interfaces);
		}
	}
	
	/**
	 * This class defines the InvocationHandler which will handle the call to the ResourceReader proxy
	 * 
	 * @author Ibrahim Chaehoi
	 */
	private static class ResourceGeneratorReaderWrapperInvocationHandler implements InvocationHandler {
		
		/** The resource generator wrapped */
		private PrefixedResourceGenerator generator;
		
		/** The resource readre wrapper */
		private ResourceGeneratorReaderWrapper rsReaderWrapper;
		
		/** The resource readre wrapper */
		private StreamResourceGeneratorReaderWrapper streamRsReaderWrapper;
		
		/** The resource reader methods */
		private static Method[] resourceReaderMethods = TextResourceReader.class.getMethods();
		
		/** The stream resource reader methods */
		private static Method[] streamResourceReaderMethods = StreamResourceReader.class.getMethods();
		
		/**
		 * Constructor
		 * @param generator the generator
		 * @param rsReaderHandler the resourceReaderHandler
		 * @param config the jawr config
		 */
		public ResourceGeneratorReaderWrapperInvocationHandler(
				PrefixedResourceGenerator generator, ResourceReaderHandler rsReaderHandler, JawrConfig config) {
			this.generator = generator;
			if(generator instanceof ResourceGenerator){
				this.rsReaderWrapper = new ResourceGeneratorReaderWrapper((ResourceGenerator)generator, rsReaderHandler, config);
			}
			if(generator instanceof StreamResourceGenerator){
				this.streamRsReaderWrapper = new StreamResourceGeneratorReaderWrapper((StreamResourceGenerator)generator, rsReaderHandler, config);
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			
			Object result = null;
			if(rsReaderWrapper != null && methodBelongsTo(method, resourceReaderMethods)){
				result = method.invoke(rsReaderWrapper, args);
			
			}else if(streamRsReaderWrapper != null && methodBelongsTo(method, streamResourceReaderMethods)){
				result = method.invoke(streamRsReaderWrapper, args);
			
			}else{
				result = method.invoke(generator, args);
			}
			return result;
		}
		
		/**
		 * Returns true if the method is in the method array.
		 * @param m the method
		 * @param methods the method array
		 * @return  true if the method is in the method array.
		 */
		private boolean methodBelongsTo(Method m, Method[] methods){
			boolean result = false;
			
			for (int i = 0; i < methods.length && !result; i++) {
				if(methods[i].equals(m)){
					result = true;
				}
			}
			
			return result;
		}
	}
	
	
	
}
