/*
 * Crowdin Maven Plugin, an Apache Maven plugin for synchronizing translation
 * files using the crowdin.com API.
 * Copyright (C) 2024 Digital Media Server developers
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

import javax.annotation.Nullable;


/**
 * A utility class containing string related routines.
 *
 * @author Nadahar
 */
public class StringUtil {

	/**
	 * Not to be instantiated.
	 */
	private StringUtil() {
	}

	/**
	 * Evaluates if the specified character sequence is {@code null}, empty or
	 * only consists of whitespace.
	 *
	 * @param cs the {@link CharSequence} to evaluate.
	 * @return {@code false} if {@code cs} is {@code null}, empty or only consists of
	 *         whitespace, {@code true} otherwise.
	 */
	public static boolean isNotBlank(@Nullable CharSequence cs) {
		return !isBlank(cs);
	}

	/**
	 * Evaluates if the specified character sequence is {@code null}, empty or
	 * only consists of whitespace.
	 *
	 * @param cs the {@link CharSequence} to evaluate.
	 * @return {@code true} if {@code cs} is {@code null}, empty or only
	 *         consists of whitespace, {@code false} otherwise.
	 */
	public static boolean isBlank(@Nullable CharSequence cs) {
		if (cs == null) {
			return true;
		}
		int strLen = cs.length();
		if (strLen == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
