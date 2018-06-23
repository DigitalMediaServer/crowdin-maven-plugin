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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;


/**
 * A {@link OrderedProperties} implementation that sorts the elements by key by
 * grouping the keys into groups separated by {@code "."}. Integer groups are
 * sorted numerically, all other groups are sorted lexicographically. When
 * comparing an integer group with a non-integer group, integer groups are
 * sorted last.
 * <p>
 * The elements are sorted when {@link #iterator()} is called instead of every
 * time a key/value pair is added.
 *
 * @author Nadahar
 */
public class GroupSortedProperties extends OrderedProperties {

	private static final long serialVersionUID = 1L;

	/**
	 * The static {@link EntryComparator} instance.
	 */
	protected static final EntryComparator COMPARATOR = new EntryComparator();

	private final ArrayList<Map.Entry<String, String>> storage = new ArrayList<>();

	@Override
	public Iterator<Map.Entry<String, String>> iterator() {
		Collections.sort(storage, COMPARATOR);
		return storage.iterator();
	}

	@Override
	public String put(String key, String value) {
		for (Map.Entry<String, String> entry : storage) {
			if (Objects.equals(key, entry.getKey())) {
				return entry.setValue(value);
			}
		}
		storage.add(new Entry(key, value));
		return null;
	}

	/**
	 * A {@link Comparator} that sorts {@link Entry} elements by key by grouping
	 * the keys into groups separated by {@code "."}. Integer groups are sorted
	 * numerically, all other groups are sorted lexicographically. When
	 * comparing an integer group with a non-integer group, integer groups are
	 * sorted last.
	 *
	 * @author Nadahar
	 */
	protected static class EntryComparator implements Comparator<Map.Entry<String, String>>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
			if (o1 == null || o2 == null) {
				return o1 == null && o2 == null ? 0 : o1 == null ? 1 : -1;
			}

			String s1 = o1.getKey();
			String s2 = o2.getKey();
			if (s1 == null || s2 == null) {
				return s1 == null && s2 == null ? 0 : s1 == null ? 1 : -1;
			}
			int s1start = 0;
			int s2start = 0;
			int s1end = 0;
			int s2end = 0;
			int result = 0;
			while (result == 0 && (s1start < s1.length() || s2start < s2.length())) {
				while (s1start < s1.length() && Character.isWhitespace(s1.charAt(s1start))) {
					s1start++;
				}
				while (s2start < s2.length() && Character.isWhitespace(s2.charAt(s2start))) {
					s2start++;
				}
				boolean s1IsNumber = true;
				boolean s1HasDigit = false;
				boolean s2IsNumber = true;
				boolean s2HasDigit = false;
				s1end = s1start;
				s2end = s2start;
				while (s1end < s1.length() && s1.charAt(s1end) != '.') {
					boolean isDigit = Character.isDigit(s1.charAt(s1end));
					s1IsNumber &= isDigit;
					s1HasDigit |= isDigit;
					s1end++;
				}
				while (s2end < s2.length() && s2.charAt(s2end) != '.') {
					boolean isDigit = Character.isDigit(s2.charAt(s2end));
					s2IsNumber &= isDigit;
					s2HasDigit |= isDigit;
					s2end++;
				}
				String s1Part = s1.substring(s1start, s1end);
				String s2Part = s2.substring(s2start, s2end);
				if (s1IsNumber && s1HasDigit && s2IsNumber && s2HasDigit) {
					// Compare numerically
					result = Integer.parseInt(s1Part) - Integer.parseInt(s2Part);
				} else if (s1IsNumber && s1HasDigit) {
					return 1;
				} else if (s2IsNumber && s2HasDigit) {
					return -1;
				} else {
					// Compare lexicographically
					result = s1Part.compareTo(s2Part);
				}
				s1start = s1end == s1.length() ? s1end : s1end + 1;
				s2start = s2end == s2.length() ? s2end : s2end + 1;
			}

			return result;
		}
	}

	/**
	 * A {@link Serializable} {@link Entry} implementation that has an
	 * accessible constructor.
	 *
	 * @author Nadahar
	 */
	protected static class Entry implements Map.Entry<String, String>, Serializable {
		private static final long serialVersionUID = 1L;
		private final String key;
		private String value;

		/**
		 * Creates a new instance with the specified key and value.
		 *
		 * @param key the key.
		 * @param value the value.
		 */
		public Entry(@Nullable String key, @Nullable String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public String setValue(String value) {
			String oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Entry)) {
				return false;
			}
			Entry other = (Entry) obj;
			if (key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!key.equals(other.key)) {
				return false;
			}
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}
}
