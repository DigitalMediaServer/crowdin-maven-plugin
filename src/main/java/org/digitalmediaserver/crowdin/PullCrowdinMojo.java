/*
 * Crowdin Maven Plugin, an Apache Maven plugin for synchronizing translation
 * files using the crowdin.com API.
 * Copyright (C) 2018 Digital Media Server developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.digitalmediaserver.crowdin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;


/**
 * Executes {@link BuildCrowdinMojo}, {@link FetchCrowdinMojo} and
 * {@link DeployCrowdinMojo} in that order. This effectively downloads the
 * latest translations from Crowdin and deploys them to the local project.
 */
@Mojo(name = "pull", defaultPhase = LifecyclePhase.NONE)
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
		deploy.setLineSeparator(lineSeparator);
		deploy.setComment(comment);
		deploy.setLog(getLog());
		deploy.execute();

		getLog().info("Pull sequence completed");
	}
}
