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

import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

/**
 * A utility class containing Git operations.
 *
 * @author Nadahar
 */
public class GitUtil {

	/**
	 * Not to be instantiated.
	 */
	private GitUtil() {
	}

	/**
	 * Get the current Git branch.
	 *
	 * @param projectBasedir the project "root" folder.
	 * @param logger the {@link Log} to use for logging.
	 * @return The name of the current Git branch or {@code null} if it couldn't
	 *         be established.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	public static String getBranch(@Nonnull File projectBasedir, Log logger) throws MojoExecutionException {
		if (!projectBasedir.exists()) {
			if (logger != null) {
				logger.warn("Project basedir (" + projectBasedir + ") doesn't exist - cannot determine git branch");
			}
			return null;
		} else if (!projectBasedir.isDirectory()) {
			if (logger != null) {
				logger.warn("Project basedir (" + projectBasedir + ") must be a folder - cannot determine git branch");
			}
			return null;
		}

		Git git;
		try {
			if (logger != null) {
				logger.debug("Trying to read \"" + projectBasedir + "\" with git");
			}
			git = Git.open(projectBasedir);
			Repository repo = git.getRepository();
			try {
				String branch = repo.getBranch();
				if (repo.getRef(Constants.HEAD).getTarget().getName().endsWith(branch)) {
					if (logger != null) {
						logger.debug("Git branch determined to be \"" + branch + "\"");
					}
					return branch;
				}
				if (logger != null) {
					logger.error("Git branch was reported to be \"" + branch + "\" which means that HEAD is detached");
				}
			} catch (IOException e) {
				throw new MojoExecutionException("An error occurred while reading git branch: " + e.getMessage(), e);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(
				"An error occurred while opening project basedir with git: " + e.getMessage(),
				e
			);
		}

		if (logger != null) {
			logger.error("Can't determine git branch");
		}
		return null;
	}
}
