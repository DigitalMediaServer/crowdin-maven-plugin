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

import static org.digitalmediaserver.crowdin.tool.StringUtil.isBlank;
import static org.digitalmediaserver.crowdin.tool.StringUtil.isNotBlank;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.response.BranchInfo;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.digitalmediaserver.crowdin.tool.GitUtil;
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

	/** The Maven Session object */
	@Parameter(defaultValue = "${session}", required = true, readonly = true)
	protected MavenSession mavenSession;

	/** The current Maven project */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
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
	 */
	@Parameter(property = "downloadFolder", required = true)
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
	 */
	@Parameter(property = "comment")
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
	 */
	@Parameter(property = "lineSeparator")
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
	 */
	@Parameter(property = "rootBranch", defaultValue = "master")
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
	 * Server id in settings.xml, whose {@code <password>} is the API token to
	 * use.
	 */
	@Parameter(property = "crowdinServerId", required = true)
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
	 * The Project Id, can be found on Crowdin under Tools -> API.
	 */
	@Parameter(property = "projectId", required = true)
	protected long projectId;

	/**
	 * Sets the {@link #projectId} value.
	 *
	 * @param projectId the project id to set.
	 * @see #projectId
	 */
	protected void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	/**
	 * A list of {@link TranslationFileSet} elements that defines a set of
	 * translation files.
	 */
	@Parameter(property = "translationFileSets", required = true)
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
	 */
	@Parameter(property = "statusFiles", required = true)
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
		if (isBlank(server.getPassword())) {
			throw new MojoExecutionException(
				"No password/token is configured for the server setting with ID " + crowdinServerId
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
			client = CrowdinAPI.createHTTPClient(getPluginVersion());
		} catch (IOException e) {
			throw new MojoExecutionException("An error occurred while creating the HTTP client: " + e.getMessage(), e);
		}
	}

	/**
	 * Retrieves the version of this plugin from Maven's
	 * {@code pluginDescriptor}.
	 *
	 * @return A {@link String} with current version.
	 */
	protected String getPluginVersion() {
		Map<?, ?> context = getPluginContext();
		Object o = context.get("pluginDescriptor");
		if (o instanceof PluginDescriptor) {
			PluginDescriptor descriptor = (PluginDescriptor) o;
			String result;
			if (isNotBlank(result = descriptor.getVersion())) {
				return result;
			}
		}
		return "unknown";
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
	 * @return The {@link BranchInfo} or {@code null} if the current git
	 *         branch is the Crowdin root.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nullable
	protected BranchInfo getBranch() throws MojoExecutionException {
		return getBranch(false, null);
	}

	/**
	 * Creates or gets the Crowdin branch name that matches the name of the
	 * current Git branch.
	 *
	 * @param create whether the branch should be created at Crowdin if it
	 *            doesn't exist.
	 * @param branches a {@link List} of {@link BranchInfo} if it's already
	 *            possessed, {@code null} to make this method retrieve it.
	 * @return The {@link BranchInfo} or {@code null} if the current git branch
	 *         is the Crowdin root.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nullable
	protected BranchInfo getBranch(
		boolean create,
		@Nullable List<BranchInfo> branches
	) throws MojoExecutionException {
		getLog().info("Determining git branch..");
		String branch = GitUtil.getBranch(project.getBasedir(), getLog());
		if (isBlank(branch)) {
			throw new MojoExecutionException("Could not determine current git branch");
		}
		if (branch.equals(rootBranch)) {
			getLog().info("Git branch is root branch \"" + branch + "\"");
			return null;
		}
		getLog().info("Git branch is \"" + branch + "\"");

		String token = server.getPassword();
		if (branches == null) {
			branches = CrowdinAPI.listBranches(client, projectId, token, branch, getLog());
		}
		for (BranchInfo branchInfo : branches) {
			if (branch.equals(branchInfo.getName())) {
				getLog().info("Found branch \"" + branch + "\" on Crowdin");
				return branchInfo;
			}
		}
		if (!create) {
			throw new MojoExecutionException(
				"Crowdin project doesn't contain branch \"" + branch + "\". Please push this branch first."
			);
		}

		getLog().info("Creating branch \"" + branch + "\" on Crowdin");
		BranchInfo result = CrowdinAPI.createBranch(client, projectId, token, branch, getLog());
		return result;
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
}
