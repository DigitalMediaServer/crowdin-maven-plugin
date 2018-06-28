package org.digitalmediaserver.crowdin.tool;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class sorts {@link Properties} when saving and is based on <a
 * href="http://www.rgagnon.com/javadetails/java-0614.html"
 * >http://www.rgagnon.com/javadetails/java-0614.html</a>.
 */
public class SortedProperties extends Properties {

	/** Sorts and convert the .properties files */
	private static final long serialVersionUID = 7676620742633491575L;

	/**
	 * Overrides, called by the store method.
	 */
	@Override
	public synchronized Enumeration<Object> keys() {
		Enumeration<Object> keysEnum = super.keys();

		LineList<Object> lineList = new LineList<Object>();
		while (keysEnum.hasMoreElements()) {
			lineList.add(keysEnum.nextElement());
		}
		lineList.sort();
		return lineList.lines();
	}

	/**
	 * Writes this property list (key and element pairs) in this
	 * {@link Properties} table to the output stream in a format suitable for
	 * loading into a {@link Properties} table using the
	 * {@link #load(InputStream) load(InputStream)} method.
	 * <p>
	 * Properties from the defaults table of this {@link Properties} table (if
	 * any) are <i>not</i> written out by this method.
	 * <p>
	 * This method outputs the properties keys and values in the same format as
	 * specified in {@link #store(java.io.Writer, java.lang.String)
	 * store(Writer)}, with the following differences:
	 * <ul>
	 * <li>The stream is written using the ISO 8859-1 character encoding.
	 *
	 * <li>Characters not in Latin-1 in the comments are written as
	 * {@code &#92;u}<i>xxxx</i> for their appropriate unicode hexadecimal value
	 * <i>xxxx</i>.
	 *
	 * <li>Characters less than {@code &#92;u0020} and characters greater than
	 * {@code &#92;u007E} in property keys or values are written as
	 * {@code &#92;u}<i>xxxx</i> for the appropriate hexadecimal value
	 * <i>xxxx</i>.
	 * </ul>
	 * <p>
	 * After the entries have been written, the output stream is flushed. The
	 * output stream remains open after this method returns.
	 * <p>
	 *
	 * @param out an output stream.
	 * @exception IOException if writing this property list to the specified
	 *                output stream throws an {@link IOException}.
	 * @exception ClassCastException if this {@link Properties} object contains
	 *                any keys or values that are not {@link String}s.
	 * @exception NullPointerException if {@code out} is null.
	 */
	public void store(OutputStream out) throws IOException {
		store0(new BufferedWriter(new OutputStreamWriter(out, "8859_1")), null, true);
	}

	@Override
	public void store(OutputStream out, String comments) throws IOException {
		store0(new BufferedWriter(new OutputStreamWriter(out, "8859_1")), comments, true);
	}

	private void store0(BufferedWriter bw, String comments, boolean escUnicode) throws IOException {
		if (comments != null) {
			writeComments(bw, comments);
		}
		bw.newLine();
		synchronized (this) {
			for (Enumeration<?> e = keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String val = (String) get(key);
				key = saveConvert(key, true, escUnicode);
				/* No need to escape embedded and trailing spaces for value, hence
				 * pass false to flag.
				 */
				val = saveConvert(val, false, escUnicode);
				bw.write(key + "=" + val);
				bw.newLine();
			}
		}
		bw.flush();
	}

	/**
	 * Converts unicodes to encoded &#92;uxxxx and escapes special characters
	 * with a preceding slash.
	 */
	private static String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuffer outBuffer = new StringBuffer(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if (aChar > 61 && aChar < 127) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				if (x == 0 || escapeSpace) {
					outBuffer.append('\\');
				}
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			/*case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;*/
			default:
				if ((aChar < 0x0020 || aChar > 0x007e) & escapeUnicode) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	private static void writeComments(BufferedWriter bw, String comments) throws IOException {
		bw.write("# ");
		int len = comments.length();
		int current = 0;
		int last = 0;
		char[] uu = new char[6];
		uu[0] = '\\';
		uu[1] = 'u';
		while (current < len) {
			char c = comments.charAt(current);
			if (c > '\u00ff' || c == '\n' || c == '\r') {
				if (last != current) {
					bw.write(comments.substring(last, current));
				}
				if (c > '\u00ff') {
					uu[2] = toHex((c >> 12) & 0xf);
					uu[3] = toHex((c >> 8) & 0xf);
					uu[4] = toHex((c >> 4) & 0xf);
					uu[5] = toHex(c & 0xf);
					bw.write(new String(uu));
				} else {
					bw.newLine();
					if (c == '\r' && current != len - 1 && comments.charAt(current + 1) == '\n') {
						current++;
					}
					if (current == len - 1 || comments.charAt(current + 1) != '#' && comments.charAt(current + 1) != '!') {
						bw.write("#");
					}
				}
				last = current + 1;
			}
			current++;
		}
		if (last != current) {
			bw.write(comments.substring(last, current));
		}
		bw.newLine();
	}

	/**
	 * Convert a nibble to a hex character.
	 *
	 * @param nibble the nibble to convert.
	 */
	private static char toHex(int nibble) {
		return HEX_DIGIT[nibble & 0xF];
	}

	/** A table of hex digits */
	private static final char[] HEX_DIGIT = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
