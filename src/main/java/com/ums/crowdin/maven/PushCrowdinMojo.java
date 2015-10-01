package com.ums.crowdin.maven;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Push Maven translations of this project to crowdin
 *
 * @goal push
 * @threadSafe
 */
public class PushCrowdinMojo extends AbstractCrowdinMojo {

	/**
	 * The base language file that should be uploaded to crowdin.
	 *
	 * @parameter property = "filename"
	 * @required
	 */
	protected String pushFileName;

	/**
	 * The title of the pushed file to be displayed on crowdin.
	 *
	 * @parameter property = "title"
	 * @required
	 */
	protected String pushFileTitle;

	/**
	 * A parameter that must be <code>true</code> for push to execute. If this
	 * is not specified in the POM file, <code>-Dconfirm=true</code> is required
	 * as a command line argument for the push to execute.
	 *
	 * @parameter property="confirm"
	 * @required
	 */
	protected String confirm;

	/**
	 * This parameter must match the POM name of the current project in is used
	 * to prevent pushing from the wrong project.
	 *
	 * @parameter
	 * @required
	 */
	protected String projectName;

	@SuppressWarnings("unused")
	private void crowdinCreateFolder(String folderName) throws MojoExecutionException {
		getLog().info("Creating " + folderName + " folder on crowdin");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", folderName);
		crowdinRequestAPI("add-directory", parameters, null, true);
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		if (!confirm.equalsIgnoreCase("confirm") && !confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("true")) {
			throw new MojoExecutionException("Push is not confirmed - aborted!");
		}

		if (!project.getName().equals(projectName)) {
			throw new MojoExecutionException("POM name (" + project.getName() + ") differs from \"projectName\" parameter (" + projectName + ") - push aborted!");
		}

		super.execute();
		if (languageFilesFolder.exists()) {

			// Retrieve project informations
			getLog().info("Retrieving project information");
			Document projectDetails = crowdinRequestAPI("info", null, null, false);

			String crowdinProjectName = projectDetails.getRootElement().getChild("details").getChild("name").getText();
			if (!crowdinProjectName.equals(projectName)) {
				throw new MojoExecutionException("crowdin project name (" + crowdinProjectName + ") differs from \"projectName\" parameter (" + projectName + ") - push aborted!");
			}

			// Get crowdin files
			Element filesElement = projectDetails.getRootElement().getChild("files");

			// Get language file
			getLog().debug("Retrieving message file " + pushFileName);

			File pushFile = new File(languageFilesFolder, pushFileName);
			if (pushFile.exists()) {
				if (pushFileTitle == null || pushFileTitle.trim().isEmpty()) {
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
					parameters.put("update_option", "update_as_unapproved");
					parameters.put("escape_quotes", "0");
					crowdinRequestAPI("update-file", parameters, fileMap, titleMap, patternMap, true);

				} else {
					// add
					getLog().info("Adding " + pushFileName + " to crowdin");
					Map<String, String> parameters = new HashMap<String, String>();
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
