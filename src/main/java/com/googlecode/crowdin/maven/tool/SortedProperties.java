package com.googlecode.crowdin.maven.tool;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

// http://www.rgagnon.com/javadetails/java-0614.html
public class SortedProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7676620742633491575L;

	/**
	 * Overrides, called by the store method.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector keyList = new Vector();
		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}
		Collections.sort(keyList);
		return keyList.elements();
	}

}
