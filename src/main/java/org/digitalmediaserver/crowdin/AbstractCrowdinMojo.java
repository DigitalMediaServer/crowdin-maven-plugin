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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.digitalmediaserver.crowdin.tool.CrowdinFileSystem;
import org.digitalmediaserver.crowdin.tool.GitUtil;
import org.jdom2.Document;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * The abstract base class for the Crowdin {@link Mojo}s.
 *
 * @author Nadahar
 */
@SuppressFBWarnings({
	"UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD",
	"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
	"UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
	"UWF_UNWRITTEN_FIELD"
})
public abstract class AbstractCrowdinMojo extends AbstractMojo {

	/**
	* The Maven Session object.
	*
	* @parameter default-value="${session}"
	* @readonly
	* @required
	*/
	protected MavenSession mavenSession;

	/**
	 * The current Maven project
	 *
	 * @parameter default-value="${project}"
	 * @readonly
	 * @required
	 */
	protected MavenProject project;

	/**
	 * Sets the {@link #project} value.
	 *
	 * @param value the {@link MavenProject} to set.
	 */
	protected void setProject(MavenProject value) {
		project = value;
	}

	/**
	 * The folder where the downloaded language files should be placed.
	 *
	 * @parameter
	 * @required
	 */
	protected File downloadFolder;

	/**
	 * The folder where the downloaded language files should be places as a
	 * {@link Path}. Since Maven doesn't seem to be able to handle {@link Path}
	 * as a parameter, the {@link File} {@link #downloadFolder} is used as an
	 * intermediate.
	 */
	protected Path downloadFolderPath;

	/**
	 * Sets the {@link #downloadFolder} and {@link #downloadFolderPath} values.
	 *
	 * @param folder the {@link Path} representing a folder to set.
	 */
	protected void setDownloadFolder(Path folder) {
		downloadFolder = folder.toFile();
		downloadFolderPath = folder;
	}

	/**
	 * Sets the {@link #downloadFolder} and {@link #downloadFolderPath} values.
	 *
	 * @param folder the {@link File} representing a folder to set.
	 */
	protected void setDownloadFolder(File folder) {
		downloadFolder = folder;
		downloadFolderPath = folder.toPath();
	}

	/**
	 * The custom comment header to add to translation files if
	 * {@link TranslationFileSet#addComment} is {@code true}. If not configured,
	 * a generic "do not modify" comment will be added.
	 *
	 * @parameter
	 */
	protected String comment;

	/**
	 * Sets the {@link #comment} value.
	 *
	 * @param comment the comment to set.
	 */
	protected void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * The string to use as line separator. Specify {@code \n}, {@code \r} or
	 * {@code \r\n} as needed. If not specified, the default will be used.
	 *
	 * @parameter
	 */
	protected String lineSeparator;

	/**
	 * Sets the {@link #lineSeparator} value.
	 *
	 * @param lineSeparator the line-separator to set.
	 */
	protected void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	/**
	 * The git branch that should be treated as root in Crowdin versions
	 * management.
	 *
	 * @parameter property="rootBranch" default-value="master"
	 */
	protected String rootBranch;

	/**
	 * Sets the {@link #rootBranch} value.
	 *
	 * @param branch the Git root branch name.
	 */
	protected void setRootBranch(String branch) {
		rootBranch = branch;
	}

	/**
	 * Server id in settings.xml. {@code <username>} is project identifier,
	 * {@code <password>} is API key.
	 *
	 * @parameter property="crowdinServerId"
	 * @required
	 */
	protected String crowdinServerId;

	/**
	 * Sets the {@link #crowdinServerId} value.
	 *
	 * @param serverId The Maven server id to set.
	 * @see #crowdinServerId
	 */
	protected void setCrowdinServerId(String serverId) {
		crowdinServerId = serverId;
	}

	/**
	 * A list of {@link TranslationFileSet} elements that defines a set of
	 * translation files.
	 *
	 * @parameter
	 * @required
	 */
	protected List<TranslationFileSet> translationFileSets;

	/**
	 * Sets the {@link TranslationFileSet}s.
	 *
	 * @param translationFileSets the {@link TranslationFileSet}s to set.
	 */
	protected void setTranslationFileSets(List<TranslationFileSet> translationFileSets) {
		this.translationFileSets = translationFileSets;
	}

	/**
	 * A list of {@link StatusFile} elements that defines status files.
	 *
	 * @parameter
	 * @required
	 */
	protected List<StatusFile> statusFiles;

	/**
	 * Sets the {@link StatusFile}s.
	 *
	 * @param statusFiles the {@link StatusFile}s to set.
	 */
	protected void setStatusFiles(List<StatusFile> statusFiles) {
		this.statusFiles = statusFiles;
	}

	/** The HTTP client */
	protected CloseableHttpClient client;

	/**
	 * Sets the {@link CloseableHttpClient}.
	 *
	 * @param client the {@link CloseableHttpClient} to set.
	 */
	protected void setClient(CloseableHttpClient client) {
		this.client = client;
	}

	/** The {@link Server} to use for Crowdin credentials */
	protected Server server;

	/**
	 * Sets the {@link Server} to use for Crowdin credentials.
	 *
	 * @param server the Server to set.
	 */
	protected void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Initializes {@link #server} by retrieving the appropriate {@link Server}
	 * instance from the Maven settings and validating its settings.
	 *
	 * @throws MojoExecutionException If a required parameter is missing or the
	 *             specified {@code crowdinServerId} isn't configured.
	 */
	protected void initializeServer() throws MojoExecutionException {
		if (server == null) {
			if (isBlank(crowdinServerId)) {
				throw new MojoExecutionException("Parameter \"crowdinServerId\" must be specified");
			}
			if (mavenSession == null) {
				throw new MojoExecutionException("Parameter \"mavenSession\" is null");
			}
			Settings settings = mavenSession.getSettings();
			if (settings != null) {
				server = settings.getServer(crowdinServerId);
			}
		}
		if (server == null) {
			throw new MojoExecutionException(
				"Failed to find server setting with ID " + crowdinServerId + " in the Maven settings (~/.m2/settings.xml)"
			);
		}
		if (isBlank(server.getUsername())) {
			throw new MojoExecutionException(
				"No username is configured for the server setting with ID " + crowdinServerId
			);
		}
		if (isBlank(server.getPassword())) {
			throw new MojoExecutionException(
				"No password is configured for the server setting with ID " + crowdinServerId
			);
		}
	}

	/**
	 * Initializes {@link #client} with a new {@link CloseableHttpClient}
	 * instance unless one has already been created.
	 *
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected void createClient() throws MojoExecutionException {
		if (client != null) {
			return;
		}
		try {
			client = CrowdinAPI.createHTTPClient();
		} catch (IOException e) {
			throw new MojoExecutionException("An error occurred while creating the HTTP client: " + e.getMessage(), e);
		}
	}

	/**
	 * Initializes the {@link Mojo} parameters since they are set via reflection
	 * and can't be handled in the constructor.
	 *
	 * @throws MojoExecutionException If an error occurs during initialization.
	 */
	protected void initializeParameters() throws MojoExecutionException {
		downloadFolderPath = downloadFolder != null ?  downloadFolder.toPath() : null;
	}

	/**
	 * Gets the Crowdin branch name that matches the name of the current Git
	 * branch.
	 *
	 * @return The branch name or {@code null} if the current git branch is the
	 *         Crowdin root.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nullable
	protected String getBranch() throws MojoExecutionException {
		return getBranch(false, null);
	}

	/**
	 * Creates or gets the Crowdin branch name that matches the name of the
	 * current Git branch.
	 *
	 * @param create whether the branch should be created at Crowdin if it
	 *            doesn't exist.
	 * @param projectDetails the {@link Document} containing the project
	 *            details. If {@code null} the project details will be retrieved
	 *            from Crowdin.
	 * @return The branch name or {@code null} if the current git branch is the
	 *         Crowdin root.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nullable
	protected String getBranch(boolean create, @Nullable Document projectDetails) throws MojoExecutionException {
		getLog().info("Determining git branch..");
		String branch = GitUtil.getBranch(project.getBasedir(), getLog());
		if (isBlank(branch)) {
			throw new MojoExecutionException("Could not determine current git branch");
		}
		getLog().info("Git branch is \"" + branch + "\"");
		if (branch.equals(rootBranch)) {
			return null;
		}
		try {
			if (CrowdinFileSystem.containsBranch(
				CrowdinAPI.getFiles(client, server, null, projectDetails, getLog()),
				branch,
				getLog()
			)) {
				getLog().info("Found branch \"" + branch + "\" on Crowdin");
				return branch;
			} else if (create) {
				CrowdinFileSystem.createBranch(client, server, branch, getLog());
				return branch;
			}
		} catch (IOException e) {
			throw new MojoExecutionException(
				"An error occured while trying to resolve Crowdin branch: " + e.getMessage(),
				e
			);
		}
		throw new MojoExecutionException(
			"Crowdin project doesn't contain branch \"" + branch + "\". Please push this branch first."
		);
	}

	/**
	 * Deletes all files and folders in {@link #downloadFolder}.
	 *
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected void cleanDownloadFolder() throws MojoExecutionException {
		if (downloadFolderPath != null && Files.exists(downloadFolderPath)) {
			getLog().info("Deleting the content of \"" + downloadFolderPath.toAbsolutePath() + "\"");
			try {
				Files.walkFileTree(downloadFolderPath, new FileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
						throw exc;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						if (!downloadFolderPath.equals(dir)) {
							Files.delete(dir);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Could not clean \"" + downloadFolderPath.toAbsolutePath() + "\": " + e.getMessage(),
					e
				);
			}
		} else {
			getLog().info("Creating download folder \"" + downloadFolderPath.toAbsolutePath() + "\"");
			try {
				Files.createDirectories(downloadFolderPath);
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Couldn't create folder \"" + downloadFolderPath.toAbsolutePath() + "\": " + e.getMessage(),
					e
				);
			}
		}
	}

	/**
	 * Evaluates if the specified character sequence is {@code null}, empty or
	 * only consists of whitespace.
	 *
	 * @param cs the {@link CharSequence} to evaluate.
	 * @return true if {@code cs} is {@code null}, empty or only consists of
	 *         whitespace, {@code false} otherwise.
	 */
	public static boolean isBlank(@Nullable CharSequence cs) {
		if (cs == null) {
			return true;
		}
		int strLen = cs.length();
		if (strLen == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
