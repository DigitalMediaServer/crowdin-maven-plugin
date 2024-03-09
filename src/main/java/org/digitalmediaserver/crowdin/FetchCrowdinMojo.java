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
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
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
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;


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

		String token = server.getPassword();
		BranchInfo branch = null; //getBranch(); TODO: (Nad) Temp test
		buildTranslations(branch, token);
		//cleanDownloadFolder(); TODO: (Nad) Temp disabled



		Map<String, String> parameters = new HashMap<>();
		if (branch != null) {
//			parameters.put("branch", branch); //TODO: (Nad) Redo
		}

		getLog().info("Downloading translations from Crowdin");

		try {
//			CrowdinAPI.sendRequest(client, HTTPMethod.GET, "", parameters, token, payload, clazz)
			HttpResponse response = CrowdinAPI.requestPost(client, server, "download/all.zip", parameters, getLog());
			int returnCode = response.getStatusLine().getStatusCode();
			getLog().debug("Crowdin return code : " + returnCode);

			if (returnCode == 200) {
				int count = 0;
				byte[] buf = new byte[1024];
				try (
					InputStream responseBodyAsStream = response.getEntity().getContent();
					ZipInputStream zis = new ZipInputStream(responseBodyAsStream);
				) {
					ZipEntry entry;
					while ((entry = zis.getNextEntry()) != null) {
						if (entry.isDirectory()) {
							getLog().debug("Creating folder \"" + entry.getName() + "\"");

							Files.createDirectories(downloadFolderPath.resolve(entry.getName()));
						} else {
							getLog().debug("Writing \"" + entry.getName() + "\"");

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
				} catch (IOException e) {
					throw new MojoExecutionException("Failed to download translation files: " + e.getMessage(), e);
				}
				EntityUtils.consumeQuietly(response.getEntity());

				if (count == 0) {
					getLog().info("No translations available for this project!");
				} else {
					getLog().info("Successfully downloaded " + count + " files from Crowdin");
				}

				downloadStatusFile();
			} else if (returnCode == 404) {
				throw new MojoExecutionException(
					"Could not find any files in branch \"" + (branch != null ? branch : rootBranch) + "\" on Crowdin"
				);
			} else {
				throw new MojoExecutionException(
					"Failed to get translations from Crowdin with return code " + Integer.toString(returnCode)
				);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to call API: " + e.getMessage(), e);
		}
	}

	protected void buildTranslations(
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
		build = waitForBuild(build, 500L, 30000L, token, getLog());
		if (build.getStatus() != ProjectBuildStatus.FINISHED) {
			throw new MojoExecutionException(
				"Failed to build translations at Crowdin with status: " + build.getStatus()
			);
		}
		getLog().info("Crowdin successfully built translations");
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

		Document document;
		try {
			document = CrowdinAPI.requestPostDocument(client, server, "status", null, null, true, getLog());
		} catch (IOException e) {
			throw new MojoExecutionException("An error occurred while getting the Crowdin status: " + e.getMessage(), e);
		}

		XMLOutputter xmlOut = new XMLOutputter();
		Path statusFile = downloadFolderPath.resolve(STATUS_DOWNLOAD_FILENAME);
		getLog().info("Writing translations status to \"" + statusFile + "\"");
		try (BufferedWriter writer = Files.newBufferedWriter(statusFile, StandardCharsets.UTF_8)) {
			xmlOut.output(document, writer);
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to write file \"" + statusFile + "\": " + e.getMessage(), e);
		}
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

	@Override
	protected void initializeServer() throws MojoExecutionException { //TODO: (Nad) Is this called for "pull"?
		super.initializeServer();
		if (skipUntranslatedFiles && skipUntranslatedStrings) {
			throw new MojoExecutionException(
				"Both 'skipUntranslatedFiles' and 'skipUntranslatedStrings' cannot be 'true'"
			);
		}
	}
}
