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
package org.digitalmediaserver.crowdin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.response.BranchInfo;
import org.digitalmediaserver.crowdin.api.response.BuildInfo;
import org.digitalmediaserver.crowdin.api.response.BuildInfo.ProjectBuildStatus;
import org.jdom2.Document;

/**
 * Asks Crowdin to build/prepare the latest translations for download.
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.NONE)
public class BuildCrowdinMojo extends AbstractCrowdinMojo {

	/**
	 * Only translated strings will be included in the exported translation
	 * files. This option is not applied to text documents: *.docx, *.pptx,
	 * *.xlsx, etc., since missing texts may cause the resulting files to be
	 * unreadable.
	 * <p>
	 * <b>Note</b>: This parameter cannot be {@code true} if
	 * {@link #skipUntranslatedFiles} is {@code true}.
	 */
	@Parameter(property = "skipUntranslatedStrings", defaultValue = "true")
	protected boolean skipUntranslatedStrings;

	/**
	 * Only translated files will be included in the exported translation files.
	 * <p>
	 * <b>Note</b>: This parameter cannot be {@code true} if
	 * {@link #skipUntranslatedStrings} is {@code true}.
	 */
	@Parameter(property = "skipUntranslatedFiles", defaultValue = "false")
	protected boolean skipUntranslatedFiles;

	/**
	 * Only texts that are both translated and approved will be included in the
	 * exported translation files. This will require additional efforts from
	 * your proofreaders to approve all suggestions.
	 */
	@Parameter(property = "exportApprovedOnly", defaultValue = "false")
	protected boolean exportApprovedOnly;

	@Override
	public void execute() throws MojoExecutionException {
		createClient();
		initializeServer();
		doExecute();
	}

	@Nonnull
	public BuildInfo waitForBuild(
		@Nonnull BuildInfo build,
		long pollIntervalMS,
		long timeoutMS,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		BuildInfo result = build;
		ProjectBuildStatus status;
		long now = System.currentTimeMillis();
		long expiry = now + timeoutMS;
		while ((
				(status = result.getStatus()) == ProjectBuildStatus.CREATED ||
				status == ProjectBuildStatus.IN_PROGRESS
			) && (
				(now = System.currentTimeMillis()) < expiry
			)
		) {
			if (logger != null) {
				if (status == ProjectBuildStatus.CREATED) {
					logger.info("Build hasn't started yet");
				} else {
					logger.info("Build is " + result.getProgress() + "% completed");
				}
			}
			try {
				if (logger != null && logger.isDebugEnabled()) {
					logger.debug("Waiting for " + pollIntervalMS + " ms");
				}
				Thread.sleep(pollIntervalMS);
			} catch (InterruptedException e) {
				throw new MojoExecutionException("Interrupted while waiting for build to finish", e);
			}
			result = CrowdinAPI.getBuildStatus(client, build.getProjectId(), build.getId(), token, logger);
		}

		if (now >= expiry) {
			throw new MojoExecutionException(
				"Timed out while waiting for build to finish (timeout = " + timeoutMS + " ms)"
			);
		}
		return result;
	}

	/**
	 * Performs the actual task. Requires that:
	 * <ul>
	 * <li>{@link #createClient()} has been called first</li>
	 * <li>{@link #initializeServer()} has been called first</li>
	 * </ul>
	 *
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	public void doExecute() throws MojoExecutionException {
		BranchInfo branch = null; //getBranch(); TODO: (Nad) Temp test
		if (branch == null) {
			getLog().info("Asking Crowdin to build translations");
		} else {
			getLog().info("Asking Crowdin to build translations for branch \"" + branch.getName() + "\"");
		}

		String token = server.getPassword();
		BuildInfo build = CrowdinAPI.createBuild(
			client,
			projectId,
			token,
			branch == null ? null : Long.valueOf(branch.getId()),
			skipUntranslatedStrings,
			skipUntranslatedFiles,
			exportApprovedOnly,
			getLog()
		);
		build = waitForBuild(build, 500L, 30000L, token, getLog());
		if (build.getStatus() != ProjectBuildStatus.FINISHED) {
			throw new MojoExecutionException(
				"Failed to build translations at Crowdin with status: " + build.getStatus()
			);
		}
		getLog().info("Crowdin successfully built translations");
	}
}
