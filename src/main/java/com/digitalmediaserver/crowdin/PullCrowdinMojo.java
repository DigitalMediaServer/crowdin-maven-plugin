package com.digitalmediaserver.crowdin;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;

/**
 * Pull is a convenience Mojo that executes {@link BuildCrowdinMojo},
 * {@link FetchCrowdinMojo} and {@link ApplyCrowdinMojo} in that order.
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
	 * @component
	 * @required
	 * @readonly
	 */
	protected DependencyTreeBuilder treeBuilder;

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

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactCollector artifactCollector;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Executing build, fetch and apply goals");

		getLog().debug("Executing build");
		BuildCrowdinMojo build = new BuildCrowdinMojo();
		build.setCrowdinServerId(crowdinServerId);
		build.setProject(project);
		build.setRootBranch(rootBranch);
		build.setWagonManager(wagonManager);
		build.setLog(getLog());
		build.execute();

		getLog().debug("Executing fetch");
		FetchCrowdinMojo fetch = new FetchCrowdinMojo();
		fetch.setCrowdinServerId(crowdinServerId);
		fetch.setDownloadFolder(downloadFolder);
		fetch.setProject(project);
		fetch.setRootBranch(rootBranch);
		fetch.setStatusFile(statusFile);
		fetch.setWagonManager(wagonManager);
		fetch.setLog(getLog());
		fetch.setArtifactCollector(artifactCollector);
		fetch.setArtifactFactory(artifactFactory);
		fetch.setArtifactMetadataSource(artifactMetadataSource);
		fetch.setLocalRepository(localRepository);
		fetch.setTreeBuilder(treeBuilder);
		fetch.execute();

		getLog().debug("Executing apply");
		ApplyCrowdinMojo apply = new ApplyCrowdinMojo();
		apply.setDownloadFolder(downloadFolder);
		apply.setLanguageFilesFolder(languageFilesFolder);
		apply.setStatusFile(statusFile);
		apply.setLog(getLog());
		apply.execute();

		getLog().info("Pull sequence completed");
	}
}
