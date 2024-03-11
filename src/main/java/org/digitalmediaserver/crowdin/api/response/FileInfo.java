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

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nullable;
import org.digitalmediaserver.crowdin.api.Priority;
import org.digitalmediaserver.crowdin.api.FileExportOptions;
import org.digitalmediaserver.crowdin.api.FileImportOptions;
import org.digitalmediaserver.crowdin.api.FileType;


/**
 * This class is used for deserializing JSON response objects describing files
 * received from Crowdin's v2 API. It represents
 * {@code FileInfo} and {@code File}.
 *
 * @author Nadahar
 */
public class FileInfo {

	/** The file ID */
	private long id;

	/** The project ID */
	private long projectId;

	/** The branch ID */
	@Nullable
	private Long branchId;

	/** The folder ID */
	@Nullable
	private Long directoryId;

	/** The file name */
	private String name;

	/** The title */
	@Nullable
	private String title;

	/** The context */
	@Nullable
	private String context;

	/** The {@link FileType} */
	private FileType type;

	/** The file path */
	private String path;

	/** The file status */
	private String status;

	// Fields below belong to "File" class only

	/** The revision ID */
	@Nullable
	private Long revisionId;

	/** The {@link Priority} */
	@Nullable
	private Priority priority;

	/** The {@link FileImportOptions} */
	@Nullable
	private FileImportOptions importOptions;

	/** The {@link FileExportOptions} */
	@Nullable
	private FileExportOptions exportOptions;

	/** The array of excluded target languages */
	@Nullable
	private String[] excludedTargetLanguages;

	/** The parser version */
	@Nullable
	private Integer parserVersion;

	/** The creation time */
	@Nullable
	private Date createdAt;

	/** The update time */
	@Nullable
	private Date updatedAt;

	/**
	 * Creates a new instance.
	 */
	public FileInfo() {
	}

	/**
	 * @return The file ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the file ID to set.
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
	 * @return The file name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the file name to set.
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
	 * @return The context.
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @param context the context to set.
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * @return The {@link FileType}.
	 */
	public FileType getType() {
		return type;
	}

	/**
	 * @param type the {@link FileType} to set.
	 */
	public void setType(FileType type) {
		this.type = type;
	}

	/**
	 * @return The file path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the file path to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return The file status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the file status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return The revision ID.
	 */
	public Long getRevisionId() {
		return revisionId;
	}

	/**
	 * @param revisionId the revision ID to set.
	 */
	public void setRevisionId(Long revisionId) {
		this.revisionId = revisionId;
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
	 * @return The {@link FileImportOptions}.
	 */
	public FileImportOptions getImportOptions() {
		return importOptions;
	}

	/**
	 * @param importOptions the {@link FileImportOptions} to set.
	 */
	public void setImportOptions(FileImportOptions importOptions) {
		this.importOptions = importOptions;
	}

	/**
	 * @return The {@link FileExportOptions}.
	 */
	public FileExportOptions getExportOptions() {
		return exportOptions;
	}

	/**
	 * @param exportOptions the {@link FileExportOptions} to set.
	 */
	public void setExportOptions(FileExportOptions exportOptions) {
		this.exportOptions = exportOptions;
	}

	/**
	 * @return The {@link #excludedTargetLanguages} array.
	 */
	public String[] getExcludedTargetLanguages() {
		return excludedTargetLanguages;
	}

	/**
	 * @param excludedTargetLanguages the {@link #excludedTargetLanguages} array
	 *            to set.
	 */
	public void setExcludedTargetLanguages(String[] excludedTargetLanguages) {
		this.excludedTargetLanguages = excludedTargetLanguages;
	}

	/**
	 * @return The parser version.
	 */
	public Integer getParserVersion() {
		return parserVersion;
	}

	/**
	 * @param parserVersion the parser version to set.
	 */
	public void setParserVersion(Integer parserVersion) {
		this.parserVersion = parserVersion;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(excludedTargetLanguages);
		result = prime * result + Objects.hash(
			branchId,
			context,
			createdAt,
			directoryId,
			exportOptions,
			id,
			importOptions,
			name,
			parserVersion,
			path,
			priority,
			projectId,
			revisionId,
			status,
			title,
			type,
			updatedAt
		);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FileInfo)) {
			return false;
		}
		FileInfo other = (FileInfo) obj;
		return
			Objects.equals(branchId, other.branchId) &&
			Objects.equals(context, other.context) &&
			Objects.equals(createdAt, other.createdAt) &&
			Objects.equals(directoryId, other.directoryId) &&
			Arrays.equals(excludedTargetLanguages, other.excludedTargetLanguages) &&
			Objects.equals(exportOptions, other.exportOptions) &&
			id == other.id &&
			Objects.equals(importOptions, other.importOptions) &&
			Objects.equals(name, other.name) &&
			Objects.equals(parserVersion, other.parserVersion) &&
			Objects.equals(path, other.path) && priority == other.priority &&
			projectId == other.projectId &&
			Objects.equals(revisionId, other.revisionId) &&
			Objects.equals(status, other.status) &&
			Objects.equals(title, other.title) &&
			Objects.equals(type, other.type) &&
			Objects.equals(updatedAt, other.updatedAt);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("FileInfo [id=").append(id)
			.append(", projectId=").append(projectId).append(", ")
			.append("name=").append(name).append(", ");
		if (branchId != null) {
			sb.append("branchId=").append(branchId).append(", ");
		}
		if (directoryId != null) {
			sb.append("directoryId=").append(directoryId).append(", ");
		}
		if (title != null) {
			sb.append("title=").append(title).append(", ");
		}
		if (context != null) {
			sb.append("context=").append(context).append(", ");
		}
		if (type != null) {
			sb.append("type=").append(type).append(", ");
		}
		if (path != null) {
			sb.append("path=").append(path).append(", ");
		}
		if (status != null) {
			sb.append("status=").append(status).append(", ");
		}
		if (revisionId != null) {
			sb.append("revisionId=").append(revisionId).append(", ");
		}
		if (priority != null) {
			sb.append("priority=").append(priority).append(", ");
		}
		if (importOptions != null) {
			sb.append("importOptions=").append(importOptions).append(", ");
		}
		if (exportOptions != null) {
			sb.append("exportOptions=").append(exportOptions).append(", ");
		}
		if (excludedTargetLanguages != null) {
			sb.append("excludedTargetLanguages=").append(Arrays.toString(excludedTargetLanguages)).append(", ");
		}
		if (parserVersion != null) {
			sb.append("parserVersion=").append(parserVersion).append(", ");
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
