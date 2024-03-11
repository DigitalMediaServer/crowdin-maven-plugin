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

import java.util.Date;
import java.util.Objects;
import javax.annotation.Nullable;
import org.digitalmediaserver.crowdin.api.Priority;


/**
 * This class is used for deserializing JSON response objects describing folders
 * received from Crowdin's v2 API. It represents {@code Directory}.
 *
 * @author Nadahar
 */
public class FolderInfo {

	/** The folder ID */
	private long id;

	/** The project ID */
	private long projectId;

	/** The branch ID */
	@Nullable
	private Long branchId;

	/** The folder ID */
	@Nullable
	private Long directoryId;

	/** The folder name */
	private String name;

	/** The title */
	private String title;

	/** The export pattern */
	private String exportPattern;

	/** The folder path */
	private String path;

	/** The folder {@link Priority} */
	private Priority priority;

	/** The creation time */
	@Nullable
	private Date createdAt;

	/** The update time */
	@Nullable
	private Date updatedAt;

	/**
	 * Creates a new instance.
	 */
	public FolderInfo() {
	}

	/**
	 * @return The folder ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the folder ID to set.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return The project ID.
	 */
	public long getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the project ID to set.
	 */
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return The branch ID.
	 */
	public Long getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId the branch ID to set.
	 */
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	/**
	 * @return The folder ID.
	 */
	public Long getDirectoryId() {
		return directoryId;
	}

	/**
	 * @param directoryId the folder ID to set.
	 */
	public void setDirectoryId(Long directoryId) {
		this.directoryId = directoryId;
	}

	/**
	 * @return The folder name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the folder name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return The export pattern.
	 */
	public String getExportPattern() {
		return exportPattern;
	}

	/**
	 * @param exportPattern the export pattern to set.
	 */
	public void setExportPattern(String exportPattern) {
		this.exportPattern = exportPattern;
	}

	/**
	 * @return The folder path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the folder path to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return The {@link Priority}.
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * @param priority the {@link Priority} to set.
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * @return The creation time.
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the creation time to set.
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return The update time.
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the update time to set.
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			branchId,
			createdAt,
			directoryId,
			exportPattern,
			id,
			name,
			path,
			priority,
			projectId,
			title,
			updatedAt
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FolderInfo)) {
			return false;
		}
		FolderInfo other = (FolderInfo) obj;
		return
			Objects.equals(branchId, other.branchId) &&
			Objects.equals(createdAt, other.createdAt) &&
			Objects.equals(directoryId, other.directoryId) &&
			Objects.equals(exportPattern, other.exportPattern) &&
			id == other.id &&
			Objects.equals(name, other.name) &&
			Objects.equals(path, other.path) &&
			Objects.equals(priority, other.priority) &&
			projectId == other.projectId &&
			Objects.equals(title, other.title) &&
			Objects.equals(updatedAt, other.updatedAt);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("FolderInfo [id=").append(id)
			.append(", projectId=").append(projectId).append(", ");
		if (branchId != null) {
			sb.append("branchId=").append(branchId).append(", ");
		}
		if (directoryId != null) {
			sb.append("directoryId=").append(directoryId).append(", ");
		}
		if (name != null) {
			sb.append("name=").append(name).append(", ");
		}
		if (title != null) {
			sb.append("title=").append(title).append(", ");
		}
		if (exportPattern != null) {
			sb.append("exportPattern=").append(exportPattern).append(", ");
		}
		if (path != null) {
			sb.append("path=").append(path).append(", ");
		}
		if (priority != null) {
			sb.append("priority=").append(priority).append(", ");
		}
		if (createdAt != null) {
			sb.append("createdAt=").append(createdAt).append(", ");
		}
		if (updatedAt != null) {
			sb.append("updatedAt=").append(updatedAt);
		}
		sb.append("]");
		return sb.toString();
	}
}
