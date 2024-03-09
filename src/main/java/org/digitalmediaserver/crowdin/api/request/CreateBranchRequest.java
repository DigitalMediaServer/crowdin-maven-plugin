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
import org.digitalmediaserver.crowdin.api.Priority;


/**
 * This class is used for serializing a JSON object when requesting to create a
 * branch with Crowdin's v2 API. It represents {@code BranchCreateForm}.
 *
 * @author Nadahar
 */
public class CreateBranchRequest {

	/**
	 * Branch name. <b>Required</b>.
	 * <p>
	 * <b>Note:</b> Can't contain {@code \ / : * ? \" < > |} symbols.
	 */
	@Nonnull
	private final String name;

	/** Use to provide more details for translators. Available in UI only */
	@Nullable
	private String title;

	/**
	 * Branch export pattern. Defines branch name and path in the resulting
	 * translations bundle.
	 * <p>
	 * <b>Note:</b> Can't contain {@code : * ? \" < > |} symbols.
	 */
	@Nullable
	private String exportPattern;

	/** Defines priority level for the branch */
	@Nonnull
	private Priority priority = Priority.NORMAL;

	/**
	 * Creates a new instance with the specified branch name.
	 *
	 * @param name the branch name.
	 * @throws IllegalArgumentException If {@code name} is blank.
	 */
	public CreateBranchRequest(@Nonnull String name) {
		if (isBlank(name)) {
			throw new IllegalArgumentException("name cannot be blank");
		}
		this.name = name;
	}

	/**
	 * @return The {@link #name} value.
	 */
	public String getName() {
		return name;
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

	@Override
	public int hashCode() {
		return Objects.hash(exportPattern, name, priority, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CreateBranchRequest)) {
			return false;
		}
		CreateBranchRequest other = (CreateBranchRequest) obj;
		return
			Objects.equals(exportPattern, other.exportPattern) &&
			Objects.equals(name, other.name) &&
			priority == other.priority &&
			Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreateBranchRequest [").append("name=").append(name);
		if (title != null) {
			builder.append(", ").append("title=").append(title);
		}
		if (exportPattern != null) {
			builder.append(", ").append("exportPattern=").append(exportPattern);
		}
		if (priority != null) {
			builder.append(", ").append("priority=").append(priority);
		}
		builder.append("]");
		return builder.toString();
	}
}
