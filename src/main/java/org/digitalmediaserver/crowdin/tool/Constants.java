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

import java.util.regex.Pattern;
import org.jdom2.input.SAXBuilder;


/**
 * This class has no fields or methods, it's only used to hold various static
 * constants.
 *
 * @author Nadahar
 */
public class Constants {

	/** The standard header for generated translation files */
	public static final String DEFAULT_COMMENT =
		"This file has been generated automatically, modifications will be overwritten. " +
		"If you'd like to change the content, please do so at Crowdin.";

	/** The name of the translation status document in the download folder */
	public static final String STATUS_DOWNLOAD_FILENAME = "crowdin_status.json";

	/** The generic placeholder {@link Pattern} */
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[^%]+%");

	/** The path filter delimiters to use when building a regular expression */
	public static final Pattern FILTER_DELIMITERS = Pattern.compile("\\?|\\*|\\\\");

	/** The static {@link SAXBuilder} instance */
	public static final SAXBuilder SAX_BUILDER = new SAXBuilder();

	/** The Crowdin API URL */
	public static final String API_URL = "https://api.crowdin.com/api/v2/";
	/** The system property to use for NTLM domain */
	public static final String HTTP_AUTH_NTLM_DOMAIN = "http.auth.ntlm.domain"; //TODO: (Nad) CHeck what's in use

	/** The system property to use for proxy password */
	public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";

	/** The system property to use for proxy user */
	public static final String HTTP_PROXY_USER = "http.proxyUser";

	/** The system property to use for proxy port */
	public static final String HTTP_PROXY_PORT = "http.proxyPort";

	/** The system property to use for proxy host */
	public static final String HTTP_PROXY_HOST = "http.proxyHost";

	/** A {@link Pattern} that matches semicolon {@code ";"} */
	public static final Pattern SEMICOLON = Pattern.compile("\\s*;\\s*");

	/**
	 * Not to be instantiated.
	 */
	private Constants() {
	}
}
