package com.digitalmediaserver.crowdin.tool;

import java.util.Locale;

public class CodeConversion {

	public static String crowdinCodeToLanguageTag(String code) {
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

	public static String crowdinCodeToFileTag(String code) {
		return crowdinCodeToLanguageTag(code).replace("-",	"_");
	}
}
