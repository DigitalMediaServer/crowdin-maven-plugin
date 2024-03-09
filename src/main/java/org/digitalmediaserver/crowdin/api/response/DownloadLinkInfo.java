/*
 * Crowdin Maven Plugin, an Apache Maven plugin for synchronizing translation
 * files using the crowdin.com API.
 * Copyright (C) 2024 Digital Media Server developers
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
package org.digitalmediaserver.crowdin.api.response;

import java.net.URI;
import java.util.Date;
import java.util.Objects;


/**
 * This class is used for deserializing JSON response objects describing
 * download links received from Crowdin's v2 API. It represents
 * {@code DownloadLinkResponseModel}.
 *
 * @author Nadahar
 */
public class DownloadLinkInfo { //Doc: Represents DownloadLinkResponseModel

	/** The download URL */
	private URI url;

	/** The expiration time */
	private Date expireIn;

	/**
	 * Creates a new instance.
	 */
	public DownloadLinkInfo() {
	}

	/**
	 * @return The download URL.
	 */
	public URI getUrl() {
		return url;
	}

	/**
	 * @param url the download URL to set.
	 */
	public void setUrl(URI url) {
		this.url = url;
	}

	/**
	 * @return The expiration time.
	 */
	public Date getExpireIn() {
		return expireIn;
	}

	/**
	 * @param expireIn the expiration time to set.
	 */
	public void setExpireIn(Date expireIn) {
		this.expireIn = expireIn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(expireIn, url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DownloadLinkInfo)) {
			return false;
		}
		DownloadLinkInfo other = (DownloadLinkInfo) obj;
		return Objects.equals(expireIn, other.expireIn) && Objects.equals(url, other.url);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DownloadLinkInfo [").append("url=").append(url);
		if (expireIn != null) {
			sb.append(", ").append("expireIn=").append(expireIn);
		}
		sb.append("]");
		return sb.toString();
	}
}
