package com.ums.crowdin.maven;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Apply (copy) the translations of this project from the crowdin download
 * folder to the language files location.
 *
 * @goal apply
 * @threadSafe
 */

public class ApplyCrowdinMojo extends AbstractCrowdinMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (downloadFolder.exists()) {
			if (languageFilesFolder.exists()) {
				getLog().info("Applying all translation files");

				File[] folderEntries = downloadFolder.listFiles();

				getLog().debug("Checking folder " + downloadFolder.getAbsolutePath());
				for (File entry : folderEntries) {
					if (!entry.getName().startsWith(".")) {
						if (entry.isDirectory()) {
							getLog().debug("Checking subfolder " + entry.getName());
							File[] entryFiles = entry.listFiles(new FileFilter() {
								public boolean accept(File pathname) {
									return pathname.isFile() && !pathname.getName().startsWith(".") &&
									       pathname.getName().toLowerCase(Locale.US).endsWith(".properties");
								}
							});
							for (File file : entryFiles) {
								getLog().info("Copying file " + file.getName() + " to " + languageFilesFolder.getAbsolutePath());
								try {
									copyFile(file, new File(languageFilesFolder, file.getName()), true);
								} catch (IOException e) {
									throw new MojoExecutionException("Error copying file " + file.getName(), e);
								}
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
				throw new MojoExecutionException("Language files folder (" + languageFilesFolder + ") does not exist.");
			}
		} else {
			throw new MojoExecutionException("Crowdin download folder (" + downloadFolder + ") does not exist. Call fetch first.");
		}
	}

	private void copyFile(File sourceFile, File destinationFile, boolean overwrite) throws IOException {
		if(!destinationFile.exists()) {
			if (overwrite) {
				destinationFile.createNewFile();
			} else {
				throw new IOException("File \"" + destinationFile.getAbsolutePath() + "\" already exists");
			}
		}

		FileInputStream source = new FileInputStream(sourceFile);
		FileOutputStream destination = null;
		try {
			destination = new FileOutputStream(destinationFile);
			try {
				long count = 0;
				long size = source.getChannel().size();
				while((count += destination.getChannel().transferFrom(source.getChannel(), count, size-count)) < size);
			} finally {
				destination.close();
			}
		}
		finally {
			source.close();
		}
	}
}
