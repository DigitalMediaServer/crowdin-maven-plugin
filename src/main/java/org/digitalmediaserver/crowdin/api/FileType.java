/*
 * Crowdin Maven Plugin, an Apache Maven plugin for synchronizing translation
 * files using the crowdin.com API.
 * Copyright (C) 2018 Digital Media Server developers
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

import static org.digitalmediaserver.crowdin.AbstractCrowdinMojo.isBlank;
import java.util.Locale;
import javax.annotation.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * An {@code enum} representing the Crowdin {@code fileType}. Represents API
 * class {@code ProjectFileType}.
 *
 * @author Nadahar
 */
public enum FileType {

	/** Try to detect file type by extension or MIME type */
	auto(null),

	/** Android */
	android("text/xml", "xml"),

	/** Mac OS X / iOS */
	macosx("text/plain", "strings"),

	/** .NET, Windows Phone */
	resx("application/xml", "resx", "resw"),

	/** Java */
	properties("text/plain", "properties"),

	/** GNU GetText */
	gettext("text/plain", "po", "pot"),

	/** Ruby On Rails */
	yaml("application/yaml", "yaml", "yml"),

	/** Hypertext Preprocessor */
	php("application/x-httpd-php", "php"),

	/** Generic JSON */
	json("application/json", "json"),

	/** Generic XML */
	xml("application/xml", "xml"),

	/** Generic INI */
	ini("text/plain", "ini"),

	/** Windows Resources */
	rc("text/plain", "rc"),

	/** Windows 8 Metro */
	resw("application/xml", "resw"),

	/** Windows 8 Metro */
	resjson("application/json", "resjson"),

	/** Nokia Qt */
	qtts("application/xml", "ts"),

	/** Joomla localizable resources */
	joomla("text/plain", "ini"),

	/** Google Chrome Extension */
	chrome("application/json", "json"),

	/** Mozilla DTD */
	dtd("text/plain", "dtd"),

	/** Delphi DKLang */
	dklang("text/plain", "dklang"),

	/** Flex */
	flex("text/plain", "properties"),

	/** NSIS Installer Resources */
	nsh("text/plain", "nsh"),

	/** WiX Installer */
	wxl("application/xml", "wxl"),

	/** XLIFF */
	xliff("application/xliff+xml", "xliff", "xlf"),

	/** XLIFF 2.0 */
	xliff_two("application/xliff+xml", "xliff", "xlf"),

	/** HTML */
	html("text/html", "html", "htm", "xhtml", "xhtm", "xht", "hbs", "liquid"),

	/** Haml */
	haml("text/x-haml", "haml"),

	/** Plain Text */
	txt("text/plain", "txt"),

	/** Comma Separated Values */
	csv("text/csv", "csv", "tsv"),

	/** Markdown */
	md("text/markdown", "md", "text", "markdown"),

	/** MDX (v1) */
	mdx_v1("text/mdx", "mdx"),

	/** MDX (v2) */
	mdx_v2("text/mdx", "mdx"),

	/** MadCap Flare */
	flsnp("application/xml", "flnsp", "flpgpl", "fltoc"),

	/** Jekyll HTML */
	fm_html("text/html", "html"),

	/** Jekyll Markdown */
	fm_md("text/markdown", "md"),

	/** MediaWiki */
	mediawiki("text/plain", "wiki", "wikitext", "mediawiki"),

	/** Microsoft Office, OpenOffice.org Documents, Adobe InDesign Adobe FrameMaker */
	docx("application/vnd.openxmlformats-officedocument",
		"docx", "dotx", "docm", "dotm", "xltx", "xlsm", "xltm", "pptx", "potx", "ppsx", "pptm",
		"potm", "ppsm", "odt", "ods", "ots", "odg", "otg", "odp", "otp", "ott", "imdl", "mif"
	),

	/** Microsoft Excel */
	xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xls", "xlsx"),

	/** Youtube .sbv */
	sbv("text/plain", "sbv"),

	/** Play Framework */
	properties_play(null),

	/** Java Application */
	properties_xml("application/xml", "xml"),

	/** Maxthon Browser */
	maxthon("text/plain", "ini"),

	/** Go */
	go_json("application/json", "gotext.json"),

	/** DITA Document */
	dita("text/xml", "dita", "ditamap"),

	/** Adobe FrameMaker */
	mif("application/vnd.mif", "mif"),

	/** Adobe InDesign */
	idml("application/vnd.adobe.indesign-idml-package", "idml"),

	/** iOS */
	stringsdict("application/xml", "stringsdict"),

	/** Mac OS property list */
	plist("application/x-plist", "plist"),

	/** Video Subtitling and WebVTT */
	vtt("text/vtt", "vtt"),

	/** Steamworks Localization Valve Data File */
	vdf("text/vdf", "vdf"),

	/** SubRip .srt */
	srt("text/plain", "srt"),

	/** Salesforce */
	stf("text/plain", "stf"),

	/** Toml */
	toml("application/toml", "toml"),

	/** Contentful */
	contentful_rt("application/json", "json"),

	/** SVG */
	svg("image/svg+xml", "svg"),

	/** JavaScript */
	js("text/javascript", "js"),

	/** CoffeeScript */
	coffee("application/vnd.coffeescript", "coffee"),

	/** TypeScript */
	ts("application/typescript", "ts"),

	/** i18next */
	i18next_json("application/json", "json"),

	/** XAML */
	xaml("application/xaml+xml", "xaml"),

	/** Application Resource Bundle */
	arb("text/plain", "arb"),

	/** AsciiDoc */
	adoc("text/asciidoc", "adoc"),

	/** Facebook FBT */
	fbt("application/json", "json"),

	/** Mozilla Project Fluent */
	ftl("text/plain", "ftl"),

	/** Web XML */
	webxml("application/xml", "xml"),

	/** NestJS i18n */
	nestjs_i18n(null);

	@Nullable
	private final String[] extensions;

	@Nullable
	private final String contentType;

	private FileType(String contentType, String... extensions) {
		this.extensions = extensions;
		this.contentType = contentType;
	}

	/**
	 * @return An array of lower-case file extensions associated with this file
	 *         type.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	@Nullable
	public String[] getExtensions() {
		return extensions;
	}

	/**
	 * Determines if this {@link FileType} is associated with the specified
	 * extension. Case insensitive.
	 *
	 * @param extension the extension to check.
	 * @return {@code true} if {@code extension} is associated with this
	 *         {@link FileType}, {@code false} otherwise.
	 */
	public boolean hasExtension(String extension) {
		if (isBlank(extension) || extensions == null || extensions.length == 0) {
			return false;
		}
		if (extension.startsWith(".")) {
			extension = extension.substring(1);
		}
		extension = extension.trim().toLowerCase(Locale.ROOT);
		for (String memberExtension : extensions) {
			if (extension.equals(memberExtension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return The {@code Content-Type} string for this {@link FileType} or
	 *         {@code null} if not applicable.
	 */
	@Nullable
	public String getContentType() {
		return contentType;
	}
}
