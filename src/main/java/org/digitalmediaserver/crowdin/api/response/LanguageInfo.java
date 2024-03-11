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
import java.util.Objects;
import javax.annotation.Nullable;
import com.google.gson.annotations.SerializedName;


/**
 * This class is used for deserializing JSON response objects describing
 * languages received from Crowdin's v2 API. It represents {@code Language}.
 *
 * @author Nadahar
 */
public class LanguageInfo {

	/** The language ID */
	private String id;

	/** The language name */
	private String name;

	/** The editor code */
	private String editorCode;

	/** The two-letters code */
	private String twoLettersCode;

	/** The three-letters code */
	private String threeLettersCode;

	/** The locale */
	private String locale;

	/** The Android code */
	private String androidCode;

	/** The macOS code */
	private String osxCode;

	/** The macOS locale */
	private String osxLocale;

	/** The array of plural category names */
	private String[] pluralCategoryNames;

	/** The plural rules */
	private String pluralRules;

	/** The array of plural examples */
	private String[] pluralExamples;

	/** The {@link TextDirection} */
	private TextDirection textDirection;

	/** The dialect of */
	@Nullable
	private String dialectOf;

	/**
	 * Creates a new instance.
	 */
	public LanguageInfo() {
	}

	/**
	 * @return The language ID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the language ID to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return The language name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the language name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The editor code.
	 */
	public String getEditorCode() {
		return editorCode;
	}

	/**
	 * @param editorCode the editor code to set.
	 */
	public void setEditorCode(String editorCode) {
		this.editorCode = editorCode;
	}

	/**
	 * @return The two-letters code.
	 */
	public String getTwoLettersCode() {
		return twoLettersCode;
	}

	/**
	 * @param twoLettersCode the two-letters code to set.
	 */
	public void setTwoLettersCode(String twoLettersCode) {
		this.twoLettersCode = twoLettersCode;
	}

	/**
	 * @return The three-letters code.
	 */
	public String getThreeLettersCode() {
		return threeLettersCode;
	}

	/**
	 * @param threeLettersCode the three-letters code to set.
	 */
	public void setThreeLettersCode(String threeLettersCode) {
		this.threeLettersCode = threeLettersCode;
	}

	/**
	 * @return The locale.
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set.
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return The Android code.
	 */
	public String getAndroidCode() {
		return androidCode;
	}

	/**
	 * @param androidCode the Android code to set.
	 */
	public void setAndroidCode(String androidCode) {
		this.androidCode = androidCode;
	}

	/**
	 * @return The macOS code.
	 */
	public String getOsxCode() {
		return osxCode;
	}

	/**
	 * @param osxCode the macOS code to set.
	 */
	public void setOsxCode(String osxCode) {
		this.osxCode = osxCode;
	}

	/**
	 * @return The macOS locale.
	 */
	public String getOsxLocale() {
		return osxLocale;
	}

	/**
	 * @param osxLocale the macOS locale to set.
	 */
	public void setOsxLocale(String osxLocale) {
		this.osxLocale = osxLocale;
	}

	/**
	 * @return The array of plural category names.
	 */
	public String[] getPluralCategoryNames() {
		return pluralCategoryNames;
	}

	/**
	 * @param pluralCategoryNames the array of plural category names to set.
	 */
	public void setPluralCategoryNames(String[] pluralCategoryNames) {
		this.pluralCategoryNames = pluralCategoryNames;
	}

	/**
	 * @return The plural rules.
	 */
	public String getPluralRules() {
		return pluralRules;
	}

	/**
	 * @param pluralRules the plural rules to set.
	 */
	public void setPluralRules(String pluralRules) {
		this.pluralRules = pluralRules;
	}

	/**
	 * @return The array of plural examples.
	 */
	public String[] getPluralExamples() {
		return pluralExamples;
	}

	/**
	 * @param pluralExamples the array of plural examples to set.
	 */
	public void setPluralExamples(String[] pluralExamples) {
		this.pluralExamples = pluralExamples;
	}

	/**
	 * @return The {@link TextDirection}.
	 */
	public TextDirection getTextDirection() {
		return textDirection;
	}

	/**
	 * @param textDirection the {@link TextDirection} to set.
	 */
	public void setTextDirection(TextDirection textDirection) {
		this.textDirection = textDirection;
	}

	/**
	 * @return The dialect of.
	 */
	public String getDialectOf() {
		return dialectOf;
	}

	/**
	 * @param dialectOf the dialect of to set.
	 */
	public void setDialectOf(String dialectOf) {
		this.dialectOf = dialectOf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(pluralCategoryNames);
		result = prime * result + Arrays.hashCode(pluralExamples);
		result = prime * result + Objects.hash(
			androidCode,
			dialectOf,
			editorCode,
			id,
			locale,
			name,
			osxCode,
			osxLocale,
			pluralRules,
			textDirection,
			threeLettersCode,
			twoLettersCode
		);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LanguageInfo)) {
			return false;
		}
		LanguageInfo other = (LanguageInfo) obj;
		return
			Objects.equals(androidCode, other.androidCode) &&
			Objects.equals(dialectOf, other.dialectOf) &&
			Objects.equals(editorCode, other.editorCode) &&
			Objects.equals(id, other.id) &&
			Objects.equals(locale, other.locale) &&
			Objects.equals(name, other.name) &&
			Objects.equals(osxCode, other.osxCode) &&
			Objects.equals(osxLocale, other.osxLocale) &&
			Arrays.equals(pluralCategoryNames, other.pluralCategoryNames) &&
			Arrays.equals(pluralExamples, other.pluralExamples) &&
			Objects.equals(pluralRules, other.pluralRules) &&
			textDirection == other.textDirection &&
			Objects.equals(threeLettersCode, other.threeLettersCode) &&
			Objects.equals(twoLettersCode, other.twoLettersCode);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("LanguageInfo [");
		if (id != null) {
			sb.append("id=").append(id).append(", ");
		}
		if (name != null) {
			sb.append("name=").append(name).append(", ");
		}
		if (editorCode != null) {
			sb.append("editorCode=").append(editorCode).append(", ");
		}
		if (twoLettersCode != null) {
			sb.append("twoLettersCode=").append(twoLettersCode).append(", ");
		}
		if (threeLettersCode != null) {
			sb.append("threeLettersCode=").append(threeLettersCode).append(", ");
		}
		if (locale != null) {
			sb.append("locale=").append(locale).append(", ");
		}
		if (androidCode != null) {
			sb.append("androidCode=").append(androidCode).append(", ");
		}
		if (osxCode != null) {
			sb.append("osxCode=").append(osxCode).append(", ");
		}
		if (osxLocale != null) {
			sb.append("osxLocale=").append(osxLocale).append(", ");
		}
		if (pluralCategoryNames != null) {
			sb.append("pluralCategoryNames=").append(Arrays.toString(pluralCategoryNames)).append(", ");
		}
		if (pluralRules != null) {
			sb.append("pluralRules=").append(pluralRules).append(", ");
		}
		if (pluralExamples != null) {
			sb.append("pluralExamples=").append(Arrays.toString(pluralExamples)).append(", ");
		}
		if (textDirection != null) {
			sb.append("textDirection=").append(textDirection).append(", ");
		}
		if (dialectOf != null) {
			sb.append("dialectOf=").append(dialectOf);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * An enum representing text direction.
	 *
	 * @author Nadahar
	 */
	public enum TextDirection {

		/** Left-to-right */
		@SerializedName("ltr")
		LTR,

		/** Right-to-left */
		@SerializedName("rtl")
		RTL
	}
}
