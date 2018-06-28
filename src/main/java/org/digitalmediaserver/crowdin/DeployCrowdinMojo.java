package org.digitalmediaserver.crowdin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * This class applies (copies) the translations of this project from the crowdin
 * download folder to the language files location.
 *
 * @goal deploy
 * @threadSafe
 */
public class DeployCrowdinMojo extends AbstractCrowdinMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (downloadFolder.exists()) {
			if (languageFilesFolder.exists()) {
				getLog().info("Deploying all translation files");

				File[] folderEntries = downloadFolder.listFiles();

				if (folderEntries != null) {
					getLog().debug("Checking folder " + downloadFolder.getAbsolutePath());
					for (File entry : folderEntries) {
						if (!entry.getName().startsWith(".")) {
							if (entry.isDirectory()) {
								getLog().debug("Checking subfolder " + entry.getName());
								File[] entryFiles = entry.listFiles(new FileFilter() {
									@Override
									public boolean accept(File pathname) {
										return
											pathname.isFile() && !pathname.getName().startsWith(".") &&
											pathname.getName().toLowerCase(Locale.US).endsWith(".properties");
									}
								});
								if (entryFiles != null) {
									for (File file : entryFiles) {
										getLog().info("Copying file " + file.getName() + " to " + languageFilesFolder.getAbsolutePath());
										try {
											copyFile(file, new File(languageFilesFolder, file.getName()), true);
										} catch (IOException e) {
											throw new MojoExecutionException("Error copying file " + file.getName(), e);
										}
									}
								} else {
									getLog().debug("Subfolder " + entry.getName() + " is empty");
								}

							} else {
								if (entry.getName().equals(statusFile.getName())) {
									getLog().info("Copying file " + entry.getName() + " to " + statusFile.getParent());
									try {
										copyFile(entry, statusFile, true);
									} catch (IOException e) {
										throw new MojoExecutionException("Error copying file " + entry.getName(), e);
									}
								} else {
									getLog().warn("Unexpected file (" + entry.getAbsolutePath() + ") encountered, skipping");
								}
							}
						}
					}
				} else {
					getLog().warn("No files found for application");
				}
			} else {
				throw new MojoExecutionException("Language files folder (" + languageFilesFolder + ") does not exist.");
			}
		} else {
			throw new MojoExecutionException("Crowdin download folder (" + downloadFolder + ") does not exist. Call fetch first.");
		}
	}

	private static void copyFile(File sourceFile, File destinationFile, boolean overwrite) throws IOException {
		if (!overwrite && destinationFile.exists()) {
			throw new IOException("File \"" + destinationFile.getAbsolutePath() + "\" already exists");
		}

		FileInputStream source = new FileInputStream(sourceFile);
		FileOutputStream destination = null;
		try {
			destination = new FileOutputStream(destinationFile);
			try {
				long count = 0;
				long size = source.getChannel().size();
				while (count < size) {
					count += destination.getChannel().transferFrom(source.getChannel(), count, size - count);
				}
			} finally {
				destination.close();
			}
		} finally {
			source.close();
		}
	}
}
