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


/**
 * This class is used for deserializing JSON response objects describing
 * projects received from Crowdin's v2 API. It represents
 * {@code ProjectResponse}.
 *
 * @author Nadahar
 */
public class ProjectInfo {

	/** The project ID */
	private long id;

	/** The project type */
	private long type;

	/** The user ID */
	private long userId;

	/** The source language ID */
	private String sourceLanguageId;

	/** The array of target language IDs */
	private String[] targetLanguageIds;

	/** The language access policy */
	private String languageAccessPolicy;

	/** The project name */
	private String name;

	/** The cname */
	@Nullable
	private String cname;

	/** The project identifier */
	private String identifier;

	/** The description */
	private String description;

	/** The visibility */
	private String visibility;

	/** The project logo */
	private String logo;

	/** The publicDownloads value */
	@Nullable
	private Boolean publicDownloads;

	/** The creation time */
	@Nullable
	private Date createdAt;

	/** The update time */
	@Nullable
	private Date updatedAt;

	/** The last activity */
	@Nullable
	private Date lastActivity;

	/** The source language */
	private LanguageInfo sourceLanguage;

	/** The array of target languages */
	private LanguageInfo[] targetLanguages;

	/**
	 * Creates a new instance.
	 */
	public ProjectInfo() {
	}

	/**
	 * @return The project ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the project ID to set.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return The project type.
	 */
	public long getType() {
		return type;
	}

	/**
	 * @param type the project type to set.
	 */
	public void setType(long type) {
		this.type = type;
	}

	/**
	 * @return The user ID.
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the user ID to set.
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return The source language ID.
	 */
	public String getSourceLanguageId() {
		return sourceLanguageId;
	}

	/**
	 * @param sourceLanguageId the source language ID to set.
	 */
	public void setSourceLanguageId(String sourceLanguageId) {
		this.sourceLanguageId = sourceLanguageId;
	}

	/**
	 * @return The array of target language IDs.
	 */
	public String[] getTargetLanguageIds() {
		return targetLanguageIds;
	}

	/**
	 * @param targetLanguageIds the array of target language IDs to set.
	 */
	public void setTargetLanguageIds(String[] targetLanguageIds) {
		this.targetLanguageIds = targetLanguageIds;
	}

	/**
	 * @return The language access policy.
	 */
	public String getLanguageAccessPolicy() {
		return languageAccessPolicy;
	}

	/**
	 * @param languageAccessPolicy the language access policy to set.
	 */
	public void setLanguageAccessPolicy(String languageAccessPolicy) {
		this.languageAccessPolicy = languageAccessPolicy;
	}

	/**
	 * @return The project name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the project name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The cname.
	 */
	public String getCname() {
		return cname;
	}

	/**
	 * @param cname the cname to set.
	 */
	public void setCname(String cname) {
		this.cname = cname;
	}

	/**
	 * @return The project identifier.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the project identifier to set.
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return The visibility.
	 */
	public String getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set.
	 */
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return The logo.
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set.
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * @return The {@link #publicDownloads} value.
	 */
	public Boolean getPublicDownloads() {
		return publicDownloads;
	}

	/**
	 * @param publicDownloads the {@link #publicDownloads} value to set.
	 */
	public void setPublicDownloads(Boolean publicDownloads) {
		this.publicDownloads = publicDownloads;
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
	 * @return The last activity.
	 */
	public Date getLastActivity() {
		return lastActivity;
	}

	/**
	 * @param lastActivity the last activity to set.
	 */
	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}

	/**
	 * @return The source language.
	 */
	public LanguageInfo getSourceLanguage() {
		return sourceLanguage;
	}

	/**
	 * @param sourceLanguage the source language to set.
	 */
	public void setSourceLanguage(LanguageInfo sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	/**
	 * @return The array of target languages.
	 */
	public LanguageInfo[] getTargetLanguages() {
		return targetLanguages;
	}

	/**
	 * @param targetLanguages the array of target languages to set.
	 */
	public void setTargetLanguages(LanguageInfo[] targetLanguages) {
		this.targetLanguages = targetLanguages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(targetLanguageIds);
		result = prime * result + Arrays.hashCode(targetLanguages);
		result = prime * result + Objects.hash(
			cname,
			createdAt,
			description,
			id,
			identifier,
			languageAccessPolicy,
			lastActivity,
			logo,
			name,
			publicDownloads,
			sourceLanguage,
			sourceLanguageId,
			type,
			updatedAt,
			userId,
			visibility
		);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ProjectInfo)) {
			return false;
		}
		ProjectInfo other = (ProjectInfo) obj;
		return
			Objects.equals(cname, other.cname) &&
			Objects.equals(createdAt, other.createdAt) &&
			Objects.equals(description, other.description) &&
			id == other.id &&
			Objects.equals(identifier, other.identifier) &&
			Objects.equals(languageAccessPolicy, other.languageAccessPolicy) &&
			Objects.equals(lastActivity, other.lastActivity) &&
			Objects.equals(logo, other.logo) &&
			Objects.equals(name, other.name) &&
			Objects.equals(publicDownloads, other.publicDownloads) &&
			Objects.equals(sourceLanguage, other.sourceLanguage) &&
			Objects.equals(sourceLanguageId, other.sourceLanguageId) &&
			Arrays.equals(targetLanguageIds, other.targetLanguageIds) &&
			Arrays.equals(targetLanguages, other.targetLanguages) &&
			type == other.type &&
			Objects.equals(updatedAt, other.updatedAt) &&
			userId == other.userId &&
			Objects.equals(visibility, other.visibility);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("ProjectInfo [id=").append(id)
			.append(", type=").append(type)
			.append(", userId=").append(userId).append(", ");
		if (sourceLanguageId != null) {
			sb.append("sourceLanguageId=").append(sourceLanguageId).append(", ");
		}
		if (targetLanguageIds != null) {
			sb.append("targetLanguageIds=").append(Arrays.toString(targetLanguageIds)).append(", ");
		}
		if (languageAccessPolicy != null) {
			sb.append("languageAccessPolicy=").append(languageAccessPolicy).append(", ");
		}
		if (name != null) {
			sb.append("name=").append(name).append(", ");
		}
		if (cname != null) {
			sb.append("cname=").append(cname).append(", ");
		}
		if (identifier != null) {
			sb.append("identifier=").append(identifier).append(", ");
		}
		if (description != null) {
			sb.append("description=").append(description).append(", ");
		}
		if (visibility != null) {
			sb.append("visibility=").append(visibility).append(", ");
		}
		if (logo != null) {
			sb.append("logo=").append(logo).append(", ");
		}
		if (publicDownloads != null) {
			sb.append("publicDownloads=").append(publicDownloads).append(", ");
		}
		if (createdAt != null) {
			sb.append("createdAt=").append(createdAt).append(", ");
		}
		if (updatedAt != null) {
			sb.append("updatedAt=").append(updatedAt).append(", ");
		}
		if (lastActivity != null) {
			sb.append("lastActivity=").append(lastActivity).append(", ");
		}
		if (sourceLanguage != null) {
			sb.append("sourceLanguage=").append(sourceLanguage.getName()).append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
