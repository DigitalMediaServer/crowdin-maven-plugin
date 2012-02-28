package net.crowdin.maven;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public abstract class AbstractCrowdinMojo extends AbstractMojo {

	protected static final SAXBuilder saxBuilder = new SAXBuilder();

	/**
	 * The current Maven project
	 * 
	 * @parameter expression="${project}"
	 * @readonly
	 * @required
	 */
	protected MavenProject project;

	/**
	 * The Maven Wagon manager to use when obtaining server authentication
	 * details.
	 * 
	 * @component role="org.apache.maven.artifact.manager.WagonManager"
	 */
	protected WagonManager wagonManager;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 */
	protected MavenProjectHelper projectHelper;

	/**
	 * 
	 * Server id in settings.xml. username is project identifier, password is
	 * API key
	 * 
	 * @parameter expression= "${crowdinServerId}"
	 * @required
	 */
	protected String crowdinServerId;

	/**
	 * The directory where the messages can be fund.
	 * 
	 * @parameter expression="${project.basedir}/src/main/messages"
	 * @required
	 */
	protected File messagesInputDirectory;

	/**
	 * The directory where the messages can be fund.
	 * 
	 * @parameter expression="${project.basedir}/src/main/crowdin"
	 * @required
	 */
	protected File messagesOutputDirectory;

	protected HttpClient client;
	protected AuthenticationInfo authenticationInfo;

	public void execute() throws MojoExecutionException, MojoFailureException {
		authenticationInfo = wagonManager.getAuthenticationInfo(crowdinServerId);
		if (authenticationInfo == null) {
			throw new MojoExecutionException("Failed to find server with id " + crowdinServerId + " in Maven settings (~/.m2/settings.xml)");
		}

		client = new HttpClient();
	}

	protected boolean crowdinContainsFile(Element files, String fileName, boolean folder) {
		getLog().debug("Check that crowdin project contains " + fileName);
		@SuppressWarnings("unchecked")
		List<Element> items = files.getChildren("item");
		int slash = fileName.indexOf('/');
		if (slash == -1) {
			if (folder) {
				Element folderElement = crowdinGetFolder(items, fileName);
				if (folderElement != null) {
					getLog().debug("Crowdin project contains " + fileName);
					return true;
				}
			} else {
				for (Element item : items) {
					if (fileName.equals(item.getChildTextNormalize("name"))) {
						getLog().debug("Crowdin project contains " + fileName);
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
				return crowdinContainsFile(subFiles, subPath, folder);
			}
		}
		getLog().debug("Crowdin project does not contain " + fileName);
		return false;
	}

	protected Element crowdinGetFolder(List<Element> items, String fileName) {
		for (Element item : items) {
			if (fileName.equals(item.getChildTextNormalize("name"))) {
				if (crowdinIsFolder(item)) {
					return item;
				}
			}
		}
		return null;
	}

	protected boolean crowdinIsFolder(Element item) {
		return item.getChild("node_type") != null && "directory".equals(item.getChildTextNormalize("node_type"));
	}

	protected Document crowdinRequestAPI(String method, Map<String, String> parameters, Map<String, File> files, boolean shallSuccess)
			throws MojoExecutionException {
		try {
			String uri = "http://api.crowdin.net/api/project/" + authenticationInfo.getUserName() + "/" + method + "?key=" + authenticationInfo.getPassword();
			getLog().debug("Calling " + uri);
			PostMethod postMethod = new PostMethod(uri);
			List<Part> parts = new ArrayList<Part>();
			if (parameters != null) {
				Set<Entry<String, String>> entrySetParameters = parameters.entrySet();
				for (Entry<String, String> entryParameter : entrySetParameters) {
					parts.add(new StringPart(entryParameter.getKey(), entryParameter.getValue()));
				}
			}
			if (files != null) {
				Set<Entry<String, File>> entrySetFiles = files.entrySet();
				for (Entry<String, File> entryFile : entrySetFiles) {
					String key = "files[" + entryFile.getKey() + "]";
					parts.add(new FilePart(key, entryFile.getValue()));
				}
			}
			postMethod.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), postMethod.getParams()));
			getLog().debug("Sent request : ");

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			postMethod.getRequestEntity().writeRequest(bos);
			getLog().debug(bos.toString());

			int returnCode = client.executeMethod(postMethod);
			getLog().debug("Return code : " + returnCode);
			getLog().debug("Response : " + postMethod.getResponseBodyAsString());
			InputStream responseBodyAsStream = postMethod.getResponseBodyAsStream();
			Document document = saxBuilder.build(responseBodyAsStream);
			if (shallSuccess && document.getRootElement().getName().equals("error")) {
				String code = document.getRootElement().getChildTextNormalize("code");
				String message = document.getRootElement().getChildTextNormalize("message");
				throw new MojoExecutionException("Failed to call API - " + code + " - " + message);
			}
			return document;
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to call API", e);
		}
	}

}
