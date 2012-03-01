package net.crowdin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Export crowdin translations in this project, for a fresh translation file
 * 
 * @goal export
 * @aggregator
 * @threadSafe
 */
public class ExportCrowdinMojo extends AbstractCrowdinMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		getLog().info("Asking crowdin to export translations");
		crowdinRequestAPI("export", null, null, true);

	}
}
