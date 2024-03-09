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

import java.io.IOException;
import java.util.HashMap;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.jdom2.Document;

/**
 * Asks Crowdin to build/prepare the latest translations for download.
 *
 * @goal build
 */
public class BuildCrowdinMojo extends AbstractCrowdinMojo {

	@Override
	public void execute() throws MojoExecutionException {
		createClient();
		initializeServer();
		doExecute();
	}

	/**
	 * Performs the actual task. Requires that:
	 * <ul>
	 * <li>{@link #createClient()} has been called first</li>
	 * <li>{@link #initializeServer()} has been called first</li>
	 * </ul>
	 *
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	public void doExecute() throws MojoExecutionException {
		String branch = getBranch();
		if (branch == null) {
			getLog().info("Asking Crowdin to build translations");
		} else {
			getLog().info("Asking Crowdin to build translations for branch \"" + branch + "\"");
		}

		HashMap<String, String> parameters = null;
		if (branch != null) {
			parameters = new HashMap<>();
			parameters.put("branch", branch);
		}

		Document document;
		try {
			document = CrowdinAPI.requestGetDocument(client, server, "export", parameters, getLog());
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to build translations at Crowdin: " + e.getMessage(), e);
		}

		String status = document.getRootElement().getAttributeValue("status");
		if (status.equals("skipped")) {
			getLog().warn(
				"Crowdin build skipped either because the files are up to " +
				"date or because the last build was less than 30 minutes ago"
			);
		} else if (status.equals("built")) {
			getLog().info("Crowdin successfully built translations");
		} else {
			getLog().warn("Crowdin replied to build request with an unexpected status: \"" + status + "\"");
		}
	}
}
