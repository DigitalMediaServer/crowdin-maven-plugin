package com.digitalmediaserver.crowdin.tool;

import java.io.File;
import java.io.IOException;
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
	 * @param log the {@link Log} to use for logging.
	 * @return The name of the current Git branch or {@code null} if it couldn't
	 *         be established.
	 */
	public static String getBranch(File projectBasedir, Log log) {
		if (!projectBasedir.exists()) {
			log.warn("Project basedir (" + projectBasedir + ") doesn't exist - cannot determine git branch");
			return null;
		} else if (!projectBasedir.isDirectory()) {
			log.warn("Project basedir (" + projectBasedir + ") must be a folder - cannot determine git branch");
			return null;
		}

		Git git;
		try {
			log.debug("Trying to read \"" + projectBasedir + "\" with git");
			git = Git.open(projectBasedir);
			Repository repo = git.getRepository();
			try {
				String branch = repo.getBranch();
				if (repo.getRef(Constants.HEAD).getTarget().getName().endsWith(branch)) {
					log.debug("Git branch determined to be \"" + branch + "\"");
					return branch;
				}
				log.warn("Git branch was reported to be \"" + branch + "\" which means that HEAD is detached");
			} catch (IOException e) {
				log.warn("An error occurred while reading git branch: " + e.getMessage());
			}
		} catch (IOException e) {
			log.warn("An error occurred while opening project basedir with git: " + e.getMessage());
		}

		log.warn("Cannot determine git branch");
		return null;
	}
}
