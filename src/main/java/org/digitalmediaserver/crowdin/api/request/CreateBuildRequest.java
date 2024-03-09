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

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.logging.Log;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;


/**
 * This class is used for serializing a JSON object when requesting to create a
 * build with Crowdin's v2 API. It represents
 * {@code CrowdinTranslationCreateProjectBuildForm}.
 *
 * @author Nadahar
 */
public class CreateBuildRequest {

	/**
	 * Branch Identifier. Get via
	 * {@link CrowdinAPI#listBranches(CloseableHttpClient, long, String, String, Log)}.
	 */
	@Nullable
	private Long branchId;

	/**
	 * Specify an array of target languages for build. See
	 * <a href="https://developer.crowdin.com/language-codes/">here</a>
	 * <p>
	 * Leave this field empty to build all target languages.
	 */
	@Nullable
	private String[] targetLanguageIds;

	/**
	 * Defines whether to export only translated strings.
	 * <p>
	 * <b>Note:</b> Can't be {@code true} if {@link #skipUntranslatedFiles} is
	 * {@code true} in the same request.
	 * <p>
	 * <a href="https://support.crowdin.com/advanced-project-setup/#export">More
	 * info</a>.
	 */
	private boolean skipUntranslatedStrings;

	/**
	 * Defines whether to export only translated files.
	 * <p>
	 * <b>Note:</b> Can't be {@code true} if {@link #skipUntranslatedStrings} is
	 * {@code true} in the same request.
	 * <p>
	 * <a href="https://support.crowdin.com/advanced-project-setup/#export">More
	 * info</a>.
	 */
	private boolean skipUntranslatedFiles;

	/**
	 * Defines whether to export only approved strings.
	 * <p>
	 * <a href="https://support.crowdin.com/advanced-project-setup/#export">More
	 * info</a>.
	 */
	private boolean exportApprovedOnly;

	/**
	 * Creates a new instance.
	 */
	public CreateBuildRequest() {
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
	 * @return The {@link #targetLanguageIds} array.
	 */
	public String[] getTargetLanguageIds() {
		return targetLanguageIds;
	}

	/**
	 * @param targetLanguageIds the {@link #targetLanguageIds} array to set.
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
	 * @param skipUntranslatedStrings the {@link #skipUntranslatedStrings} value
	 *            to set.
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
	 * @param skipUntranslatedFiles the {@link #skipUntranslatedFiles} value to
	 *            set.
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
	 * @param exportApprovedOnly the {@link #exportApprovedOnly} value to set.
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
		if (!(obj instanceof CreateBuildRequest)) {
			return false;
		}
		CreateBuildRequest other = (CreateBuildRequest) obj;
		return
			Objects.equals(branchId, other.branchId) &&
			exportApprovedOnly == other.exportApprovedOnly &&
			skipUntranslatedFiles == other.skipUntranslatedFiles &&
			skipUntranslatedStrings == other.skipUntranslatedStrings &&
			Arrays.equals(targetLanguageIds, other.targetLanguageIds);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CreateBuildRequest [");
		if (branchId != null) {
			sb.append("branchId=").append(branchId).append(", ");
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
