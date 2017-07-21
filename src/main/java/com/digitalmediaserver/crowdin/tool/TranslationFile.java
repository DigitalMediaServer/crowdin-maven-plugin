package com.digitalmediaserver.crowdin.tool;

/**
 * A class representing a local translation file.
 */
public class TranslationFile {
	private String language;
	private String mavenId;
	private String name;

	/**
	 * Creates a new translation file representation using the given parameters.
	 *
	 * @param language the language of the new translation file.
	 * @param mavenId the Maven id of the new translation file.
	 * @param name the name of the new translation file.
	 */
	public TranslationFile(String language, String mavenId, String name) {
		super();
		this.language = language;
		this.mavenId = mavenId;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((mavenId == null) ? 0 : mavenId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TranslationFile other = (TranslationFile) obj;
		if (language == null) {
			if (other.language != null) {
				return false;
			}
		} else if (!language.equals(other.language)) {
			return false;
		}
		if (mavenId == null) {
			if (other.mavenId != null) {
				return false;
			}
		} else if (!mavenId.equals(other.mavenId)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The language of this translation file.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language of this translation file.
	 *
	 * @param language the language to set.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return The Maven id of this translation file.
	 */
	public String getMavenId() {
		return mavenId;
	}

	/**
	 * Sets the Maven id of this translation file.
	 *
	 * @param mavenId the Maven id to set.
	 */
	public void setMavenId(String mavenId) {
		this.mavenId = mavenId;
	}

	/**
	 * @return The name of this translation file.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this translation file.
	 *
	 * @param name the name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
