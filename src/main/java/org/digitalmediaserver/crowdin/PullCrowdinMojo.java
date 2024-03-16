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
import org.apache.maven.plugins.annotations.Parameter;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;


/**
 * Executes {@link FetchCrowdinMojo} and {@link DeployCrowdinMojo} in that
 * order. This effectively builds and downloads the latest translations from
 * Crowdin and then deploys them to the local project.
 */
@Mojo(name = "pull", defaultPhase = LifecyclePhase.NONE)
public class PullCrowdinMojo extends AbstractCrowdinMojo {

	/**
	 * Only translated strings will be included in the exported translation
	 * files. This option is not applied to text documents: *.docx, *.pptx,
	 * *.xlsx, etc., since missing texts may cause the resulting files to be
	 * unreadable.
	 * <p>
	 * <b>Note</b>: This parameter cannot be {@code true} if
	 * {@link #skipUntranslatedFiles} is {@code true}.
	 */
	@Parameter(property = "skipUntranslatedStrings", defaultValue = "true")
	protected boolean skipUntranslatedStrings;

	/**
	 * Only translated files will be included in the exported translation files.
	 * <p>
	 * <b>Note</b>: This parameter cannot be {@code true} if
	 * {@link #skipUntranslatedStrings} is {@code true}.
	 */
	@Parameter(property = "skipUntranslatedFiles", defaultValue = "false")
	protected boolean skipUntranslatedFiles;

	/**
	 * Only texts that are both translated and approved will be included in the
	 * exported translation files. This will require additional efforts from
	 * your proofreaders to approve all suggestions.
	 */
	@Parameter(property = "exportApprovedOnly", defaultValue = "false")
	protected boolean exportApprovedOnly;

	@Override
	public void execute() throws MojoExecutionException {
		initializeParameters();
		createClient();
		initializeServer();
		TranslationFileSet.initialize(translationFileSets);
		StatusFile.initialize(statusFiles);

		getLog().info("Executing fetch and deploy goals");

		getLog().debug("Executing fetch");
		FetchCrowdinMojo fetch = new FetchCrowdinMojo();
		fetch.setCrowdinServerId(crowdinServerId);
		fetch.setDownloadFolder(downloadFolderPath);
		fetch.setHTTPTimeout(httpTimeout);
		fetch.setProject(project);
		fetch.setProjectId(projectId);
		fetch.setRootBranch(rootBranch);
		fetch.setServer(server);
		fetch.setClient(client);
		fetch.setSkipUntranslatedFiles(skipUntranslatedFiles);
		fetch.setSkipUntranslatedStrings(skipUntranslatedStrings);
		fetch.setExportApprovedOnly(exportApprovedOnly);
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

	@Override
	protected void initializeServer() throws MojoExecutionException {
		super.initializeServer();
		if (skipUntranslatedFiles && skipUntranslatedStrings) {
			throw new MojoExecutionException(
				"Both 'skipUntranslatedFiles' and 'skipUntranslatedStrings' cannot be 'true'"
			);
		}
	}
}
