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
package org.digitalmediaserver.crowdin.api;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.maven.plugin.MojoExecutionException;


/**
 * This class is used for serialization and deserialization of JSON objects when
 * communicating with Crowdin's v2 API. It's a combination of several API
 * classes, and thus represents {@code SpreadsheetFileImportOptions},
 * {@code XlsxFileImportOptions}, {@code HtmlFileImportOptions},
 * {@code FmHtmlFileImportOptions}, {@code MdxV1FileImportOptions},
 * {@code MdxV2FileImportOptions}, {@code XmlFileImportOptions},
 * {@code DocxFileImportOptions}, {@code SegmentationImportOptions},
 * {@code CustomSegmentationImportOptions}, {@code StringCatalogImportOptions},
 * {@code AdocFileImportOptions} and {@code OtherFilesImportOptions}.
 * <p>
 * To make sure that only valid parameters are used for a given
 * {@link FileType}, call {@link #validate(FileType)}.
 *
 * @author Nadahar
 */
public class FileImportOptions {

	/**
	 * Defines whether to split long texts into smaller text segments. Only
	 * valid for {@code xml}, {@code md}, {@code flsnp}, {@code docx},
	 * {@code mif}, {@code idml}, {@code dita}, {@code android8} files.
	 * <p>
	 * <b>Note:</b> When Content segmentation is enabled, the translation upload
	 * is handled by an experimental machine learning technology. To achieve the
	 * best results, we recommend uploading translation files with the same or
	 * as close as possible file structure as in source files.
	 * <p>
	 * <b>Important!</b> This option disables the possibility to upload existing
	 * translations for Spreadsheet files when enabled. <b>Important!</b> This
	 * option disables the possibility to upload existing translations for
	 * {@code XML} files when enabled.
	 * <p>
	 * The default varies by file type.
	 */
	@Nullable
	private Boolean contentSegmentation;

	/**
	 * Custom Segmentation File Import Options. Only valid for AsciiDoc, DITA,
	 * Office documents, Madcap Flare, HTML, Markdown, Adobe Indesign, MDX,
	 * MediaWiki, Adobe Framemaker, Plain text and XML files.
	 */
	@Nullable
	private Boolean customSegmentation;

	/**
	 * Storage identifier of the SRX segmentation rules file. Read more about
	 * <a href=
	 * "https://support.crowdin.com/custom-segmentation/#segmentation-examples">Custom
	 * Segmentation</a>.
	 */
	@Nullable
	private Long srxStorageId;

	// "SpreadsheetFileImportOptions" class

	/**
	 * Defines whether the file includes a first-row header that should not be
	 * imported. Default is {@code false};
	 */
	@Nullable
	private Boolean firstLineContainsHeader;

	/**
	 * Defines whether hidden sheets should be imported. Default is
	 * {@code false}.
	 */
	@Nullable
	private Boolean importHiddenSheets;

	/**
	 * Defines whether to import translations from the file. Default is
	 * {@code false}.
	 */
	@Nullable
	private Boolean importTranslations;

	// "XmlFileImportOptions" class

	/**
	 * Defines whether to translate texts placed inside the tags. Default is
	 * {@code true}.
	 */
	@Nullable
	private Boolean translateContent;

	/**
	 * Defines whether to translate tags attributes. Default is {@code true}.
	 */
	@Nullable
	private Boolean translateAttributes;

	/**
	 * This is an array of strings, where each item is the {@code XPaths} to a
	 * {@code DOM element} that should be imported.
	 * <p>
	 * Possible options:
	 * <ul>
	 * <li>/path/to/node</li>
	 * <li>/path/to/attribute[@attr]</li>
	 * <li>//node</li>
	 * <li>//[@attr]</li>
	 * <li>nodeone/nodetwo</li>
	 * <li>/nodeone//nodetwo</li>
	 * <li>//node[@attr]</li>
	 * </ul>
	 */
	@Nullable
	private String[] translatableElements;

	// "DocxFileImportOptions" class

	/**
	 * When checked, strips additional formatting tags related to text spacing.
	 * <p>
	 * <b>Note:</b> Works only for files with the following extensions: *.docx,
	 * *.dotx, *.docm, *.dotm, *.xlsx, *.xltx, *.xlsm, *.xltm, *.pptx, *.potx,
	 * *.ppsx, *.pptm, *.potm, *.ppsm.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean cleanTagsAggressively;

	/**
	 * When checked, exposes hidden text for translation.
	 * <p>
	 * <b>Note:</b> Works only for files with the following extensions: *.docx,
	 * *.dotx, *.docm, *.dotm.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean translateHiddenText;

	/**
	 * When checked, exposes hidden hyperlinks for translation.
	 * <p>
	 * <b>Note:</b> Works only for files with the following extensions: *.docx,
	 * *.dotx, *.docm, *.dotm, *.pptx, *.potx, *.ppsx, *.pptm, *.potm, *.ppsm.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean translateHyperlinkUrls;

	/**
	 * When checked, exposes hidden rows and columns for translation.
	 * <p>
	 * <b>Note:</b> Works only for files with the following extensions: *.xlsx,
	 * *.xltx, *.xlsm, *.xltm.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean translateHiddenRowsAndColumns;

	/**
	 * When checked, expose slide notes for translation.
	 * <p>
	 * <b>Note:</b> Works only for files with the following extensions: *.pptx,
	 * *.potx, *.ppsx, *.pptm, *.potm, *.ppsm.
	 * <p>
	 * Default is {@code true}.
	 */
	@Nullable
	private Boolean importNotes;

	/**
	 * When checked, exposes hidden slides for translation.
	 * <p>
	 * <b>Note:</b> Works only for files with the following extensions: *.pptx,
	 * *.potx, *.ppsx, *.pptm, *.potm, *.ppsm.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean importHiddenSlides;

	// "HtmlFileImportOptions" and "FMHtmlFileImportOptions" class

	/**
	 * Specify CSS selectors for elements that should not be imported.
	 */
	@Nullable
	private String[] excludedElements;

	// "FMHtmlFileImportOptions", "MdxV1FileImportOptions" and "MdxV2FileImportOptions" classes

	/**
	 * Specify elements that should not be imported.
	 */
	@Nullable
	private String[] excludedFrontMatterElements;

	// "MdxV1FileImportOptions" and "MdxV2FileImportOptions" classes

	/**
	 * Defines whether to import code blocks.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean excludeCodeBlocks;

	// "StringCatalogImportOptions" class

	/**
	 * Determines whether to import the key as source string if it does not
	 * exist.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean importKeyAsSource;

	// "AdocFileImportOptions" class

	/**
	 * Skip Include Directives.
	 * <p>
	 * Default is {@code false}.
	 */
	@Nullable
	private Boolean excludeIncludeDirectives;

	/**
	 * Create a new instance.
	 */
	public FileImportOptions() {
	}

	/**
	 * Validates that no option has been set that is invalid for the specified
	 * {@link FileType}.
	 * <p>
	 * <b>Warning</b>: If {@code fileType} is {@code null} or
	 * {@link FileType#auto}, no validation is performed.
	 *
	 * @param fileType the {@link FileType} for which to validate.
	 *
	 * @throws MojoExecutionException If validation fails.
	 */
	public void validate(@Nullable FileType fileType) throws MojoExecutionException {
		if (fileType == null || fileType == FileType.auto) {
			return;
		}

		// Only valid for xml, md, flsnp, docx, mif, idml, dita, android8 files.
		if (
			contentSegmentation != null &&
			fileType != FileType.android &&
			fileType != FileType.chrome &&
			fileType != FileType.csv &&
			fileType != FileType.docx &&
			fileType != FileType.flsnp &&
			fileType != FileType.fm_html &&
			fileType != FileType.fm_md &&
			fileType != FileType.html &&
			fileType != FileType.json &&
			fileType != FileType.md &&
			fileType != FileType.xml
		) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: contentSegmentation is only " +
				"valid for xml, md, flsnp, docx, mif, idml, dita, android8 files"
			);
		}
		if (
			customSegmentation != null &&
			fileType != FileType.docx &&
			fileType != FileType.flsnp &&
			fileType != FileType.fm_html &&
			fileType != FileType.fm_md &&
			fileType != FileType.html &&
			fileType != FileType.md &&
			fileType != FileType.mediawiki &&
			fileType != FileType.txt &&
			fileType != FileType.xml
		) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: customSegmentation is only valid for " +
				"AsciiDoc, DITA, Office documents, Madcap Flare, HTML, Markdown, Adobe Indesign, " +
				"MDX, MediaWiki, Adobe Framemaker, Plain text and XML files"
			);
		}
		if (
			srxStorageId != null &&
			fileType != FileType.docx &&
			fileType != FileType.flsnp &&
			fileType != FileType.fm_html &&
			fileType != FileType.fm_md &&
			fileType != FileType.html &&
			fileType != FileType.md &&
			fileType != FileType.mediawiki &&
			fileType != FileType.txt &&
			fileType != FileType.xml
		) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: srxStorageId is only valid for " +
				"AsciiDoc, DITA, Office documents, Madcap Flare, HTML, Markdown, Adobe Indesign, " +
				"MDX, MediaWiki, Adobe Framemaker, Plain text and XML files"
			);
		}
		if (translateContent != null && fileType != FileType.xml) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: translateContent is only valid for XML files"
			);
		}
		if (translateAttributes != null && fileType != FileType.xml) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: translateAttributes is only valid for XML files"
			);
		}
		if (translatableElements != null && fileType != FileType.xml) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: translatableElements is only valid for XML files"
			);
		}
		if (cleanTagsAggressively != null && fileType != FileType.docx) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: cleanTagsAggressively is only valid for Office documents"
			);
		}
		if (translateHiddenText != null && fileType != FileType.docx) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: translateHiddenText is only valid for Office documents"
			);
		}
		if (translateHyperlinkUrls != null && fileType != FileType.docx) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: translateHyperlinkUrls is only valid for Office documents"
			);
		}
		if (importNotes != null && fileType != FileType.docx) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: importNotes is only valid for Office documents"
			);
		}
		if (importHiddenSlides != null && fileType != FileType.docx) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: importHiddenSlides is only valid for Office documents"
			);
		}
		if (excludedElements != null && fileType != FileType.html && fileType != FileType.fm_html) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: excludedElements is only valid for HTML files"
			);
		}
		if (
			excludedFrontMatterElements != null &&
			fileType != FileType.fm_html
		) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: excludedFrontMatterElements is only valid for FrontMatter and MDX files"
			);
		}
		if (
			excludeCodeBlocks != null
		) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: excludeCodeBlocks is only valid for MDX files"
			);
		}
		if (importKeyAsSource != null) {
			throw new MojoExecutionException(
				"FileImportOptions validation failed: importKeyAsSource is only valid for String Catalog files"
			);
		}
	}

	/**
	 * @return The {@link #contentSegmentation} value.
	 */
	@Nullable
	public Boolean getContentSegmentation() {
		return contentSegmentation;
	}

	/**
	 * Sets whether content segmentation is enabled.
	 *
	 * @param contentSegmentation the {@link #contentSegmentation} value to set.
	 */
	public void setContentSegmentation(Boolean contentSegmentation) {
		this.contentSegmentation = contentSegmentation;
	}

	/**
	 * @return The customSegmentation.
	 */
	@Nullable
	public Boolean getCustomSegmentation() {
		return customSegmentation;
	}

	/**
	 * Sets whether custom segmentation is enabled.
	 *
	 * @param customSegmentation the {@link #customSegmentation} value to set.
	 */
	public void setCustomSegmentation(Boolean customSegmentation) {
		this.customSegmentation = customSegmentation;
	}

	/**
	 * @return The {@link #srxStorageId} value.
	 */
	@Nullable
	public Long getSrxStorageId() {
		return srxStorageId;
	}

	/**
	 * Sets the SRX segmentation rules storage id.
	 *
	 * @param srxStorageId the {@link #srxStorageId} value to set.
	 */
	public void setSrxStorageId(Long srxStorageId) {
		this.srxStorageId = srxStorageId;
	}

	/**
	 * @return The {@link #firstLineContainsHeader} value.
	 */
	@Nullable
	public Boolean getFirstLineContainsHeader() {
		return firstLineContainsHeader;
	}

	/**
	 * Sets whether the first line contains a header.
	 *
	 * @param firstLineContainsHeader the {@link #firstLineContainsHeader} value
	 *            to set.
	 */
	public void setFirstLineContainsHeader(Boolean firstLineContainsHeader) {
		this.firstLineContainsHeader = firstLineContainsHeader;
	}

	/**
	 * @return The {@link #importHiddenSheets} value.
	 */
	@Nullable
	public Boolean getImportHiddenSheets() {
		return importHiddenSheets;
	}

	/**
	 * Sets whether to import hidden sheets.
	 *
	 * @param importHiddenSheets the {@link #importHiddenSheets} value to set.
	 */
	public void setImportHiddenSheets(Boolean importHiddenSheets) {
		this.importHiddenSheets = importHiddenSheets;
	}

	/**
	 * @return The {@link #importTranslations} value.
	 */
	@Nullable
	public Boolean getImportTranslations() {
		return importTranslations;
	}

	/**
	 * Sets whether to import translations.
	 *
	 * @param importTranslations the {@link #importTranslations} value to set.
	 */
	public void setImportTranslations(Boolean importTranslations) {
		this.importTranslations = importTranslations;
	}

	/**
	 * @return The {@link #translateContent} value.
	 */
	@Nullable
	public Boolean getTranslateContent() {
		return translateContent;
	}

	/**
	 * Sets whether to translate content inside tags.
	 *
	 * @param translateContent the {@link #translateContent} value to set.
	 */
	public void setTranslateContent(Boolean translateContent) {
		this.translateContent = translateContent;
	}

	/**
	 * @return The {@link #translateAttributes} value.
	 */
	@Nullable
	public Boolean getTranslateAttributes() {
		return translateAttributes;
	}

	/**
	 * Sets whether to translate tags attributes.
	 *
	 * @param translateAttributes the {@link #translateAttributes} value to set.
	 */
	public void setTranslateAttributes(Boolean translateAttributes) {
		this.translateAttributes = translateAttributes;
	}

	/**
	 * @return The {@link #translatableElements} value.
	 */
	@Nullable
	public String[] getTranslatableElements() {
		return translatableElements;
	}

	/**
	 * Sets the array of translatable elements.
	 *
	 * @param translatableElements the {@link #translatableElements} value to
	 *            set.
	 */
	public void setTranslatableElements(String[] translatableElements) {
		this.translatableElements = translatableElements;
	}

	/**
	 * @return The {@link #cleanTagsAggressively} value.
	 */
	@Nullable
	public Boolean getCleanTagsAggressively() {
		return cleanTagsAggressively;
	}

	/**
	 * Sets whether to clean tags aggressively.
	 *
	 * @param cleanTagsAggressively the {@link #cleanTagsAggressively} value to
	 *            set.
	 */
	public void setCleanTagsAggressively(Boolean cleanTagsAggressively) {
		this.cleanTagsAggressively = cleanTagsAggressively;
	}

	/**
	 * @return The {@link #translateHiddenText} value.
	 */
	@Nullable
	public Boolean getTranslateHiddenText() {
		return translateHiddenText;
	}

	/**
	 * Sets whether to translate hidden text.
	 *
	 * @param translateHiddenText the {@link #translateHiddenText} value to set.
	 */
	public void setTranslateHiddenText(Boolean translateHiddenText) {
		this.translateHiddenText = translateHiddenText;
	}

	/**
	 * @return The {@link #translateHyperlinkUrls} value.
	 */
	@Nullable
	public Boolean getTranslateHyperlinkUrls() {
		return translateHyperlinkUrls;
	}

	/**
	 * Sets whether to translate hyperlinks.
	 *
	 * @param translateHyperlinkUrls the {@link #translateHyperlinkUrls} value
	 *            to set.
	 */
	public void setTranslateHyperlinkUrls(Boolean translateHyperlinkUrls) {
		this.translateHyperlinkUrls = translateHyperlinkUrls;
	}

	/**
	 * @return The {@link #translateHiddenRowsAndColumns} value.
	 */
	@Nullable
	public Boolean getTranslateHiddenRowsAndColumns() {
		return translateHiddenRowsAndColumns;
	}

	/**
	 * Sets whether to translate hidden rows and columns.
	 *
	 * @param translateHiddenRowsAndColumns the
	 *            {@link #translateHiddenRowsAndColumns} value to set.
	 */
	public void setTranslateHiddenRowsAndColumns(Boolean translateHiddenRowsAndColumns) {
		this.translateHiddenRowsAndColumns = translateHiddenRowsAndColumns;
	}

	/**
	 * @return The {@link #importNotes} value.
	 */
	@Nullable
	public Boolean getImportNotes() {
		return importNotes;
	}

	/**
	 * Sets whether to import notes.
	 *
	 * @param importNotes the {@link #importNotes} value to set.
	 */
	public void setImportNotes(Boolean importNotes) {
		this.importNotes = importNotes;
	}

	/**
	 * @return The {@link #importHiddenSlides} value.
	 */
	@Nullable
	public Boolean getImportHiddenSlides() {
		return importHiddenSlides;
	}

	/**
	 * Sets whether to import hidden slides.
	 *
	 * @param importHiddenSlides the {@link #importHiddenSlides} value to set.
	 */
	public void setImportHiddenSlides(Boolean importHiddenSlides) {
		this.importHiddenSlides = importHiddenSlides;
	}

	/**
	 * @return The {@link #excludedElements} value.
	 */
	@Nullable
	public String[] getExcludedElements() {
		return excludedElements;
	}

	/**
	 * Sets an array CSS selectors for elements that should not be imported.
	 *
	 * @param excludedElements the {@link #excludedElements} value to set.
	 */
	public void setExcludedElements(String[] excludedElements) {
		this.excludedElements = excludedElements;
	}

	/**
	 * @return The {@link #excludedFrontMatterElements} value.
	 */
	@Nullable
	public String[] getExcludedFrontMatterElements() {
		return excludedFrontMatterElements;
	}

	/**
	 * Sets an array of elements that should not be imported.
	 *
	 * @param excludedFrontMatterElements the
	 *            {@link #excludedFrontMatterElements} value to set.
	 */
	public void setExcludedFrontMatterElements(String[] excludedFrontMatterElements) {
		this.excludedFrontMatterElements = excludedFrontMatterElements;
	}

	/**
	 * @return The {@link #excludeCodeBlocks} value.
	 */
	@Nullable
	public Boolean getExcludeCodeBlocks() {
		return excludeCodeBlocks;
	}

	/**
	 * Sets whether to exclude code blocks.
	 *
	 * @param excludeCodeBlocks the {@link #excludeCodeBlocks} value to set.
	 */
	public void setExcludeCodeBlocks(Boolean excludeCodeBlocks) {
		this.excludeCodeBlocks = excludeCodeBlocks;
	}

	/**
	 * @return The {@link #importKeyAsSource} value.
	 */
	@Nullable
	public Boolean getImportKeyAsSource() {
		return importKeyAsSource;
	}

	/**
	 * Sets whether to import keys as source strings.
	 *
	 * @param importKeyAsSource the {@link #importKeyAsSource} value to set.
	 */
	public void setImportKeyAsSource(Boolean importKeyAsSource) {
		this.importKeyAsSource = importKeyAsSource;
	}

	/**
	 * @return The {@link #excludeIncludeDirectives} value.
	 */
	@Nullable
	public Boolean getExcludeIncludeDirectives() {
		return excludeIncludeDirectives;
	}

	/**
	 * Sets whether to skip include directives.
	 *
	 * @param excludeIncludeDirectives the {@link #excludeIncludeDirectives}
	 *            value to set.
	 */
	public void setExcludeIncludeDirectives(Boolean excludeIncludeDirectives) {
		this.excludeIncludeDirectives = excludeIncludeDirectives;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(excludedElements);
		result = prime * result + Arrays.hashCode(excludedFrontMatterElements);
		result = prime * result + Arrays.hashCode(translatableElements);
		result = prime * result + Objects.hash(
			cleanTagsAggressively,
			contentSegmentation,
			customSegmentation,
			excludeCodeBlocks,
			excludeIncludeDirectives,
			firstLineContainsHeader,
			importHiddenSheets,
			importHiddenSlides,
			importKeyAsSource,
			importNotes,
			importTranslations,
			srxStorageId,
			translateAttributes,
			translateContent,
			translateHiddenRowsAndColumns,
			translateHiddenText,
			translateHyperlinkUrls
		);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FileImportOptions)) {
			return false;
		}
		FileImportOptions other = (FileImportOptions) obj;
		return
			Objects.equals(cleanTagsAggressively, other.cleanTagsAggressively) &&
			Objects.equals(contentSegmentation, other.contentSegmentation) &&
			Objects.equals(customSegmentation, other.customSegmentation) &&
			Objects.equals(excludeCodeBlocks, other.excludeCodeBlocks) &&
			Objects.equals(excludeIncludeDirectives, other.excludeIncludeDirectives) &&
			Arrays.equals(excludedElements, other.excludedElements) &&
			Arrays.equals(excludedFrontMatterElements, other.excludedFrontMatterElements) &&
			Objects.equals(firstLineContainsHeader, other.firstLineContainsHeader) &&
			Objects.equals(importHiddenSheets, other.importHiddenSheets) &&
			Objects.equals(importHiddenSlides, other.importHiddenSlides) &&
			Objects.equals(importKeyAsSource, other.importKeyAsSource) &&
			Objects.equals(importNotes, other.importNotes) &&
			Objects.equals(importTranslations, other.importTranslations) &&
			Objects.equals(srxStorageId, other.srxStorageId) &&
			Arrays.equals(translatableElements, other.translatableElements) &&
			Objects.equals(translateAttributes, other.translateAttributes) &&
			Objects.equals(translateContent, other.translateContent) &&
			Objects.equals(translateHiddenRowsAndColumns, other.translateHiddenRowsAndColumns) &&
			Objects.equals(translateHiddenText, other.translateHiddenText) &&
			Objects.equals(translateHyperlinkUrls, other.translateHyperlinkUrls);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileImportOptions [");
		if (contentSegmentation != null) {
			builder.append("contentSegmentation=").append(contentSegmentation).append(", ");
		}
		if (customSegmentation != null) {
			builder.append("customSegmentation=").append(customSegmentation).append(", ");
		}
		if (srxStorageId != null) {
			builder.append("srxStorageId=").append(srxStorageId).append(", ");
		}
		if (firstLineContainsHeader != null) {
			builder.append("firstLineContainsHeader=").append(firstLineContainsHeader).append(", ");
		}
		if (importHiddenSheets != null) {
			builder.append("importHiddenSheets=").append(importHiddenSheets).append(", ");
		}
		if (importTranslations != null) {
			builder.append("importTranslations=").append(importTranslations).append(", ");
		}
		if (translateContent != null) {
			builder.append("translateContent=").append(translateContent).append(", ");
		}
		if (translateAttributes != null) {
			builder.append("translateAttributes=").append(translateAttributes).append(", ");
		}
		if (translatableElements != null) {
			builder.append("translatableElements=").append(Arrays.toString(translatableElements)).append(", ");
		}
		if (cleanTagsAggressively != null) {
			builder.append("cleanTagsAggressively=").append(cleanTagsAggressively).append(", ");
		}
		if (translateHiddenText != null) {
			builder.append("translateHiddenText=").append(translateHiddenText).append(", ");
		}
		if (translateHyperlinkUrls != null) {
			builder.append("translateHyperlinkUrls=").append(translateHyperlinkUrls).append(", ");
		}
		if (translateHiddenRowsAndColumns != null) {
			builder.append("translateHiddenRowsAndColumns=").append(translateHiddenRowsAndColumns).append(", ");
		}
		if (importNotes != null) {
			builder.append("importNotes=").append(importNotes).append(", ");
		}
		if (importHiddenSlides != null) {
			builder.append("importHiddenSlides=").append(importHiddenSlides).append(", ");
		}
		if (excludedElements != null) {
			builder.append("excludedElements=").append(Arrays.toString(excludedElements)).append(", ");
		}
		if (excludedFrontMatterElements != null) {
			builder.append("excludedFrontMatterElements=").append(Arrays.toString(excludedFrontMatterElements)).append(", ");
		}
		if (excludeCodeBlocks != null) {
			builder.append("excludeCodeBlocks=").append(excludeCodeBlocks).append(", ");
		}
		if (importKeyAsSource != null) {
			builder.append("importKeyAsSource=").append(importKeyAsSource).append(", ");
		}
		if (excludeIncludeDirectives != null) {
			builder.append("excludeIncludeDirectives=").append(excludeIncludeDirectives);
		}
		builder.append("]");
		return builder.toString();
	}
}
