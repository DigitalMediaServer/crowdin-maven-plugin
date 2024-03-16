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

import static org.digitalmediaserver.crowdin.tool.Constants.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.CrowdinAPI.HTTPMethod;
import org.digitalmediaserver.crowdin.api.response.BranchInfo;
import org.digitalmediaserver.crowdin.api.response.BuildInfo;
import org.digitalmediaserver.crowdin.api.response.BuildInfo.ProjectBuildStatus;
import org.digitalmediaserver.crowdin.api.response.DownloadLinkInfo;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.digitalmediaserver.crowdin.tool.FileUtil;


/**
 * Downloads the translations files to the intermediary
 * {@link AbstractCrowdinMojo#downloadFolder}.
 */
@Mojo(name = "fetch", defaultPhase = LifecyclePhase.NONE)
public class FetchCrowdinMojo extends AbstractCrowdinMojo {

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
	 * Sets the {@link #skipUntranslatedStrings} value.
	 *
	 * @param value the value to set.
	 */
	protected void setSkipUntranslatedStrings(boolean value) {
		skipUntranslatedStrings = value;
	}

	/**
	 * Only translated files will be included in the exported translation files.
	 * <p>
	 * <b>Note</b>: This parameter cannot be {@code true} if
	 * {@link #skipUntranslatedStrings} is {@code true}.
	 */
	@Parameter(property = "skipUntranslatedFiles", defaultValue = "false")
	protected boolean skipUntranslatedFiles;

	/**
	 * Sets the {@link #skipUntranslatedFiles} value.
	 *
	 * @param value the value to set.
	 */
	protected void setSkipUntranslatedFiles(boolean value) {
		skipUntranslatedFiles = value;
	}

	/**
	 * Only texts that are both translated and approved will be included in the
	 * exported translation files. This will require additional efforts from
	 * your proofreaders to approve all suggestions.
	 */
	@Parameter(property = "exportApprovedOnly", defaultValue = "false")
	protected boolean exportApprovedOnly;

	/**
	 * Sets the {@link #exportApprovedOnly} value.
	 *
	 * @param value the value to set.
	 */
	protected void setExportApprovedOnly(boolean value) {
		exportApprovedOnly = value;
	}

	/** The number of seconds to wait for builds to complete */
	@Parameter(property = "buildTimeout", defaultValue = "60")
	protected Integer buildTimeout;

	/**
	 * Sets the {@link #buildTimeout} value.
	 *
	 * @param buildTimeout the build timeout in seconds to set.
	 */
	protected void setBuildTimeout(@Nonnull Integer buildTimeout) {
		this.buildTimeout = buildTimeout;
	}

	@Override
	public void execute() throws MojoExecutionException {
		initializeParameters();
		TranslationFileSet.initialize(translationFileSets);
		StatusFile.initialize(statusFiles);
		createClient();
		initializeServer();
		doExecute();
	}

	/**
	 * Performs the task of this {@link org.apache.maven.plugin.Mojo}.
	 *
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	public void doExecute() throws MojoExecutionException {
		if (translationFileSets == null || translationFileSets.isEmpty()) {
			throw new MojoExecutionException("No filesets are defined");
		}

		Log logger = getLog();
		String token = server.getPassword();
		List<BranchInfo> branches = CrowdinAPI.listBranches(client, projectId, token, null, logger);
		BranchInfo branch = getBranch(false, branches);
		BuildInfo build = buildTranslations(branch, token);
		cleanDownloadFolder();

		logger.info("Downloading translations from Crowdin");
		DownloadLinkInfo downloadLinkInfo = CrowdinAPI.getDownloadLink(
			client,
			projectId,
			build.getId(),
			token,
			logger
		);

		// Crowdin doesn't filter out branches from the root branch archive,
		// so they have to be filtered here.
		Set<String> filterBranchNames = null;
		if (branch == null && !branches.isEmpty()) {
			filterBranchNames = new HashSet<>(branches.size(), 1f);
			for (BranchInfo info : branches) {
				filterBranchNames.add(info.getName());
			}
		}

		List<String> pathElements;
		int count = 0;
		boolean filter;
		byte[] buf = new byte[1024];
		try (
			CloseableHttpResponse response = CrowdinAPI.sendStreamRequest(
				client,
				HTTPMethod.GET,
				downloadLinkInfo.getUrl(),
				null,
				logger
			);
			InputStream responseBodyAsStream = response.getEntity().getContent();
			ZipInputStream zis = new ZipInputStream(responseBodyAsStream);
		) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (filterBranchNames != null) {
					pathElements = FileUtil.splitPath(entry.getName(), true);
					filter = false;
					for (int i = 1; i < pathElements.size(); i++) {
						if (filterBranchNames.contains(pathElements.get(i))) {
							filter = true;
							break;
						}
					}
					if (filter) {
						if (logger.isDebugEnabled()) {
							logger.debug("Filtering out branch element \"" + entry.getName() + "\"");
						}
						continue;
					}
				}
				if (entry.isDirectory()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Creating folder \"" + entry.getName() + "\"");
					}

					Files.createDirectories(downloadFolderPath.resolve(entry.getName()));
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Writing \"" + entry.getName() + "\"");
					}

					Path path = downloadFolderPath.resolve(entry.getName());
					try (OutputStream os = Files.newOutputStream(path)) {
						while (zis.available() > 0) {
							int read = zis.read(buf);
							if (read != -1) {
								os.write(buf, 0, read);
							}
						}
					}
					count++;
				}
			}
		} catch (IOException | HttpException e) {
			throw new MojoExecutionException("Failed to download translation files: " + e.getMessage(), e);
		}
		if (count == 0) {
			logger.info("No translations are available!");
		} else {
			logger.info("Successfully downloaded " + count + " files from Crowdin");
		}

		downloadStatusFile();
	}

	/**
	 * Requests a new build at Crowdin and returns the resulting
	 * {@link BuildInfo}.
	 *
	 * @param branch the {@link BranchInfo} if building for a branch.
	 * @param token the API token.
	 * @return The resulting {@link BuildInfo}.
	 * @throws MojoExecutionException If the build fails for some reason.
	 */
	@Nonnull
	protected BuildInfo buildTranslations(
		@Nullable BranchInfo branch,
		@Nonnull String token
	) throws MojoExecutionException {
		if (branch == null) {
			getLog().info("Asking Crowdin to build translations");
		} else {
			getLog().info("Asking Crowdin to build translations for branch \"" + branch.getName() + "\"");
		}

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
		build = waitForBuild(
			build,
			2000L,
			buildTimeout == null ? 60000L : buildTimeout.longValue() * 1000L,
			token,
			getLog()
		);
		if (build.getStatus() != ProjectBuildStatus.FINISHED) {
			throw new MojoExecutionException(
				"Failed to build translations at Crowdin with status: " + build.getStatus()
			);
		}
		getLog().info("Crowdin successfully built translations");
		return build;
	}

	/**
	 * Downloads the translations status file to the intermediary
	 * {@link AbstractCrowdinMojo#downloadFolder}.
	 *
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected void downloadStatusFile() throws MojoExecutionException {
		if (statusFiles == null || statusFiles.isEmpty()) {
			return;
		}

		String status = CrowdinAPI.getTranslationStatus(client, projectId, server.getPassword(), getLog());
		Path statusFile = downloadFolderPath.resolve(STATUS_DOWNLOAD_FILENAME);
		getLog().info("Writing translations status to \"" + statusFile + "\"");
		try (BufferedWriter writer = Files.newBufferedWriter(statusFile, StandardCharsets.UTF_8)) {
			writer.write(status);
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to write file \"" + statusFile + "\": " + e.getMessage(), e);
		}
	}

	/**
	 * Polls Crowdin for the status of the specified build until the build is
	 * either completed or failed.
	 *
	 * @param build the {@link BuildInfo} for the build to wait for.
	 * @param pollIntervalMS the time in milliseconds between each poll.
	 * @param timeoutMS the time in milliseconds before abandoning waiting and
	 *            declaring the build a failure.
	 * @param token the API token.
	 * @param logger the {@link Log} to log to.
	 * @return the {@link BuildInfo} with containing the new build status.
	 * @throws MojoExecutionException If the polling fails or the timeout
	 *             expires.
	 */
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

	@Override
	protected void initializeServer() throws MojoExecutionException {
		super.initializeServer();
		if (skipUntranslatedFiles && skipUntranslatedStrings) {
			throw new MojoExecutionException(
				"Both 'skipUntranslatedFiles' and 'skipUntranslatedStrings' cannot be 'true'"
			);
		}
	}
}
