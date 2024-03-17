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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	 * Gets the current Git branch.
	 *
	 * @param gitBaseFolder the Git repository "root" folder.
	 * @param logger the {@link Log} to use for logging.
	 * @return The name of the current Git branch or {@code null} if it couldn't
	 *         be determined.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nullable
	public static String getBranch(@Nonnull Path gitBaseFolder, Log logger) throws MojoExecutionException {
		if (!Files.exists(gitBaseFolder)) {
			if (logger != null) {
				logger.warn("Git base folder (" + gitBaseFolder.toString() + ") doesn't exist - cannot determine git branch");
			}
			return null;
		} else if (!Files.isDirectory(gitBaseFolder)) {
			if (logger != null) {
				logger.warn("Git base folder (" + gitBaseFolder.toString() + ") must be a folder - cannot determine git branch");
			}
			return null;
		}

		Git git;
		try {
			if (logger != null && logger.isDebugEnabled()) {
				logger.debug("Trying to read \"" + gitBaseFolder.toString() + "\" with Git");
			}
			git = Git.open(gitBaseFolder.toFile());
			Repository repo = git.getRepository();
			try {
				String branch = repo.getBranch();
				if (repo.getRef(Constants.HEAD).getTarget().getName().endsWith(branch)) {
					if (logger != null && logger.isDebugEnabled()) {
						logger.debug("Git branch determined to be \"" + branch + "\"");
					}
					return branch;
				}
				if (logger != null) {
					logger.error("Git branch was reported to be \"" + branch + "\" which means that HEAD is detached");
				}
			} catch (IOException e) {
				throw new MojoExecutionException("An error occurred while reading Git branch: " + e.getMessage(), e);
			}
		} catch (IOException e) {
			throw new MojoExecutionException(
				"An error occurred when opening Git base folder: " + e.getMessage(),
				e
			);
		}

		if (logger != null) {
			logger.error("Can't determine Git branch");
		}
		return null;
	}
}
