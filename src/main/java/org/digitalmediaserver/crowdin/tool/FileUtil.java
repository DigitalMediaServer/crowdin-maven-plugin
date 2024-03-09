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

import static org.digitalmediaserver.crowdin.tool.StringUtil.isBlank;
import static org.digitalmediaserver.crowdin.tool.StringUtil.isNotBlank;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;


/**
 * A utility class containing file related routines.
 *
 * @author Nadahar
 */
public class FileUtil {

	/**
	 * Not to be instantiated.
	 */
	private FileUtil() {
	}

	/**
	 * Splits a file path into its elements, optionally omitting the filename
	 * (whatever is after the last separator) if there is one.
	 *
	 * @param path the file path to split.
	 * @param omitFilename whether or not to omit the last "element" (after the
	 *            last separator).
	 * @return The {@link List} of folder elements.
	 */
	@Nonnull
	public static List<String> splitPath(@Nullable String path, boolean omitFilename) {
		if (path == null || path.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>();
		int len = path.length();
		int i = 0;
		int start = 0;
		boolean match = false;
		char c;
		while (i < len) {
			if ((c = path.charAt(i)) == '/' || c == '\\') {
				if (match) {
					result.add(path.substring(start, i));
					match = false;
				}
				start = ++i;
				continue;
			}
			match = true;
			i++;
		}
		if (!omitFilename && match) {
			result.add(path.substring(start, i));
		}
		return result;
	}

	/**
	 * Returns the file extension from {@code fileName} or {@code null} if
	 * {@code fileName} has no extension.
	 *
	 * @param fileName the file name from which to extract the extension.
	 * @param convertTo if {@code null} makes no letter case change to the
	 *            returned {@link String}, otherwise converts the extracted
	 *            extension (if any) to the corresponding letter case.
	 * @param locale the {@link Locale} to use for letter case conversion.
	 *            Defaults to {@link Locale#ROOT} if {@code null}.
	 * @return The extracted and potentially letter case converted extension or
	 *         {@code null}.
	 */
	@Nullable
	public static String getExtension(@Nullable String fileName, @Nullable LetterCase convertTo, @Nullable Locale locale) {
		if (isBlank(fileName)) {
			return null;
		}

		int point = getExtensionIndex(fileName);
		if (point == -1) {
			return null;
		}
		if (convertTo != null && locale == null) {
			locale = Locale.ROOT;
		}

		String extension = fileName.substring(point + 1);
		if (convertTo == LetterCase.UPPER) {
			return extension.toUpperCase(locale);
		}
		if (convertTo == LetterCase.LOWER) {
			return extension.toLowerCase(locale);
		}
		return extension;
	}

	/**
	 * Finds the index of the "extension dot" in the specified string. This is
	 * the last {@code .} in the string that is <i>not</i> the last character
	 * and that comes after the last separator.
	 *
	 * @param filePath the file path to process.
	 * @return The index of the extension {@code .} or {@code -1} if none could
	 *         be found.
	 */
	public static int getExtensionIndex(@Nullable String filePath) {
		if (filePath == null || filePath.length() < 2) {
			return -1;
		}
		char[] filePathArray = filePath.toCharArray();
		for (int i = filePathArray.length - 1; i >= 0; i--) {
			switch (filePathArray[i]) {
				case '.':
					return i == filePathArray.length - 1 ? -1 : i;
				case '/':
				case '\\':
					return -1;
			}
		}
		return -1;
	}

	/**
	 * Appends a path separator of the same type at the last position in the
	 * specified path if it's not already there, or {@code /} if there are none,
	 * unless the path is blank.
	 *
	 * @param path the path to be modified.
	 * @return The corrected path or {@code null} if {@code path} is
	 *         {@code null}.
	 */
	@Nullable
	public static String appendPathSeparator(@Nullable String path) {
		if (isBlank(path)) {
			return path;
		}
		char c;
		int len = path.length();
		if ((c = path.charAt(len - 1)) == '/' || c == '\\') {
			return path;
		}
		boolean found = false;
		for (int i = len - 2; i >= 0; i--) {
			if ((c = path.charAt(i)) == '/' || c == '\\') {
				found = true;
				break;
			}
		}
		if (!found) {
			c = '/';
		}
		return path + c;
	}

	/**
	 * Formats a {@link Path} by converting backslashes to slashes and
	 * optionally appends a slash to the end of the {@link Path}.
	 *
	 * @param path the {@link Path} to format.
	 * @param appendSeparator if {@code true} a slash will be appended to the
	 *            {@link Path}.
	 * @return The formatted file path or {@code null} if {@code path} was
	 *         {@code null}.
	 */
	@Nullable
	public static String formatPath(@Nullable Path path, boolean appendSeparator) {
		return path == null ? null : formatPath(path.toString(), appendSeparator);
	}

	/**
	 * Formats a file path by converting backslashes to slashes and optionally
	 * appends a slash to the end of the path if it's not already present.
	 *
	 * @param path the file path to format.
	 * @param appendSeparator if {@code true} a slash will be appended to the
	 *            path if one isn't already there.
	 * @return The formatted file path or {@code null} if {@code path} was
	 *         {@code null}.
	 */
	@Nullable
	public static String formatPath(@Nullable String path, boolean appendSeparator) {
		if (path == null) {
			return null;
		}
		path = path.replace('\\', '/');
		return appendSeparator ? appendPathSeparator(path) : path;
	}

	/**
	 * Determines and returns the push file folder by taking
	 * {@link TranslationFileSet#crowdinPath} (optionally) and any folders in
	 * {@link TranslationFileSet#baseFileName} into account. The resulting path
	 * will use {@code /} as a separator and will end with a separator if it has
	 * at least one element.
	 *
	 * @param fileSet the {@link TranslationFileSet} for which to find the
	 *            Crowdin push folder.
	 * @param includeCrowdinPath {@code true} to include
	 *            {@link TranslationFileSet#crowdinPath} in the returned path,
	 *            {@code false} otherwise.
	 * @return The Crowdin push folder path or an empty string if the
	 *         {@link TranslationFileSet} is placed in the Crowdin root.
	 */
	@Nonnull
	public static String getPushFolder(@Nonnull TranslationFileSet fileSet, boolean includeCrowdinPath) {
		ArrayList<String> folders = new ArrayList<>();
		String crowdinPath, baseFileName;
		if (includeCrowdinPath && isNotBlank(crowdinPath = fileSet.getCrowdinPath())) {
			folders.addAll(splitPath(crowdinPath, false));
		}
		if (isNotBlank(baseFileName = fileSet.getBaseFileName())) {
			folders.addAll(splitPath(baseFileName, true));
		}
		StringBuilder sb = new StringBuilder();
		for (String folder : folders) {
			if (sb.length() > 0) {
				sb.append('/');
			}
			sb.append(folder);
		}
		if (!folders.isEmpty()) {
			sb.append('/');
		}
		return sb.toString();
	}

	/**
	 * An {@code enum} representing letter cases.
	 */
	public enum LetterCase {

		/** Upper-case, uppercase, capital or majuscule */
		UPPER,

		/** Lower-case, lowercase or minuscule */
		LOWER
	}
}
