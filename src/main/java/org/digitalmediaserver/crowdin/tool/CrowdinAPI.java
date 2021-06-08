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
package org.digitalmediaserver.crowdin.tool;

import static org.digitalmediaserver.crowdin.AbstractCrowdinMojo.isBlank;
import static org.digitalmediaserver.crowdin.tool.Constants.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Server;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;


/**
 * This is a utility class for calling the Crowdin API and related tasks.
 *
 * @author Nadahar
 */
public class CrowdinAPI {

	/**
	 * Not to be instantiated.
	 */
	private CrowdinAPI() {
	}

	/**
	 * Creates a new {@link CloseableHttpClient} instance.
	 *
	 * @return The new {@link CloseableHttpClient}.
	 * @throws IOException If an error occurs during the operation.
	 */
	public static CloseableHttpClient createHTTPClient() throws IOException {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
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
}
