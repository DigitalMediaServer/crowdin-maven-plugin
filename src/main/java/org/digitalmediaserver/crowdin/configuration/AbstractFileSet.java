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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import javax.annotation.Nullable;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.api.FileType;


/**
 * An abstract {@link org.apache.maven.plugin.Mojo} configuration class
 * describing a set of files.
 *
 * @author Nadahar
 */
public abstract class AbstractFileSet {

	/**
	 * The encoding to use when deploying the translation files. This can either
	 * be a valid {@link Charset} name, or the special value
	 * {@code "Properties"}. Encoding is often determined by {@link #type} and
	 * only need to be explicitly set to deviate from the default.
	 * <p>
	 * The {@code "Properties"} maps to {@link StandardCharsets#ISO_8859_1}.
	 * Together with {@link #escapeUnicode} == {@code true}, any characters that
	 * don't exist in ISO 8859-1 will be encoded as &#92;u{@code <xxxx>} where
	 * {@code <xxxx>} is the hexadecimal Unicode value.
	 *
	 * @parameter
	 */
	@Nullable
	protected String encoding;

	/**
	 * Whether or not the language strings should be sorted by their key in the
	 * translation files when exporting them from Crowdin. Mostly useful for
	 * {@link Properties} files. Defaults to {@code true} if {@code encoding} is
	 * {@code "Properties"}, {@code false} otherwise.
	 *
	 * @parameter
	 */
	@Nullable
	protected Boolean sortLines;

	/**
	 * Whether or not to add a comment header to the files when exporting them
	 * from Crowdin. If no custom comment is provided, a generic "do not modify"
	 * comment will be added.
	 *
	 * @parameter default-value="true"
	 */
	@Nullable
	protected Boolean addComment;

	/**
	 * The custom comment header to add to translation files when exporting them
	 * from Crowdin if {@link #addComment} is {@code true}. If not configured, a
	 * generic "do not modify" comment will be added.
	 *
	 * @parameter
	 */
	@Nullable
	protected String comment;

	/**
	 * The string to use as line separator when exporting files from Crowdin.
	 * Specify \n, \r or \r\n as needed. If not specified, the default will be
	 * used.
	 *
	 * @parameter
	 */
	@Nullable
	protected String lineSeparator;

	/**
	 * Whether or not to encode Unicode characters in the form "&#92;uxxxx" when
	 * exporting files from Crowdin. This setting only applies to
	 * {@link FileType#properties} file sets.
	 *
	 * @parameter default-value="true"
	 */
	@Nullable
	protected Boolean escapeUnicode;

	/**
	 * The {@link FileType} for this fileset. If not specified,
	 * auto-detection will be attempted with fall-back to
	 * {@link FileType#auto}.
	 *
	 * @parameter
	 */
	@Nullable
	protected FileType type;

	/**
	 * A list of {@link Conversion} elements to apply to the
	 * translation file names.
	 *
	 * @parameter
	 */
	@Nullable
	protected List<Conversion> conversions;

	/**
	 * For internal use.
	 */
	@Nullable
	protected Charset charset;

	/**
	 * @return The character encoding to convert translation files to when
	 *         exporting them from Crowdin or {@code null} if not set.
	 */
	@Nullable
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the character encoding to convert translation files to
	 *            when exporting them from Crowdin or {@code null} to use the
	 *            default.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return The Charset to convert translation files to when exporting them
	 *         from Crowdin or {@code null} to use the default.
	 */
	@Nullable
	public Charset getCharset() {
		return charset;
	}

	/**
	 * @param charset the {@link Charset} to convert translation files to when
	 *            exporting them from Crowdin or {@code null} to use the
	 *            default.
	 */
	public void setCharset(@Nullable Charset charset) {
		this.charset = charset;
	}

	/**
	 * @return {@code true} if lines should be sorted when exporting files from
	 *         Crowdin, {@code false} otherwise.
	 */
	@Nullable
	public Boolean getSortLines() {
		return sortLines;
	}

	/**
	 * @return {@code true} if a comment should be added at the top of the
	 *         translated files when exporting them from Crowdin, {@code false}
	 *         if it should not or {@code null} if not specified.
	 */
	@Nullable
	public Boolean getAddComment() {
		return addComment;
	}

	/**
	 * @return The custom comment header to add to translation files when
	 *         exporting them from Crowdin, or {@code null} if the default
	 *         should be used.
	 */
	@Nullable
	public String getComment() {
		return comment;
	}

	/**
	 * @return The {@link String} to use as line separator when exporting files
	 *         from Crowdin or {@code null} to use the default.
	 */
	@Nullable
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * @return Whether to escape Unicode characters with "&#92;uxxxx" in
	 *         {@link FileType#properties} files when exporting them from
	 *         Crowdin, or {@code null} if not set.
	 */
	@Nullable
	public Boolean getEscapeUnicode() {
		return escapeUnicode;
	}

	/**
	 * @return The {@link FileType}.
	 */
	@Nullable
	public FileType getType() {
		return type;
	}

	/**
	 * @param type the {@link FileType} to set.
	 */
	public void setType(@Nullable FileType type) {
		this.type = type;
	}

	/**
	 * @return The {@link List} of {@link Conversion}s.
	 */
	@Nullable
	public List<Conversion> getConversions() {
		return conversions;
	}

	/**
	 * Since the constructor is called automagically by Maven, verification and
	 * initialization of defaults is done here.
	 *
	 * @param fileSets the {@link List} of file sets to initialize.
	 * @throws MojoExecutionException If the initialization fails.
	 */
	public static void initialize(@Nullable List<? extends AbstractFileSet> fileSets) throws MojoExecutionException {
		if (fileSets == null || fileSets.isEmpty()) {
			return;
		}
		for (AbstractFileSet fileSet : fileSets) {
			fileSet.initializeInstance();
		}
	}

	/**
	 * Since the constructor is called automagically by Maven, verification and
	 * initialization of defaults is done here.
	 *
	 * @throws MojoExecutionException If the initialization fails.
	 */
	protected void initializeInstance() throws MojoExecutionException {
		if (conversions != null && !conversions.isEmpty()) {
			for (Conversion conversion : conversions) {
				if (conversion.getFrom() == null || conversion.getFrom().isEmpty() || conversion.getTo() == null) {
					String from = conversion.getFrom() == null ? "null" : "\"" + conversion.getFrom() + "\"";
					String to = conversion.getTo() == null ? "null" : "\"" + conversion.getTo() + "\"";
					throw new MojoExecutionException(
						"Invalid conversion: \"" + from + " -> " + to + "\" in file set \"" + toString() + "\""
					);
				}
			}
		}
	}
}
