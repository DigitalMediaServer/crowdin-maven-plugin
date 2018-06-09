package org.digitalmediaserver.crowdin;

import static org.digitalmediaserver.crowdin.tool.CrowdinFileSystem.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.configuration.UpdateOption;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.digitalmediaserver.crowdin.tool.CrowdinAPI;
import org.jdom2.Document;
import org.jdom2.Element;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Uploads the base/root language file(s) to Crowdin for translation.
 *
 * @goal push
 */
public class PushCrowdinMojo extends AbstractCrowdinMojo {

	/**
	 * This parameter must match the POM name of the current project in is used
	 * to prevent pushing from the wrong project.
	 *
	 * @parameter
	 * @required
	 */
	protected String projectName;

	/**
	 * This parameter must be {@code true} for push to execute. If this isn't
	 * specified in the POM file, {@code -Dconfirm=true} is required as a
	 * command line argument for the push to execute.
	 *
	 * @parameter property="confirm"
	 * @required
	 */
	protected String confirm;

	/**
	 * The default "escape_quotes" parameter to use. Valid values are:
	 * <ul>
	 * <li>0 — Do not escape single quote</li>
	 * <li>1 — Escape single quote by another single quote</li>
	 * <li>2 — Escape single quote by backslash</li>
	 * <li>3 — Escape single quote by another single quote only in strings
	 * containing variables (<code>{0}</code>)</li>
	 * </ul>
	 *
	 * @parameter default-value=0
	 */
	protected int escapeQuotes;

	/**
	 * The default update behavior for updates string when pushing. Valid values
	 * are:
	 * <ul>
	 * <li>delete_translations — Delete translations of changed strings</li>
	 * <li>update_as_unapproved — Preserve translations of changed strings but
	 * remove validations of those translations if they exist</li>
	 * <li>update_without_changes — Preserve translations and validations of
	 * changed strings</li>
	 * </ul>
	 *
	 * @parameter default-value="delete_translations"
	 */
	protected UpdateOption updateOption;

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

		// Retrieve project information
		getLog().info("Retrieving Crowdin project information");
		Document projectDetails;
		try {
			projectDetails = CrowdinAPI.requestPostDocument(client, server, "info", null, null, true, getLog());
		} catch (IOException e) {
			throw new MojoExecutionException("An error occurred while getting Crowdin information: " + e.getMessage(), e);
		}

		String crowdinProjectName = projectDetails.getRootElement().getChild("details").getChild("name").getText();
		if (!crowdinProjectName.equals(projectName)) {
			throw new MojoExecutionException(
				"Crowdin project name (" + crowdinProjectName +
				") differs from the \"projectName\" parameter (" + projectName +
				") - push aborted!"
			);
		}

		String branch = getBranch(true, projectDetails);

		// Update project information in case the branch was created in the
		// previous step
		if (branch != null && !containsBranch(projectDetails.getRootElement().getChild("files"), branch, getLog())) {
			try {
				projectDetails = CrowdinAPI.requestPostDocument(client, server, "info", null, null, true, getLog());
			} catch (IOException e) {
				throw new MojoExecutionException("An error occurred while getting Crowdin information: " + e.getMessage(), e);
			}
		}

		// Get Crowdin files
		Element filesElement;
		try {
			filesElement = CrowdinAPI.getFiles(client, server, branch, projectDetails, getLog());
		} catch (IOException e) {
			throw new MojoExecutionException("An error occurred while getting Crowdin files: " + e.getMessage(), e);
		}

		// Set values
		for (TranslationFileSet fileSet : translationFileSets) {
			File pushFile = new File(fileSet.getLanguageFilesFolder(), fileSet.getBaseFileName());
			if (pushFile.exists()) {
				String pushFolder = getPushFolder(fileSet, true);
				if (!isBlank(pushFolder) && !containsFolder(filesElement, pushFolder, getLog())) {
					try {
						createFolders(client, server, filesElement, pushFolder, getLog());
					} catch (IOException e) {
						throw new MojoExecutionException(
							"An error occurred while creating folder \"" + pushFolder + "\" on Crowdin: " + e.getMessage(),
							e
						);
					}
				}

				Map<String, File> fileMap = new HashMap<String, File>();
				Map<String, String> titleMap = new HashMap<String, String>();
				Map<String, String> patternMap = new HashMap<String, String>();

				String pushName = isBlank(fileSet.getCrowdinPath()) ?
					fileSet.getBaseFileName() :
					formatPath(fileSet.getCrowdinPath(), true) + fileSet.getBaseFileName();
				boolean update = containsFile(filesElement, pushName, getLog());

				fileMap.put(pushName, pushFile);
				if (!fileSet.getBaseFileName().equals(fileSet.getTitle())) {
					titleMap.put(pushName, fileSet.getTitle());
				}
				patternMap.put(pushName, fileSet.getFileNameWhenExported());

				if (!fileSet.getBaseFileName().equals(fileSet.getTitle())) {
					getLog().info(
						(update ? "Updating" : "Adding") + " file \"" + fileSet.getBaseFileName() +
						"\" for fileset \"" + fileSet.getTitle() + "\" on Crowdin"
					);
				} else {
					getLog().info((update ? "Updating" : "Adding") + " file \"" + fileSet.getBaseFileName() + "\" on Crowdin");
				}

				Map<String, String> parameters = new HashMap<String, String>();
				if (branch != null) {
					parameters.put("branch", branch);
				}
				parameters.put("escape_quotes", Integer.toString(getEscapeQuotes(fileSet)));
				try {
					if (update) {
						UpdateOption tmpUpdateOption = getUpdateOption(fileSet);
						if (tmpUpdateOption != UpdateOption.delete_translations) {
							parameters.put("update_option", tmpUpdateOption.name());
						}
						CrowdinAPI.requestPostDocument(
							client,
							server,
							"update-file",
							parameters,
							fileMap,
							titleMap,
							patternMap,
							true,
							getLog()
						);
					} else {
						parameters.put("type", fileSet.getType().name());
						CrowdinAPI.requestPostDocument(
							client,
							server,
							"add-file",
							parameters,
							fileMap,
							titleMap,
							patternMap,
							true,
							getLog()
						);
					}
				} catch (IOException e) {
					throw new MojoExecutionException(
						"An error occurred while " + (update ? "updating" : "adding") +
						" file \"" + pushFile + "\": " + e.getMessage(),
						e
					);
				}
			} else {
				if (!fileSet.getBaseFileName().equals(fileSet.getTitle())) {
					getLog().warn(
						"\"" + pushFile.getAbsolutePath() + "\" not found - upload skipped for fileset \"" +
						fileSet.getTitle() + "\""
					);
				} else {
					getLog().warn("\"" + pushFile.getAbsolutePath() + "\" not found - upload skipped");
				}
			}
		}
	}

	/**
	 * Gets the effective "{@code escape_quotes}" value from either the
	 * {@link TranslationFileSet} or the default parameter.
	 *
	 * @param fileSet the {@link TranslationFileSet} to use.
	 * @return The effective "{@code escape_quotes}" value.
	 */
	protected int getEscapeQuotes(TranslationFileSet fileSet) {
		return fileSet != null && fileSet.getEscapeQuotes() != null ? fileSet.getEscapeQuotes().intValue() : escapeQuotes;
	}

	/**
	 * Gets the effective "{@code update_option}" value from either the
	 * {@link TranslationFileSet} or the default parameter.
	 *
	 * @param fileSet the {@link TranslationFileSet} to use.
	 * @return The effective "{@code update_option}" value.
	 */
	protected UpdateOption getUpdateOption(TranslationFileSet fileSet) {
		return fileSet != null && fileSet.getUpdateOption() != null ? fileSet.getUpdateOption() : updateOption;
	}

	@Override
	protected void initializeParameters() throws MojoExecutionException {
		super.initializeParameters();

		// Check escapeQuotes
		if (escapeQuotes < 0 || escapeQuotes > 3) {
			throw new MojoExecutionException("Invalid default \"escapeQuotes\" value " + escapeQuotes);
		}

		// Check updateOption
		if (updateOption == null) {
			updateOption = UpdateOption.delete_translations;
		}
	}
}
