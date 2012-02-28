package net.crowdin.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Synchronize the translations of this project with crowdin
 * 
 * @goal synchronize
 * @phase generate-resources
 * @threadSafe
 */
public class SynchroniseCrowdinMojo extends AbstractCrowdinMojo {

	private boolean cleanFolders(Element languagesElement) {
		Set<String> codes = new HashSet<String>();

		@SuppressWarnings("unchecked")
		List<Element> items = languagesElement.getChildren("item");
		for (Element element : items) {
			String code = element.getChildTextNormalize("code");
			codes.add(code);
		}

		File[] listFiles = messagesOutputDirectory.listFiles();
		if (listFiles != null) {
			for (File file : listFiles) {
				if (!file.getName().startsWith(".") && file.isDirectory()) {
					boolean deleteRoot = !codes.contains(file.getName());
					if (deleteRoot) {
						getLog().info("Deleting " + file + " folder");
					} else {
						getLog().info("Clearing " + file + " folder");
					}
					if (!deleteFolder(file, deleteRoot)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void crowdinCreateFolder() throws MojoExecutionException {
		getLog().info("Creating " + project.getArtifactId() + " folder on crowdin");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", project.getArtifactId());
		crowdinRequestAPI("add-directory", parameters, null, true);
	}

	private boolean deleteFolder(File folder, boolean deleteRoot) {
		File[] listFiles = folder.listFiles();
		if (listFiles != null) {
			for (File file : listFiles) {
				if (file.isDirectory()) {
					deleteFolder(file, true);
				}
				if (!file.delete()) {
					return false;
				}
				getLog().debug("Deleted " + file);
			}
		}
		if (deleteRoot) {
			boolean deleted = folder.delete();
			getLog().debug("Deleted " + folder);
			return deleted;
		} else {
			return true;
		}
	}

	private void downloadTranslations(Element languagesElement) throws MojoExecutionException {
		try {
			getLog().info("Downloading translations from crowdin.");
			String uri = "http://api.crowdin.net/api/project/" + authenticationInfo.getUserName() + "/download/all.zip?key=" + authenticationInfo.getPassword();
			getLog().debug("Calling " + uri);
			GetMethod getMethod = new GetMethod(uri);
			int returnCode = client.executeMethod(getMethod);
			getLog().debug("Return code : " + returnCode);

			if (returnCode == 200) {
				if (!cleanFolders(languagesElement)) {
					throw new MojoExecutionException("Failed to delete folders in " + messagesOutputDirectory);
				}

				InputStream responseBodyAsStream = getMethod.getResponseBodyAsStream();
				ZipInputStream zis = new ZipInputStream(responseBodyAsStream);
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory()) {
						String name = entry.getName();
						getLog().debug("Processing " + name);
						int slash = name.indexOf('/');
						String language = name.substring(0, slash);
						name = name.substring(slash + 1);
						slash = name.indexOf('/');
						String module = name.substring(0, slash);
						name = name.substring(slash + 1);
						if (module.equals(project.getArtifactId())) {
							File languageFolder = new File(messagesOutputDirectory, language);
							if (!languageFolder.exists()) {
								languageFolder.mkdirs();
							}
							File targetFile = new File(languageFolder, name);
							getLog().info("Copying translation to " + targetFile);
							FileOutputStream fos = new FileOutputStream(targetFile);
							while (zis.available() > 0) {
								int read = zis.read();
								if (read != -1) {
									fos.write(read);
								}
							}
							fos.close();
						}
					}
				}
			} else {
				throw new MojoExecutionException("Failed to get translations from crowdin");
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to call API", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (messagesInputDirectory.exists()) {

			Map<String, File> newFiles = new HashMap<String, File>();
			Map<String, File> toUpdateFiles = new HashMap<String, File>();
			List<String> toDeleteFiles = new ArrayList<String>();

			// Retrieve project informations
			getLog().info("Retrieving project informations");
			Document projectDetails = crowdinRequestAPI("info", null, null, false);

			// Get crowdin files
			Element filesElement = projectDetails.getRootElement().getChild("files");
			String folderName = project.getArtifactId();

			// Get Maven files
			getLog().debug("Retrieving message files from this project");
			Map<String, File> files = getMessageFiles();

			if (!crowdinContainsFile(filesElement, folderName, true)) {
				// Create project folder if it does not exist
				crowdinCreateFolder();
			} else {
				// List crowdin files
				List<Element> items = filesElement.getChildren("item");
				Element projectFolder = crowdinGetFolder(items, folderName);
				Element subFiles = projectFolder.getChild("files");
				List<Element> subItems = subFiles.getChildren("item");
				for (Element subItem : subItems) {
					if (!crowdinIsFolder(subItem)) {
						// get crowdin name
						String name = subItem.getChildTextNormalize("name");
						// check that files still exist
						String mapName = project.getArtifactId() + "/" + name;
						if (!files.containsKey(mapName)) {
							getLog().debug(mapName + " is in crowdin project but not in this project, delete it later");
							// otherwise delete it from crowdin
							toDeleteFiles.add(mapName);
						}
					}
				}
			}

			// For existing maven files, check if file exist or not on crowdin
			Set<Entry<String, File>> entrySet = files.entrySet();
			for (Entry<String, File> entry : entrySet) {
				if (crowdinContainsFile(filesElement, entry.getKey(), false)) {
					// update
					getLog().debug(entry.getKey() + " has to be updated");
					toUpdateFiles.put(entry.getKey(), entry.getValue());
				} else {
					// put
					getLog().debug(entry.getKey() + " has to be added");
					newFiles.put(entry.getKey(), entry.getValue());
				}
			}

			if (toUpdateFiles.size() != 0) {
				crowdinRequestAPI("update-file", null, toUpdateFiles, true);
			}
			if (newFiles.size() != 0) {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("type", "properties");
				crowdinRequestAPI("add-file", parameters, newFiles, true);
			}
			for (String toDeleteFile : toDeleteFiles) {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("file", toDeleteFile);
				getLog().info("Deleting " + toDeleteFile + " on crowdin");
				crowdinRequestAPI("delete-file", parameters, null, true);
			}

			getLog().info("Asking crowdin to export translations");
			crowdinRequestAPI("export", null, null, true);

			downloadTranslations(projectDetails.getRootElement().getChild("languages"));
		} else {
			getLog().info(messagesInputDirectory.getPath() + " not found");
		}
	}

	private Map<String, File> getMessageFiles() {
		Map<String, File> result = new HashMap<String, File>();
		File[] listFiles = messagesInputDirectory.listFiles();
		for (File file : listFiles) {
			if (!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".properties")) {
				String crowdinPath = project.getArtifactId() + "/" + file.getName();
				getLog().debug("Found " + crowdinPath);
				result.put(crowdinPath, file);
			}
		}
		return result;
	}
}
