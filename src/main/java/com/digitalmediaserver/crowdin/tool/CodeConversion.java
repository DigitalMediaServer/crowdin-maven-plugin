package com.digitalmediaserver.crowdin.tool;

import java.util.Locale;

/**
 * An class for converting crowdin language codes to project language
 * codes.
 *
 * @author Nadahar
 */
public class CodeConversion {

	/**
	 * Converts a crowdin language code to the corresponding project language
	 * code.
	 *
	 * @param code the crowdin language code to convert.
	 * @return The corresponding project language code.
	 */
	public String crowdinCodeToLanguageTag(String code) {
		String lcCode = code.toLowerCase(Locale.US);
		if (lcCode.equals("es-es")) {
			return "es";
		} else if (lcCode.equals("he")) {
			return "iw";
		} else if (lcCode.equals("pt-pt")) {
			return "pt";
		} else if (lcCode.equals("sv-se")) {
			return "sv";
		} else if (lcCode.equals("zh-cn")) {
			return "zh-Hans";
		} else if (lcCode.equals("zh-tw")) {
			return "zh-Hant";
		}
		return code;
	}

	/**
	 * Converts a crowding language code to the corresponding code to append to
	 * file names.
	 *
	 * @param code the crowdin language code to convert.
	 * @return The corresponding project file name tag code.
	 */
	public String crowdinCodeToFileTag(String code) {
		return crowdinCodeToLanguageTag(code).replace("-",	"_");
	}
}
