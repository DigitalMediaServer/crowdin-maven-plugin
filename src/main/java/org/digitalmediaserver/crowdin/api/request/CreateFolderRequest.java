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
package org.digitalmediaserver.crowdin.api.request;

import static org.apache.commons.lang3.StringUtils.isBlank;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.logging.Log;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.Priority;


/**
 * This class is used for serializing a JSON object when requesting to create a
 * folder with Crowdin's v2 API. It represents {@code DirectoryCreateForm}.
 *
 * @author Nadahar
 */
public class CreateFolderRequest {

	/**
	 * Folder name. <b>Required</b>.
	 * <p>
	 * Can't contain {@code \ / : * ? \" < > |}.
	 */
	@Nonnull
	private final String name;

	/**
	 * Branch identifier.
	 * <p>
	 * <b>Note:</b> Can't be used with {@link #directoryId} in same request.
	 * <p>
	 * Get via
	 * {@link CrowdinAPI#listBranches(CloseableHttpClient, long, String, String, Log)}.
	 */
	@Nullable
	private Long branchId;

	/**
	 * Parent folder identifier.
	 * <p>
	 * <b>Note:</b> Can't be used with {@link #branchId} in same request.
	 * <p>
	 * Get via
	 * {@link CrowdinAPI#listFolders(CloseableHttpClient, long, Long, Long, String, boolean, String, Log)}.
	 */
	@Nullable
	private Long directoryId;

	/** Use to provide more details for translators. Available in UI only. */
	@Nullable
	private String title;

	/**
	 * Folder export pattern. Defines folder name and path in the resulting
	 * translations bundle.
	 * <p>
	 * <b>Note:</b> Can't contain {@code : * ? \" < > |} symbols.
	 */
	@Nullable
	private String exportPattern;

	/**
	 * Defines priority level for each branch.
	 */
	@Nullable
	private Priority priority;

	/**
	 * Creates a new instance with the specified folder name.
	 *
	 * @param name the folder name.
	 * @throws IllegalArgumentException If {@code name} is blank.
	 */
	public CreateFolderRequest(@Nonnull String name) {
		if (isBlank(name)) {
			throw new IllegalArgumentException("name cannot be blank");
		}
		this.name = name;
	}

	/**
	 * @return The {@link #branchId} value.
	 */
	public Long getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId the {@link #branchId} value to set.
	 */
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	/**
	 * @return The {@link #directoryId} value.
	 */
	public Long getDirectoryId() {
		return directoryId;
	}

	/**
	 * @param directoryId the {@link #directoryId} value to set.
	 */
	public void setDirectoryId(Long directoryId) {
		this.directoryId = directoryId;
	}

	/**
	 * @return The {@link #title} value.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the {@link #title} value to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return The {@link #exportPattern} value.
	 */
	public String getExportPattern() {
		return exportPattern;
	}

	/**
	 * @param exportPattern the {@link #exportPattern} value to set.
	 */
	public void setExportPattern(String exportPattern) {
		this.exportPattern = exportPattern;
	}

	/**
	 * @return The {@link #priority} value.
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * @param priority the {@link #priority} value to set.
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * @return The {@link #name} value.
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(branchId, directoryId, exportPattern, name, priority, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CreateFolderRequest)) {
			return false;
		}
		CreateFolderRequest other = (CreateFolderRequest) obj;
		return
			Objects.equals(branchId, other.branchId) &&
			Objects.equals(directoryId, other.directoryId) &&
			Objects.equals(exportPattern, other.exportPattern) &&
			Objects.equals(name, other.name) &&
			priority == other.priority &&
			Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("CreateFolderRequest [")
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
		if (exportPattern != null) {
			sb.append("exportPattern=").append(exportPattern).append(", ");
		}
		if (priority != null) {
			sb.append("priority=").append(priority);
		}
		sb.append("]");
		return sb.toString();
	}
}
