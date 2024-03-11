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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.logging.Log;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.FileExportOptions;
import org.digitalmediaserver.crowdin.api.FileImportOptions;
import org.digitalmediaserver.crowdin.api.response.StorageInfo;
import org.digitalmediaserver.crowdin.configuration.UpdateOption;


/**
 * This class is used for serializing a JSON object when requesting to update a
 * file with Crowdin's v2 API. It represents {@code FileReplaceFromStorageForm}.
 *
 * @author Nadahar
 */
public class UpdateFileRequest {

	/**
	 * Storage Identifier. <b>Required</b>.
	 * <p>
	 * Get via
	 * {@link CrowdinAPI#listStorages(CloseableHttpClient, String, Log)}.
	 */
	private final long storageId;

	/**
	 * File name.
	 * <p>
	 * <b>Note:</b> Can't contain {@code \ / : * ? \" < > |} symbols.
	 */
	private String name;

	/**
	 * Defines whether to keep existing translations and approvals for updated
	 * strings.
	 * <p>
	 * <b>Default</b>: {@link UpdateOption#clear_translations_and_approvals}.
	 */
	private UpdateOption updateOption;

	/** The {@link FileImportOptions} for the updated file */
	private FileImportOptions importOptions;

	/** The {@link FileExportOptions} for the updated file */
	private FileExportOptions exportOptions;

	/**
	 * Attach labels to strings. Support for labels isn't implemented in this
	 * plugin.
	 */
	private long[] attachLabelIds;

	/**
	 * Detach labels from updated strings. Support for labels isn't implemented
	 * in this plugin.
	 */
	private long[] detachLabelIds;

	/**
	 * Enable to replace context that have been modified at Crowdin.
	 * <p>
	 * <b>Default</b>: {@code false}.
	 */
	private Boolean replaceModifiedContext;

	/**
	 * Creates a new instance with the specified storage.
	 *
	 * @param storage the {@link StorageInfo} for the already uploaded file.
	 */
	public UpdateFileRequest(@Nonnull StorageInfo storage) {
		this.storageId = storage.getId();
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
	public String getName() {
		return name;
	}

	/**
	 * @param name the {@link #name} value to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The {@link UpdateOption}.
	 */
	public UpdateOption getUpdateOption() {
		return updateOption;
	}

	/**
	 * @param updateOption the {@link UpdateOption} to set.
	 */
	public void setUpdateOption(UpdateOption updateOption) {
		this.updateOption = updateOption;
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
	 * @return The {@link #detachLabelIds} array.
	 */
	public long[] getDetachLabelIds() {
		return detachLabelIds;
	}

	/**
	 * @param detachLabelIds the {@link #detachLabelIds} array to set.
	 */
	public void setDetachLabelIds(long[] detachLabelIds) {
		this.detachLabelIds = detachLabelIds;
	}

	/**
	 * @return The {@link #replaceModifiedContext} value.
	 */
	public Boolean getReplaceModifiedContext() {
		return replaceModifiedContext;
	}

	/**
	 * @param replaceModifiedContext the {@link #replaceModifiedContext} value
	 *            to set.
	 */
	public void setReplaceModifiedContext(Boolean replaceModifiedContext) {
		this.replaceModifiedContext = replaceModifiedContext;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(attachLabelIds);
		result = prime * result + Arrays.hashCode(detachLabelIds);
		result = prime * result + Objects.hash(
			exportOptions,
			importOptions,
			name,
			replaceModifiedContext,
			storageId,
			updateOption
		);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof UpdateFileRequest)) {
			return false;
		}
		UpdateFileRequest other = (UpdateFileRequest) obj;
		return
			Arrays.equals(attachLabelIds, other.attachLabelIds) &&
			Arrays.equals(detachLabelIds, other.detachLabelIds) &&
			Objects.equals(exportOptions, other.exportOptions) &&
			Objects.equals(importOptions, other.importOptions) &&
			Objects.equals(name, other.name) &&
			Objects.equals(replaceModifiedContext, other.replaceModifiedContext) &&
			storageId == other.storageId && updateOption == other.updateOption;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UpdateFileRequest [storageId=").append(storageId).append(", ");
		if (name != null) {
			sb.append("name=").append(name).append(", ");
		}
		if (updateOption != null) {
			sb.append("updateOption=").append(updateOption).append(", ");
		}
		if (importOptions != null) {
			sb.append("importOptions=").append(importOptions).append(", ");
		}
		if (exportOptions != null) {
			sb.append("exportOptions=").append(exportOptions).append(", ");
		}
		if (attachLabelIds != null) {
			sb.append("attachLabelIds=").append(Arrays.toString(attachLabelIds)).append(", ");
		}
		if (detachLabelIds != null) {
			sb.append("detachLabelIds=").append(Arrays.toString(detachLabelIds)).append(", ");
		}
		if (replaceModifiedContext != null) {
			sb.append("replaceModifiedContext=").append(replaceModifiedContext);
		}
		sb.append("]");
		return sb.toString();
	}
}
