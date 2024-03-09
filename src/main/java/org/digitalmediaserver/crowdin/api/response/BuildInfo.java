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
import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects describing builds
 * received from Crowdin's v2 API. It represents
 * {@code AbstractProjectBuildResponse}.
 *
 * @author Nadahar
 */
public class BuildInfo {

	/** The build ID */
	private long id;

	/** The project ID */
	private long projectId;

	/** The {@link ProjectBuildStatus} */
	private ProjectBuildStatus status;

	/** The progress indicator (percent) */
	private int progress;

	/** The creation time */
	private Date createdAt;

	/** The update time */
	private Date updatedAt;

	/** The finish time */
	private Date finishedAt;

	/** The {@link BuildAttributes} */
	private BuildAttributes attributes;

	/**
	 * Creates a new instance.
	 */
	public BuildInfo() {
	}

	/**
	 * @return The build ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the build ID to set.
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
	 * @return The {@link ProjectBuildStatus}.
	 */
	public ProjectBuildStatus getStatus() {
		return status;
	}

	/**
	 * @param status the {@link ProjectBuildStatus} to set.
	 */
	public void setStatus(ProjectBuildStatus status) {
		this.status = status;
	}

	/**
	 * @return The progress indication.
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress indication to set.
	 */
	public void setProgress(int progress) {
		this.progress = progress;
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
	 * @return The finish time.
	 */
	public Date getFinishedAt() {
		return finishedAt;
	}

	/**
	 * @param finishedAt the finish time to set.
	 */
	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

	/**
	 * @return The {@link BuildAttributes}.
	 */
	public BuildAttributes getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the {@link BuildAttributes} to set.
	 */
	public void setAttributes(BuildAttributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			attributes,
			createdAt,
			finishedAt,
			id,
			progress,
			projectId,
			status,
			updatedAt
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BuildInfo)) {
			return false;
		}
		BuildInfo other = (BuildInfo) obj;
		return
			Objects.equals(attributes, other.attributes) &&
			Objects.equals(createdAt, other.createdAt) &&
			Objects.equals(finishedAt, other.finishedAt) &&
			id == other.id &&
			progress == other.progress &&
			projectId == other.projectId &&
			status == other.status &&
			Objects.equals(updatedAt, other.updatedAt);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("BuildInfo [id=").append(id)
			.append(", projectId=").append(projectId).append(", ");
		if (status != null) {
			sb.append("status=").append(status).append(", ");
		}
		sb.append("progress=").append(progress).append(", ");
		if (createdAt != null) {
			sb.append("createdAt=").append(createdAt).append(", ");
		}
		if (updatedAt != null) {
			sb.append("updatedAt=").append(updatedAt).append(", ");
		}
		if (finishedAt != null) {
			sb.append("finishedAt=").append(finishedAt).append(", ");
		}
		if (attributes != null) {
			sb.append("attributes=").append(attributes);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * This class is used for deserializing JSON response objects describing
	 * build attributes received from Crowdin's v2 API. It represents
	 * {@code CrowdinBuildAttributesResponseModel}.
	 *
	 * @author Nadahar
	 */
	public static class BuildAttributes {

		/** The branch ID */
		private Long branchId;

		/** The folder ID */
		private Long directoryId;

		/** The array of target language IDs */
		private String[] targetLanguageIds;

		/** Whether to skip untranslated strings */
		private boolean skipUntranslatedStrings;

		/** Whether to skip untranslated files */
		private boolean skipUntranslatedFiles;

		/** Whether to export only approved translations */
		private boolean exportApprovedOnly;

		/**
		 * Creates a new instance.
		 */
		public BuildAttributes() {
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
		 * @return The array of target language IDs.
		 */
		public String[] getTargetLanguageIds() {
			return targetLanguageIds;
		}

		/**
		 * @param targetLanguageIds array of target language IDs to set.
		 */
		public void setTargetLanguageIds(String[] targetLanguageIds) {
			this.targetLanguageIds = targetLanguageIds;
		}

		/**
		 * @return The {@link #skipUntranslatedStrings} value.
		 */
		public boolean isSkipUntranslatedStrings() {
			return skipUntranslatedStrings;
		}

		/**
		 * @param skipUntranslatedStrings the {@link #skipUntranslatedStrings}
		 *            value to set.
		 */
		public void setSkipUntranslatedStrings(boolean skipUntranslatedStrings) {
			this.skipUntranslatedStrings = skipUntranslatedStrings;
		}

		/**
		 * @return The {@link #skipUntranslatedFiles} value.
		 */
		public boolean isSkipUntranslatedFiles() {
			return skipUntranslatedFiles;
		}

		/**
		 * @param skipUntranslatedFiles the {@link #skipUntranslatedFiles} value
		 *            to set.
		 */
		public void setSkipUntranslatedFiles(boolean skipUntranslatedFiles) {
			this.skipUntranslatedFiles = skipUntranslatedFiles;
		}

		/**
		 * @return The {@link #exportApprovedOnly} value.
		 */
		public boolean isExportApprovedOnly() {
			return exportApprovedOnly;
		}

		/**
		 * @param exportApprovedOnly the {@link #exportApprovedOnly} value to
		 *            set.
		 */
		public void setExportApprovedOnly(boolean exportApprovedOnly) {
			this.exportApprovedOnly = exportApprovedOnly;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(targetLanguageIds);
			result = prime * result + Objects.hash(
				branchId,
				directoryId,
				exportApprovedOnly,
				skipUntranslatedFiles,
				skipUntranslatedStrings
			);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof BuildAttributes)) {
				return false;
			}
			BuildAttributes other = (BuildAttributes) obj;
			return
				Objects.equals(branchId, other.branchId) &&
				Objects.equals(directoryId, other.directoryId) &&
				exportApprovedOnly == other.exportApprovedOnly &&
				skipUntranslatedFiles == other.skipUntranslatedFiles &&
				skipUntranslatedStrings == other.skipUntranslatedStrings &&
				Arrays.equals(targetLanguageIds, other.targetLanguageIds);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("BuildAttributes [");
			if (branchId != null) {
				sb.append("branchId=").append(branchId).append(", ");
			}
			if (directoryId != null) {
				sb.append("directoryId=").append(directoryId).append(", ");
			}
			if (targetLanguageIds != null) {
				sb.append("targetLanguageIds=").append(Arrays.toString(targetLanguageIds)).append(", ");
			}
			sb
				.append("skipUntranslatedStrings=").append(skipUntranslatedStrings)
				.append(", skipUntranslatedFiles=").append(skipUntranslatedFiles)
				.append(", exportApprovedOnly=").append(exportApprovedOnly).append("]");
			return sb.toString();
		}
	}

	/**
	 * An enum representing Crowdin API build statuses.
	 *
	 * @author Nadahar
	 */
	public enum ProjectBuildStatus {

		/** The build has been created */
		@SerializedName("created")
		CREATED,

		/** The build is in progress */
		@SerializedName("inProgress")
		IN_PROGRESS,

		/** The build was cancelled */
		@SerializedName("canceled")
		CANCELED,

		/** The build failed */
		@SerializedName("failed")
		FAILED,

		/** The build is finished */
		@SerializedName("finished")
		FINISHED;
	}
}
