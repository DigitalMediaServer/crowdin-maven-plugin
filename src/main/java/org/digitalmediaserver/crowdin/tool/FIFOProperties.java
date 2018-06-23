/*
 * Crowdin Maven Plugin, an Apache Maven plugin for synchronizing translation
 * files using the crowdin.com API.
 * Copyright (C) 2018 Digital Media Server developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.digitalmediaserver.crowdin.tool;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


/**
 * A {@link OrderedProperties} implementation that retains the order of the
 * elements in which they were added. Replacing values does not affect the
 * order.
 *
 * @author Nadahar
 */
public class FIFOProperties extends OrderedProperties {

	private static final long serialVersionUID = 1L;

	private final LinkedHashMap<String, String> storage = new LinkedHashMap<>();

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return storage.entrySet().iterator();
	}

	@Override
	public String put(String key, String value) {
		return storage.put(key, value);
	}
}
