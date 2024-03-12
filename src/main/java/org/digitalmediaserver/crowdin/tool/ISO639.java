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
package org.digitalmediaserver.crowdin.tool;

import static org.digitalmediaserver.crowdin.tool.StringUtil.isBlank;
import static org.digitalmediaserver.crowdin.tool.StringUtil.isNotBlank;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * This {@code enum} represents the {@code ISO 639-1} and {@code ISO 639-2}
 * languages.
 * <p>
 * {@code ISO 639 codes} updated <b>2018-02-01</b>.
 *
 * @author Nadahar
 */
public enum ISO639 {

	/** Abkhazian */
	ABKHAZIAN("Abkhazian", LanguageType.NORMAL, "ab", "abk"),

	/** Achinese */
	ACHINESE("Achinese", LanguageType.NORMAL, null, "ace"),

	/** Acoli */
	ACOLI("Acoli", LanguageType.NORMAL, null, "ach"),

	/** Adangme */
	ADANGME("Adangme", LanguageType.NORMAL, null, "ada"),

	/** Adyghe/Adygei */
	ADYGHE("Adyghe;Adygei", LanguageType.NORMAL, null, "ady"),

	/** Afar */
	AFAR("Afar", LanguageType.NORMAL, "aa", "aar"),

	/** Afrihili */
	AFRIHILI("Afrihili", LanguageType.NORMAL, null, "afh"),

	/** Afrikaans */
	AFRIKAANS("Afrikaans", LanguageType.NORMAL, "af", "afr"),

	/** Afro-Asiatic languages */
	AFRO_ASIATIC_LANGUAGES("Afro-Asiatic languages", LanguageType.GROUP, null, "afa"),

	/** Ainu/Ainu (Japan) */
	AINU("Ainu;Ainu (Japan)", LanguageType.NORMAL, null, "ain"),

	/** Akan */
	AKAN("Akan", LanguageType.NORMAL, "ak", "aka"),

	/** Akkadian */
	AKKADIAN("Akkadian", LanguageType.NORMAL, null, "akk"),

	/** Albanian */
	ALBANIAN("Albanian", LanguageType.NORMAL, "sq", "alb", "sqi"),

	/** Alemannic/Alsatian/Swiss German */
	ALEMANNIC("Alemannic;Alsatian;Swiss German", LanguageType.NORMAL, null, "gsw"),

	/** Aleut */
	ALEUT("Aleut", LanguageType.NORMAL, null, "ale"),

	/** Algonquian/Algonquian languages */
	ALGONQUIAN("Algonquian;Algonquian languages", LanguageType.GROUP, null, "alg"),

	/** Altaic/Altaic languages */
	ALTAIC("Altaic;Altaic languages", LanguageType.GROUP, null, "tut"),

	/** Amharic */
	AMHARIC("Amharic", LanguageType.NORMAL, "am", "amh"),

	/** Ancient Greek (to 1453) */
	ANCIENT_GREEK("Ancient Greek (to 1453)", LanguageType.HISTORICAL, null, "grc"),

	/** Angika */
	ANGIKA("Angika", LanguageType.NORMAL, null, "anp"),

	/** Apache/Apache languages */
	APACHE("Apache;Apache languages", LanguageType.GROUP, null, "apa"),

	/** Arabic */
	ARABIC("Arabic", LanguageType.NORMAL, "ar", "ara"),

	/** Aragonese */
	ARAGONESE("Aragonese", LanguageType.NORMAL, "an", "arg"),

	/** Arapaho */
	ARAPAHO("Arapaho", LanguageType.NORMAL, null, "arp"),

	/** Arawak */
	ARAWAK("Arawak", LanguageType.NORMAL, null, "arw"),

	/** Armenian */
	ARMENIAN("Armenian", LanguageType.NORMAL, "hy", "arm", "hye"),

	/** Aromanian/Arumanian/Macedo-Romanian */
	AROMANIAN("Aromanian;Arumanian;Macedo-Romanian", LanguageType.NORMAL, null, "rup"),

	/** Artificial languages */
	ARTIFICIAL("Artificial languages", LanguageType.GROUP, null, "art"),

	/** Assamese */
	ASSAMESE("Assamese", LanguageType.NORMAL, "as", "asm"),

	/** Asturian/Asturleonese/Bable/Leonese */
	ASTURIAN("Asturian;Asturleonese;Bable;Leonese", LanguageType.NORMAL, null, "ast"),

	/** Athapascan/Athapascan languages */
	ATHAPASCAN("Athapascan;Athapascan languages", LanguageType.GROUP, null, "ath"),

	/** Australian/Australian languages */
	AUSTRALIAN("Australian;Australian languages", LanguageType.GROUP, null, "aus"),

	/** Austronesian/Austronesian languages */
	AUSTRONESIAN("Austronesian;Austronesian languages", LanguageType.GROUP, null, "map"),

	/** Avaric */
	AVARIC("Avaric", LanguageType.NORMAL, "av", "ava"),

	/** Avestan */
	AVESTAN("Avestan", LanguageType.NORMAL, "ae", "ave"),

	/** Awadhi */
	AWADHI("Awadhi", LanguageType.NORMAL, null, "awa"),

	/** Aymara */
	AYMARA("Aymara", LanguageType.NORMAL, "ay", "aym"),

	/** Azerbaijani */
	AZERBAIJANI("Azerbaijani", LanguageType.NORMAL, "az", "aze"),

	/** Balinese */
	BALINESE("Balinese", LanguageType.NORMAL, null, "ban"),

	/** Baltic/Baltic languages */
	BALTIC("Baltic;Baltic languages", LanguageType.GROUP, null, "bat"),

	/** Baluchi */
	BALUCHI("Baluchi", LanguageType.NORMAL, null, "bal"),

	/** Bambara */
	BAMBARA("Bambara", LanguageType.NORMAL, "bm", "bam"),

	/** Bamileke/Bamileke languages */
	BAMILEKE("Bamileke;Bamileke languages", LanguageType.GROUP, null, "bai"),

	/** Banda/Banda languages */
	BANDA("Banda;Banda languages", LanguageType.GROUP, null, "bad"),

	/** Bantu/Bantu languages */
	BANTU("Bantu;Bantu languages", LanguageType.GROUP, null, "bnt"),

	/** Basa (Cameroon) */
	BASA("Basa (Cameroon)", LanguageType.NORMAL, null, "bas"),

	/** Bashkir */
	BASHKIR("Bashkir", LanguageType.NORMAL, "ba", "bak"),

	/** Basque */
	BASQUE("Basque", LanguageType.NORMAL, "eu", "baq", "eus"),

	/** Batak/Batak languages */
	BATAK("Batak;Batak languages", LanguageType.GROUP, null, "btk"),

	/** Bedawiyet/Beja */
	BEJA("Bedawiyet;Beja", LanguageType.NORMAL, null, "bej"),

	/** Belarusian */
	BELARUSIAN("Belarusian", LanguageType.NORMAL, "be", "bel"),

	/** Bemba (Zambia) */
	BEMBA("Bemba (Zambia)", LanguageType.NORMAL, null, "bem"),

	/** Bengali */
	BENGALI("Bengali", LanguageType.NORMAL, "bn", "ben"),

	/** Berber/Berber languages */
	BERBER("Berber;Berber languages", LanguageType.GROUP, null, "ber"),

	/** Bhojpuri */
	BHOJPURI("Bhojpuri", LanguageType.NORMAL, null, "bho"),

	/** Bihari/Bihari languages */
	BIHARI("Bihari;Bihari languages", LanguageType.GROUP, "bh", "bih"),

	/** Bikol */
	BIKOL("Bikol", LanguageType.NORMAL, null, "bik"),

	/** Bilen/Bilin/Blin */
	BILEN("Bilen;Bilin;Blin", LanguageType.NORMAL, null, "byn"),

	/** Bini/Edo */
	EDO("Bini;Edo", LanguageType.NORMAL, null, "bin"),

	/** Bislama */
	BISLAMA("Bislama", LanguageType.NORMAL, "bi", "bis"),

	/** Bliss/Blissymbolics/Blissymbols */
	BLISS("Bliss;Blissymbolics;Blissymbols", LanguageType.NORMAL, null, "zbl"),

	/** Bosnian */
	BOSNIAN("Bosnian", LanguageType.NORMAL, "bs", "bos"),

	/** Braj */
	BRAJ("Braj", LanguageType.NORMAL, null, "bra"),

	/** Breton */
	BRETON("Breton", LanguageType.NORMAL, "br", "bre"),

	/** Buginese */
	BUGINESE("Buginese", LanguageType.NORMAL, null, "bug"),

	/** Bulgarian */
	BULGARIAN("Bulgarian", LanguageType.NORMAL, "bg", "bul"),

	/** Buriat */
	BURIAT("Buriat", LanguageType.NORMAL, null, "bua"),

	/** Burmese */
	BURMESE("Burmese", LanguageType.NORMAL, "my", "bur", "mya"),

	/** Caddo */
	CADDO("Caddo", LanguageType.NORMAL, null, "cad"),

	/** Castilian/Spanish */
	SPANISH("Castilian;Spanish", LanguageType.NORMAL, "es", "spa"),

	/** Catalan/Valencian */
	CATALAN("Catalan;Valencian", LanguageType.NORMAL, "ca", "cat"),

	/** Caucasian/Caucasian languages */
	CAUCASIAN("Caucasian;Caucasian languages", LanguageType.GROUP, null, "cau"),

	/** Cebuano */
	CEBUANO("Cebuano", LanguageType.NORMAL, null, "ceb"),

	/** Celtic/Celtic languages */
	CELTIC("Celtic;Celtic languages", LanguageType.GROUP, null, "cel"),

	/** Central American Indian languages */
	CENTRAL_AMERICAN_INDIAN_LANGUAGES("Central American Indian languages", LanguageType.GROUP, null, "cai"),

	/** Central Khmer/Khmer */
	KHMER("Central Khmer;Khmer", LanguageType.NORMAL, "km", "khm"),

	/** Chagatai */
	CHAGATAI("Chagatai", LanguageType.NORMAL, null, "chg"),

	/** Chamic/Chamic languages */
	CHAMIC("Chamic;Chamic languages", LanguageType.GROUP, null, "cmc"),

	/** Chamorro */
	CHAMORRO("Chamorro", LanguageType.NORMAL, "ch", "cha"),

	/** Chechen */
	CHECHEN("Chechen", LanguageType.NORMAL, "ce", "che"),

	/** Cherokee */
	CHEROKEE("Cherokee", LanguageType.NORMAL, null, "chr"),

	/** Chewa/Chichewa/Nyanja */
	CHEWA("Chewa;Chichewa;Nyanja", LanguageType.NORMAL, "ny", "nya"),

	/** Cheyenne */
	CHEYENNE("Cheyenne", LanguageType.NORMAL, null, "chy"),

	/** Chibcha */
	CHIBCHA("Chibcha", LanguageType.NORMAL, null, "chb"),

	/** Chinese */
	CHINESE("Chinese", LanguageType.NORMAL, "zh", "chi", "zho"),

	/** Chinook jargon */
	CHINOOK("Chinook jargon", LanguageType.NORMAL, null, "chn"),

	/** Chipewyan/Dene Suline */
	CHIPEWYAN("Chipewyan;Dene Suline", LanguageType.NORMAL, null, "chp"),

	/** Choctaw */
	CHOCTAW("Choctaw", LanguageType.NORMAL, null, "cho"),

	/** Chuang/Zhuang */
	ZHUANG("Chuang;Zhuang", LanguageType.NORMAL, "za", "zha"),

	/** Church Slavic/Church Slavonic/Old Bulgarian/Old Church Slavonic/Old Slavonic */
	CHURCH_SLAVIC(
		"Church Slavic;Church Slavonic;Old Bulgarian;Old Church Slavonic;Old Slavonic",
		LanguageType.HISTORICAL,
		"cu",
		"chu"
	),

	/** Chuukese */
	CHUUKESE("Chuukese", LanguageType.NORMAL, null, "chk"),

	/** Chuvash */
	CHUVASH("Chuvash", LanguageType.NORMAL, "cv", "chv"),

	/** Classical Nepal Bhasa/Classical Newari/Old Newari */
	CLASSICAL_NEWAR("Classical Nepal Bhasa;Classical Newari;Classical Newar;Old Newari", LanguageType.HISTORICAL, null, "nwc"),

	/** Classical Syriac */
	CLASSICAL_SYRIAC("Classical Syriac", LanguageType.HISTORICAL, null, "syc"),

	/** Cook Islands Maori/Rarotongan */
	COOK_ISLANDS_MAORI("Cook Islands Maori;Rarotongan", LanguageType.NORMAL, null, "rar"),

	/** Coptic */
	COPTIC("Coptic", LanguageType.NORMAL, null, "cop"),

	/** Cornish */
	CORNISH("Cornish", LanguageType.NORMAL, "kw", "cor"),

	/** Corsican */
	CORSICAN("Corsican", LanguageType.NORMAL, "co", "cos"),

	/** Cree */
	CREE("Cree", LanguageType.NORMAL, "cr", "cre"),

	/** Creek */
	CREEK("Creek", LanguageType.NORMAL, null, "mus"),

	/** Creoles and pidgins */
	CREOLES_AND_PIDGINS("Creoles and pidgins", LanguageType.GROUP, null, "crp"),

	/** Creoles and pidgins, English based */
	ENGLISH_BASED_CREOLES_AND_PIDGINS("English based Creoles and pidgins", LanguageType.GROUP, null, "cpe"),

	/** Creoles and pidgins, French-based */
	FRENCH_BASED_CREOLES_AND_PIDGINS("French-based Creoles and pidgins", LanguageType.GROUP, null, "cpf"),

	/** Creoles and pidgins, Portuguese-based */
	PORTUGUESE_BASED_CREOLES_AND_PIDGINS("Portuguese-based Creoles and pidgins", LanguageType.GROUP, null, "cpp"),

	/** Crimean Tatar/Crimean Turkish */
	CRIMEAN_TATAR("Crimean Tatar;Crimean Turkish", LanguageType.NORMAL, null, "crh"),

	/** Croatian */
	CROATIAN("Croatian", LanguageType.NORMAL, "hr", "hrv"),

	/** Cushitic/Cushitic languages */
	CUSHITIC("Cushitic;Cushitic languages", LanguageType.GROUP, null, "cus"),

	/** Czech */
	CZECH("Czech", LanguageType.NORMAL, "cs", "cze", "ces"),

	/** Dakota */
	DAKOTA("Dakota", LanguageType.NORMAL, null, "dak"),

	/** Danish */
	DANISH("Danish", LanguageType.NORMAL, "da", "dan"),

	/** Dargwa */
	DARGWA("Dargwa", LanguageType.NORMAL, null, "dar"),

	/** Delaware */
	DELAWARE("Delaware", LanguageType.NORMAL, null, "del"),

	/** Dhivehi/Divehi/Maldivian */
	MALDIVIAN("Dhivehi;Divehi;Maldivian", LanguageType.NORMAL, "dv", "div"),

	/** Dholuo/Luo (Kenya and Tanzania) */
	DHOLUO("Dholuo;Luo (Kenya and Tanzania)", LanguageType.NORMAL, null, "luo"),

	/** Dimili/Dimli (macrolanguage)/Kirdki/Kirmanjki (macrolanguage)/Zaza/Zazaki */
	ZAZA("Dimili;Dimli (macrolanguage);Kirdki;Kirmanjki (macrolanguage);Zaza;Zazaki", LanguageType.NORMAL, null, "zza"),

	/** Dinka */
	DINKA("Dinka", LanguageType.NORMAL, null, "din"),

	/** Dogri (macrolanguage) */
	DOGRI("Dogri (macrolanguage)", LanguageType.NORMAL, null, "doi"),

	/** Dogrib */
	DOGRIB("Dogrib", LanguageType.NORMAL, null, "dgr"),

	/** Dravidian/Dravidian languages */
	DRAVIDIAN("Dravidian;Dravidian languages", LanguageType.GROUP, null, "dra"),

	/** Duala */
	DUALA("Duala", LanguageType.NORMAL, null, "dua"),

	/** Dutch/Flemish */
	DUTCH("Dutch;Flemish", LanguageType.NORMAL, "nl", "dut", "nld"),

	/** Dyula */
	DYULA("Dyula", LanguageType.NORMAL, null, "dyu"),

	/** Dzongkha */
	DZONGKHA("Dzongkha", LanguageType.NORMAL, "dz", "dzo"),

	/** Eastern Frisian */
	EASTERN_FRISIAN("Eastern Frisian", LanguageType.NORMAL, null, "frs"),

	/** Efik */
	EFIK("Efik", LanguageType.NORMAL, null, "efi"),

	/** Egyptian (Ancient) */
	EGYPTIAN("Egyptian (Ancient)", LanguageType.HISTORICAL, null, "egy"),

	/** Ekajuk */
	EKAJUK("Ekajuk", LanguageType.NORMAL, null, "eka"),

	/** Elamite */
	ELAMITE("Elamite", LanguageType.NORMAL, null, "elx"),

	/** English */
	ENGLISH("English", LanguageType.NORMAL, "en", "eng"),

	/** Erzya */
	ERZYA("Erzya", LanguageType.NORMAL, null, "myv"),

	/** Esperanto */
	ESPERANTO("Esperanto", LanguageType.NORMAL, "eo", "epo"),

	/** Estonian */
	ESTONIAN("Estonian", LanguageType.NORMAL, "et", "est"),

	/** Ewe */
	EWE("Ewe", LanguageType.NORMAL, "ee", "ewe"),

	/** Ewondo */
	EWONDO("Ewondo", LanguageType.NORMAL, null, "ewo"),

	/** Fang (Equatorial Guinea) */
	FANG("Fang (Equatorial Guinea)", LanguageType.NORMAL, null, "fan"),

	/** Fanti */
	FANTI("Fanti", LanguageType.NORMAL, null, "fat"),

	/** Faroese */
	FAROESE("Faroese", LanguageType.NORMAL, "fo", "fao"),

	/** Fijian */
	FIJIAN("Fijian", LanguageType.NORMAL, "fj", "fij"),

	/** Filipino/Pilipino */
	FILIPINO("Filipino;Pilipino", LanguageType.NORMAL, null, "fil"),

	/** Finnish */
	FINNISH("Finnish", LanguageType.NORMAL, "fi", "fin"),

	/** Finno-Ugrian languages */
	FINNO_UGRIAN_LANGUAGES("Finno-Ugrian languages", LanguageType.GROUP, null, "fiu"),

	/** Fon */
	FON("Fon", LanguageType.NORMAL, null, "fon"),

	/** French */
	FRENCH("French", LanguageType.NORMAL, "fr", "fre", "fra"),

	/** Friulian */
	FRIULIAN("Friulian", LanguageType.NORMAL, null, "fur"),

	/** Fulah */
	FULAH("Fulah", LanguageType.NORMAL, "ff", "ful"),

	/** Ga */
	GA("Ga", LanguageType.NORMAL, null, "gaa"),

	/** Gaelic/Scottish Gaelic */
	GAELIC("Gaelic;Scottish Gaelic", LanguageType.NORMAL, "gd", "gla"),

	/** Galibi Carib */
	CARIB("Galibi Carib", LanguageType.NORMAL, null, "car"),

	/** Galician */
	GALICIAN("Galician", LanguageType.NORMAL, "gl", "glg"),

	/** Ganda */
	GANDA("Ganda", LanguageType.NORMAL, "lg", "lug"),

	/** Gayo */
	GAYO("Gayo", LanguageType.NORMAL, null, "gay"),

	/** Gbaya (Central African Republic) */
	GBAYA("Gbaya (Central African Republic)", LanguageType.NORMAL, null, "gba"),

	/** Geez */
	GEEZ("Geez", LanguageType.NORMAL, null, "gez"),

	/** Georgian */
	GEORGIAN("Georgian", LanguageType.NORMAL, "ka", "geo", "kat"),

	/** German */
	GERMAN("German", LanguageType.NORMAL, "de", "ger", "deu"),

	/** Germanic/Germanic languages */
	GERMANIC("Germanic;Germanic languages", LanguageType.GROUP, null, "gem"),

	/** Gikuyu/Kikuyu */
	KIKUYU("Gikuyu;Kikuyu", LanguageType.NORMAL, "ki", "kik"),

	/** Gilbertese */
	GILBERTESE("Gilbertese", LanguageType.NORMAL, null, "gil"),

	/** Gondi */
	GONDI("Gondi", LanguageType.NORMAL, null, "gon"),

	/** Gorontalo */
	GORONTALO("Gorontalo", LanguageType.NORMAL, null, "gor"),

	/** Gothic */
	GOTHIC("Gothic", LanguageType.NORMAL, null, "got"),

	/** Grebo */
	GREBO("Grebo", LanguageType.NORMAL, null, "grb"),

	/** Greek/Modern Greek (1453-) */
	GREEK("Greek;Modern Greek (1453-)", LanguageType.NORMAL, "el", "gre", "ell"),

	/** Greenlandic/Kalaallisut */
	GREENLANDIC("Greenlandic;Kalaallisut", LanguageType.NORMAL, "kl", "kal"),

	/** Guarani */
	GUARANI("Guarani", LanguageType.NORMAL, "gn", "grn"),

	/** Gujarati */
	GUJARATI("Gujarati", LanguageType.NORMAL, "gu", "guj"),

	/** Gwichʼin */
	GWICH_IN("Gwichʼin", LanguageType.NORMAL, null, "gwi"),

	/** Haida */
	HAIDA("Haida", LanguageType.NORMAL, null, "hai"),

	/** Haitian/Haitian Creole */
	HAITIAN("Haitian;Haitian Creole", LanguageType.NORMAL, "ht", "hat"),

	/** Hausa */
	HAUSA("Hausa", LanguageType.NORMAL, "ha", "hau"),

	/** Hawaiian */
	HAWAIIAN("Hawaiian", LanguageType.NORMAL, null, "haw"),

	/** Hebrew */
	HEBREW("Hebrew", LanguageType.HISTORICAL, "he", "heb"),

	/** Herero */
	HERERO("Herero", LanguageType.NORMAL, "hz", "her"),

	/** Hiligaynon */
	HILIGAYNON("Hiligaynon", LanguageType.NORMAL, null, "hil"),

	/** Himachali languages/Western Pahari languages */
	WESTERN_PAHARI("Himachali languages;Western Pahari languages", LanguageType.GROUP, null, "him"),

	/** Hindi */
	HINDI("Hindi", LanguageType.NORMAL, "hi", "hin"),

	/** Hiri Motu */
	HIRI_MOTU("Hiri Motu", LanguageType.NORMAL, "ho", "hmo"),

	/** Hittite */
	HITTITE("Hittite", LanguageType.NORMAL, null, "hit"),

	/** Hmong/Mong */
	HMONG("Hmong;Mong", LanguageType.NORMAL, null, "hmn"),

	/** Hungarian */
	HUNGARIAN("Hungarian", LanguageType.NORMAL, "hu", "hun"),

	/** Hupa */
	HUPA("Hupa", LanguageType.NORMAL, null, "hup"),

	/** Iban */
	IBAN("Iban", LanguageType.NORMAL, null, "iba"),

	/** Icelandic */
	ICELANDIC("Icelandic", LanguageType.NORMAL, "is", "ice", "isl"),

	/** Ido */
	IDO("Ido", LanguageType.NORMAL, "io", "ido"),

	/** Igbo */
	IGBO("Igbo", LanguageType.NORMAL, "ig", "ibo"),

	/** Ijo/Ijo languages */
	IJO("Ijo;Ijo languages", LanguageType.GROUP, null, "ijo"),

	/** Iloko */
	ILOKO("Iloko", LanguageType.NORMAL, null, "ilo"),

	/** Imperial Aramaic (700-300 BCE)/Official Aramaic (700-300 BCE) */
	IMPERIAL_ARAMAIC("Imperial Aramaic (700-300 BCE);Official Aramaic (700-300 BCE)", LanguageType.HISTORICAL, null, "arc"),

	/** Inari Sami */
	INARI_SAMI("Inari Sami", LanguageType.NORMAL, null, "smn"),

	/** Indic/Indic languages */
	INDIC("Indic;Indic languages", LanguageType.GROUP, null, "inc"),

	/** Indo-European languages */
	INDO_EUROPEAN_LANGUAGES("Indo-European languages", LanguageType.GROUP, null, "ine"),

	/** Indonesian */
	INDONESIAN("Indonesian", LanguageType.NORMAL, "id", "ind"),

	/** Ingush */
	INGUSH("Ingush", LanguageType.NORMAL, null, "inh"),

	/** Interlingua (International Auxiliary Language Association) */
	INTERLINGUA("Interlingua (International Auxiliary Language Association)", LanguageType.NORMAL, "ia", "ina"),

	/** Interlingue/Occidental */
	INTERLINGUE("Interlingue;Occidental", LanguageType.NORMAL, "ie", "ile"),

	/** Inuktitut */
	INUKTITUT("Inuktitut", LanguageType.NORMAL, "iu", "iku"),

	/** Inupiaq */
	INUPIAQ("Inupiaq", LanguageType.NORMAL, "ik", "ipk"),

	/** Iranian/Iranian languages */
	IRANIAN("Iranian;Iranian languages", LanguageType.GROUP, null, "ira"),

	/** Irish */
	IRISH("Irish", LanguageType.NORMAL, "ga", "gle"),

	/** Iroquoian/Iroquoian languages */
	IROQUOIAN("Iroquoian;Iroquoian languages", LanguageType.GROUP, null, "iro"),

	/** Italian */
	ITALIAN("Italian", LanguageType.NORMAL, "it", "ita"),

	/** Japanese */
	JAPANESE("Japanese", LanguageType.NORMAL, "ja", "jpn"),

	/** Javanese */
	JAVANESE("Javanese", LanguageType.NORMAL, "jv", "jav"),

	/** Jingpho/Kachin */
	JINGPHO("Jingpho;Kachin", LanguageType.NORMAL, null, "kac"),

	/** Judeo-Arabic */
	JUDEO_ARABIC("Judeo-Arabic", LanguageType.NORMAL, null, "jrb"),

	/** Judeo-Persian */
	JUDEO_PERSIAN("Judeo-Persian", LanguageType.NORMAL, null, "jpr"),

	/** Kabardian */
	KABARDIAN("Kabardian", LanguageType.NORMAL, null, "kbd"),

	/** Kabyle */
	KABYLE("Kabyle", LanguageType.NORMAL, null, "kab"),

	/** Kalmyk/Oirat */
	KALMYK_OIRAT("Kalmyk;Oirat;Kalmyk Oirat", LanguageType.NORMAL, null, "xal"),

	/** Kamba (Kenya) */
	KAMBA("Kamba (Kenya)", LanguageType.NORMAL, null, "kam"),

	/** Kannada */
	KANNADA("Kannada", LanguageType.NORMAL, "kn", "kan"),

	/** Kanuri */
	KANURI("Kanuri", LanguageType.NORMAL, "kr", "kau"),

	/** Kapampangan/Pampanga */
	KAPAMPANGAN("Kapampangan;Pampanga", LanguageType.NORMAL, null, "pam"),

	/** Karachay-Balkar */
	KARACHAY_BALKAR("Karachay-Balkar", LanguageType.NORMAL, null, "krc"),

	/** Kara-Kalpak */
	KARA_KALPAK("Kara-Kalpak", LanguageType.NORMAL, null, "kaa"),

	/** Karelian */
	KARELIAN("Karelian", LanguageType.NORMAL, null, "krl"),

	/** Karen/Karen languages */
	KAREN("Karen;Karen languages", LanguageType.GROUP, null, "kar"),

	/** Kashmiri */
	KASHMIRI("Kashmiri", LanguageType.NORMAL, "ks", "kas"),

	/** Kashubian */
	KASHUBIAN("Kashubian", LanguageType.NORMAL, null, "csb"),

	/** Kawi */
	KAWI("Kawi", LanguageType.NORMAL, null, "kaw"),

	/** Kazakh */
	KAZAKH("Kazakh", LanguageType.NORMAL, "kk", "kaz"),

	/** Khasi */
	KHASI("Khasi", LanguageType.NORMAL, null, "kha"),

	/** Khoisan/Khoisan languages */
	KHOISAN("Khoisan;Khoisan languages", LanguageType.GROUP, null, "khi"),

	/** Saka/Khotanese/Sakan */
	SAKA("Saka;Khotanese;Sakan", LanguageType.NORMAL, null, "kho"),

	/** Kimbundu */
	KIMBUNDU("Kimbundu", LanguageType.NORMAL, null, "kmb"),

	/** Kinyarwanda */
	KINYARWANDA("Kinyarwanda", LanguageType.NORMAL, "rw", "kin"),

	/** Kirghiz/Kyrgyz */
	KYRGYZ("Kirghiz;Kyrgyz", LanguageType.NORMAL, "ky", "kir"),

	/** Klingon/tlhIngan Hol */
	KLINGON("Klingon;tlhIngan Hol", LanguageType.NORMAL, null, "tlh"),

	/** Komi */
	KOMI("Komi", LanguageType.NORMAL, "kv", "kom"),

	/** Kongo */
	KONGO("Kongo", LanguageType.NORMAL, "kg", "kon"),

	/** Konkani (macrolanguage) */
	KONKANI("Konkani (macrolanguage)", LanguageType.NORMAL, null, "kok"),

	/** Korean */
	KOREAN("Korean", LanguageType.NORMAL, "ko", "kor"),

	/** Kosraean */
	KOSRAEAN("Kosraean", LanguageType.NORMAL, null, "kos"),

	/** Kpelle */
	KPELLE("Kpelle", LanguageType.NORMAL, null, "kpe"),

	/** Kru/Kru languages */
	KRU("Kru;Kru languages", LanguageType.GROUP, null, "kro"),

	/** Kuanyama/Kwanyama */
	KWANYAMA("Kuanyama;Kwanyama", LanguageType.NORMAL, "kj", "kua"),

	/** Kumyk */
	KUMYK("Kumyk", LanguageType.NORMAL, null, "kum"),

	/** Kurdish */
	KURDISH("Kurdish", LanguageType.NORMAL, "ku", "kur"),

	/** Kurukh */
	KURUKH("Kurukh", LanguageType.NORMAL, null, "kru"),

	/** Kutenai */
	KUTENAI("Kutenai", LanguageType.NORMAL, null, "kut"),

	/** Ladino */
	LADINO("Ladino", LanguageType.NORMAL, null, "lad"),

	/** Lahnda */
	LAHNDA("Lahnda", LanguageType.NORMAL, null, "lah"),

	/** Lamba */
	LAMBA("Lamba", LanguageType.NORMAL, null, "lam"),

	/** Land Dayak languages */
	LAND_DAYAK("Land Dayak languages", LanguageType.GROUP, null, "day"),

	/** Lao */
	LAO("Lao", LanguageType.NORMAL, "lo", "lao"),

	/** Latin */
	LATIN("Latin", LanguageType.NORMAL, "la", "lat"),

	/** Latvian */
	LATVIAN("Latvian", LanguageType.NORMAL, "lv", "lav"),

	/** Letzeburgesch/Luxembourgish */
	LUXEMBOURGISH("Letzeburgesch;Luxembourgish", LanguageType.NORMAL, "lb", "ltz"),

	/** Lezghian */
	LEZGHIAN("Lezghian", LanguageType.NORMAL, null, "lez"),

	/** Limburgan/Limburger/Limburgish */
	LIMBURGISH("Limburgan;Limburger;Limburgish", LanguageType.NORMAL, "li", "lim"),

	/** Lingala */
	LINGALA("Lingala", LanguageType.NORMAL, "ln", "lin"),

	/** Lithuanian */
	LITHUANIAN("Lithuanian", LanguageType.NORMAL, "lt", "lit"),

	/** Lojban */
	LOJBAN("Lojban", LanguageType.NORMAL, null, "jbo"),

	/** Low German/Low Saxon */
	LOW_GERMAN("Low German;Low Saxon", LanguageType.NORMAL, null, "nds"),

	/** Lower Sorbian */
	LOWER_SORBIAN("Lower Sorbian", LanguageType.NORMAL, null, "dsb"),

	/** Lozi */
	LOZI("Lozi", LanguageType.NORMAL, null, "loz"),

	/** Luba-Katanga */
	LUBA_KATANGA("Luba-Katanga", LanguageType.NORMAL, "lu", "lub"),

	/** Luba-Lulua */
	LUBA_LULUA("Luba-Lulua", LanguageType.NORMAL, null, "lua"),

	/** Luiseno */
	LUISENO("Luiseno", LanguageType.NORMAL, null, "lui"),

	/** Lule Sami */
	LULE_SAMI("Lule Sami", LanguageType.NORMAL, null, "smj"),

	/** Lunda */
	LUNDA("Lunda", LanguageType.NORMAL, null, "lun"),

	/** Lushai */
	LUSHAI("Lushai", LanguageType.NORMAL, null, "lus"),

	/** Macedonian */
	MACEDONIAN("Macedonian", LanguageType.NORMAL, "mk", "mac", "mkd"),

	/** Madurese */
	MADURESE("Madurese", LanguageType.NORMAL, null, "mad"),

	/** Magahi */
	MAGAHI("Magahi", LanguageType.NORMAL, null, "mag"),

	/** Maithili */
	MAITHILI("Maithili", LanguageType.NORMAL, null, "mai"),

	/** Makasar */
	MAKASAR("Makasar", LanguageType.NORMAL, null, "mak"),

	/** Malagasy */
	MALAGASY("Malagasy", LanguageType.NORMAL, "mg", "mlg"),

	/** Malay (macrolanguage) */
	MALAY("Malay (macrolanguage)", LanguageType.NORMAL, "ms", "may", "msa"),

	/** Malayalam */
	MALAYALAM("Malayalam", LanguageType.NORMAL, "ml", "mal"),

	/** Maltese */
	MALTESE("Maltese", LanguageType.NORMAL, "mt", "mlt"),

	/** Manchu */
	MANCHU("Manchu", LanguageType.NORMAL, null, "mnc"),

	/** Mandar */
	MANDAR("Mandar", LanguageType.NORMAL, null, "mdr"),

	/** Manding/Mandingo */
	MANDING("Manding;Mandingo", LanguageType.NORMAL, null, "man"),

	/** Manipuri */
	MANIPURI("Manipuri", LanguageType.NORMAL, null, "mni"),

	/** Manobo/Manobo languages */
	MANOBO("Manobo;Manobo languages", LanguageType.GROUP, null, "mno"),

	/** Manx */
	MANX("Manx", LanguageType.NORMAL, "gv", "glv"),

	/** Maori */
	MAORI("Maori", LanguageType.NORMAL, "mi", "mao", "mri"),

	/** Mapuche/Mapudungun */
	MAPUCHE("Mapuche;Mapudungun", LanguageType.NORMAL, null, "arn"),

	/** Marathi */
	MARATHI("Marathi", LanguageType.NORMAL, "mr", "mar"),

	/** Mari (Russia) */
	MARI("Mari (Russia)", LanguageType.NORMAL, null, "chm"),

	/** Marshallese */
	MARSHALLESE("Marshallese", LanguageType.NORMAL, "mh", "mah"),

	/** Marwari */
	MARWARI("Marwari", LanguageType.NORMAL, null, "mwr"),

	/** Masai */
	MASAI("Masai", LanguageType.NORMAL, null, "mas"),

	/** Mayan/Mayan languages */
	MAYAN("Mayan;Mayan languages", LanguageType.GROUP, null, "myn"),

	/** Mende (Sierra Leone) */
	MENDE("Mende (Sierra Leone)", LanguageType.NORMAL, null, "men"),

	/** Migmaw/Mikmaw/Micmac/Mi'kmaq */
	MIGMAW("Micmac;Mi'kmaq", LanguageType.NORMAL, null, "mic"),

	/** Middle Dutch (ca. 1050-1350) */
	MIDDLE_DUTCH("Middle Dutch (ca. 1050-1350)", LanguageType.HISTORICAL, null, "dum"),

	/** Middle English (1100-1500) */
	MIDDLE_ENGLISH("Middle English (1100-1500)", LanguageType.HISTORICAL, null, "enm"),

	/** Middle French (ca. 1400-1600) */
	MIDDLE_FRENCH("Middle French (ca. 1400-1600)", LanguageType.HISTORICAL, null, "frm"),

	/** Middle High German (ca. 1050-1500) */
	MIDDLE_HIGH_GERMAN("Middle High German (ca. 1050-1500)", LanguageType.HISTORICAL, null, "gmh"),

	/** Middle Irish (900-1200) */
	MIDDLE_IRISH("Middle Irish (900-1200)", LanguageType.HISTORICAL, null, "mga"),

	/** Minangkabau */
	MINANGKABAU("Minangkabau", LanguageType.NORMAL, null, "min"),

	/** Mirandese */
	MIRANDESE("Mirandese", LanguageType.NORMAL, null, "mwl"),

	/** Mohawk */
	MOHAWK("Mohawk", LanguageType.NORMAL, null, "moh"),

	/** Moksha */
	MOKSHA("Moksha", LanguageType.NORMAL, null, "mdf"),

	/** Romanian/Moldavian/Moldovan */
	MOLDOVAN("Romanian;Moldavian;Moldovan", LanguageType.NORMAL, "ro", "rum", "ron"),

	/** Mongo */
	MONGO("Mongo", LanguageType.NORMAL, null, "lol"),

	/** Mongolian */
	MONGOLIAN("Mongolian", LanguageType.NORMAL, "mn", "mon"),

	/** Mon-Khmer languages */
	MON_KHMER("Mon-Khmer languages", LanguageType.GROUP, null, "mkh"),

	/** Montenegrin */
	MONTENEGRIN("Montenegrin", LanguageType.NORMAL, null, "cnr"),

	/** Mossi */
	MOSSI("Mossi", LanguageType.NORMAL, null, "mos"),

	/** Multiple languages */
	MULTIPLE("Multiple languages", LanguageType.NON_LANGUAGE, null, "mul"),

	/** Munda/Munda languages */
	MUNDA("Munda;Munda languages", LanguageType.GROUP, null, "mun"),

	/** Nahuatl/Nahuatl languages */
	NAHUATL("Nahuatl;Nahuatl languages", LanguageType.GROUP, null, "nah"),

	/** Nauru */
	NAURU("Nauru", LanguageType.NORMAL, "na", "nau"),

	/** Navaho/Navajo */
	NAVAJO("Navaho;Navajo", LanguageType.NORMAL, "nv", "nav"),

	/** Ndonga */
	NDONGA("Ndonga", LanguageType.NORMAL, "ng", "ndo"),

	/** Neapolitan */
	NEAPOLITAN("Neapolitan", LanguageType.NORMAL, null, "nap"),

	/** Nepal Bhasa/Newari/Newar */
	NEWAR("Nepal Bhasa;Newari;Newar", LanguageType.NORMAL, null, "new"),

	/** Nepali (macrolanguage) */
	NEPALI("Nepali (macrolanguage)", LanguageType.NORMAL, "ne", "nep"),

	/** Nias */
	NIAS("Nias", LanguageType.NORMAL, null, "nia"),

	/** Niger-Kordofanian languages */
	NIGER_KORDOFANIAN("Niger-Kordofanian languages", LanguageType.GROUP, null, "nic"),

	/** Nilo-Saharan languages */
	NILO_SAHARAN("Nilo-Saharan languages", LanguageType.GROUP, null, "ssa"),

	/** Niuean */
	NIUEAN("Niuean", LanguageType.NORMAL, null, "niu"),

	/** N'Ko */
	NKO("N'Ko", LanguageType.NORMAL, null, "nqo"),

	/** No linguistic content/Not applicable */
	NA("No linguistic content;Not applicable;N/A", LanguageType.NORMAL, null, "zxx"),

	/** Nogai */
	NOGAI("Nogai", LanguageType.NORMAL, null, "nog"),

	/** North American Indian languages */
	NORTH_AMERICAN_INDIAN("North American Indian languages", LanguageType.GROUP, null, "nai"),

	/** North Ndebele */
	NORTH_NDEBELE("North Ndebele", LanguageType.NORMAL, "nd", "nde"),

	/** Northern Frisian */
	NORTHERN_FRISIAN("Northern Frisian", LanguageType.NORMAL, null, "frr"),

	/** Northern Sami */
	NORTHERN_SAMI("Northern Sami", LanguageType.NORMAL, "se", "sme"),

	/** Northern Sotho/Pedi/Sepedi */
	NORTHERN_SOTHO("Northern Sotho;Pedi;Sepedi", LanguageType.NORMAL, null, "nso"),

	/** Norwegian Bokmål */
	NORWEGIAN_BOKMAAL("Norwegian Bokmål", LanguageType.NON_LANGUAGE, "nb", "nob"),

	/** Norwegian Nynorsk */
	NORWEGIAN_NYNORSK("Norwegian Nynorsk", LanguageType.NON_LANGUAGE, "nn", "nno"),

	/** Norwegian */
	NORWEGIAN("Norwegian", LanguageType.NORMAL, "no", "nor"),

	/** Nubian/Nubian languages */
	NUBIAN("Nubian;Nubian languages", LanguageType.GROUP, null, "nub"),

	/** Nuosu/Sichuan Yi */
	NUOSU("Nuosu;Sichuan Yi", LanguageType.NORMAL, "ii", "iii"),

	/** Nyamwezi */
	NYAMWEZI("Nyamwezi", LanguageType.NORMAL, null, "nym"),

	/** Nyankole */
	NYANKOLE("Nyankole", LanguageType.NORMAL, null, "nyn"),

	/** Nyoro */
	NYORO("Nyoro", LanguageType.NORMAL, null, "nyo"),

	/** Nzima */
	NZIMA("Nzima", LanguageType.NORMAL, null, "nzi"),

	/** Occitan (post 1500) */
	OCCITAN("Occitan (post 1500)", LanguageType.NORMAL, "oc", "oci"),

	/** Ojibwa */
	OJIBWA("Ojibwa", LanguageType.NORMAL, "oj", "oji"),

	/** Old English (ca. 450-1100) */
	OLD_ENGLISH("Old English (ca. 450-1100)", LanguageType.HISTORICAL, null, "ang"),

	/** Old French (842-ca. 1400) */
	OLD_FRENCH("Old French (842-ca. 1400)", LanguageType.HISTORICAL, null, "fro"),

	/** Old High German (ca. 750-1050) */
	OLD_HIGH_GERMAN("Old High German (ca. 750-1050)", LanguageType.HISTORICAL, null, "goh"),

	/** Old Irish (to 900) */
	OLD_IRISH("Old Irish (to 900)", LanguageType.HISTORICAL, null, "sga"),

	/** Old Norse */
	OLD_NORSE("Old Norse", LanguageType.HISTORICAL, null, "non"),

	/** Old Occitan (to 1500)/Old Provençal (to 1500) */
	OLD_OCCITAN("Old Occitan (to 1500);Old Provençal (to 1500)", LanguageType.HISTORICAL, null, "pro"),

	/** Old Persian (ca. 600-400 B.C.) */
	OLD_PERSIAN("Old Persian (ca. 600-400 B.C.)", LanguageType.HISTORICAL, null, "peo"),

	/** Oriya (macrolanguage) */
	ORIYA("Oriya (macrolanguage)", LanguageType.NORMAL, "or", "ori"),

	/** Oromo */
	OROMO("Oromo", LanguageType.NORMAL, "om", "orm"),

	/** Osage */
	OSAGE("Osage", LanguageType.NORMAL, null, "osa"),

	/** Ossetian/ Ossetic */
	OSSETIAN("Ossetian; Ossetic", LanguageType.NORMAL, "os", "oss"),

	/** Otomian/Otomian languages */
	OTOMIAN("Otomian;Otomian languages", LanguageType.GROUP, null, "oto"),

	/** Ottoman Turkish (1500-1928) */
	OTTOMAN_TURKISH("Ottoman Turkish (1500-1928)", LanguageType.HISTORICAL, null, "ota"),

	/** Pahlavi */
	PAHLAVI("Pahlavi", LanguageType.NORMAL, null, "pal"),

	/** Palauan */
	PALAUAN("Palauan", LanguageType.NORMAL, null, "pau"),

	/** Pali */
	PALI("Pali", LanguageType.NORMAL, "pi", "pli"),

	/** Pangasinan */
	PANGASINAN("Pangasinan", LanguageType.NORMAL, null, "pag"),

	/** Panjabi/Punjabi */
	PUNJABI("Panjabi;Punjabi", LanguageType.NORMAL, "pa", "pan"),

	/** Papiamento */
	PAPIAMENTO("Papiamento", LanguageType.NORMAL, null, "pap"),

	/** Papuan/Papuan languages */
	PAPUAN("Papuan;Papuan languages", LanguageType.GROUP, null, "paa"),

	/** Pashto/Pushto */
	PASHTO("Pashto;Pushto", LanguageType.NORMAL, "ps", "pus"),

	/** Persian */
	PERSIAN("Persian", LanguageType.NORMAL, "fa", "per", "fas"),

	/** Philippine/Philippine languages */
	PHILIPPINE("Philippine;Philippine languages", LanguageType.GROUP, null, "phi"),

	/** Phoenician */
	PHOENICIAN("Phoenician", LanguageType.NORMAL, null, "phn"),

	/** Pohnpeian */
	POHNPEIAN("Pohnpeian", LanguageType.NORMAL, null, "pon"),

	/** Polish */
	POLISH("Polish", LanguageType.NORMAL, "pl", "pol"),

	/** Portuguese */
	PORTUGUESE("Portuguese", LanguageType.NORMAL, "pt", "por"),

	/** Prakrit/Prakrit languages */
	PRAKRIT("Prakrit;Prakrit languages", LanguageType.GROUP, null, "pra"),

	/** Quechua */
	QUECHUA("Quechua", LanguageType.NORMAL, "qu", "que"),

	/** Rajasthani */
	RAJASTHANI("Rajasthani", LanguageType.NORMAL, null, "raj"),

	/** Rapanui */
	RAPANUI("Rapanui", LanguageType.NORMAL, null, "rap"),

	/** Reserved for local use */
	RESERVED_LOCAL("Reserved for local use", LanguageType.NON_LANGUAGE, null, "qaa-qtz"),

	/** Romance/Romance languages */
	ROMANCE("Romance;Romance languages", LanguageType.GROUP, null, "roa"),

	/** Romansh */
	ROMANSH("Romansh", LanguageType.NORMAL, "rm", "roh"),

	/** Romany */
	ROMANY("Romany", LanguageType.NORMAL, null, "rom"),

	/** Rundi */
	RUNDI("Rundi", LanguageType.NORMAL, "rn", "run"),

	/** Russian */
	RUSSIAN("Russian", LanguageType.NORMAL, "ru", "rus"),

	/** Salishan/Salishan languages */
	SALISHAN("Salishan;Salishan languages", LanguageType.GROUP, null, "sal"),

	/** Samaritan Aramaic */
	SAMARITAN_ARAMAIC("Samaritan Aramaic", LanguageType.NORMAL, null, "sam"),

	/** Sami/Sami languages */
	SAMI("Sami;Sami languages", LanguageType.GROUP, null, "smi"),

	/** Samoan */
	SAMOAN("Samoan", LanguageType.NORMAL, "sm", "smo"),

	/** Sandawe */
	SANDAWE("Sandawe", LanguageType.NORMAL, null, "sad"),

	/** Sango */
	SANGO("Sango", LanguageType.NORMAL, "sg", "sag"),

	/** Sanskrit */
	SANSKRIT("Sanskrit", LanguageType.NORMAL, "sa", "san"),

	/** Santali */
	SANTALI("Santali", LanguageType.NORMAL, null, "sat"),

	/** Sardinian */
	SARDINIAN("Sardinian", LanguageType.NORMAL, "sc", "srd"),

	/** Sasak */
	SASAK("Sasak", LanguageType.NORMAL, null, "sas"),

	/** Scots */
	SCOTS("Scots", LanguageType.NORMAL, null, "sco"),

	/** Selkup */
	SELKUP("Selkup", LanguageType.NORMAL, null, "sel"),

	/** Semitic languages */
	SEMITIC("Semitic languages", LanguageType.GROUP, null, "sem"),

	/** Serbian */
	SERBIAN("Serbian", LanguageType.NORMAL, "sr", "srp"),

	/** Serer */
	SERER("Serer", LanguageType.NORMAL, null, "srr"),

	/** Shan */
	SHAN("Shan", LanguageType.NORMAL, null, "shn"),

	/** Shona */
	SHONA("Shona", LanguageType.NORMAL, "sn", "sna"),

	/** Sicilian */
	SICILIAN("Sicilian", LanguageType.NORMAL, null, "scn"),

	/** Sidamo */
	SIDAMO("Sidamo", LanguageType.NORMAL, null, "sid"),

	/** Sign/Sign Languages */
	SIGN("Sign;Sign Languages", LanguageType.GROUP, null, "sgn"),

	/** Siksika */
	SIKSIKA("Siksika", LanguageType.NORMAL, null, "bla"),

	/** Sindhi */
	SINDHI("Sindhi", LanguageType.NORMAL, "sd", "snd"),

	/** Sinhala/Sinhalese */
	SINHALESE("Sinhala;Sinhalese", LanguageType.NORMAL, "si", "sin"),

	/** Sino-Tibetan languages */
	SINO_TIBETAN("Sino-Tibetan languages", LanguageType.GROUP, null, "sit"),

	/** Siouan/Siouan languages */
	SIOUAN("Siouan;Siouan languages", LanguageType.GROUP, null, "sio"),

	/** Skolt Sami */
	SKOLT_SAMI("Skolt Sami", LanguageType.NORMAL, null, "sms"),

	/** Slave (Athapascan) */
	SLAVE("Slave (Athapascan)", LanguageType.NORMAL, null, "den"),

	/** Slavic languages */
	SLAVIC("Slavic languages", LanguageType.GROUP, null, "sla"),

	/** Slovak */
	SLOVAK("Slovak", LanguageType.NORMAL, "sk", "slo", "slk"),

	/** Slovenian */
	SLOVENIAN("Slovenian", LanguageType.NORMAL, "sl", "slv"),

	/** Sogdian */
	SOGDIAN("Sogdian", LanguageType.NORMAL, null, "sog"),

	/** Somali */
	SOMALI("Somali", LanguageType.NORMAL, "so", "som"),

	/** Songhai languages */
	SONGHAI("Songhai languages", LanguageType.GROUP, null, "son"),

	/** Soninke */
	SONINKE("Soninke", LanguageType.NORMAL, null, "snk"),

	/** Sorbian languages */
	SORBIAN("Sorbian languages", LanguageType.GROUP, null, "wen"),

	/** South American Indian languages */
	SOUTH_AMERICAN_INDIAN("South American Indian languages", LanguageType.GROUP, null, "sai"),

	/** South Ndebele */
	SOUTH_NDEBELE("South Ndebele", LanguageType.NORMAL, "nr", "nbl"),

	/** Southern Altai */
	SOUTHERN_ALTAI("Southern Altai", LanguageType.NORMAL, null, "alt"),

	/** Southern Sami */
	SOUTHERN_SAMI("Southern Sami", LanguageType.NORMAL, null, "sma"),

	/** Southern Sotho */
	SOUTHERN_SOTHO("Southern Sotho", LanguageType.NORMAL, "st", "sot"),

	/** Sranan Tongo */
	SRANAN_TONGO("Sranan Tongo", LanguageType.NORMAL, null, "srn"),

	/** Standard Moroccan Tamazight */
	TAMAZIGHT("Standard Moroccan Tamazight", LanguageType.NORMAL, null, "zgh"),

	/** Sukuma */
	SUKUMA("Sukuma", LanguageType.NORMAL, null, "suk"),

	/** Sumerian */
	SUMERIAN("Sumerian", LanguageType.NORMAL, null, "sux"),

	/** Sundanese */
	SUNDANESE("Sundanese", LanguageType.NORMAL, "su", "sun"),

	/** Susu */
	SUSU("Susu", LanguageType.NORMAL, null, "sus"),

	/** Swahili (macrolanguage) */
	SWAHILI("Swahili (macrolanguage)", LanguageType.NORMAL, "sw", "swa"),

	/** Swati */
	SWATI("Swati", LanguageType.NORMAL, "ss", "ssw"),

	/** Swedish */
	SWEDISH("Swedish", LanguageType.NORMAL, "sv", "swe"),

	/** Syriac */
	SYRIAC("Syriac", LanguageType.NORMAL, null, "syr"),

	/** Tagalog */
	TAGALOG("Tagalog", LanguageType.NORMAL, "tl", "tgl"),

	/** Tahitian */
	TAHITIAN("Tahitian", LanguageType.NORMAL, "ty", "tah"),

	/** Tai/Tai languages */
	TAI("Tai;Tai languages", LanguageType.GROUP, null, "tai"),

	/** Tajik */
	TAJIK("Tajik", LanguageType.NORMAL, "tg", "tgk"),

	/** Tamashek */
	TAMASHEK("Tamashek", LanguageType.NORMAL, null, "tmh"),

	/** Tamil */
	TAMIL("Tamil", LanguageType.NORMAL, "ta", "tam"),

	/** Tatar */
	TATAR("Tatar", LanguageType.NORMAL, "tt", "tat"),

	/** Telugu */
	TELUGU("Telugu", LanguageType.NORMAL, "te", "tel"),

	/** Tereno */
	TERENO("Tereno", LanguageType.NORMAL, null, "ter"),

	/** Tetum */
	TETUM("Tetum", LanguageType.NORMAL, null, "tet"),

	/** Thai */
	THAI("Thai", LanguageType.NORMAL, "th", "tha"),

	/** Tibetan */
	TIBETAN("Tibetan", LanguageType.NORMAL, "bo", "tib", "bod"),

	/** Tigre */
	TIGRE("Tigre", LanguageType.NORMAL, null, "tig"),

	/** Tigrinya */
	TIGRINYA("Tigrinya", LanguageType.NORMAL, "ti", "tir"),

	/** Timne */
	TIMNE("Timne", LanguageType.NORMAL, null, "tem"),

	/** Tiv */
	TIV("Tiv", LanguageType.NORMAL, null, "tiv"),

	/** Tlingit */
	TLINGIT("Tlingit", LanguageType.NORMAL, null, "tli"),

	/** Tok Pisin */
	TOK_PISIN("Tok Pisin", LanguageType.NORMAL, null, "tpi"),

	/** Tokelau */
	TOKELAU("Tokelau", LanguageType.NORMAL, null, "tkl"),

	/** Tonga (Nyasa) */
	TONGA_NYASA("Tonga (Nyasa)", LanguageType.NORMAL, null, "tog"),

	/** Tonga (Tonga Islands) */
	TONGA_TONGA_ISLANDS("Tonga (Tonga Islands)", LanguageType.NORMAL, "to", "ton"),

	/** Tsimshian */
	TSIMSHIAN("Tsimshian", LanguageType.NORMAL, null, "tsi"),

	/** Tsonga */
	TSONGA("Tsonga", LanguageType.NORMAL, "ts", "tso"),

	/** Tswana */
	TSWANA("Tswana", LanguageType.NORMAL, "tn", "tsn"),

	/** Tumbuka */
	TUMBUKA("Tumbuka", LanguageType.NORMAL, null, "tum"),

	/** Tupi languages */
	TUPI("Tupi languages", LanguageType.GROUP, null, "tup"),

	/** Turkish */
	TURKISH("Turkish", LanguageType.NORMAL, "tr", "tur"),

	/** Turkmen */
	TURKMEN("Turkmen", LanguageType.NORMAL, "tk", "tuk"),

	/** Tuvalu */
	TUVALU("Tuvalu", LanguageType.NORMAL, null, "tvl"),

	/** Tuvinian */
	TUVINIAN("Tuvinian", LanguageType.NORMAL, null, "tyv"),

	/** Twi */
	TWI("Twi", LanguageType.NORMAL, "tw", "twi"),

	/** Udmurt */
	UDMURT("Udmurt", LanguageType.NORMAL, null, "udm"),

	/** Ugaritic */
	UGARITIC("Ugaritic", LanguageType.NORMAL, null, "uga"),

	/** Uighur/Uyghur */
	UYGHUR("Uighur;Uyghur", LanguageType.NORMAL, "ug", "uig"),

	/** Ukrainian */
	UKRAINIAN("Ukrainian", LanguageType.NORMAL, "uk", "ukr"),

	/** Umbundu */
	UMBUNDU("Umbundu", LanguageType.NORMAL, null, "umb"),

	/** Uncoded languages */
	UNCODED("Uncoded languages", LanguageType.NON_LANGUAGE, null, "mis"),

	/** Undetermined */
	UND("Undetermined", LanguageType.UNDEFINED, null, "und"),

	/** Upper Sorbian */
	UPPER_SORBIAN("Upper Sorbian", LanguageType.NORMAL, null, "hsb"),

	/** Urdu */
	URDU("Urdu", LanguageType.NORMAL, "ur", "urd"),

	/** Uzbek */
	UZBEK("Uzbek", LanguageType.NORMAL, "uz", "uzb"),

	/** Vai */
	VAI("Vai", LanguageType.NORMAL, null, "vai"),

	/** Venda */
	VENDA("Venda", LanguageType.NORMAL, "ve", "ven"),

	/** Vietnamese */
	VIETNAMESE("Vietnamese", LanguageType.NORMAL, "vi", "vie"),

	/** Volapük */
	VOLAPUK("Volapük", LanguageType.NORMAL, "vo", "vol"),

	/** Votic */
	VOTIC("Votic", LanguageType.NORMAL, null, "vot"),

	/** Wakashan languages */
	WAKASHAN("Wakashan languages", LanguageType.GROUP, null, "wak"),

	/** Walloon */
	WALLOON("Walloon", LanguageType.NORMAL, "wa", "wln"),

	/** Waray (Philippines) */
	WARAY("Waray (Philippines)", LanguageType.NORMAL, null, "war"),

	/** Washo */
	WASHO("Washo", LanguageType.NORMAL, null, "was"),

	/** Welsh */
	WELSH("Welsh", LanguageType.NORMAL, "cy", "wel", "cym"),

	/** Western Frisian */
	WESTERN_FRISIAN("Western Frisian", LanguageType.NORMAL, "fy", "fry"),

	/** Wolaitta/Wolaytta */
	WOLAYTTA("Wolaitta;Wolaytta", LanguageType.NORMAL, null, "wal"),

	/** Wolof */
	WOLOF("Wolof", LanguageType.NORMAL, "wo", "wol"),

	/** Xhosa */
	XHOSA("Xhosa", LanguageType.NORMAL, "xh", "xho"),

	/** Yakut */
	YAKUT("Yakut", LanguageType.NORMAL, null, "sah"),

	/** Yao */
	YAO("Yao", LanguageType.NORMAL, null, "yao"),

	/** Yapese */
	YAPESE("Yapese", LanguageType.NORMAL, null, "yap"),

	/** Yiddish */
	YIDDISH("Yiddish", LanguageType.NORMAL, "yi", "yid"),

	/** Yoruba */
	YORUBA("Yoruba", LanguageType.NORMAL, "yo", "yor"),

	/** Yupik/Yupik languages */
	YUPIK("Yupik;Yupik languages", LanguageType.GROUP, null, "ypk"),

	/** Zande/Zande languages */
	ZANDE("Zande;Zande languages", LanguageType.GROUP, null, "znd"),

	/** Zapotec */
	ZAPOTEC("Zapotec", LanguageType.NORMAL, null, "zap"),

	/** Zenaga */
	ZENAGA("Zenaga", LanguageType.NORMAL, null, "zen"),

	/** Zulu */
	ZULU("Zulu", LanguageType.NORMAL, "zu", "zul"),

	/** Zuni */
	ZUNI("Zuni", LanguageType.NORMAL, null, "zun");

	/**
	 * A {@link Map} of common language name misspellings and their correct
	 * counterparts
	 */
	public static final Map<String, String> COMMON_MISSPELLINGS;

	/**
	 * A {@link Map} of {@code ISO 639-1} and {@code ISO 639-2} codes mapped to
	 * the corresponding {@link ISO639} instances for fast lookups.
	 */
	public static final Map<String, ISO639> LOOKUP_CODES;

	/**
	 * A {@link Map} of {@code ISO 639} language names mapped to the
	 * corresponding {@link ISO639} instances for fast lookups.
	 */
	public static final Map<String, ISO639> LOOKUP_NAMES;

	static {
		// Populate misspellings
		HashMap<String, String> misspellings = new HashMap<>();
		misspellings.put("ameircan", "american");
		misspellings.put("artifical", "artificial");
		misspellings.put("brasillian", "brazilian");
		misspellings.put("carrib", "carib");
		misspellings.put("centeral", "central");
		misspellings.put("chineese", "chinese");
		misspellings.put("curch", "church");
		misspellings.put("dravadian", "dravidian");
		misspellings.put("enlish", "english");
		misspellings.put("euorpean", "european");
		misspellings.put("farsi", "persian");
		misspellings.put("hawaian", "hawaiian");
		misspellings.put("hebrwe", "hebrew");
		misspellings.put("japaneese", "japanese");
		misspellings.put("javaneese", "javanese");
		misspellings.put("laguage", "language");
		misspellings.put("madureese", "madurese");
		misspellings.put("malteese", "maltese");
		misspellings.put("maltesian", "maltese");
		misspellings.put("miscelaneous", "miscellaneous");
		misspellings.put("miscellanious", "miscellaneous");
		misspellings.put("miscellanous", "miscellaneous");
		misspellings.put("northen", "northern");
		misspellings.put("norweigan", "norwegian");
		misspellings.put("ottaman", "ottoman");
		misspellings.put("philipine", "philippine");
		misspellings.put("phonecian", "phoenician");
		misspellings.put("portugese", "portuguese");
		misspellings.put("rusian", "russian");
		misspellings.put("sinhaleese", "sinhalese");
		misspellings.put("sourth", "south");
		misspellings.put("spainish", "spanish");
		misspellings.put("sweedish", "swedish");
		misspellings.put("ukranian", "ukrainian");
		misspellings.put("vietnameese", "vietnamese");
		COMMON_MISSPELLINGS = Collections.unmodifiableMap(misspellings);

		// Populate lookup maps
		Map<String, ISO639> codes = new HashMap<>();
		Map<String, ISO639> names = new HashMap<>();
		for (ISO639 entry : values()) {
			for (String name : entry.names) {
				if (isNotBlank(name)) {
					names.put(name.replaceAll("\\s*\\([^\\)]*\\)\\s*", "").toLowerCase(Locale.ROOT), entry);
				}
			}
			if (isNotBlank(entry.iso639Part1)) {
				codes.put(entry.iso639Part1, entry);
			}
			if (isNotBlank(entry.iso639Part2B)) {
				codes.put(entry.iso639Part2B, entry);
			}
			codes.put(entry.iso639Part2T, entry);
		}
		LOOKUP_CODES = Collections.unmodifiableMap(codes);
		LOOKUP_NAMES = Collections.unmodifiableMap(names);
	}

	@Nonnull
	private final LanguageType type;

	@Nonnull
	private final List<String> names;

	@Nullable
	private final String iso639Part1;

	@Nullable
	private final String iso639Part2B;

	@Nonnull
	private final String iso639Part2T;

	private ISO639(
		@Nonnull String names,
		@Nonnull LanguageType type,
		@Nullable String part1,
		@Nonnull String part2T
	) {
		this.type = type;
		this.names = Collections.unmodifiableList(Arrays.asList(Constants.SEMICOLON.split(names)));
		this.iso639Part1 = part1;
		this.iso639Part2B = null;
		this.iso639Part2T = part2T;
	}

	private ISO639(
		@Nonnull String names,
		@Nonnull LanguageType type,
		@Nullable String part1,
		@Nonnull String part2B,
		@Nonnull String part2T
	) {
		this.type = type;
		this.names = Collections.unmodifiableList(Arrays.asList(Constants.SEMICOLON.split(names)));
		this.iso639Part1 = part1;
		this.iso639Part2B = part2B;
		this.iso639Part2T = part2T;
	}

	/**
	 * @return The English language name.
	 */
	@Nonnull
	public String getName() {
		return getFirstName();
	}

	/**
	 * @return The first registered English language name.
	 */
	@Nonnull
	public String getFirstName() {
		return names.get(0);
	}

	/**
	 * @return The English language names.
	 */
	@Nonnull
	public List<String> getNames() {
		return names;
	}

	/**
	 * @return The 2-letter code if any.
	 */
	@Nullable
	public String get2LetterCode() {
		return getPart1();
	}

	/**
	 * @return The {@code ISO 639-1} (2-letter) code.
	 */
	@Nullable
	public String getPart1() {
		return iso639Part1;
	}

	/**
	 * @return The "main" code, normally a 3-letter code.
	 */
	@Nonnull
	public String getCode() {
		return getPart2B();
	}

	/**
	 * @return The bibliographic {@code ISO 639-2} (3-letter) code.
	 */
	@Nonnull
	public String getPart2B() {
		return iso639Part2B == null ? iso639Part2T : iso639Part2B;
	}

	/**
	 * @return The terminology {@code ISO 639-2} (3-letter) code.
	 */
	@Nonnull
	public String getPart2T() {
		return iso639Part2T;
	}

	/**
	 * Gets the shortest possible (as per {@link Locale} specification)
	 * {@code ISO 639} (2- or 3-letter) code.
	 *
	 * @return The {@code ISO 639-1} (2-letter), {@code ISO 639-2} (3-letter)
	 *         code.
	 */
	@Nonnull
	public String getShortestCode() {
		return isNotBlank(iso639Part1) ? iso639Part1 : getPart2B();
	}

	/**
	 * @return The {@link LanguageType}.
	 */
	@Nonnull
	public LanguageType getType() {
		return type;
	}

	/**
	 * Verifies if the specified {@code ISO 639} code matches any of the
	 * {@code ISO 639} codes for this instance.
	 *
	 * @param code the {@code ISO 639} (2- or 3-letter) code.
	 * @return {@code true} if a match is found, {@code false} otherwise.
	 */
	public boolean matches(@Nullable String code) {
		if (isBlank(code)) {
			return false;
		}
		return matchesInternal(normalize(code));
	}

	/**
	 * Verifies if the specified lower-case {@code ISO 639} code matches any of
	 * the {@code ISO 639} codes for this instance.
	 * <p>
	 * <b>Note:</b> {@code code} must already be in lower-case.
	 *
	 * @param code the lower-case {@code ISO 639} (2- or 3-letter) code.
	 * @return {@code true} if a match is found, {@code false} otherwise.
	 */
	private boolean matchesInternal(@Nullable String code) {
		if (code == null) {
			return false;
		}
		return code.equals(iso639Part1) || code.equals(iso639Part2B) || code.equals(iso639Part2T);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * Returns a {@link String} representation of this instance.
	 *
	 * @param debug if {@code true} the result includes all fields, if
	 *            {@code false} only the the first language name is returned.
	 * @return The {@link String} representation.
	 */
	public String toString(boolean debug) {
		if (!debug) {
			return names.get(0);
		}
		StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(" [");
		if (names.size() > 1) {
			sb.append("Names=");
			for (int i = 0; i < names.size(); i++) {
				if (i == names.size() - 1) {
					sb.append(" and ");
				} else if (i > 0) {
					sb.append(", ");
				}
				sb.append("\"").append(names.get(i)).append("\"");
			}
		} else {
			sb.append("Name=").append("\"").append(names.get(0)).append("\"");
		}
		if (isNotBlank(iso639Part1)) {
			sb.append(", 639-1=").append(iso639Part1);
		}
		sb.append(", 639-2=");
		if (iso639Part2B != null) {
			sb.append(iso639Part2B).append(" (B), ").append(iso639Part2T);
		} else {
			sb.append(iso639Part2T);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Gets the {@link ISO639} for an {@code ISO 639} code, or {@code null} if
	 * no match is found.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code to find.
	 * @return The matching {@link ISO639} or {@code null}.
	 */
	@Nullable
	public static ISO639 getCode(@Nullable String code) {
		return code == null ? null : LOOKUP_CODES.get(normalize(code));
	}

	/**
	 * Gets the {@link ISO639} for an English {@code ISO 639} language name or
	 * an {@code ISO 639} code, or {@code null} if no match is found.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @return The matching {@link ISO639} or {@code null}.
	 */
	@Nullable
	public static ISO639 get(@Nullable String code) {
		return get(code, false);
	}

	/**
	 * Gets the {@link ISO639} for an English {@code ISO 639} language name or
	 * an {@code ISO 639} code, or {@code null} if no match is found. Can
	 * optionally also search {@code code} for the English language name.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @param containsName if {@code true}, a search for the English language
	 *            name will also be performed.
	 * @return The matching {@link ISO639} or {@code null}.
	 */
	@Nullable
	public static ISO639 get(@Nullable String code, boolean containsName) {
		if (isBlank(code)) {
			return null;
		}
		code = normalize(code);
		ISO639 result = LOOKUP_CODES.get(code);
		if (result != null) {
			return result;
		}
		result = LOOKUP_NAMES.get(code);
		if (result != null) {
			return result;
		}

		String correctedCode = COMMON_MISSPELLINGS.get(code);
		if (correctedCode != null) {
			result = LOOKUP_NAMES.get(correctedCode);
			if (result != null) {
				return result;
			}
			code = correctedCode;
		}

		if (containsName && code.length() > 2) {
			// Do a search for a match for the language name in "code"
			for (Entry<String, String> misspelling : COMMON_MISSPELLINGS.entrySet()) {
				if (code.contains(misspelling.getKey())) {
					code = code.replace(misspelling.getKey(), misspelling.getValue());
				}
			}
			result = LOOKUP_NAMES.get(code);
			if (result != null) {
				return result;
			}

			for (Entry<String, ISO639> entry : LOOKUP_NAMES.entrySet()) {
				if (code.contains(entry.getKey())) {
					return entry.getValue();
				}
			}
		}

		return null;
	}

	/**
	 * Gets the first defined English {@code ISO 639} language name for an
	 * English {@code ISO 639} language name or an {@code ISO 639} code, or
	 * {@code null} if no match is found.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @return The {@code ISO 639} English language name or {@code null}.
	 */
	@Nullable
	public static String getFirstName(@Nullable String code) {
		return getFirstName(code, false);
	}

	/**
	 * Gets the first defined English {@code ISO 639} language name for an
	 * English {@code ISO 639} language name or an {@code ISO 639} code, or
	 * {@code null} if no match is found. Can optionally also search
	 * {@code code} for the English language name.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @param containsName if {@code true}, a search for the English language
	 *            name will also be performed.
	 * @return The {@code ISO 639} English language name or {@code null}.
	 */
	@Nullable
	public static String getFirstName(@Nullable String code, boolean containsName) {
		ISO639 entry = get(code, containsName);
		return entry == null ? null : entry.getFirstName();
	}

	/**
	 * Gets the {@link List} of English {@code ISO 639} language names for an
	 * English {@code ISO 639} language name or an {@code ISO 639} code, or
	 * {@code null} if no match is found.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @return The array of {@code ISO 639} English language names or
	 *         {@code null}.
	 */
	@Nullable
	public static List<String> getNames(@Nullable String code) {
		return getNames(code, false);
	}

	/**
	 * Gets the array of English {@code ISO 639} language names for an English
	 * {@code ISO 639} language name or an {@code ISO 639} code, or {@code null}
	 * if no match is found. Can optionally also search {@code code} for the
	 * English language name.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @param containsName if {@code true}, a search for the English language
	 *            name will also be performed.
	 * @return The array of {@code ISO 639} English language names or
	 *         {@code null}.
	 */
	@Nullable
	public static List<String> getNames(@Nullable String code, boolean containsName) {
		ISO639 entry = get(code, containsName);
		return entry == null ? null : entry.getNames();
	}

	/**
	 * Gets the shortest possible (as per {@link Locale} specification)
	 * {@code ISO 639} (2- or 3-letter) code for an English {@code ISO 639}
	 * language name or an {@code ISO 639} code, or {@code null} if no match can
	 * be found.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @return The {@code ISO 639-1} (2-letter), {@code ISO 639-2} (three
	 *         letter) code or {@code null}.
	 */
	@Nullable
	public static String getISOCode(@Nullable String code) {
		return getISOCode(code, false);
	}

	/**
	 * Gets the shortest possible (as per {@link Locale} specification)
	 * {@code ISO 639} (2- or 3-letter) code for an English {@code ISO 639}
	 * language name or an {@code ISO 639} code, or {@code null} if no is found.
	 * Can optionally also search {@code code} for the English language name.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @param containsName if {@code true}, a search for the English language
	 *            name will also be performed.
	 * @return The {@code ISO 639-1} (2-letter), {@code ISO 639-2} (three
	 *         letter) code or {@code null}.
	 */
	@Nullable
	public static String getISOCode(@Nullable String code, boolean containsName) {
		ISO639 entry = get(code, containsName);
		return entry == null ? null : entry.getShortestCode();
	}

	/**
	 * Gets the {@code ISO 639-2} (3-letter) code for an English {@code ISO 639}
	 * language name or an {@code ISO 639} code, or {@code null} if no match is
	 * found.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @return The {@code ISO 639-2} (3-letter) code or {@code null}.
	 */
	@Nullable
	public static String getISO639Part2Code(@Nullable String code) {
		return getISO639Part2Code(code, false);
	}

	/**
	 * Gets the {@code ISO 639-2} (3-letter) code for an English {@code ISO 639}
	 * language name or an {@code ISO 639} code, or {@code null} if no match is
	 * found. Can optionally also search {@code code} for the English language
	 * name.
	 *
	 * @param code the {@code ISO 639} 2- or 3-letter code or the English
	 *            language name to find.
	 * @param containsName if {@code true}, a search for the English language
	 *            name will also be performed.
	 * @return The {@code ISO 639-2} (3-letter) code or {@code null}.
	 */
	@Nullable
	public static String getISO639Part2Code(@Nullable String code, boolean containsName) {
		ISO639 entry = get(code, containsName);
		return entry == null ? null : entry.getPart2B();
	}

	/**
	 * Returns the code after trimming and converting it to lower-case.
	 *
	 * @param isoCode the {@code ISO 639} code.
	 * @return The code.
	 */
	@Nullable
	private static String normalize(@Nullable String isoCode) {
		if (isBlank(isoCode)) {
			return isoCode;
		}
		isoCode = isoCode.trim().toLowerCase(Locale.ROOT);
		return isoCode;
	}

	/**
	 * Verifies that a {@code ISO 639} English language name is matching an
	 * {@code ISO 639} code. Returns {@code true} if a match can be made,
	 * {@code false} otherwise.
	 *
	 * @param language the full language name.
	 * @param code the {@code ISO 639} code.
	 * @return {@code true} if they match, {@code false} otherwise.
	 */
	public static boolean isCodeMatching(@Nullable String language, @Nullable String code) {
		if (isBlank(language) || isBlank(code)) {
			return false;
		}

		ISO639 codeEntry = getCode(code);
		if (codeEntry == null) {
			return false;
		}
		ISO639 nameEntry = LOOKUP_NAMES.get(language.trim().toLowerCase(Locale.ROOT));

		return codeEntry == nameEntry;
	}

	/**
	 * Verifies that two {@code ISO 639} codes match the same language. Returns
	 * {@code true} if a match can be made, {@code false} otherwise.
	 *
	 * @param code1 The first {@code ISO 639} code.
	 * @param code2 The second {@code ISO 639} code.
	 * @return {@code true} if both match, {@code false} otherwise.
	 */
	public static boolean isCodesMatching(@Nullable String code1, @Nullable String code2) {
		ISO639 code1Entry = getCode(code1);
		if (code1Entry == null) {
			return false;
		}
		ISO639 code2Entry = getCode(code2);

		return code1Entry == code2Entry;
	}

	/**
	 * Converts an {@code IETF BCP 47} language tag to an {@link ISO639}.
	 *
	 * @param bcp47Tag the {@code IETF BCP 47} language tag to convert.
	 * @return The {@link ISO639} or {@code null}.
	 */
	@Nullable
	public static ISO639 fromBCP47(@Nullable String bcp47Tag) {
		if (isBlank(bcp47Tag)) {
			return null;
		}
		int remove = bcp47Tag.indexOf('-');
		int slash = bcp47Tag.indexOf('/');
		if (remove >= 0 && slash >= 0) {
			remove = Math.min(remove, slash);
		} else if (slash >= 0) {
			remove = slash;
		}
		if (remove >= 0) {
			bcp47Tag = bcp47Tag.substring(0, remove);
		}
		return get(bcp47Tag);
	}

	/**
	 * This {@code enum} is used to categorize {@link ISO639} instances.
	 */
	public enum LanguageType {

		/** Language group entries */
		GROUP,

		/** Historical language entries */
		HISTORICAL,

		/** Non-language entries */
		NON_LANGUAGE,

		/** Normal entries */
		NORMAL,

		/** The "undefined" language entry {@link ISO639#UND} */
		UNDEFINED;
	}
}
