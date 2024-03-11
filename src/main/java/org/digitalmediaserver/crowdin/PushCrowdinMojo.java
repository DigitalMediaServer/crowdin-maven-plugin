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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.FileExportOptions;
import org.digitalmediaserver.crowdin.api.FileType;
import org.digitalmediaserver.crowdin.api.response.BranchInfo;
import org.digitalmediaserver.crowdin.api.response.FileInfo;
import org.digitalmediaserver.crowdin.api.response.FolderInfo;
import org.digitalmediaserver.crowdin.api.response.ProjectInfo;
import org.digitalmediaserver.crowdin.api.response.StorageInfo;
import org.digitalmediaserver.crowdin.configuration.UpdateOption;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.digitalmediaserver.crowdin.tool.FileUtil;
import org.digitalmediaserver.crowdin.tool.NSISUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Uploads the base/root language file(s) to Crowdin for translation.
 */
@Mojo(name = "push", defaultPhase = LifecyclePhase.NONE)
public class PushCrowdinMojo extends AbstractCrowdinMojo {

	/**
	 * This parameter must match the POM name of the current project in is used
	 * to prevent pushing from the wrong project.
	 */
	@Parameter(property = "projectName", required = true)
	protected String projectName;

	/**
	 * This parameter must be {@code true} for push to execute. If this isn't
	 * specified in the POM file, {@code -Dconfirm=true} is required as a
	 * command line argument for the push to execute.
	 */
	@Parameter(property = "confirm", defaultValue = "false")
	protected String confirm;

	/**
	 * The default update behavior for updated strings when pushing. Valid
	 * values are:
	 * <ul>
	 * <li>clear_translations_and_approvals — Delete translations of changed
	 * strings</li>
	 * <li>keep_translations — Preserve translations of changed strings but
	 * remove approvals of those translations if they exist</li>
	 * <li>keep_translations_and_approvals — Preserve translations and approvals
	 * of changed strings</li>
	 * </ul>
	 */
	@Parameter(property = "updateOption", defaultValue = "clear_translations_and_approvals")
	protected UpdateOption updateOption;

	/**
	 * The global option of whether to overwrite context when updating source
	 * files, even if the context has been modified on Crowdin.
	 */
	@Parameter(property = "replaceModifiedContext", defaultValue = "false")
	protected Boolean replaceModifiedContext;

	@Override
	@SuppressFBWarnings({"NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD"})
	public void execute() throws MojoExecutionException {
		if (!confirm.equalsIgnoreCase("confirm") && !confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("true")) {
			throw new MojoExecutionException("Push is not confirmed - aborting!");
		}

		if (!project.getName().equals(projectName)) {
			throw new MojoExecutionException(
				"POM name (" + project.getName() + ") differs from \"projectName\" parameter (" + projectName + ") - push aborted!"
			);
		}

		initializeParameters();
		initializeServer();
		createClient();
		TranslationFileSet.initialize(translationFileSets);

		getLog().info("Retrieving Crowdin project information");

		String token = server.getPassword();
		ProjectInfo projectInfo = CrowdinAPI.getProjectInfo(client, projectId, token, getLog());
		if (projectInfo.getName() == null || !projectInfo.getName().equals(projectName)) {
			throw new MojoExecutionException(
				"Crowdin project name (" + projectInfo.getName() +
				") differs from the \"projectName\" parameter (" + projectName +
				") - push aborted!"
			);
		}

		BranchInfo branch = getBranch(true, null);
		String loggingTitle, pushFileName;
		FolderInfo folder;
		Path tmpPath; // This is here just to shut FindBugs up - the NPE can't actually happen
		FileInfo file, templateFile; // templateFile is the corresponding file from the "root" branch
		for (TranslationFileSet fileSet : translationFileSets) {
			Path pushFile = fileSet.getLanguageFilesFolder().toPath().resolve(fileSet.getBaseFileName());
			loggingTitle = fileSet.getTitle();
			loggingTitle = isBlank(loggingTitle) || loggingTitle.equals(fileSet.getBaseFileName()) ? null : loggingTitle;
			if (Files.exists(pushFile)) {
				folder = null;
				templateFile = null;
				pushFileName = (tmpPath = pushFile.getFileName()) == null ? "" : tmpPath.toString();
				String pushFolder = FileUtil.getPushFolder(fileSet, true);
				if (isNotBlank(pushFolder)) {
					folder = CrowdinAPI.getFolder(client, projectId, branch, pushFolder, true, token, getLog());
				}
				file = CrowdinAPI.getFileIfExists(
					client,
					projectId,
					branch,
					folder,
					fileSet.getBaseFileName(),
					token,
					getLog()
				);
				if (file == null && branch != null) {
					FolderInfo templateFolder = folder == null ? null : CrowdinAPI.getFolder(
						client,
						projectId,
						null,
						pushFolder,
						false,
						token,
						getLog()
					);
					if (folder == null || templateFolder != null) {
						templateFile = CrowdinAPI.getFileIfExists(
							client,
							projectId,
							null,
							templateFolder,
							fileSet.getBaseFileName(),
							token, getLog()
						);
					}
				}

				// At this stage we know if the file exists at Crowdin, and if it doesn't
				// we know if we have a corresponding "root" file to copy settings from.

				if (loggingTitle != null) {
					getLog().info(
						"Uploading \"" + fileSet.getBaseFileName() +
						"\" for fileset \"" + loggingTitle + "\" to Crowdin"
					);
				} else {
					getLog().info("Uploading \"" + fileSet.getBaseFileName() + "\" to Crowdin");
				}

				StorageInfo storage;
				InputStream is = null;
				try {
					HttpEntity entity;
					ContentType contentType = ContentType.APPLICATION_OCTET_STREAM;
					FileType fileType = fileSet.getType();
					if (fileSet.getType() == FileType.nsh) {
						is = new NSISUtil.NSISInputStream(pushFile);
						entity = new InputStreamEntity(is, contentType);
					} else {
						entity = new FileEntity(pushFile.toFile(), contentType);
					}
					storage = CrowdinAPI.createStorage(client, pushFileName, entity, token, getLog());
				} catch (FileNotFoundException e) {
					if (loggingTitle != null) {
						getLog().warn(
							"\"" + pushFile.toAbsolutePath() + "\" not found - upload skipped for fileset \"" +
							loggingTitle + "\": " + e.getMessage()
						);
					} else {
						getLog().warn(
							"\"" + pushFile.toAbsolutePath() + "\" not found - upload skipped: " + e.getMessage()
						);
					}
					continue;
				} catch (IOException e) {
					if (loggingTitle != null) {
						getLog().error(
							"An error occurred while reading \"" + pushFile.toAbsolutePath() +
							"\" - upload skipped for fileset \"" + loggingTitle + "\": " + e.getMessage()
						);
					} else {
						getLog().error(
							"An error occurred while reading \"" + pushFile.toAbsolutePath() +
							"\" - upload skipped: " + e.getMessage()
						);
					}
					continue;
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							getLog().warn("Couldn't close \"" + pushFile.toAbsolutePath() + "\" after reading");
						}
					}
				}

				try {
					if (loggingTitle != null) {
						getLog().info(
							(file != null ? "Updating" : "Adding") + " file \"" + fileSet.getBaseFileName() +
							"\" for fileset \"" + loggingTitle + "\" at Crowdin"
						);
					} else {
						getLog().info(
							(file != null ? "Updating" : "Adding") + " file \"" + fileSet.getBaseFileName() + "\" at Crowdin"
						);
					}

					if (file != null) {
						if (CrowdinAPI.updateFile(
							client,
							projectId,
							file.getId(),
							storage,
							getUpdateOption(fileSet),
							file.getImportOptions(),
							file.getExportOptions(),
							getReplaceModifiedContext(fileSet),
							token,
							getLog()
						) != null) {
							if (loggingTitle != null) {
								getLog().info(
									"Successfully updated file \"" + fileSet.getBaseFileName() +
									"\" for fileset \"" + loggingTitle + "\" at Crowdin"
								);
							} else {
								getLog().info(
									"Successfully updated file \"" + fileSet.getBaseFileName() + "\" at Crowdin"
								);
							}
						} else {
							if (loggingTitle != null) {
								getLog().info(
									"No updates were needed for file \"" + fileSet.getBaseFileName() +
									"\" for fileset \"" + loggingTitle + "\" at Crowdin"
								);
							} else {
								getLog().info(
									"No updates were needed for file \"" + fileSet.getBaseFileName() + "\" at Crowdin"
								);
							}
						}
					} else {
						CrowdinAPI.createFile(
							client,
							projectId,
							storage,
							pushFileName,
							templateFile != null && (fileSet.getType() == null || fileSet.getType() == FileType.auto) ?
								templateFile.getType() :
								fileSet.getType(),
							folder == null && branch != null ? branch.getId() : null,
							folder == null ? null : folder.getId(),
							fileSet.getTitle(),
							templateFile == null ? null : templateFile.getContext(),
							templateFile == null ? null : templateFile.getExcludedTargetLanguages(),
							generateExportOptions(fileSet, templateFile),
							templateFile == null ? null : templateFile.getImportOptions(),
							templateFile == null ? null : templateFile.getParserVersion(),
							token,
							getLog()
						);
						if (loggingTitle != null) {
							getLog().info(
								"Successfully added file \"" + fileSet.getBaseFileName() +
								"\" for fileset \"" + loggingTitle + "\" at Crowdin"
							);
						} else {
							getLog().info(
								"Successfully added file \"" + fileSet.getBaseFileName() + "\" at Crowdin"
							);
						}
					}
				} finally {
					// Delete storage
					CrowdinAPI.deleteStorage(client, storage, token, getLog());
				}
			} else {
				if (!fileSet.getBaseFileName().equals(fileSet.getTitle())) {
					getLog().warn(
						"\"" + pushFile.toAbsolutePath() + "\" not found - upload skipped for fileset \"" +
						fileSet.getTitle() + "\""
					);
				} else {
					getLog().warn("\"" + pushFile.toAbsolutePath() + "\" not found - upload skipped");
				}
			}
		}
	}

	/**
	 * Gets the effective "{@code updateOption}" value from either the
	 * {@link TranslationFileSet} or the default parameter.
	 *
	 * @param fileSet the {@link TranslationFileSet} to use.
	 * @return The effective "{@code updateOption}" value.
	 */
	protected UpdateOption getUpdateOption(TranslationFileSet fileSet) {
		return fileSet != null && fileSet.getUpdateOption() != null ? fileSet.getUpdateOption() : updateOption;
	}

	/**
	 * Gets the effective "{@code replaceModifiedContext}" value from either the
	 * {@link TranslationFileSet} or the default parameter.
	 *
	 * @param fileSet the {@link TranslationFileSet} to use.
	 * @return The effective "{@code replaceModifiedContext}" value.
	 */
	protected Boolean getReplaceModifiedContext(TranslationFileSet fileSet) {
		return fileSet != null && fileSet.getReplaceModifiedContext() != null ?
			fileSet.getReplaceModifiedContext() :
			replaceModifiedContext;
	}

	/**
	 * Builds the "effective" {@link FileExportOptions} from a combination of
	 * the template file settings (if any) and the {@link TranslationFileSet}
	 * configuration.
	 *
	 * @param fileSet the {@link TranslationFileSet} from which to get the
	 *            configuration.
	 * @param templateFile the {@link FileInfo} for the template file, if any.
	 * @return the combined/merged {@link FileExportOptions}.
	 * @throws MojoExecutionException If validation of the resulting options
	 *             fails.
	 */
	protected FileExportOptions generateExportOptions(
		@Nonnull TranslationFileSet fileSet,
		@Nullable FileInfo templateFile
	) throws MojoExecutionException {
		FileExportOptions result = templateFile == null ? null : templateFile.getExportOptions();
		if (result == null) {
			result = new FileExportOptions();
		}
		result.setExportPattern(fileSet.getExportPattern());
		if (fileSet.getType() == FileType.properties) {
			if (fileSet.getEscapeQuotes() != null) {
				result.setEscapeQuotes(fileSet.getEscapeQuotes());
			}
			if (fileSet.getEscapeSpecialCharacters() != null) {
				result.setEscapeSpecialCharacters(fileSet.getEscapeSpecialCharacters());
			}
		}

		result.validate(fileSet.getType());
		return result;
	}

	@Override
	protected void initializeParameters() throws MojoExecutionException {
		super.initializeParameters();

		// Check updateOption
		if (updateOption == null) {
			updateOption = UpdateOption.clear_translations_and_approvals;
		}
	}
}
