package org.objenesis;

import java.rmi.dgc.VMID;

/**
 * Guess the best instantiator for a given class. Currently, the selection doesn't depend on the class. It relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 *
 * @see ObjectInstantiator
 */
public class StdInstantiatorStrategy extends BaseInstantiatorStrategy {

	public ObjectInstantiator newInstantiatorOf(Class type) {

      if(JVM_NAME.startsWith(SUN)) {
         if(VM_VERSION.startsWith("1.3")) {
            return new Sun13Instantiator(type);
         }       
      }
      else if(JVM_NAME.startsWith(JROCKIT)) {
         if(VM_VERSION.startsWith("1.3")) {
            return new JRockit131Instantiator(type);
         }
         else if(VM_VERSION.startsWith("1.4")) {
        	 // JRockit vendor version will be RXX where XX is the version
            // Versions prior to 26 need special handling
        	// From R26 on, java.vm.version starts with R
        	if(!VENDOR_VERSION.startsWith("R")) {
        		// On R25.1 and R25.2, ReflectionFactory should work. Otherwise, we must use the Legacy instantiator.
        		if(VM_INFO == null || !VM_INFO.startsWith("R25.1") || !VM_INFO.startsWith("R25.2")) {
        			return new JRockitLegacyInstantiator(type);	
        		}
        	}
         }
      }
      else if(JVM_NAME.startsWith(GNU)) {
         return new GCJInstantiator(type);
      }
		
      // Fallback instantiator, should work with:
      // - Java Hotspot version 1.4 and higher
		// - JRockit 1.4-R26 and higher
		// - IBM and Hitachi JVMs
      // ... might works for others so we just give it a try 
		return new SunReflectionFactoryInstantiator(type);
	}
}