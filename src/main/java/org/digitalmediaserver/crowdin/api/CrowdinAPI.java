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
package org.digitalmediaserver.crowdin.api;

import static org.digitalmediaserver.crowdin.AbstractCrowdinMojo.isBlank;
import static org.digitalmediaserver.crowdin.tool.Constants.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Server;
import org.digitalmediaserver.crowdin.api.request.CreateBranchRequest;
import org.digitalmediaserver.crowdin.api.request.CreateBuildRequest;
import org.digitalmediaserver.crowdin.api.response.BranchInfo;
import org.digitalmediaserver.crowdin.api.response.BuildInfo;
import org.digitalmediaserver.crowdin.api.response.DownloadLinkInfo;
import org.digitalmediaserver.crowdin.api.response.FileInfo;
import org.digitalmediaserver.crowdin.api.response.FolderInfo;
import org.digitalmediaserver.crowdin.api.response.ProjectInfo;
import org.digitalmediaserver.crowdin.tool.Constants;
import org.digitalmediaserver.crowdin.tool.CrowdinFileSystem;
import org.eclipse.jgit.util.io.InterruptTimer;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * This is a utility class for calling the Crowdin API and related tasks.
 *
 * @author Nadahar
 */
public class CrowdinAPI {

	//TODO: (Nad) JavaDocs
	public static final Gson GSON = new Gson();

	/**
	 * Not to be instantiated.
	 */
	private CrowdinAPI() {
	}

	public static Gson getGsonInstance() {
		return GSON;
	}

	/**
	 * Creates a new {@link CloseableHttpClient} instance.
	 *
	 * @return The new {@link CloseableHttpClient}.
	 * @throws IOException If an error occurs during the operation.
	 */
	public static CloseableHttpClient createHTTPClient(String projectVersion) throws IOException {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.setUserAgent("crowdin-maven-plugin/" + projectVersion);
		if (System.getProperty(HTTP_PROXY_HOST) != null) {
			String host = System.getProperty(HTTP_PROXY_HOST);
			String port = System.getProperty(HTTP_PROXY_PORT);

			if (port == null) {
				throw new IOException("http.proxyHost without http.proxyPort");
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
					throw new IOException("http.auth.ntlm.domain without http.proxyUser and http.proxyPassword");
				}
				credentials = new NTCredentials(user, password, host, domain);
			} else {
				if (user != null || password != null) {
					if (user == null || password == null) {
						throw new IOException("http.proxyUser and http.proxyPassword go together");
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
		return clientBuilder.build();
	}

	/**
	 * Requests project information including all files and returns the files
	 * element. Branch may be {@code null} in which case the root files
	 * {@link Element} is returned.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param branch the branch name.
	 * @param projectDetails the project details.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The relevant files {@link Element}.
	 * @throws IOException If an error occurs during the operation.
	 */
	@Nullable
	public static Element getFiles(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nullable String branch,
		@Nullable Document projectDetails,
		@Nullable Log logger
	) throws IOException {
		if (projectDetails == null) {
			// Retrieve project informations
			if (logger != null) {
				logger.info("Retrieving Crowdin project information");
			}
			projectDetails = requestPostDocument(httpClient, server, "info", null, null, true, logger);
		}

		// Get Crowdin files
		if (branch != null) {
			Element branchElement = CrowdinFileSystem.getBranch(
				projectDetails.getRootElement().getChild("files").getChildren(),
				branch
			);
			if (branchElement == null || !CrowdinFileSystem.isBranch(branchElement)) {
				throw new IOException("Can't find branch \"" + branch + "\" in Crowdin project information");
			}
			return branchElement.getChild("files");
		}
		return projectDetails.getRootElement().getChild("files");
	}

	@Nonnull
	public static BranchInfo createBranch( //TODO: (Nad) JavaDocs,
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String token,
		@Nonnull String branchName,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (isBlank(branchName)) {
			throw new MojoExecutionException("Cannot create a branch with a blank name");
		}
		CreateBranchRequest payload = new CreateBranchRequest(branchName);

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting to create branch with> " + payload);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.POST,
				"projects/" + projectId + "/branches",
				null,
				token,
				payload,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while creating branch: " + e.getMessage(),
				e
			);
		}

		BranchInfo branch;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			branch = GSON.fromJson(jsonObject.get("data"), BranchInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing branch creation response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with new branch: " + branch);
		}
		return branch;
	}

	@Nonnull
	public static List<BranchInfo> listBranches(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String token,
		@Nullable String branchName, //Doc: filters the result
		@Nullable Log logger
	) throws MojoExecutionException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("limit", "500");
		if (!isBlank(branchName)) {
			parameters.put("name", branchName);
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a list of branches");
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId + "/branches",
				parameters,
				token,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting list of branches: " + e.getMessage(),
				e
			);
		}

		List<BranchInfo> result = new ArrayList<>();
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
			for (JsonElement element : jsonArray) {
				result.add(GSON.fromJson(element.getAsJsonObject().get("data"), BranchInfo.class));
			}
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing list of branches response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with branches: " + result);
		}
		return result;
	}

	@Nonnull
	public static BuildInfo createBuild( //TODO: (Nad) JavaDocs,
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String token,
		@Nullable Long branchId,
		boolean skipUntranslatedStrings,
		boolean skipUntranslatedFiles,
		boolean exportApprovedOnly,
		@Nullable Log logger
	) throws MojoExecutionException {
		CreateBuildRequest payload = new CreateBuildRequest();
		payload.setSkipUntranslatedStrings(skipUntranslatedStrings);
		payload.setSkipUntranslatedFiles(skipUntranslatedFiles);
		payload.setExportApprovedOnly(exportApprovedOnly);
		if (branchId != null) {
			payload.setBranchId(branchId);
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a new build with: " + payload);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.POST,
				"projects/" + projectId + "/translations/builds",
				null,
				token,
				payload,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while triggering build: " + e.getMessage(),
				e
			);
		}

		BuildInfo build;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			build = GSON.fromJson(jsonObject.get("data"), BuildInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing build creation response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with build: " + build);
		}
		return build;
	}

	@Nonnull
	public static BuildInfo getBuildStatus(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		long buildId,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting build status for buildId " + buildId);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId + "/translations/builds/" + buildId,
				null,
				token,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting builds status: " + e.getMessage(),
				e
			);
		}

		BuildInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data").getAsJsonObject(), BuildInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing build status response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with build: " + result);
		}
		return result;
	}

	@Nonnull
	public static List<BuildInfo> listProjectBuilds(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String token,
		@Nullable Long branchId,
		@Nullable Log logger
	) throws MojoExecutionException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("limit", "500");
		if (branchId != null) {
			parameters.put("branchId", branchId.toString());
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a list of builds");
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId + "/translations/builds",
				parameters,
				token,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting list of project builds: " + e.getMessage(),
				e
			);
		}

		List<BuildInfo> result = new ArrayList<>();
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
			for (JsonElement element : jsonArray) {
				result.add(GSON.fromJson(element.getAsJsonObject().get("data"), BuildInfo.class));
			}
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing list of project builds response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with builds: " + result);
		}
		return result;
	}

	@Nonnull
	public static String getProjectStatus(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("limit", "500");

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting translations status");
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId + "/languages/progress",
				parameters,
				token,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting translations status: " + e.getMessage(),
				e
			);
		}

		JsonArray languages = new JsonArray();
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
			for (JsonElement element : jsonArray) {
				languages.add(element.getAsJsonObject().get("data"));
			}
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing translations status response: " + e.getMessage(),
				e
			);
		}
		String result = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(languages);
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with translations status: " + languages.toString());
		}
		return result;
	}

	@Nonnull
	public static ProjectInfo getProjectInfo(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting project info");
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId,
				null,
				token,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting project info: " + e.getMessage(),
				e
			);
		}

		ProjectInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data").getAsJsonObject(), ProjectInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing project info response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with project info: " + result);
		}
		return result;
	}

	@Nonnull
	public static DownloadLinkInfo getDownloadLink(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		long buildId,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting download link for buildId " + buildId);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId + "/translations/builds/" + buildId + "/download",
				null,
				token,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting download link for build: " + e.getMessage(),
				e
			);
		}

		DownloadLinkInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data").getAsJsonObject(), DownloadLinkInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing download link response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with download link: " + result);
		}
		return result;
	}

	@Nonnull
	public static List<FileInfo> listFiles(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nullable Long branchId,
		@Nullable Long directoryId,
		@Nullable String filter,
		boolean recursion,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		int chunkSize = 500;
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting file list"); //TODO: (Nad) More detail?
		}
		HashMap<String, String> parameters = new HashMap<>();
		if (branchId != null) {
			parameters.put("branchId", branchId.toString());
		}
		if (directoryId != null) {
			parameters.put("directoryId", directoryId.toString());
		}
		if (!isBlank(filter)) {
			parameters.put("filter", filter);
		}
		if (recursion) {
			parameters.put("recursion", "1");
		}
		parameters.put("limit", Integer.toString(chunkSize));

		List<FileInfo> result = new ArrayList<>();
		String response;
		int i = 0;
		int prevCount = 0;
		int count;
		while (true) { //TODO: (Nad) Apply "pagination" logic to other relevant calls
			parameters.put("offset", Integer.toString(i * chunkSize));
			try {
				response = CrowdinAPI.sendRequest(
					httpClient,
					HTTPMethod.GET,
					"projects/" + projectId + "/files",
					parameters,
					token,
					null,
					String.class,
					logger
				);
			} catch (HttpException e) {
				throw new MojoExecutionException(
					"Error while requesting file list: " + e.getMessage(),
					e
				);
			}

			try {
				JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
				JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
				for (JsonElement element : jsonArray) {
					result.add(GSON.fromJson(element.getAsJsonObject().get("data"), FileInfo.class));
				}
			} catch (JsonParseException | IllegalStateException e) {
				throw new MojoExecutionException(
					"Error while parsing file list response: " + e.getMessage(),
					e
				);
			}
			count = result.size() - prevCount;
			if (count < chunkSize) {
				break;
			}
			prevCount += count;
			i++;
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with " + result.size() + " files");
		}
		return result;
	}

	@Nonnull
	public static List<FolderInfo> listFolders(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nullable Long branchId,
		@Nullable Long directoryId,
		@Nullable String filter,
		boolean recursion,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		int chunkSize = 500;
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting folder list"); //TODO: (Nad) More detail?
		}
		HashMap<String, String> parameters = new HashMap<>();
		if (branchId != null) {
			parameters.put("branchId", branchId.toString());
		}
		if (directoryId != null) {
			parameters.put("directoryId", directoryId.toString());
		}
		if (!isBlank(filter)) {
			parameters.put("filter", filter);
		}
		if (recursion) {
			parameters.put("recursion", "1");
		}
		parameters.put("limit", Integer.toString(chunkSize));

		List<FolderInfo> result = new ArrayList<>();
		String response;
		int i = 0;
		int prevCount = 0;
		int count;
		while (true) {
			parameters.put("offset", Integer.toString(i * chunkSize));
			try {
				response = CrowdinAPI.sendRequest(
					httpClient,
					HTTPMethod.GET,
					"projects/" + projectId + "/directories",
					parameters,
					token,
					null,
					String.class,
					logger
				);
			} catch (HttpException e) {
				throw new MojoExecutionException(
					"Error while requesting folder list: " + e.getMessage(),
					e
				);
			}

			try {
				JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
				JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
				for (JsonElement element : jsonArray) {
					result.add(GSON.fromJson(element.getAsJsonObject().get("data"), FolderInfo.class));
				}
			} catch (JsonParseException | IllegalStateException e) {
				throw new MojoExecutionException(
					"Error while parsing folder list response: " + e.getMessage(),
					e
				);
			}
			count = result.size() - prevCount;
			if (count < chunkSize) {
				break;
			}
			prevCount += count;
			i++;
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with " + result.size() + " folders");
		}
		return result;
	}

	//TODO: (Nad) JavaDocs all over
	@Nonnull
	public static URI buildURI(
		@Nullable String function,
		@Nullable Map<String, String> parameters
	) throws HttpException {
		StringBuilder uri = new StringBuilder(API_URL).append(function);
		if (parameters != null) {
			boolean first = true;
			String key, value;
			for (Entry<String, String> parameter : parameters.entrySet()) {
				if (!isBlank(key = parameter.getKey())) {
					uri.append(first ? "?" : "&").append(key);
					if (!isBlank(value = parameter.getValue())) {
						uri.append('=').append(value);
					}
					first = false;
				}
			}
		}
		try {
			return new URI(uri.toString());
		} catch (URISyntaxException e) {
			throw new HttpException("Invalid URI \"" + uri.toString() + "\": " + e.getMessage(), e);
		}
	}

	//protected static final Type STRING_MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

	public static <T, V> T sendRequest(
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull HTTPMethod method,
		@Nullable String function,
		@Nullable Map<String, String> parameters,
		@Nullable String token,
		@Nullable V payload,
		@Nonnull Class<T> clazz,
		@Nullable Log logger
	) throws HttpException {
		return sendRequest(httpClient, method, buildURI(function, parameters), token, payload, clazz, logger);
	}

	public static <T, V> T sendRequest( //TODO: (Nad) Handle HTTP timeout
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull HTTPMethod method,
		@Nonnull URI uri,
		@Nullable String token,
		@Nullable V payload,
		@Nonnull Class<T> clazz,
		@Nullable Log logger
	) throws HttpException {
		RequestBuilder requestBuilder = RequestBuilder.create(method.getValue());
		requestBuilder.setUri(uri);
		if (!isBlank(token)) { //TODO: (Nad) Content-type - or any other headers..?
			requestBuilder.addHeader("Authorization", "Bearer " + token);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Calling " + requestBuilder.getUri().toString());
		}

		if (payload != null) {
			if (payload instanceof String) {
				requestBuilder.setEntity(new StringEntity((String) payload, ContentType.APPLICATION_OCTET_STREAM)); //toDO: (Nad) Octet stream basically means "unknown" - it this appropriate?
			} else if (payload instanceof InputStream) {
				requestBuilder.setEntity(new InputStreamEntity((InputStream) payload)); //toDO: (Nad) Will this be used? If so, content type..
			} else { //TODO: (Nad) Is this ever used..?
				//Serialize JSON
				requestBuilder.setEntity(new StringEntity(GSON.toJson(payload), ContentType.APPLICATION_JSON));
			}
		} else if (method == HTTPMethod.POST) {
			requestBuilder.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));
		}

		HttpUriRequest request = requestBuilder.build();
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine == null) {
				throw new HttpException("Request \"" + request.getURI() + "\" returned no status");
			}
			int statusCode = statusLine.getStatusCode();
			if (statusCode < 200 || statusCode >= 300) {
				throw new HttpException("Request \"" + request.getURI() + "\" failed with: " + entityToString(response.getEntity()));
			}
			if (logger != null && logger.isDebugEnabled()) {
				logger.debug("Crowdin API replied with status code " + statusCode);
			}

			if (Void.class.equals(clazz)) {
				return null;
			}
			if (InputStream.class.equals(clazz)) {
				return (T) response.getEntity().getContent();
			}
			if (String.class.equals(clazz)) {
				return (T) entityToString(response.getEntity());
			} else {
				return GSON.fromJson(entityToString(response.getEntity()), clazz);
			}
		} catch (IOException e) {
			throw new HttpException("Error closing HTTPResponse: " + e.getMessage(), e);
		}
	}

	//Doc: Does NOT close the response
	public static CloseableHttpResponse sendStreamRequest( //TODO: (Nad) Handle HTTP timeout
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull HTTPMethod method,
		@Nonnull URI uri,
		@Nullable String token,
		@Nullable Log logger
	) throws HttpException, IOException {
		RequestBuilder requestBuilder = RequestBuilder.create(method.getValue());
		requestBuilder.setUri(uri);
		if (!isBlank(token)) { //TODO: (Nad) Content-type - or any other headers..?
			requestBuilder.addHeader("Authorization", "Bearer " + token);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Calling " + requestBuilder.getUri().toString());
		}

		HttpUriRequest request = requestBuilder.build();
		CloseableHttpResponse response = httpClient.execute(request);
		StatusLine statusLine = response.getStatusLine();
		if (statusLine == null) {
			httpClient.close();
			throw new HttpException("Request \"" + request.getURI() + "\" returned no status");
		}
		int statusCode = statusLine.getStatusCode();
		if (statusCode < 200 || statusCode >= 300) {
			String error = entityToString(response.getEntity());
			httpClient.close();
			throw new HttpException("Request \"" + request.getURI() + "\" failed with: " + error);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin API replied with status code " + statusCode);
		}

		return response;
	}

	@Nullable
	public static String entityToString(@Nullable HttpEntity entity) throws IOException {
		if (entity == null) {
			return null;
		}
		InputStream stream = entity.getContent();
		int bufferSize = 1024;
		char[] buffer = new char[bufferSize];
		StringBuilder result = new StringBuilder();
		Reader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
		int read;
		while ((read = isr.read(buffer, 0, buffer.length)) > 0) {
			result.append(buffer, 0, read);
		}
		return result.toString();
	}

//    private <T, V> T request(String url,
//        V data,
//        HttpRequestConfig config,
//        Class<T> clazz,
//        String method) throws HttpException, HttpBadRequestException
//    {
//		HttpUriRequest request = this.buildRequest(method, url, data, config);
//		try (CloseableHttpResponse response = httpClient.execute(request)) {
//			int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode < 200 || statusCode >= 300) {
//				String error = this.toString(response.getEntity());
//				throw this.jsonTransformer.parse(error, CrowdinApiException.class);
//			}
//			//plain response
//			if (String.class.equals(clazz)) {
//				return (T) this.toString(response.getEntity());
//			}
//			//not interested in response at all
//			if (Void.class.equals(clazz)) {
//				return null;
//			}
//			return this.jsonTransformer.parse(this.toString(response.getEntity()), clazz);
//		} catch (IOException e) {
//			throw HttpException.fromMessage(e.getMessage());
//		}
//    }


	// TODO (Nad) Request stuff
//    private <V> HttpUriRequest buildRequest(String httpMethod, String url, V data, HttpRequestConfig config) {
//        RequestBuilder requestBuilder = RequestBuilder.create(httpMethod);
//        requestBuilder.setUri(URI.create(this.appendUrlParams(url, config.getUrlParams())));
//        requestBuilder.addHeader("Authorization", "Bearer " + this.credentials.getToken());
//        if (data != null) {
//            HttpEntity entity;
//            if (data instanceof InputStream) {
//                entity = new InputStreamEntity((InputStream) data);
//            } else if (data instanceof String) {
//                entity = new StringEntity((String) data, ContentType.APPLICATION_OCTET_STREAM);
//            } else {
//                entity = new StringEntity(this.jsonTransformer.convert(data), ContentType.APPLICATION_JSON);
//            }
//            requestBuilder.setEntity(entity);
//        } else if (HttpPost.METHOD_NAME.equals(httpMethod)) {
//            requestBuilder.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));
//        }
//        Map<String, Object> headers = new HashMap<>();
//        headers.putAll(config.getHeaders());
//        headers.putAll(this.defaultHeaders);
//        for (Map.Entry<String, ?> entry : headers.entrySet()) {
//            requestBuilder = requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
//        }
//        return requestBuilder.build();
//    }
//
//    private String toString(HttpEntity entity) throws IOException {
//        final InputStream stream = entity.getContent();
//        final int bufferSize = 1024;
//        final char[] buffer = new char[bufferSize];
//        final StringBuilder out = new StringBuilder();
//        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
//        int charsRead;
//        while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
//            out.append(buffer, 0, charsRead);
//        }
//        return out.toString();
//    }

	/**
	 * Makes a GET request to the Crowdin API and returns the result as a
	 * {@link Document}.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param method the API method to use.
	 * @param parameters the {@link Map} of API parameters to use.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The retrieved {@link Document}.
	 * @throws IOException If an error occurs during the operation.
	 * @throws IllegalArgumentException If {@code method} is blank.
	 */
	@Nonnull
	public static Document requestGetDocument(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull String method,
		@Nullable Map<String, String> parameters,
		@Nullable Log logger
	) throws IOException {
		if (isBlank(method)) {
			throw new IllegalArgumentException("method cannot be blank");
		}
		StringBuilder url = new StringBuilder(API_URL);
		url.append(server.getUsername()).append("/").append(method);
		boolean first = true;
		if (parameters != null) {
			for (Entry<String, String> parameter : parameters.entrySet()) {
				url.append(first ? "?" : "&").append(parameter.getKey()).append("=").append(parameter.getValue());
				first = false;
			}
		}
		url.append(first ? "?" : "&").append("key=");
		if (logger != null) {
			logger.debug("Calling " + url + "<API Key>");
		}
		url.append(server.getPassword());

		HttpGet getMethod = new HttpGet(url.toString());
		HttpResponse response = httpClient.execute(getMethod);

		int returnCode = response.getStatusLine().getStatusCode();
		if (logger != null) {
			logger.debug("Return code: " + returnCode);
		}
		if (returnCode != 200) {
			throw new IOException("Failed to call API with return code " + returnCode);
		}

		Document document;
		InputStream responseBodyAsStream;
		responseBodyAsStream = response.getEntity().getContent();
		try {
			document = Constants.SAX_BUILDER.build(responseBodyAsStream);
		} catch (JDOMException e) {
			throw new IOException("Failed to parse API reponse: " + e.getMessage(), e);
		}

		if (!document.getRootElement().getName().equals("success")) {
			String code = document.getRootElement().getChildTextNormalize("code");
			String message = document.getRootElement().getChildTextNormalize("message");
			throw new IOException("Failed to call API, response was: " + code + " - " + message);
		}
		return document;
	}

	/**
	 * Makes a POST request to the Crowdin API and returns the result as a
	 * {@link Document}.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param method the API method to use.
	 * @param parameters the {@link Map} of API parameters to use.
	 * @param files the {@link Map} of files to use.
	 * @param mustSucceed whether to throw a {@link IOException} if the returned
	 *            {@link Document} contains an error code.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The retrieved {@link Document}.
	 * @throws IOException If an error occurs during the operation.
	 * @throws IllegalArgumentException If {@code method} is blank.
	 */
	@Nonnull
	public static Document requestPostDocument(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull String method,
		@Nullable Map<String, String> parameters,
		@Nullable Map<String, AbstractContentBody> files,
		boolean mustSucceed,
		@Nullable Log logger
	) throws IOException {
		return requestPostDocument(httpClient, server, method, parameters, files, null, null, mustSucceed, logger);
	}

	/**
	 * Makes a POST request to the Crowdin API and returns the result as a
	 * {@link Document}.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param method the API method to use.
	 * @param parameters the {@link Map} of API parameters to use.
	 * @param files the {@link Map} of files to use.
	 * @param titles the {@link Map} of titles to use.
	 * @param patterns the {@link Map} of patterns to use.
	 * @param mustSucceed whether to throw a {@link IOException} if the returned
	 *            {@link Document} contains an error code.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The retrieved {@link Document}.
	 * @throws IOException If an error occurs during the operation.
	 * @throws IllegalArgumentException If {@code method} is blank.
	 */
	@Nonnull
	public static Document requestPostDocument(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull String method,
		@Nullable Map<String, String> parameters,
		@Nullable Map<String, AbstractContentBody> files,
		@Nullable Map<String, String> titles,
		@Nullable Map<String, String> patterns,
		boolean mustSucceed,
		@Nullable Log logger
	) throws IOException {
		try {
			HttpResponse response = requestPost(httpClient, server, method, parameters, files, titles, patterns, logger);
			int returnCode = response.getStatusLine().getStatusCode();
			if (logger != null) {
				logger.debug("Return code : " + returnCode);
			}
			InputStream responseBodyAsStream = response.getEntity().getContent();
			Document document = SAX_BUILDER.build(responseBodyAsStream);
			if (mustSucceed && document.getRootElement().getName().equals("error")) {
				String code = document.getRootElement().getChildTextNormalize("code");
				String message = document.getRootElement().getChildTextNormalize("message");
				throw new IOException("Failed to call API (" + returnCode + "): " + code + " - " + message);
			}
			return document;
		} catch (JDOMException e) {
			throw new IOException("Failed to parse API reponse: " + e.getMessage(), e);
		}
	}

	/**
	 * Makes a POST request to the Crowdin API and returns the
	 * {@link HttpResponse}.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param method the API method to use.
	 * @param parameters the {@link Map} of API parameters to use.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The resulting {@link HttpResponse}.
	 * @throws IOException If an error occurs during the operation.
	 * @throws IllegalArgumentException If {@code method} is blank.
	 */
	@Nonnull
	public static HttpResponse requestPost(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull String method,
		@Nullable Map<String, String> parameters,
		@Nullable Log logger
	) throws IOException {
		return requestPost(httpClient, server, method, parameters, null, null, null, logger);
	}

	/**
	 * Makes a POST request to the Crowdin API and returns the
	 * {@link HttpResponse}.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param method the API method to use.
	 * @param parameters the {@link Map} of API parameters to use.
	 * @param files the {@link Map} of files to use.
	 * @param titles the {@link Map} of titles to use.
	 * @param patterns the {@link Map} of patterns to use.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The resulting {@link HttpResponse}.
	 * @throws IOException If an error occurs during the operation.
	 * @throws IllegalArgumentException If {@code method} is blank.
	 */
	@Nonnull
	public static HttpResponse requestPost(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull String method,
		@Nullable Map<String, String> parameters,
		@Nullable Map<String, AbstractContentBody> files,
		@Nullable Map<String, String> titles,
		@Nullable Map<String, String> patterns,
		@Nullable Log logger
	) throws IOException {
		if (isBlank(method)) {
			throw new IllegalArgumentException("method cannot be blank");
		}

		StringBuilder url = new StringBuilder(API_URL);
		url.append(server.getUsername()).append("/").append(method).append("?key=");
		if (logger != null) {
			logger.debug("Calling " + url + "<API Key>");
		}
		url.append(server.getPassword());
		HttpPost postMethod = new HttpPost(url.toString());

		MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();

		if (parameters != null && !parameters.isEmpty()) {
			Set<Entry<String, String>> entrySetParameters = parameters.entrySet();
			for (Entry<String, String> entryParameter : entrySetParameters) {
				reqEntityBuilder.addTextBody(entryParameter.getKey(), entryParameter.getValue());
			}
		}
		if (files != null && !files.isEmpty()) {
			for (Entry<String, AbstractContentBody> entryFile : files.entrySet()) {
				String key = "files[" + entryFile.getKey() + "]";
				reqEntityBuilder.addPart(key, entryFile.getValue());
			}
		}

		if (titles != null && !titles.isEmpty()) {
			Set<Entry<String, String>> entrySetTitles = titles.entrySet();
			for (Entry<String, String> entryTitle : entrySetTitles) {
				reqEntityBuilder.addTextBody("titles[" + entryTitle.getKey() + "]", entryTitle.getValue());
			}
		}

		if (patterns != null && !patterns.isEmpty()) {
			Set<Entry<String, String>> entrySetPatterns = patterns.entrySet();
			for (Entry<String, String> entryPattern : entrySetPatterns) {
				reqEntityBuilder.addTextBody("export_patterns[" + entryPattern.getKey() + "]", entryPattern.getValue());
			}
		}

		postMethod.setEntity(reqEntityBuilder.build());

		return httpClient.execute(postMethod);
	}

	public enum HTTPMethod {
		DELETE(HttpDelete.METHOD_NAME),
		GET(HttpGet.METHOD_NAME),
		HEAD(HttpHead.METHOD_NAME),
		PATCH(HttpPatch.METHOD_NAME),
		POST(HttpPost.METHOD_NAME),
		PUT(HttpPut.METHOD_NAME);

		@Nonnull
		private final String value;

		private HTTPMethod(String value) {
			this.value = value;
		}

		@Nonnull
		public String getValue() {
			return value;
		}
	}
}
