package org.digitalmediaserver.crowdin;

import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;


/**
 * Executes {@link BuildCrowdinMojo}, {@link FetchCrowdinMojo} and
 * {@link DeployCrowdinMojo} in that order. This effectively downloads the
 * latest translations from Crowdin and deploys them to the local project.
 *
 * @goal pull
 */
public class PullCrowdinMojo extends AbstractCrowdinMojo {

	@Override
	public void execute() throws MojoExecutionException {
		initializeParameters();
		createClient();
		initializeServer();
		TranslationFileSet.initialize(translationFileSets);
		StatusFile.initialize(statusFiles);

		getLog().info("Executing build, fetch and deploy goals");

		getLog().debug("Executing build");
		BuildCrowdinMojo build = new BuildCrowdinMojo();
		build.setCrowdinServerId(crowdinServerId);
		build.setProject(project);
		build.setRootBranch(rootBranch);
		build.setServer(server);
		build.setClient(client);
		build.setLog(getLog());
		build.doExecute();

		getLog().debug("Executing fetch");
		FetchCrowdinMojo fetch = new FetchCrowdinMojo();
		fetch.setCrowdinServerId(crowdinServerId);
		fetch.setDownloadFolder(downloadFolderPath);
		fetch.setProject(project);
		fetch.setRootBranch(rootBranch);
		fetch.setServer(server);
		fetch.setClient(client);
		fetch.setTranslationFileSets(translationFileSets);
		fetch.setStatusFiles(statusFiles);
		fetch.setLog(getLog());
		fetch.doExecute();

		getLog().debug("Executing deploy");
		DeployCrowdinMojo deploy = new DeployCrowdinMojo();
		deploy.setDownloadFolder(downloadFolderPath);
		deploy.setTranslationFileSets(translationFileSets);
		deploy.setStatusFiles(statusFiles);
		deploy.setLog(getLog());
		deploy.execute();

		getLog().info("Pull sequence completed");
	}
}
