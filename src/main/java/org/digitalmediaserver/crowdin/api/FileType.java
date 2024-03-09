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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * An {@code enum} representing the {@code type} parameter in the
 * {@code add-file} API method.
 *
 * @author Nadahar
 */
public enum FileType {

	/** Try to detect file type by extension or MIME type */
	auto,

	/** Android */
	android("xml"),

	/** Mac OS X / iOS */
	macosx("strings"),

	/** .NET, Windows Phone */
	resx("resx", "resw"),

	/** Java */
	properties("properties"),

	/** GNU GetText */
	gettext("po", "pot"),

	/** Ruby On Rails */
	yaml("yaml"),

	/** Hypertext Preprocessor */
	php("php"),

	/** Generic JSON */
	json("json"),

	/** Generic XML */
	xml("xml"),

	/** Generic INI */
	ini("ini"),

	/** Windows Resources */
	rc("rc"),

	/** Windows 8 Metro */
	resw("resw"),

	/** Windows 8 Metro */
	resjson("resjson"),

	/** Nokia Qt */
	qtts("ts"),

	/** Joomla localizable resources */
	joomla("ini"),

	/** Google Chrome Extension */
	chrome("json"),

	/** Mozilla DTD */
	dtd("dtd"),

	/** Delphi DKLang */
	dklang("dklang"),

	/** Flex */
	flex("properties"),

	/** NSIS Installer Resources */
	nsh("nsh"),

	/** WiX Installer */
	wxl("wxl"),

	/** XLIFF */
	xliff("xliff"),

	/** HTML */
	html("html", "htm", "xhtml", "xhtm"),

	/** Haml */
	haml("haml"),

	/** Plain Text */
	txt("txt"),

	/** Comma Separated Values */
	csv("csv"),

	/** Markdown */
	md("md", "text", "markdown"),

	/** MadCap Flare */
	flsnp("flnsp", "flpgpl", "fltoc"),

	/** Jekyll HTML */
	fm_html("html"),

	/** Jekyll Markdown */
	fm_md("md"),

	/** MediaWiki */
	mediawiki("wiki", "wikitext", "mediawiki"),

	/** Microsoft Office, OpenOffice.org Documents, Adobe InDesign Adobe FrameMaker */
	docx("docx", "dotx", "odt", "ott", "xslx", "xltx", "pptx", "potx", "ods", "ots", "odg", "otg", "odp", "otp", "imdl", "mif"),

	/** Youtube .sbv */
	sbv("sbv"),

	/** Video Subtitling and WebVTT */
	vtt("vtt"),

	/** SubRip .srt */
	srt("srt");

	private final String[] extensions;

	private FileType(String... extensions) {
		this.extensions = extensions;
	}

	/**
	 * @return An array of lower-case file extensions associated with this file
	 *         type.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
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
}
