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
import org.digitalmediaserver.crowdin.api.Priority;


/**
 * This class is used for deserializing JSON response objects describing
 * branches received from Crowdin's v2 API. It represents
 * {@code FileBasedProjectBranch}.
 *
 * @author Nadahar
 */
public class BranchInfo {

	/** The branch ID */
	private long id;

	/** The project ID */
	private long projectId;

	/** The branch name */
	private String name;

	/** The branch title */
	private String title;

	/** The branch creation time */
	private Date createdAt;

	/** The branch update time */
	private Date updatedAt;

	/** The branch export pattern */
	private String exportPattern;

	/** The branch priority */
	private Priority priority;

	/**
	 * @return The branch ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the branch ID to set.
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
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set.
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
	 * @return The priority.
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set.
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * Creates a new instance.
	 */
	public BranchInfo() {
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdAt, exportPattern, id, name, priority, projectId, title, updatedAt);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BranchInfo)) {
			return false;
		}
		BranchInfo other = (BranchInfo) obj;
		return
			Objects.equals(createdAt, other.createdAt) &&
			Objects.equals(exportPattern, other.exportPattern) &&
			id == other.id &&
			Objects.equals(name, other.name) &&
			Objects.equals(priority, other.priority) &&
			projectId == other.projectId &&
			Objects.equals(title, other.title) &&
			Objects.equals(updatedAt, other.updatedAt);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("BranchInfo [id=").append(id)
			.append(", projectId=").append(projectId);
		if (name != null) {
			sb.append(", ").append("name=").append(name);
		}
		if (title != null) {
			sb.append(", ").append("title=").append(title);
		}
		if (createdAt != null) {
			sb.append(", ").append("createdAt=").append(createdAt);
		}
		if (updatedAt != null) {
			sb.append(", ").append("updatedAt=").append(updatedAt);
		}
		if (exportPattern != null) {
			sb.append(", ").append("exportPattern=").append(exportPattern);
		}
		if (priority != null) {
			sb.append(", ").append("priority=").append(priority);
		}
		sb.append("]");
		return sb.toString();
	}
}
