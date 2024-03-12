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
package org.digitalmediaserver.crowdin.configuration;

import static org.digitalmediaserver.crowdin.tool.StringUtil.isBlank;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * An {@code enum} representing the {@code export_patterns} parameter in the
 * {@code add-file} and {@code update-file} API methods.
 *
 * @author Nadahar
 */
public enum PathPlaceholder {

	/** Language name (e.g. {@code Ukrainian}) */
	LANGUAGE("%language%", "([\\w,\\. \\(\\)]+)"),

	/** {@code ISO 639-1} language code (i.e. {@code uk}) */
	TWO_LETTER("%two_letters_code%", "([a-zA-Z]{2})"),

	/** {@code ISO 639-2/T} language code (i.e. {@code ukr}) */
	THREE_LETTER("%three_letters_code%", "([a-zA-Z]{3})"),

	/** Locale with hyphen (like {@code uk-UA}) */
	LOCALE_HYPHEN("%locale%", "([a-z]{2,3}(?:-[A-Z]{2})?)", "([a-z]{2,3})(?:-([A-Z]{2}))?"),

	/** Locale with underscore (i.e. {@code uk_UA}) */
	LOCALE_UNDERSCORE("%locale_with_underscore%", "([a-z]{2,3}(?:_[A-Z]{2})?)", "([a-z]{2,3})(?:_([A-Z]{2}))?"),

	/** Android Locale identifier used to name {@code "values-"} folders */
	ANDROID_CODE("%android_code%"),

	/** macOS Locale identifier used to name {@code ".lproj"} folders */
	MACOS_CODE("%osx_code%"),

	/**
	 * macOS Locale used to name translated resources (i.e. {@code uk},
	 * {@code zh_Hans})
	 */
	MACOS_LOCALE("%osx_locale%"),

	/** Original file name */
	ORIGINAL_FILENAME("%original_file_name%", "([^<>:;,?\"*|/\\\\\\r\\n\\t]+)"),

	/** File name without extension */
	FILENAME("%file_name%", "([^<>:;,?\"*|/\\\\\\r\\n\\t]+)"),

	/** Original file extension */
	FILE_EXTENSION("%file_extension%", "([^<>:;,?\"*|/\\\\\\r\\n\\t\\.]+)"),

	/**
	 * Use parent folders' names in your project to build the file path in the
	 * resulting archive
	 */
	ORIGINAL_PATH("%original_path%", "([^<>:;,?\"*|\\r\\n\\t]+)");

	private static final String BASIC_PATTERN = "(.*?)";
	private final String identifier;
	private final String pattern;
	private final String parsePattern;

	private PathPlaceholder(@Nonnull String identifier) {
		this.identifier = identifier;
		this.pattern = BASIC_PATTERN;
		this.parsePattern = BASIC_PATTERN;
	}

	private PathPlaceholder(@Nonnull String identifier, @Nonnull String pattern) {
		this.identifier = identifier;
		this.pattern = pattern;
		this.parsePattern = pattern;
	}

	private PathPlaceholder(@Nonnull String identifier, @Nonnull String pattern, @Nonnull String parsePattern) {
		this.identifier = identifier;
		this.pattern = pattern;
		this.parsePattern = parsePattern;
	}

	/**
	 * @return The identifier/code for this placeholder.
	 */
	@Nonnull
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return The regular expression used to match this placeholder.
	 */
	@Nonnull
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return The regular expression used to match the value(s) of this
	 *         placeholder.
	 */
	@Nonnull
	public String getParsePattern() {
		return parsePattern;
	}

	/**
	 * Attempts to parse the specified {@link String} value into a
	 * {@link PathPlaceholder}.
	 *
	 * @param value the {@link String} to parse.
	 * @return The corresponding {@link PathPlaceholder} or {@code null} if no
	 *         match was found.
	 */
	@Nullable
	public static PathPlaceholder typeOf(@Nullable String value) {
		if (isBlank(value)) {
			return null;
		}
		for (PathPlaceholder placeholder : values()) {
			if (value.equals(placeholder.identifier)) {
				return placeholder;
			}
		}
		return null;
	}
}
