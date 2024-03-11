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
package org.digitalmediaserver.crowdin.api;

import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.configuration.PathPlaceholder;
import com.google.gson.annotations.SerializedName;


/**
 * This class is used for serialization and deserialization of JSON objects when
 * communicating with Crowdin's v2 API. It's a combination of several API
 * classes, and thus represents {@code GeneralFileExportOptions},
 * {@code PropertyFileExportOptions} and {@code JavaScriptFileExportOptions}.
 * <p>
 * To make sure that only valid parameters are used for a given
 * {@link FileType}, call {@link #validate(FileType)}.
 *
 * @author Nadahar
 */
public class FileExportOptions {

	/**
	 * File export pattern. Defines file name and path in resulting translations
	 * bundle. This option is called "Resulting file after translation export"
	 * in the Crowdin UI.
	 * <p>
	 * Specify the file name or full path in the resulting archive using
	 * {@link PathPlaceholder}. For example, the source file can be
	 * {@code Resources.resx}, but before integration into an application, it
	 * should be named {@code Resources.uk-UA.resx}.
	 * <p>
	 * <b>Note:</b> Can't contain {@code : * ? \" < > |} symbols.
	 */
	@Nullable
	private String exportPattern;

	// "PropertyFileExportOptions" class only

	/**
	 * The {@code escapeQuotes} API parameter to use.
	 * <p>
	 * <b>Only valid for</b> {@link FileType#properties} and
	 * {@link FileType#properties_play}.
	 * <p>
	 * Valid values are:
	 * <ul>
	 * <li>{@code 0} — Do not escape single quote</li>
	 * <li>{@code 1} — Escape single quote by another single quote</li>
	 * <li>{@code 2} — Escape single quote by backslash</li>
	 * <li>{@code 3} — Escape single quote by another single quote only in strings
	 * containing variables (<code>{0}</code>)</li>
	 * </ul>
	 */
	@Nullable
	private Integer escapeQuotes;

	/**
	 * Defines whether any special characters ({@code =}, {@code :}, {@code !}
	 * and {@code #}) should be escaped by backslash in exported translations.
	 * You can add {@code escapeSpecialCharacters} per-file option.
	 * <p>
	 * <b>Only valid for</b> {@link FileType#properties} and
	 * {@link FileType#properties_play}.
	 * <p>
	 * Valid values are:
	 * <ul>
	 * <li>{@code 0} - Do not escape special characters (<b>default</b>)</li>
	 * <li>{@code 1} - Escape special characters by a backslash</li>
	 * </ul>
	 */
	@Nullable
	private Integer escapeSpecialCharacters;

	// "JavaScriptFileExportOptions" class only

	/**
	 * Defines how to quote JavaScript output.
	 * <p>
	 * <b>Only valid for</b> {@link FileType#js}.
	 * <p>
	 * Valid values are:
	 * <ul>
	 * <li>{@code single} - Output will be enclosed in single quotes
	 * (<b>default</b>)</li>
	 * <li>{@code double} - Output will be enclosed in double quotes</li>
	 * </ul>
	 */
	@Nullable
	private JavaScriptExportQuotes exportQuotes;

	/**
	 * Creates a new instance.
	 */
	public FileExportOptions() {
	}

	/**
	 * Validates that no option has been set that is invalid for the specified
	 * {@link FileType}.
	 * <p>
	 * <b>Warning</b>: If {@code fileType} is {@code null} or
	 * {@link FileType#auto}, no validation is performed.
	 *
	 * @param fileType the {@link FileType} for which to validate.
	 *
	 * @throws MojoExecutionException If validation fails.
	 */
	public void validate(@Nullable FileType fileType) throws MojoExecutionException {
		if (fileType == null || fileType == FileType.auto) {
			return;
		}

		if (escapeQuotes != null && fileType != FileType.properties) {
			throw new MojoExecutionException(
				"FileExportOptions validation failed: escapeQuotes is only valid for Properties files"
			);
		}
		if (escapeSpecialCharacters != null && fileType != FileType.properties) {
			throw new MojoExecutionException(
				"FileExportOptions validation failed: escapeSpecialCharacters is only valid for Properties files"
			);
		}
	}

	/**
	 * @return The {@code exportPattern} value.
	 *
	 * @see #exportPattern
	 */
	@Nullable
	public String getExportPattern() {
		return exportPattern;
	}

	/**
	 * Sets the {@code exportPattern} value.
	 *
	 * @param exportPattern the {@code exportPattern} value to set.
	 *
	 * @see #exportPattern
	 */
	public void setExportPattern(@Nullable String exportPattern) {
		this.exportPattern = exportPattern;
	}

	/**
	 * @return The {@code escapeQuotes} value.
	 *
	 * @see #escapeQuotes
	 */
	public Integer getEscapeQuotes() {
		return escapeQuotes;
	}

	/**
	 * Sets the {@code escapeQuotes} value.
	 *
	 * @param escapeQuotes the {@code escapeQuotes} value to set.
	 *
	 * @see #escapeQuotes
	 */
	public void setEscapeQuotes(Integer escapeQuotes) {
		this.escapeQuotes = escapeQuotes;
	}

	/**
	 * @return The {@code escapeSpecialCharacters} value.
	 *
	 * @see #escapeSpecialCharacters
	 */
	public Integer getEscapeSpecialCharacters() {
		return escapeSpecialCharacters;
	}

	/**
	 * Sets the {@code escapeSpecialCharacters} value.
	 *
	 * @param escapeSpecialCharacters the {@code escapeSpecialCharacters} value
	 *            to set.
	 *
	 * @see #escapeSpecialCharacters
	 */
	public void setEscapeSpecialCharacters(Integer escapeSpecialCharacters) {
		this.escapeSpecialCharacters = escapeSpecialCharacters;
	}

	/**
	 * @return The {@code exportQuotes} value.
	 *
	 * @see #exportQuotes
	 */
	public JavaScriptExportQuotes getExportQuotes() {
		return exportQuotes;
	}

	/**
	 * Sets the {@code exportQuotes} value.
	 *
	 * @param exportQuotes the {@link JavaScriptExportQuotes} value to set.
	 *
	 * @see #exportQuotes
	 */
	public void setExportQuotes(JavaScriptExportQuotes exportQuotes) {
		this.exportQuotes = exportQuotes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(escapeQuotes, escapeSpecialCharacters, exportPattern, exportQuotes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FileExportOptions)) {
			return false;
		}
		FileExportOptions other = (FileExportOptions) obj;
		return
			Objects.equals(escapeQuotes, other.escapeQuotes) &&
			Objects.equals(escapeSpecialCharacters, other.escapeSpecialCharacters) &&
			Objects.equals(exportPattern, other.exportPattern) &&
			exportQuotes == other.exportQuotes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FileExportOptions [");
		if (exportPattern != null) {
			sb.append("exportPattern=").append(exportPattern).append(", ");
		}
		if (escapeQuotes != null) {
			sb.append("escapeQuotes=").append(escapeQuotes).append(", ");
		}
		if (escapeSpecialCharacters != null) {
			sb.append("escapeSpecialCharacters=").append(escapeSpecialCharacters).append(", ");
		}
		if (exportQuotes != null) {
			sb.append("exportQuotes=").append(exportQuotes);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Represents the Crowdin API {@code exportQuotes} JavaScript export
	 * options.
	 */
	public enum JavaScriptExportQuotes {

		/** <b>Default</b> Output will be enclosed in single quotes */
		@SerializedName("single")
		SINGLE,

		/** Output will be enclosed in double quotes */
		@SerializedName("double")
		DOUBLE;
	}
}
