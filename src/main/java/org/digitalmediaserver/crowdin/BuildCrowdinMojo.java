package org.digitalmediaserver.crowdin;

import java.io.IOException;
import java.util.HashMap;
import org.apache.maven.plugin.MojoExecutionException;
import org.digitalmediaserver.crowdin.tool.CrowdinAPI;
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
