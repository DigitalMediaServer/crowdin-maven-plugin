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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.api.FileType;
import org.digitalmediaserver.crowdin.tool.FileUtil;
import org.digitalmediaserver.crowdin.tool.FileUtil.LetterCase;


/**
 * A {@link org.apache.maven.plugin.Mojo} configuration class describing a
 * status file.
 *
 * @author Nadahar
 */
public class StatusFile extends AbstractFileSet {

	/**
	 * <b>Required</b>. The full status file path.
	 */
	protected String targetFile;

	/**
	 * @return The full status file path.
	 */
	@Nullable
	public String getTargetFile() {
		return targetFile;
	}

	@Override
	protected void initializeInstance() throws MojoExecutionException {

		// Target filename
		if (isBlank(targetFile)) {
			throw new MojoExecutionException("\"targetFile\" must be specified for status files");
		}

		// File type and file type defaults
		if (type == null) {
			String extension = FileUtil.getExtension(targetFile, LetterCase.LOWER, Locale.ROOT);
			type = "json".equals(extension) ? FileType.json : FileType.properties;
		}
		switch (type) {
			case properties:
				if (isBlank(encoding)) {
					charset = StandardCharsets.ISO_8859_1;
					encoding = charset.name();
				} else {
					charset = Charset.forName(encoding);
				}
				if (addComment == null) {
					addComment = Boolean.TRUE;
				}
				if (sortLines == null) {
					sortLines = Boolean.TRUE;
				}
				if (escapeUnicode == null) {
					escapeUnicode = Boolean.TRUE;
				}
				break;
			case json:
				if (isBlank(encoding)) {
					charset = StandardCharsets.UTF_8;
					encoding = charset.name();
				} else {
					charset = Charset.forName(encoding);
				}
				if (addComment == null) {
					addComment = Boolean.FALSE;
				}
				break;
			default:
				throw new MojoExecutionException("Only properties and JSON formats are supported for status files");
		}

		targetFile = FileUtil.formatPath(targetFile, false);

		super.initializeInstance();
	}
}
