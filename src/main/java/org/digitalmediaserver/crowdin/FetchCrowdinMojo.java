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
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.digitalmediaserver.crowdin.tool.CrowdinAPI;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;


/**
 * Downloads the translations files to the intermediary
 * {@link AbstractCrowdinMojo#downloadFolder}.
 *
 * @goal fetch
 */
public class FetchCrowdinMojo extends AbstractCrowdinMojo {

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

		cleanDownloadFolder();
		String branch = getBranch();
		Map<String, String> parameters = new HashMap<>();
		if (branch != null) {
			parameters.put("branch", branch);
		}

		getLog().info("Downloading translations from Crowdin");

		try {
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
}
