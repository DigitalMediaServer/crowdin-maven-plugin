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

import java.util.Objects;


/**
 * This class is used for deserializing JSON response objects describing
 * storages received from Crowdin's v2 API. It represents {@code Storage}.
 *
 * @author Nadahar
 */
public class StorageInfo {

	/** The storage ID */
	private Long id;

	/** The file name */
	private String fileName;

	/**
	 * Creates a new instance.
	 */
	public StorageInfo() {
	}

	/**
	 * @return The storage ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the storage ID to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return The file name.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the file name to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fileName, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StorageInfo)) {
			return false;
		}
		StorageInfo other = (StorageInfo) obj;
		return Objects.equals(fileName, other.fileName) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("StorageInfo [")
			.append("id=").append(id).append(", ")
			.append("fileName=").append(fileName)
			.append("]");
		return sb.toString();
	}
}
