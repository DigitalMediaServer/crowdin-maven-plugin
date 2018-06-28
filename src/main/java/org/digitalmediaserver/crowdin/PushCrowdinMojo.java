package org.digitalmediaserver.crowdin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom2.Document;
import org.jdom2.Element;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Pushes Maven translations of this project to crowdin.
 *
 * @goal push
 * @threadSafe
 */
public class PushCrowdinMojo extends AbstractCrowdinMojo {

	/**
	 * The base language file that should be uploaded to crowdin.
	 *
	 * @parameter property="filename"
	 * @required
	 */
	protected String pushFileName;

	/**
	 * The title of the pushed file to be displayed on crowdin.
	 *
	 * @parameter property="title"
	 * @required
	 */
	protected String pushFileTitle;

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

	@Override
	@SuppressFBWarnings({ "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD" })
	public void execute() throws MojoExecutionException, MojoFailureException {

		if (!confirm.equalsIgnoreCase("confirm") && !confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("true")) {
			throw new MojoExecutionException("Push is not confirmed - aborting!");
		}

		if (!project.getName().equals(projectName)) {
			throw new MojoExecutionException(
				"POM name (" + project.getName() + ") differs from \"projectName\" parameter (" + projectName + ") - push aborted!"
			);
		}

		super.execute();
		if (languageFilesFolder.exists()) {

			// Retrieve project information
			getLog().info("Retrieving crowdin project information");
			Document projectDetails = crowdinRequestAPI("info", null, null, true);

			String crowdinProjectName = projectDetails.getRootElement().getChild("details").getChild("name").getText();
			if (!crowdinProjectName.equals(projectName)) {
				throw new MojoExecutionException(
					"crowdin project name (" + crowdinProjectName +
					") differs from the \"projectName\" parameter (" + projectName +
					") - push aborted!"
				);
			}

			String branch = getBranch(true, projectDetails);

			// Update project information in case the branch was created in the
			// previous step
			if (branch != null && !crowdinContainsBranch(projectDetails.getRootElement().getChild("files"), branch)) {
				projectDetails = crowdinRequestAPI("info", null, null, true);
			}

			// Get crowdin files
			Element filesElement = getCrowdinFiles(branch, projectDetails);

			// Get language file
			getLog().debug("Retrieving message file " + pushFileName);

			File pushFile = new File(languageFilesFolder, pushFileName);
			if (pushFile.exists()) {
				if (pushFileTitle == null || pushFileTitle.trim().equals("")) {
					pushFileTitle = pushFileName;
				}

				Map<String, File> fileMap = new HashMap<String, File>();
				fileMap.put(pushFile.getName(), pushFile);
				Map<String, String> titleMap = new HashMap<String, String>();
				titleMap.put(pushFileName, pushFileTitle);
				Map<String, String> patternMap = new HashMap<String, String>();
				int dotIdx = pushFileName.lastIndexOf(".");
				if (dotIdx > 0) {
					String bareFileName = pushFileName.substring(0, dotIdx);
					String fileExtension = pushFileName.substring(dotIdx);
					patternMap.put(pushFileName, bareFileName + "_%locale_with_underscore%" + fileExtension);
				} else {
					getLog().warn("Could not figure out export pattern for " + pushFileName);
				}

				if (crowdinContainsFile(filesElement, pushFileName)) {
					// update
					getLog().info("Updating " + pushFileName + " on crowdin");
					Map<String, String> parameters = new HashMap<String, String>();
					if (branch != null) {
						parameters.put("branch", branch);
					}
					parameters.put("update_option", "update_as_unapproved");
					parameters.put("escape_quotes", "0");
					crowdinRequestAPI("update-file", parameters, fileMap, titleMap, patternMap, true);

				} else {
					// add
					getLog().info("Adding " + pushFileName + " to crowdin");
					Map<String, String> parameters = new HashMap<String, String>();
					if (branch != null) {
						parameters.put("branch", branch);
					}
					parameters.put("type", "properties");
					parameters.put("escape_quotes", "0");
					crowdinRequestAPI("add-file", parameters, fileMap, titleMap, patternMap, true);
				}

			} else {
				getLog().warn(languageFilesFolder.getPath() + "/" + pushFileName + " push skipped - file not found");
			}
		} else {
			getLog().warn(languageFilesFolder.getPath() + "/" + pushFileName + " push skipped - folder not found");
		}
	}

	@SuppressWarnings("unused")
	private Map<String, File> getMessageFiles(String folderName) {
		Map<String, File> result = new HashMap<String, File>();
		File[] listFiles = languageFilesFolder.listFiles();
		for (File file : listFiles) {
			if (!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".properties")) {
				String crowdinPath = folderName + "/" + file.getName();
				getLog().debug("Found " + crowdinPath);
				result.put(crowdinPath, file);
			}
		}
		return result;
	}

}
