package com.googlecode.crowdin.maven.tool;

public class TranslationFile {
	private String language;
	private String mavenId;
	private String name;

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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TranslationFile other = (TranslationFile) obj;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (mavenId == null) {
			if (other.mavenId != null)
				return false;
		} else if (!mavenId.equals(other.mavenId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMavenId() {
		return mavenId;
	}

	public void setMavenId(String mavenId) {
		this.mavenId = mavenId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
