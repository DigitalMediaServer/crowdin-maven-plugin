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

import static org.digitalmediaserver.crowdin.tool.Constants.*;
import static org.digitalmediaserver.crowdin.tool.StringUtil.isBlank;
import static org.digitalmediaserver.crowdin.tool.StringUtil.isNotBlank;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
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
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.digitalmediaserver.crowdin.api.request.CreateBranchRequest;
import org.digitalmediaserver.crowdin.api.request.CreateBuildRequest;
import org.digitalmediaserver.crowdin.api.request.CreateFileRequest;
import org.digitalmediaserver.crowdin.api.request.CreateFolderRequest;
import org.digitalmediaserver.crowdin.api.request.UpdateFileRequest;
import org.digitalmediaserver.crowdin.api.response.BranchInfo;
import org.digitalmediaserver.crowdin.api.response.BuildInfo;
import org.digitalmediaserver.crowdin.api.response.DownloadLinkInfo;
import org.digitalmediaserver.crowdin.api.response.FileInfo;
import org.digitalmediaserver.crowdin.api.response.FolderInfo;
import org.digitalmediaserver.crowdin.api.response.ProjectInfo;
import org.digitalmediaserver.crowdin.api.response.StorageInfo;
import org.digitalmediaserver.crowdin.configuration.UpdateOption;
import org.digitalmediaserver.crowdin.tool.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


/**
 * This is a utility class for calling the Crowdin API and related tasks.
 *
 * @author Nadahar
 */
public class CrowdinAPI {

	/** The static {@link Gson} instance used for JSON (de)serialization */
	public static final Gson GSON = new Gson();

	/**
	 * Not to be instantiated.
	 */
	private CrowdinAPI() {
	}

	/**
	 * @return The static {@link Gson} instance used for JSON (de)serialization.
	 */
	public static Gson getGsonInstance() {
		return GSON;
	}

	/**
	 * Creates a new {@link CloseableHttpClient} instance.
	 *
	 * @param projectVersion a {@link String} containing the current version of
	 *            this plugin.
	 * @param timeout the timeout in seconds for HTTP operations.
	 * @return The new {@link CloseableHttpClient}.
	 * @throws IOException If an error occurs during the operation.
	 */
	public static CloseableHttpClient createHTTPClient(
		String projectVersion,
		@Nullable Integer timeout
	) throws IOException {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.setUserAgent("crowdin-maven-plugin/" + projectVersion);

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		int timeoutMS;
		if (timeout != null && (timeoutMS = timeout.intValue() * 1000) > 0) {
			requestConfigBuilder.setConnectionRequestTimeout(timeoutMS);
			requestConfigBuilder.setConnectTimeout(timeoutMS);
			requestConfigBuilder.setSocketTimeout(timeoutMS);
		}

		if (System.getProperty(HTTP_PROXY_HOST) != null) {
			String host = System.getProperty(HTTP_PROXY_HOST);
			String port = System.getProperty(HTTP_PROXY_PORT);

			if (port == null) {
				throw new IOException("http.proxyHost without http.proxyPort");
			}
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
		}
		clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
		return clientBuilder.build();
	}

	/**
	 * Queries Crowdin for information about the specified folder. If the folder
	 * doesn't exist, it can optionally be created.
	 * <p>
	 * <b>Note:</b> This method will <i>not</i> strip a path element that
	 * doesn't end in a separator, but will consider the last element to be a
	 * folder as well. Example: {@code foo/bar} - both {@code foo} and
	 * {@code bar} is assumed to be folders.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param projectId the Crowdin project ID.
	 * @param branch the {@link BranchInfo} if the folder belongs to a branch.
	 * @param folderPath the folder path.
	 * @param create if {@code true} the folder will be created if it doesn't
	 *            exist, if {@code false} the method will return {@code null}.
	 * @param token the API token.
	 * @param logger the {@link Log} to log to.
	 * @return The resulting {@link FolderInfo} if the folder exists or is
	 *         created, {@code null} otherwise.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nullable
	public static FolderInfo getFolder(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nullable BranchInfo branch,
		@Nonnull String folderPath,
		boolean create,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (isBlank(folderPath)) {
			return null;
		}
		FolderInfo result = null;
		List<FolderInfo> folders;
		boolean found;
		Long parentFolderId = null;
		List<String> elements = FileUtil.splitPath(folderPath, false);
		for (String element : elements) {
			found = false;
			folders = listFolders(
				httpClient,
				projectId,
				branch == null || parentFolderId != null ? null : branch.getId(),
				parentFolderId,
				element,
				false,
				token,
				logger
			);
			for (FolderInfo folder : folders) {
				if (
					element.equals(folder.getName()) && (
						(branch == null && folder.getBranchId() == null) ||
						(branch != null && folder.getBranchId() == branch.getId())
					) && (
						(parentFolderId == null && folder.getDirectoryId() == null) ||
						(parentFolderId != null && parentFolderId.equals(folder.getDirectoryId()))
					)
				) {
					found = true;
					result = folder;
					parentFolderId = Long.valueOf(folder.getId());
					break;
				}
			}
			if (!found) {
				if (!create) {
					return null;
				}
				//Create folder
				FolderInfo folder = createFolder(
					httpClient,
					projectId,
					element,
					branch == null || parentFolderId != null ? null : branch.getId(),
					parentFolderId,
					token,
					logger
				);
				result = folder;
				parentFolderId = Long.valueOf(folder.getId());
			}
		}

		return result;
	}

	/**
	 * Creates a branch at Crowdin.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param projectId the Crowdin project ID.
	 * @param token the API token.
	 * @param branchName the name of the new branch.
	 * @param logger the {@link Log} to log to.
	 * @return The {@link BranchInfo} for the newly created branch.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nonnull
	public static BranchInfo createBranch(
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
				null,
				token,
				payload,
				ContentType.APPLICATION_JSON,
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

	/**
	 * Queries Crowdin for a list of branches.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param projectId the Crowdin project ID.
	 * @param token the API token.
	 * @param branchName an optional filter for filtering the results.
	 * @param logger the {@link Log} to log to.
	 * @return The resulting {@link List} of {@link BranchInfo} instances.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nonnull
	public static List<BranchInfo> listBranches(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String token,
		@Nullable String branchName,
		@Nullable Log logger
	) throws MojoExecutionException {
		int chunkSize = 500;
		HashMap<String, String> parameters = new LinkedHashMap<>();
		parameters.put("limit", Integer.toString(chunkSize));
		if (isNotBlank(branchName)) {
			parameters.put("name", branchName);
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a list of branches");
		}

		List<BranchInfo> result = new ArrayList<>();
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
					"projects/" + projectId + "/branches",
					parameters,
					null,
					token,
					null,
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
			count = result.size() - prevCount;
			if (count < chunkSize) {
				break;
			}
			prevCount += count;
			i++;
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with branches: " + result);
		}
		return result;
	}

	/**
	 * Triggers a build at Crowdin.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param projectId the Crowdin project ID.
	 * @param token the API token.
	 * @param branchId the branch ID for the branch to build.
	 * @param skipUntranslatedStrings whether to skip untranslated strings.
	 *            Can't be combined with {@code skipUntranslatedFiles}.
	 * @param skipUntranslatedFiles whether to skip untranslated files. Can't be
	 *            combined with {@code skipUntranslatedStrings}.
	 * @param exportApprovedOnly whether to export approved translations only.
	 * @param logger the {@link Log} to log to.
	 * @return The resulting {@link BuildInfo}.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
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
				null,
				token,
				payload,
				ContentType.APPLICATION_JSON,
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

	/**
	 * Queries Crowdin for the status of the specified build.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param projectId the Crowdin project ID.
	 * @param buildId the build ID for which to get the build status.
	 * @param token the API token.
	 * @param logger the {@link Log} to log to.
	 * @return The resulting {@link BuildInfo}.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
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
				null,
				token,
				null,
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
		int chunkSize = 500;
		HashMap<String, String> parameters = new LinkedHashMap<>();
		parameters.put("limit", Integer.toString(chunkSize));
		if (branchId != null) {
			parameters.put("branchId", branchId.toString());
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a list of builds");
		}

		List<BuildInfo> result = new ArrayList<>();
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
					"projects/" + projectId + "/translations/builds",
					parameters,
					null,
					token,
					null,
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
			count = result.size() - prevCount;
			if (count < chunkSize) {
				break;
			}
			prevCount += count;
			i++;
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
		int chunkSize = 500;
		HashMap<String, String> parameters = new LinkedHashMap<>();
		parameters.put("limit", Integer.toString(chunkSize));

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting translations status");
		}

		String response;
		JsonArray languages = new JsonArray();
		int i = 0;
		int prevCount = 0;
		int count;
		while (true) {
			parameters.put("offset", Integer.toString(i * chunkSize));
			try {
				response = CrowdinAPI.sendRequest(
					httpClient,
					HTTPMethod.GET,
					"projects/" + projectId + "/languages/progress",
					parameters,
					null,
					token,
					null,
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
			count = languages.size() - prevCount;
			if (count < chunkSize) {
				break;
			}
			prevCount += count;
			i++;
		}

		String result = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(languages);
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with translations status for " + languages.size() + " languages");
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
				null,
				token,
				null,
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
				null,
				token,
				null,
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
		@Nullable Long folderId, //Doc: Branch and directory can't be used together
		@Nullable String filter,
		boolean recursion,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		int chunkSize = 500;
		if (logger != null && logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder().append("Requesting file list");
			if (branchId != null) {
				sb.append(" for branch ID ").append(branchId.toString());
			}
			if (folderId != null) {
				sb.append(" for folder ID ").append(folderId.toString());
			}
			if (filter != null) {
				sb.append(" with filter \"").append(filter).append('\"');
			}
			logger.debug(sb.toString());
		}
		HashMap<String, String> parameters = new LinkedHashMap<>();
		if (branchId != null) {
			parameters.put("branchId", branchId.toString());
		}
		if (folderId != null) {
			parameters.put("directoryId", folderId.toString());
		}
		if (isNotBlank(filter)) {
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
		while (true) {
			parameters.put("offset", Integer.toString(i * chunkSize));
			try {
				response = CrowdinAPI.sendRequest(
					httpClient,
					HTTPMethod.GET,
					"projects/" + projectId + "/files",
					parameters,
					null,
					token,
					null,
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

	@Nullable
	public static FileInfo getFileIfExists( //Doc: Will strip path elements from fileName
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nullable BranchInfo branch,
		@Nullable FolderInfo folder, //Doc: Folder takes precedence over branch
		@Nullable String fileName,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException { //Doc: Returns null if file doesn't exist
		if (isBlank(fileName)) {
			return null;
		}

		List<String> elements = FileUtil.splitPath(fileName, false);
		if (elements.isEmpty()) {
			return null;
		}
		String name = elements.get(elements.size() - 1);
		FileInfo result = null;
		List<FileInfo> files = listFiles(
			httpClient,
			projectId,
			folder != null || branch == null ? null : branch.getId(),
			folder == null ? null : folder.getId(),
			name,
			false,
			token,
			logger
		);
		for (FileInfo fileInfo : files) {
			if (
				name.equals(fileInfo.getName()) && (
					(branch == null && fileInfo.getBranchId() == null) ||
					(branch != null && fileInfo.getBranchId() == branch.getId())
				) && (
					(folder == null && fileInfo.getDirectoryId() == null) ||
					(folder != null && fileInfo.getDirectoryId() == folder.getId())
				)
			) {
				result = fileInfo;
				break;
			}
		}

		return result;
	}

	@Nonnull
	public static FileInfo getFile(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		long fileId,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting file info for ID " + fileId);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId + "/files/" + fileId,
				null,
				null,
				token,
				null,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting file info: " + e.getMessage(),
				e
			);
		}

		FileInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data"), FileInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing file info response: " + e.getMessage(),
				e
			);
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with file info: " + result);
		}
		return result;
	}

	@Nonnull
	public static List<FolderInfo> listFolders(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nullable Long branchId,
		@Nullable Long folderId, //Doc: Branch and directory can't be used together
		@Nullable String filter,
		boolean recursion,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		int chunkSize = 500;
		if (logger != null && logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder().append("Requesting folder list");
			if (branchId != null) {
				sb.append(" for branch ID ").append(branchId.toString());
			}
			if (folderId != null) {
				sb.append(" for folder ID ").append(folderId.toString());
			}
			if (filter != null) {
				sb.append(" with filter \"").append(filter).append('\"');
			}
			logger.debug(sb.toString());
		}
		HashMap<String, String> parameters = new LinkedHashMap<>();
		if (branchId != null) {
			parameters.put("branchId", branchId.toString());
		}
		if (folderId != null) {
			parameters.put("directoryId", folderId.toString());
		}
		if (isNotBlank(filter)) {
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
					null,
					token,
					null,
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

	@Nonnull
	public static FolderInfo getFolder(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		long directoryId,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting folder info for ID " + directoryId);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.GET,
				"projects/" + projectId + "/directories/" + directoryId,
				null,
				null,
				token,
				null,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while requesting folder info: " + e.getMessage(),
				e
			);
		}

		FolderInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data"), FolderInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing folder info response: " + e.getMessage(),
				e
			);
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with folder info: " + result);
		}
		return result;
	}

	@Nonnull
	public static FolderInfo createFolder(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull String name,
		@Nullable Long branchId,
		@Nullable Long directoryId, //Doc: branch and dir can't be specified at the same call
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		CreateFolderRequest payload = new CreateFolderRequest(name);
		payload.setBranchId(branchId);
		payload.setDirectoryId(directoryId);

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a new folder with: " + payload);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.POST,
				"projects/" + projectId + "/directories",
				null,
				null,
				token,
				payload,
				ContentType.APPLICATION_JSON,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while creating folder: " + e.getMessage(),
				e
			);
		}

		FolderInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data"), FolderInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing folder creation response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with new folder: " + result);
		}
		return result;
	}

	@Nonnull
	public static FileInfo createFile(
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		@Nonnull StorageInfo storage,
		@Nonnull String name,
		@Nullable FileType type,
		@Nullable Long branchId,
		@Nullable Long directoryId, //Doc: branch and dir can't be specified at the same call
		@Nullable String title,
		@Nullable String context,
		@Nullable String[] excludedTargetLanguages,
		@Nullable FileExportOptions exportOptions,
		@Nullable FileImportOptions importOptions,
		@Nullable Integer parserVersion,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		CreateFileRequest payload = new CreateFileRequest(storage, name);
		payload.setBranchId(branchId);
		if (isNotBlank(context)) {
			payload.setContext(context);
		}
		payload.setDirectoryId(directoryId);
		payload.setExcludedTargetLanguages(excludedTargetLanguages);
		payload.setExportOptions(exportOptions);
		payload.setImportOptions(importOptions);
		payload.setParserVersion(parserVersion);
		if (isNotBlank(title)) {
			payload.setTitle(title);
		}
		payload.setType(type);

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a new file with: " + payload);
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.POST,
				"projects/" + projectId + "/files",
				null,
				null,
				token,
				payload,
				ContentType.APPLICATION_JSON,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while creating file: " + e.getMessage(),
				e
			);
		}

		FileInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data"), FileInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing file creation response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with new file: " + result);
		}
		return result;
	}

	@Nullable
	public static FileInfo updateFile( //Doc: Returns null if not modified
		@Nonnull CloseableHttpClient httpClient,
		long projectId,
		long fileId,
		@Nonnull StorageInfo storage,
		@Nullable UpdateOption updateOption,
		@Nullable FileImportOptions importOptions,
		@Nullable FileExportOptions exportOptions,
		@Nullable Boolean replaceModifiedContext,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		UpdateFileRequest payload = new UpdateFileRequest(storage);
		payload.setUpdateOption(updateOption);
		payload.setImportOptions(importOptions);
		payload.setExportOptions(exportOptions);
		payload.setReplaceModifiedContext(replaceModifiedContext);

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a file update with: " + payload);
		}

		RequestBuilder requestBuilder = RequestBuilder.create(HttpPut.METHOD_NAME);
		requestBuilder.setUri(API_URL + "projects/" + projectId + "/files/" + fileId);
		requestBuilder.addHeader("Authorization", "Bearer " + token);
		requestBuilder.setEntity(new StringEntity(GSON.toJson(payload), ContentType.APPLICATION_JSON));

		String responseContent, responseStatus;
		try {
			HttpUriRequest request = requestBuilder.build();
			if (logger != null && logger.isDebugEnabled()) {
				logger.debug("Calling " + request.getURI().toString());
			}
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

				responseContent = entityToString(response.getEntity());
				Header[] headers = response.getHeaders("Crowdin-API-Content-Status");
				responseStatus = headers.length > 0 ? headers[0].getValue() : null;
			} catch (IOException e) {
				throw new HttpException("Error closing HTTPResponse: " + e.getMessage(), e);
			}
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while updating file: " + e.getMessage(),
				e
			);
		}

		FileInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(responseContent).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data"), FileInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing file update response: " + e.getMessage(),
				e
			);
		}
		boolean modified = !"not-modified".equals(responseStatus);
		if (logger != null && logger.isDebugEnabled()) {
			if (modified) {
				logger.debug("Crowdin responded with updated file: " + result);
			} else {
				logger.debug("Crowdin did not modify file: " + result);
			}
		}
		return modified ? result : null;
	}

	/**
	 * Lists the Crowdin storages for the current API user.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param token the API token.
	 * @param logger the {@link Log} to log to.
	 * @return The {@link List} of {@link StorageInfo} instances.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nonnull
	public static List<StorageInfo> listStorages(
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		int chunkSize = 500;
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting list of storages");
		}
		HashMap<String, String> parameters = new LinkedHashMap<>();
		parameters.put("limit", Integer.toString(chunkSize));

		List<StorageInfo> result = new ArrayList<>();
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
					"storages",
					parameters,
					null,
					token,
					null,
					null,
					String.class,
					logger
				);
			} catch (HttpException e) {
				throw new MojoExecutionException(
					"Error while requesting list of storages: " + e.getMessage(),
					e
				);
			}

			try {
				JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
				JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
				for (JsonElement element : jsonArray) {
					result.add(GSON.fromJson(element.getAsJsonObject().get("data"), StorageInfo.class));
				}
			} catch (JsonParseException | IllegalStateException e) {
				throw new MojoExecutionException(
					"Error while parsing storages list response: " + e.getMessage(),
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
			logger.debug("Crowdin responded with " + result.size() + " storages");
		}
		return result;
	}

	/**
	 * Creates a new storage on Crowdin by uploading content.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param filename the filename for the newly created storage.
	 * @param entity the {@link HttpEntity} containing the content to upload.
	 * @param token the API token.
	 * @param logger the {@link Log} to log to.
	 * @return The resulting {@link StorageInfo} for the newly created storage.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nonnull
	public static StorageInfo createStorage(
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull String filename,
		@Nonnull HttpEntity entity,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting a new storage for: " + filename);
		}
		List<Header> headers = new ArrayList<>();
		try {
			headers.add(new BasicHeader("Crowdin-API-FileName", URLEncoder.encode(filename, StandardCharsets.UTF_8.name())));
		} catch (UnsupportedEncodingException e) {
			// Can't happen
		}
		String response;
		try {
			response = CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.POST,
				"storages",
				null,
				headers,
				token,
				entity,
				null,
				String.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while creating storage: " + e.getMessage(),
				e
			);
		}

		StorageInfo result;
		try {
			JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
			result = GSON.fromJson(jsonObject.get("data"), StorageInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new MojoExecutionException(
				"Error while parsing storage creation response: " + e.getMessage(),
				e
			);
		}
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin responded with new storage: " + result);
		}
		return result;
	}

	/**
	 * Deletes the specified storage at Crowdin.
	 *
	 * @param httpClient the {@link CloseableHttpClient} to use.
	 * @param storage the {@link StorageInfo} for the storage to delete.
	 * @param token the API token.
	 * @param logger the {@link Log} to log to.
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	@Nonnull
	public static void deleteStorage(
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull StorageInfo storage,
		@Nonnull String token,
		@Nullable Log logger
	) throws MojoExecutionException {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Requesting to delete storage " + storage.getId());
		}
		try {
			CrowdinAPI.sendRequest(
				httpClient,
				HTTPMethod.DELETE,
				"storages/" + storage.getId(),
				null,
				null,
				token,
				null,
				null,
				Void.class,
				logger
			);
		} catch (HttpException e) {
			throw new MojoExecutionException(
				"Error while deleting storage: " + e.getMessage(),
				e
			);
		}

		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Crowdin deleted storage: " + storage);
		}
	}

	public static <T, V> T sendRequest(
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull HTTPMethod method,
		@Nullable String function,
		@Nullable Map<String, String> parameters,
		@Nullable Collection<Header> headers,
		@Nullable String token,
		@Nullable V payload,
		@Nullable ContentType payloadContentType,
		@Nonnull Class<T> clazz,
		@Nullable Log logger
	) throws HttpException {
		return sendRequest(
			httpClient,
			method,
			URI.create(API_URL + function),
			parameters,
			headers,
			token,
			payload,
			payloadContentType,
			clazz,
			logger
		);
	}

	@SuppressWarnings("unchecked")
	public static <T, V> T sendRequest(
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull HTTPMethod method,
		@Nonnull URI uri,
		@Nullable Map<String, String> parameters,
		@Nullable Collection<Header> headers,
		@Nullable String token,
		@Nullable V payload,
		@Nullable ContentType payloadContentType, //Doc: Only used for Sting and InputStream payloads
		@Nonnull Class<T> clazz,
		@Nullable Log logger
	) throws HttpException {
		RequestBuilder requestBuilder = RequestBuilder.create(method.getValue());
		requestBuilder.setUri(uri);
		if (parameters != null) {
			for (Entry<String, String> entry : parameters.entrySet()) {
				requestBuilder.addParameter(entry.getKey(), entry.getValue());
			}
		}
		if (isNotBlank(token)) {
			requestBuilder.addHeader("Authorization", "Bearer " + token);
		}
		if (headers != null) {
			for (Header header : headers) {
				requestBuilder.addHeader(header);
			}
		}

		if (payload != null) {
			if (payload instanceof HttpEntity) {
				requestBuilder.setEntity((HttpEntity) payload);
			} else if (payload instanceof String) {
				requestBuilder.setEntity(new StringEntity(
					(String) payload,
					payloadContentType != null ? payloadContentType : ContentType.APPLICATION_OCTET_STREAM
				));
			} else if (payload instanceof InputStream) {
				requestBuilder.setEntity(new InputStreamEntity(
					(InputStream) payload,
					payloadContentType != null ? payloadContentType : ContentType.APPLICATION_OCTET_STREAM
				));
			} else {
				//Serialize JSON
				requestBuilder.setEntity(new StringEntity(GSON.toJson(payload), ContentType.APPLICATION_JSON));
			}
		} else if (method == HTTPMethod.POST) {
			requestBuilder.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));
		}

		HttpUriRequest request = requestBuilder.build();
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug("Calling " + request.getURI().toString());
		}
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
	public static CloseableHttpResponse sendStreamRequest(
		@Nonnull CloseableHttpClient httpClient,
		@Nonnull HTTPMethod method,
		@Nonnull URI uri,
		@Nullable String token,
		@Nullable Log logger
	) throws HttpException, IOException {
		RequestBuilder requestBuilder = RequestBuilder.create(method.getValue());
		requestBuilder.setUri(uri);
		if (isNotBlank(token)) {
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

	/**
	 * Extracts the content of an {@link HttpEntity} to a string and returns it,
	 * making some assumptions along the way. Assumptions are that the content
	 * isn't very large, so that a small buffer will suffice, and that the
	 * content is UTF-8 encoded.
	 * <p>
	 * <b>Note:</b> This method does <b>NOT</b> close the {@link InputStream}
	 * after reading.
	 *
	 * @param entity the {@link HttpEntity} whose content to extract.
	 * @return The extracted {@link String} content.
	 * @throws IOException If an error occurs during the operation.
	 */
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

	/**
	 * An enum that makes it possible to pass HTTP methods in a typesafe manner.
	 *
	 * @author Nadahar
	 */
	public enum HTTPMethod {

		/** HTTP DELETE */
		DELETE(HttpDelete.METHOD_NAME),

		/** HTTP GET */
		GET(HttpGet.METHOD_NAME),

		/** HTTP HEAD */
		HEAD(HttpHead.METHOD_NAME),

		/** HTTP PATCH */
		PATCH(HttpPatch.METHOD_NAME),

		/** HTTP POST */
		POST(HttpPost.METHOD_NAME),

		/** HTTP PUT */
		PUT(HttpPut.METHOD_NAME);

		@Nonnull
		private final String value;

		private HTTPMethod(String value) {
			this.value = value;
		}

		/**
		 * @return The {@link String} representation of this HTTP method.
		 */
		@Nonnull
		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return getValue();
		}
	}
}
