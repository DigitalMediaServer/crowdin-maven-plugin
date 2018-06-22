package org.digitalmediaserver.crowdin;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Pull is a convenience Mojo that executes {@link BuildCrowdinMojo},
 * {@link FetchCrowdinMojo} and {@link DeployCrowdinMojo} in that order.
 * This effectively downloads the latest translations from Crowdin
 * and writes them into the local project files.
 *
 * @goal pull
 * @threadSafe
 */

public class PullCrowdinMojo extends AbstractCrowdinMojo {

	/*
	 * The parameters below is a copy of local parameters from FetchCrowdinMojo.
	 * Any changes must be made both places.
	 */

	/**
	 * @parameter default-value="${localRepository}"
	 */
	protected ArtifactRepository localRepository;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactFactory artifactFactory;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactMetadataSource artifactMetadataSource;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Executing build, fetch and deploy goals");

		getLog().debug("Executing build");
		BuildCrowdinMojo build = new BuildCrowdinMojo();
		build.setCrowdinServerId(crowdinServerId);
		build.setProject(project);
		build.setRootBranch(rootBranch);
		build.server = server;
		build.setLog(getLog());
		build.execute();

		getLog().debug("Executing fetch");
		FetchCrowdinMojo fetch = new FetchCrowdinMojo();
		fetch.setCrowdinServerId(crowdinServerId);
		fetch.setDownloadFolder(downloadFolder);
		fetch.setProject(project);
		fetch.setRootBranch(rootBranch);
		fetch.setStatusFile(statusFile);
		fetch.server = server;
		fetch.setLog(getLog());
		fetch.execute();

		getLog().debug("Executing deploy");
		DeployCrowdinMojo deploy = new DeployCrowdinMojo();
		deploy.setDownloadFolder(downloadFolder);
		deploy.setLanguageFilesFolder(languageFilesFolder);
		deploy.setStatusFile(statusFile);
		deploy.setLog(getLog());
		deploy.execute();

		getLog().info("Pull sequence completed");
	}
}
