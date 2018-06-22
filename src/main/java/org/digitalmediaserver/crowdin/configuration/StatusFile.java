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

import static org.digitalmediaserver.crowdin.AbstractCrowdinMojo.isBlank;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.tool.CrowdinFileSystem;


/**
 * A {@link org.apache.maven.plugin.Mojo} configuration class describing a
 * status file.
 *
 * @author Nadahar
 */
public class StatusFile extends AbstractFileSet {

	/**
	 * The full status file path.
	 *
	 * @parameter
	 * @required
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

		// Add comment
		if (addComment == null) {
			addComment = Boolean.TRUE;
		}

		// File type and file type defaults
		if (type == null) {
			int dot = targetFile.lastIndexOf('.');
			if (dot > 0 && dot < targetFile.length() - 1) {
				String extension = targetFile.substring(dot + 1).toLowerCase(Locale.ROOT);
				type = "xml".equals(extension) ? FileType.xml : FileType.properties;
			} else {
				type = FileType.properties;
			}
		}
		switch (type) {
			case properties:
				if (isBlank(encoding)) {
					charset = StandardCharsets.ISO_8859_1;
					encoding = charset.name();
				} else {
					charset = Charset.forName(encoding);
				}
				if (sortLines == null) {
					sortLines = Boolean.TRUE;
				}
				if (escapeUnicode == null) {
					escapeUnicode = Boolean.TRUE;
				}
				break;
			case xml:
				if (isBlank(encoding)) {
					charset = StandardCharsets.UTF_8;
					encoding = charset.name();
				} else {
					charset = Charset.forName(encoding);
				}
				if (sortLines == null) {
					sortLines = Boolean.FALSE;
				}
				if (escapeUnicode == null) {
					escapeUnicode = Boolean.FALSE;
				}
				break;
			default:
				throw new MojoExecutionException("Only properties and XML formats are supported for status files");
		}

		// Target filename
		if (isBlank(targetFile)) {
			throw new MojoExecutionException("\"targetFile\" must be specified for status files");
		}

		targetFile = CrowdinFileSystem.formatPath(targetFile, false);

		super.initializeInstance();
	}
}
