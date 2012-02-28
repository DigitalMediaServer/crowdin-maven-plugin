package net.crowdin.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Package the translations of this project with crowdin
 * 
 * @goal package
 * @phase prepare-package
 * @threadSafe
 */
public class PackageCrowdinMojo extends AbstractCrowdinMojo {

	/**
	 * The file containing translations for this project
	 * 
	 * @parameter expression=
	 *            "${project.build.directory}/generated-resources/crowdin.zip"
	 * @required
	 */
	private File crowdinZipFile;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (!messagesOutputDirectory.exists()) {
			getLog().info("No crowdin translations yet.");
		} else {
			try {
				buildCrowdinFile();
			} catch (Exception e) {
				throw new MojoFailureException("Failed to build zip with crowdin messages");
			}
			projectHelper.attachArtifact(project, "zip", "crowdin", crowdinZipFile);

			getLog().info("Adding has-messages to project");
			File flagFolder = new File(project.getBuild().getDirectory(), "flag-folder");
			try {
				FileUtils.writeStringToFile(new File(flagFolder, "has-messages"), "");
			} catch (IOException e) {
				throw new MojoFailureException("Failed to create flag file");
			}
			Resource resource = new Resource();
			resource.setDirectory(flagFolder.getAbsolutePath());
			this.project.addResource(resource);
		}
	}

	private void buildCrowdinFile() throws IOException {
		getLog().info("Building " + crowdinZipFile);
		crowdinZipFile.getParentFile().mkdirs();
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(crowdinZipFile));
		addFolderToZip(zos, messagesOutputDirectory, messagesOutputDirectory.getAbsolutePath());
		zos.close();
	}

	private void addFolderToZip(ZipOutputStream zos, File folder, String rootPath) throws IOException {
		byte[] buffer = new byte[1024];
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (!file.getName().startsWith(".")) {
					if (file.isDirectory()) {
						addFolderToZip(zos, file, rootPath);
					} else {
						getLog().info("Adding " + file);

						FileInputStream fis = new FileInputStream(file);
						String entryName = file.getAbsolutePath().replace(rootPath, "");
						zos.putNextEntry(new ZipEntry(entryName));
						int length;
						while ((length = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
						fis.close();
					}
				}
			}
		}
	}

}
