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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.logging.Log;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.FileExportOptions;
import org.digitalmediaserver.crowdin.api.FileImportOptions;
import org.digitalmediaserver.crowdin.api.FileType;
import org.digitalmediaserver.crowdin.api.response.StorageInfo;


/**
 * This class is used for serializing a JSON object when requesting to create a
 * file with Crowdin's v2 API. It represents {@code FileCreateForm}.
 *
 * @author Nadahar
 */
public class CreateFileRequest {

	/**
	 * Storage Identifier. <b>Required</b>.
	 * <p>
	 * Get via
	 * {@link CrowdinAPI#listStorages(CloseableHttpClient, String, Log)}.
	 */
	private final long storageId;

	/**
	 * File name. <b>Required</b>.
	 * <p>
	 * <b>Note:</b> Can't contain {@code \ / : * ? \" < > |} symbols.
	 * <p>
	 * {@code ZIP} files are not allowed.
	 */
	@Nonnull
	private final String name;

	/**
	 * Branch Identifier. Defines the branch to which file will be added.
	 * <p>
	 * <b>Note:</b> Can't be used with {@link #directoryId} in the same request.
	 * <p>
	 * Get via
	 * {@link CrowdinAPI#listBranches(CloseableHttpClient, long, String, String, Log)}.
	 */
	@Nullable
	private Long branchId;

	/**
	 * Directory Identifier. Defines the folder to which file will be added.
	 * <p>
	 * <b>Note:</b> Can't be used with {@link #branchId} in the same request.
	 * <p>
	 * Get via
	 * {@link CrowdinAPI#listFolders(CloseableHttpClient, long, Long, Long, String, boolean, String, Log)}.
	 */
	@Nullable
	private Long directoryId;

	/** Use to provide more details for translators. Available in UI only. */
	@Nullable
	private String title;

	/** Use to provide context about whole file */
	@Nullable
	private String context;

	/** The {@link FileType} for the new file */
	@Nullable
	private FileType type;

	/**
	 * Using latest parser version by default.
	 * <p>
	 * <b>Note:</b> Must be used together with {@link #type}.
	 */
	@Nullable
	private Integer parserVersion;

	/** The {@link FileImportOptions} for the new file */
	@Nullable
	private FileImportOptions importOptions;

	/** The {@link FileExportOptions} for the new file */
	@Nullable
	private FileExportOptions exportOptions;

	/**
	 * The array of target languages the new file should not be translated into.
	 * Do not use this option if the file should be available for all project
	 * languages.
	 */
	@Nullable
	private String[] excludedTargetLanguages;

	/**
	 * Attach labels to strings. Support for labels isn't implemented in this
	 * plugin.
	 */
	@Nullable
	private long[] attachLabelIds;

	/**
	 * Creates a new instance with the specified storage and name.
	 *
	 * @param storage the {@link StorageInfo} for the already uploaded file.
	 * @param name the name of the new file.
	 */
	public CreateFileRequest(@Nonnull StorageInfo storage, @Nonnull String name) {
		this.storageId = storage.getId();
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
	 * @return The {@link #context} value.
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @param context the {@link #context} value to set.
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
	 * @return The {@link #parserVersion} value.
	 */
	public Integer getParserVersion() {
		return parserVersion;
	}

	/**
	 * @param parserVersion the {@link #parserVersion} value to set.
	 */
	public void setParserVersion(Integer parserVersion) {
		this.parserVersion = parserVersion;
	}

	/**
	 * @return The {@link #importOptions} value.
	 */
	public FileImportOptions getImportOptions() {
		return importOptions;
	}

	/**
	 * @param importOptions the {@link #importOptions} value to set.
	 */
	public void setImportOptions(FileImportOptions importOptions) {
		this.importOptions = importOptions;
	}

	/**
	 * @return The {@link #exportOptions} value.
	 */
	public FileExportOptions getExportOptions() {
		return exportOptions;
	}

	/**
	 * @param exportOptions the {@link #exportOptions} value to set.
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
	 * @return The {@link #attachLabelIds} array.
	 */
	public long[] getAttachLabelIds() {
		return attachLabelIds;
	}

	/**
	 * @param attachLabelIds the {@link #attachLabelIds} array to set.
	 */
	public void setAttachLabelIds(long[] attachLabelIds) {
		this.attachLabelIds = attachLabelIds;
	}

	/**
	 * @return The {@link #storageId} value.
	 */
	public long getStorageId() {
		return storageId;
	}

	/**
	 * @return The {@link #name} value.
	 */
	@Nonnull
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(attachLabelIds);
		result = prime * result + Arrays.hashCode(excludedTargetLanguages);
		result = prime * result + Objects.hash(
			branchId,
			context,
			directoryId,
			exportOptions,
			importOptions,
			name,
			parserVersion,
			storageId,
			title,
			type
		);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CreateFileRequest)) {
			return false;
		}
		CreateFileRequest other = (CreateFileRequest) obj;
		return
			Arrays.equals(attachLabelIds, other.attachLabelIds) &&
			Objects.equals(branchId, other.branchId) &&
			Objects.equals(context, other.context) &&
			Objects.equals(directoryId, other.directoryId) &&
			Arrays.equals(excludedTargetLanguages, other.excludedTargetLanguages) &&
			Objects.equals(exportOptions, other.exportOptions) &&
			Objects.equals(importOptions, other.importOptions) &&
			Objects.equals(name, other.name) &&
			Objects.equals(parserVersion, other.parserVersion) &&
			storageId == other.storageId &&
			Objects.equals(title, other.title) &&
			type == other.type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("CreateFileRequest [storageId=").append(storageId).append(", ")
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
		if (parserVersion != null) {
			sb.append("parserVersion=").append(parserVersion).append(", ");
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
		if (attachLabelIds != null) {
			sb.append("attachLabelIds=").append(Arrays.toString(attachLabelIds));
		}
		sb.append("]");
		return sb.toString();
	}
}
