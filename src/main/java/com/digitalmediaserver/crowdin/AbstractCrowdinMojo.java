package com.digitalmediaserver.crowdin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import com.digitalmediaserver.crowdin.tool.GitUtil;

/**
 * The abstract crowdin Mojo base class.
 *
 * @author Nadahar
 */
public abstract class AbstractCrowdinMojo extends AbstractMojo {

	private static final String HTTP_AUTH_NTLM_DOMAIN = "http.auth.ntlm.domain";

	private static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";

	private static final String HTTP_PROXY_USER = "http.proxyUser";

	private static final String HTTP_PROXY_PORT = "http.proxyPort";

	private static final String HTTP_PROXY_HOST = "http.proxyHost";

	/** The static {@link SAXBuilder} instance */
	protected static final SAXBuilder SAX_BUILDER = new SAXBuilder();

	/** The crowdin API URL */
	protected static final String API_URL = "https://api.crowdin.com/api/project/";

	/**
	 * The current Maven project
	 *
	 * @parameter default-value="${project}"
	 * @readonly
	 * @required
	 */
	protected MavenProject project;

	/**
	 * Sets the {@link #project} value.
	 *
	 * @param value the {@link MavenProject} to set.
	 */
	protected void setProject(MavenProject value) {
		project = value;
	}

	/**
	 * The folder where the language files are located in the project.
	 *
	 * @parameter
	 * @required
	 */
	protected File languageFilesFolder;

	/**
	 * Sets the {@link #languageFilesFolder} value.
	 *
	 * @param folder the {@link File} representing a folder to set.
	 */
	protected void setLanguageFilesFolder(File folder) {
		languageFilesFolder = folder;
	}

	/**
	 * The folder where the downloaded language files should be placed.
	 *
	 * @parameter
	 * @required
	 */
	protected File downloadFolder;

	/**
	 * Sets the {@link #downloadFolder} value.
	 *
	 * @param folder the {@link File} representing a folder to set.
	 */
	protected void setDownloadFolder(File folder) {
		downloadFolder = folder;
	}

	/**
	 * The file where the translations status/statistics should be stored.
	 *
	 * @parameter
	 */
	protected File statusFile;

	/**
	 * Sets the {@link #statusFile} value.
	 *
	 * @param statusFile the status {@link File} to set.
	 */
	protected void setStatusFile(File statusFile) {
		this.statusFile = statusFile;
	}

	/**
	 * The git branch that should be treated as root in crowdin versions
	 * management.
	 *
	 * @parameter property="rootBranch" default-value="master"
	 */
	protected String rootBranch;

	/**
	 * Sets the {@link #rootBranch} value.
	 *
	 * @param branch the Git root branch name.
	 */
	protected void setRootBranch(String branch) {
		rootBranch = branch;
	}

	/**
	 * The Maven Wagon manager to use when obtaining server authentication details.
	 *
	 * @component role="org.apache.maven.artifact.manager.WagonManager"
	 */
	protected WagonManager wagonManager;

	/**
	 * Sets the {@link #wagonManager} value.
	 *
	 * @param wagonManager the {@link WagonManager} to set.
	 */
	protected void setWagonManager(WagonManager wagonManager) {
		this.wagonManager = wagonManager;
	}

	/**
	 * Server id in settings.xml. {@code <username>} is project identifier,
	 * {@code <password>} is API key.
	 *
	 * @parameter property="crowdinServerId"
	 * @required
	 */
	protected String crowdinServerId;

	/**
	 * Sets the {@link #crowdinServerId} value.
	 *
	 * @param serverId The Maven server id to set.
	 * @see #crowdinServerId
	 */
	protected void setCrowdinServerId(String serverId) {
		crowdinServerId = serverId;
	}

	/** The HTTP client */
	protected CloseableHttpClient client;

	/** The {@link AuthenticationInfo} */
	protected AuthenticationInfo authenticationInfo;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		authenticationInfo = wagonManager.getAuthenticationInfo(crowdinServerId);
		if (authenticationInfo == null || authenticationInfo.getUserName() == null || authenticationInfo.getPassword() == null) {
			throw new MojoExecutionException(
				"Failed to find server with id " + crowdinServerId + " in Maven settings (~/.m2/settings.xml)"
			);
		}

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		if (System.getProperty(HTTP_PROXY_HOST) != null) {
			String host = System.getProperty(HTTP_PROXY_HOST);
			String port = System.getProperty(HTTP_PROXY_PORT);

			if (port == null) {
				throw new MojoExecutionException("http.proxyHost without http.proxyPort");
			}
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			HttpHost proxy = new HttpHost(host, Integer.parseInt(port));
			requestConfigBuilder.setProxy(proxy);
			Credentials credentials = null;

			String user = System.getProperty(HTTP_PROXY_USER);
			String password = System.getProperty(HTTP_PROXY_PASSWORD);

			if (System.getProperty(HTTP_AUTH_NTLM_DOMAIN) != null) {
				String domain = System.getProperty(HTTP_AUTH_NTLM_DOMAIN);
				if (user == null || password == null) {
					throw new MojoExecutionException(
							"http.auth.ntlm.domain without http.proxyUser and http.proxyPassword");
				}
				credentials = new NTCredentials(user, password, host, domain);
			} else {
				if (user != null || password != null) {
					if (user == null || password == null) {
						throw new MojoExecutionException("http.proxyUser and http.proxyPassword go together");
					}
					credentials = new UsernamePasswordCredentials(user, password);
				}
			}
			if (credentials != null) {
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(
					new AuthScope(host, Integer.parseInt(port)),
					credentials
				);
				clientBuilder.setDefaultCredentialsProvider(credsProvider);
			}
			clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
		}
		client = clientBuilder.build();
	}

	/**
	 * Checks if the given {@link Element} contains the specified file, folder
	 * or branch.
	 *
	 * @param files the {@link Element} to check.
	 * @param fileName the file name to look for.
	 * @param folder whether the check is for a folder instead of a file.
	 * @param branch whether the check is for a branch instead of a file.
	 * @return {@code true} if the file, folder or branch exists in
	 *         {@code files}, {@code false} otherwise.
	 * @throws MojoExecutionException If an error occurs.
	 */
	protected boolean crowdinContainsFile(Element files, String fileName, boolean folder, boolean branch) throws MojoExecutionException {
		if (folder && branch) {
			throw new MojoExecutionException("fileName can't be both folder and branch!");
		}
		getLog().debug("Check if crowdin project contains " + fileName);
		List<Element> items = files.getChildren("item");
		int slash = fileName.indexOf('/');
		if (slash == -1) {
			if (folder) {
				Element folderElement = crowdinGetFolder(items, fileName);
				if (folderElement != null) {
					getLog().debug("Crowdin project contains folder " + fileName);
					return true;
				}
			} else if (branch) {
				Element branchElement = crowdinGetBranch(items, fileName);
				if (branchElement != null) {
					getLog().debug("Crowdin project contains branch " + fileName);
					return true;
				}
			} else {
				for (Element item : items) {
					if (fileName.equals(item.getChildTextNormalize("name"))) {
						getLog().debug("Crowdin project contains file " + fileName);
						return true;
					}
				}
			}
		} else {
			String folderName = fileName.substring(0, slash);
			String subPath = fileName.substring(slash + 1);
			Element folderElement = crowdinGetFolder(items, folderName);
			if (folderElement != null) {
				Element subFiles = folderElement.getChild("files");
				return crowdinContainsFile(subFiles, subPath, folder, branch);
			}
		}
		getLog().debug("Crowdin project doesn't contain " + (folder ? "folder " : branch ? "branch " : "file ") + fileName);
		return false;
	}

	/**
	 * Checks if the given {@link Element} contains the specified file.
	 *
	 * @param files the {@link Element} to check.
	 * @param fileName the file name to look for.
	 * @return {@code true} if the file exists in {@code files}, {@code false}
	 *         otherwise.
	 * @throws MojoExecutionException If an error occurs.
	 */
	protected boolean crowdinContainsFile(Element files, String fileName) throws MojoExecutionException {
		return crowdinContainsFile(files, fileName, false, false);
	}

	/**
	 * Checks if the given {@link Element} contains the specified folder.
	 *
	 * @param files the {@link Element} to check.
	 * @param folderName the folder name to look for.
	 * @return {@code true} if the folder exists in {@code files}, {@code false}
	 *         otherwise.
	 * @throws MojoExecutionException If an error occurs.
	 */
	protected boolean crowdinContainsFolder(Element files, String folderName) throws MojoExecutionException {
		return crowdinContainsFile(files, folderName, true, false);
	}

	/**
	 * Checks if the given {@link Element} contains the specified branch.
	 *
	 * @param files the {@link Element} to check.
	 * @param branchName the file name to look for.
	 * @return {@code true} if the branch exists in {@code files}, {@code false}
	 *         otherwise.
	 * @throws MojoExecutionException If an error occurs.
	 */
	protected boolean crowdinContainsBranch(Element files, String branchName) throws MojoExecutionException {
		return crowdinContainsFile(files, branchName, false, true);
	}

	/**
	 * Extracts the folder {@link Element} with the given name.
	 *
	 * @param items the {@link List} of {@link Element}s to extract from.
	 * @param folderName the folder name.
	 * @return The matching {@link Element} or {@code null}.
	 */
	protected Element crowdinGetFolder(List<Element> items, String folderName) {
		return crowdinGetFolder(items, folderName, false);
	}

	/**
	 * Extracts the branch {@link Element} with the given name.
	 *
	 * @param items the {@link List} of {@link Element}s to extract from.
	 * @param branchName the branch name.
	 * @return The matching {@link Element} or {@code null}.
	 */
	protected Element crowdinGetBranch(List<Element> items, String branchName) {
		return crowdinGetFolder(items, branchName, true);
	}

	/**
	 * Extracts the folder or branch {@link Element} with the given name.
	 *
	 * @param items the {@link List} of {@link Element}s to extract from.
	 * @param folderName the branch or folder name.
	 * @param branch whether to look for a branch instead of a folder.
	 * @return The matching {@link Element} or {@code null}.
	 */
	protected Element crowdinGetFolder(List<Element> items, String folderName, boolean branch) {
		for (Element item : items) {
			if (folderName.equals(item.getChildTextNormalize("name"))) {
				if (branch && crowdinIsBranch(item)) {
					return item;
				} else if (!branch && crowdinIsFolder(item)) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Verifies whether a given {@link Element} is a crowdin folder.
	 *
	 * @param item the {@link Element} to check.
	 * @return true if {@code item} is a crowdin folder, {@code false}
	 *         otherwise.
	 */
	protected boolean crowdinIsFolder(Element item) {
		return item.getChild("node_type") != null && item.getChildTextNormalize("node_type").equalsIgnoreCase("directory");
	}

	/**
	 * Verifies whether a given {@link Element} is a crowdin branch.
	 *
	 * @param item the {@link Element} to check.
	 * @return true if {@code item} is a crowdin branch, {@code false}
	 *         otherwise.
	 */
	protected boolean crowdinIsBranch(Element item) {
		return item.getChild("node_type") != null && item.getChildTextNormalize("node_type").equalsIgnoreCase("branch");
	}

	/**
	 * Creates a new folder at crowdin.
	 *
	 * @param folderName the name of the new folder.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected void crowdinCreateFolder(String folderName) throws MojoExecutionException {
		getLog().info("Creating " + folderName + " folder on crowdin");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", folderName);
		crowdinRequestAPI("add-directory", parameters, null, true);
	}

	/**
	 * Creates a new branch at crowdin.
	 *
	 * @param branchName the name of the new branch.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected void crowdinCreateBranch(String branchName) throws MojoExecutionException {
		getLog().info("Creating " + branchName + " branch on crowdin");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", branchName);
		parameters.put("is_branch", "1");
		crowdinRequestAPI("add-directory", parameters, null, true);
	}


	/**
	 * Makes a request to the crowdin API and returns the result as a
	 * {@link Document}.
	 *
	 * @param method the API method to use.
	 * @param parameters the {@link Map} of API parameters to use.
	 * @param files the {@link Map} of files to use.
	 * @param mustSucceed whether to throw a {@link MojoExecutionException} if
	 *            the returned {@link Document} contains an error code.
	 * @return The retrieved {@link Document}.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected Document crowdinRequestAPI(
		String method,
		Map<String, String> parameters,
		Map<String, File> files,
		boolean mustSucceed
	) throws MojoExecutionException {
		return crowdinRequestAPI(method, parameters, files, null, null, mustSucceed);
	}

	/**
	 * Makes a request to the crowdin API and returns the result as a
	 * {@link Document}.
	 *
	 * @param method the API method to use.
	 * @param parameters the {@link Map} of API parameters to use.
	 * @param files the {@link Map} of files to use.
	 * @param titles the {@link Map} of titles to use.
	 * @param patterns the {@link Map} of patterns to use.
	 * @param mustSucceed whether to throw a {@link MojoExecutionException} if
	 *            the returned {@link Document} contains an error code.
	 * @return The retrieved {@link Document}.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected Document crowdinRequestAPI(
		String method,
		Map<String, String> parameters,
		Map<String, File> files,
		Map<String, String> titles,
		Map<String, String> patterns,
		boolean mustSucceed
	) throws MojoExecutionException {
		try {
			StringBuilder url = new StringBuilder(API_URL);
			url.append(authenticationInfo.getUserName()).append("/").append(method).append("?key=");
			getLog().debug("Calling " + url + "<API Key>");
			url.append(authenticationInfo.getPassword());
			HttpPost postMethod = new HttpPost(url.toString());

			MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();

			if (parameters != null) {
				Set<Entry<String, String>> entrySetParameters = parameters.entrySet();
				for (Entry<String, String> entryParameter : entrySetParameters) {
					reqEntityBuilder.addTextBody(entryParameter.getKey(), entryParameter.getValue());
				}
			}
			if (files != null) {
				Set<Entry<String, File>> entrySetFiles = files.entrySet();
				for (Entry<String, File> entryFile : entrySetFiles) {
					String key = "files[" + entryFile.getKey() + "]";
					reqEntityBuilder.addPart(key, new FileBody(entryFile.getValue()));
				}
			}

			if (titles != null) {
				Set<Entry<String, String>> entrySetTitles = titles.entrySet();
				for (Entry<String, String> entryTitle : entrySetTitles) {
					reqEntityBuilder.addTextBody("titles[" + entryTitle.getKey() + "]", entryTitle.getValue());
				}
			}

			if (patterns != null) {
				Set<Entry<String, String>> entrySetPatterns = patterns.entrySet();
				for (Entry<String, String> entryPattern : entrySetPatterns) {
					reqEntityBuilder.addTextBody("export_patterns[" + entryPattern.getKey() + "]", entryPattern.getValue());
				}
			}

			postMethod.setEntity(reqEntityBuilder.build());

			// getLog().debug("Sent request : ");
			// ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// reqEntity.writeTo(bos);
			// getLog().debug(bos.toString());

			HttpResponse response = client.execute(postMethod);
			int returnCode = response.getStatusLine().getStatusCode();
			getLog().debug("Return code : " + returnCode);
			InputStream responseBodyAsStream = response.getEntity().getContent();
			Document document = SAX_BUILDER.build(responseBodyAsStream);
			if (mustSucceed && document.getRootElement().getName().equals("error")) {
				String code = document.getRootElement().getChildTextNormalize("code");
				String message = document.getRootElement().getChildTextNormalize("message");
				throw new MojoExecutionException("Failed to call API - " + code + " - " + message);
			}
			return document;
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to call API: " + e.getMessage(), e);
		} catch (JDOMException e) {
			throw new MojoExecutionException("Failed to call API: " + e.getMessage(), e);
		}
	}

	/**
	 * Requests project information including all files and returns the files
	 * element. Branch may be {@code null} in which case the root files
	 * {@link Element} is returned.
	 *
	 * @param branch the branch name.
	 * @param projectDetails the project details.
	 * @return The relevant files {@link Element}.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected Element getCrowdinFiles(String branch, Document projectDetails) throws MojoExecutionException {

		if (projectDetails == null) {
			// Retrieve project informations
			getLog().info("Retrieving crowdin project information");
			projectDetails = crowdinRequestAPI("info", null, null, true);
		}

		// Get crowdin files
		if (branch != null) {
			Element branchElement = crowdinGetBranch(projectDetails.getRootElement().getChild("files").getChildren(), branch);
			if (branchElement == null || !crowdinIsBranch(branchElement)) {
				throw new MojoExecutionException("Can't find branch \"" + branch + "\" in crowdin project information");
			}
			return branchElement.getChild("files");
		}
		return projectDetails.getRootElement().getChild("files");
	}

	/**
	 * Gets the Maven id from the given {@link Artifact}.
	 *
	 * @param artifact the {@link Artifact} whose Maven id to return.
	 * @return The Maven id.
	 */
	protected String getMavenId(Artifact artifact) {
		return artifact.getGroupId() + "." + artifact.getArtifactId();
	}

	/**
	 * Gets the crowdin branch name that matches the name of the current Git
	 * branch.
	 *
	 * @return The branch name or {@code null} if the current git branch is the
	 *         crowdin root.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected String getBranch() throws MojoExecutionException {
		return getBranch(false, null);
	}

	/**
	 * Creates or gets the crowdin branch name that matches the name of the
	 * current Git branch.
	 *
	 * @param create whether the branch should be created at crowdin if it
	 *            doesn't exist.
	 * @param projectDetails the {@link Document} containing the project
	 *            details. If {@code null} the project details will be retrieved
	 *            from crowdin.
	 * @return The branch name or {@code null} if the current git branch is the
	 *         crowdin root.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	protected String getBranch(boolean create, Document projectDetails) throws MojoExecutionException {

		getLog().info("Determining git branch");
		String branch = GitUtil.getBranch(project.getBasedir(), getLog());
		if (branch == null || branch.trim().equals("")) {
			throw new MojoExecutionException("Could not determine current git branch");
		}
		if (branch.equals(rootBranch)) {
			return null;
		} else if (crowdinContainsBranch(getCrowdinFiles(null, projectDetails), branch)) {
			return branch;
		} else if (create) {
			crowdinCreateBranch(branch);
			return branch;
		} else {
			throw new MojoExecutionException("Crowdin project doesn't contain branch \"" + branch + "\". Please push this branch first.");
		}
	}
}
