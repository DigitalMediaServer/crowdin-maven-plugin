package org.digitalmediaserver.crowdin;

import java.io.InputStream;
import org.apache.http.client.methods.HttpGet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.http.HttpResponse;
import org.digitalmediaserver.crowdin.tool.Constants;
import org.jdom2.Document;

/**
 * Build crowdin translations for this project to include the latest changes.
 *
 * @goal build
 * @threadSafe
 */
public class BuildCrowdinMojo extends AbstractCrowdinMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		String branch = getBranch();
		getLog().info("Asking crowdin to build translations");

		StringBuilder url = new StringBuilder(Constants.API_URL);
		url.append(server.getUsername()).append("/export?");
		if (branch != null) {
			url.append("branch=").append(branch).append("&");
		}
		url.append("key=");
		getLog().debug("Calling " + url + "<API Key>");
		url.append(server.getPassword());

		HttpGet getMethod = new HttpGet(url.toString());
		HttpResponse response = null;
		try {
			response = client.execute(getMethod);
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to call API", e);
		}

		Document document;
		InputStream responseBodyAsStream;
		try {
			responseBodyAsStream = response.getEntity().getContent();
			document = Constants.SAX_BUILDER.build(responseBodyAsStream);
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to call API", e);
		}

		if (!document.getRootElement().getName().equals("success")) {
			String code = document.getRootElement().getChildTextNormalize("code");
			String message = document.getRootElement().getChildTextNormalize("message");
			throw new MojoExecutionException("Failed to call API - " + code + " - " + message);
		}

		String status = document.getRootElement().getAttributeValue("status");
		if (status.equals("skipped")) {
			getLog().warn(
				"crowdin build skipped either because the files are up to " +
				"date or because the last build was less than 30 minutes ago"
			);
		} else if (status.equals("built")) {
			getLog().info("crowdin translations successfully built");
		} else {
			getLog().warn("crowdin replied to build request with unexpected status: " + status);
		}

		int returnCode = response.getStatusLine().getStatusCode();
		getLog().debug("Return code : " + returnCode);
		if (returnCode != 200) {
			throw new MojoExecutionException("Failed to build translations at crowdin with return code " + returnCode);
		}
	}
}
