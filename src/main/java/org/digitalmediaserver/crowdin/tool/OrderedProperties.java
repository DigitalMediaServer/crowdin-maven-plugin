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

import static org.digitalmediaserver.crowdin.AbstractCrowdinMojo.isBlank;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Properties;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * An abstract class for ordered key/value pairs behaving very similar to
 * {@link java.util.Properties} except that the keys have a defined order.
 *
 * @author Nadahar
 */
public abstract class OrderedProperties implements Iterable<Entry<String, String>>, Serializable {

	private static final long serialVersionUID = 1L;

	/** A table of hex digits */
	private static final char[] HEX_DIGIT = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	/**
	 * Associates the specified {@code value} with the specified {@code key}. If
	 * there previously was a mapping for the key, the old value is replaced.
	 *
	 * @param key the key with which the specified value is to be associated
	 * @param value value to be associated with the specified key.
	 * @return The previous value associated with {@code key}, or {@code null}
	 *         if there was no mapping for {@code key} or the previously
	 *         associated value was {@code null}.
	 */
	public abstract String put(String key, String value);

	/**
	 * Writes the data from this {@link OrderedProperties} to the specified
	 * {@link OutputStream}.
	 *
	 * @param out the {@link OutputStream} to write to.
	 * @param comment the comment to add to the start of the output, or
	 *            {@code null} if no comment should be added.
	 * @param lineSeparator the line separator sequence to write.
	 * @param escapeUnicode {@code true} if Unicode characters should be written
	 *            using the "&#92;uxxxx" notation, {@code false} otherwise.
	 * @throws IOException If an error occurs during the operation.
	 */
	public void store(
		@Nonnull OutputStream out,
		@Nullable String comment,
		@Nullable String lineSeparator,
		boolean escapeUnicode
	) throws IOException {
		store(
			new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.ISO_8859_1)),
			comment,
			lineSeparator,
			escapeUnicode
		);
	}

	/**
	 * Writes the data from this {@link OrderedProperties} to the specified
	 * {@link Writer}.
	 *
	 * @param writer the {@link Writer} to write to.
	 * @param comment the comment to add to the start of the output, or
	 *            {@code null} if no comment should be added.
	 * @param lineSeparator the line separator sequence to write.
	 * @param escapeUnicode {@code true} if Unicode characters should be written
	 *            using the "&#92;uxxxx" notation, {@code false} otherwise.
	 * @throws IOException If an error occurs during the operation.
	 */
	public void store(
		@Nonnull Writer writer,
		@Nullable String comment,
		@Nullable String lineSeparator,
		boolean escapeUnicode
	) throws IOException {
		store(
			writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer),
			comment,
			lineSeparator,
			escapeUnicode
		);
	}

	/**
	 * Writes the data from this {@link OrderedProperties} to the specified
	 * {@link BufferedWriter}.
	 *
	 * @param bw the {@link BufferedWriter} to write to.
	 * @param comment the comment to add to the start of the output, or
	 *            {@code null} if no comment should be added.
	 * @param lineSeparator the line separator sequence to write.
	 * @param escapeUnicode {@code true} if Unicode characters should be written
	 *            using the "&#92;uxxxx" notation, {@code false} otherwise.
	 * @throws IOException If an error occurs during the operation.
	 */
	public void store(
		@Nonnull BufferedWriter bw,
		@Nullable String comment,
		@Nullable String lineSeparator,
		boolean escapeUnicode
	) throws IOException {
		if (!isBlank(comment)) {
			writeComment(bw, comment, lineSeparator);
			writeNewLine(bw, lineSeparator);
		}
		for (Entry<String, String> entry : this) {
			String key = saveConvert(entry.getKey(), true, escapeUnicode);
			String value = saveConvert(entry.getValue(), false, escapeUnicode);
			bw.write(key + "=" + value);
			writeNewLine(bw, lineSeparator);
		}
		bw.flush();
	}

	/**
	 * Writes a newline to the specified {@link BufferedWriter} using the
	 * specified line separator sequence.
	 *
	 * @param bw the {@link BufferedWriter} to write to.
	 * @param lineSeparator the line separator sequence to write.
	 * @throws IOException If an error occurs during the operation.
	 */
	public static void writeNewLine(@Nonnull BufferedWriter bw, @Nullable String lineSeparator) throws IOException {
		if (lineSeparator == null) {
			bw.newLine();
		} else {
			bw.write(lineSeparator);
		}
	}


	/*
	 * Methods from java.util.Properties that has to be replicated because their
	 * visibility wasn't very well thought through by the original authors.
	 */


	/**
	 * Reads the data from the specified {@link InputStream} into this
	 * {@link OrderedProperties} instance.
	 *
	 * @param inputStream the {@link InputStream} to read from.
	 * @throws IOException If an error occurs during the operation.
	 */
	public void load(@Nullable InputStream inputStream) throws IOException {
		if (inputStream != null) {
			doLoad(new LineReader(inputStream));
		}
	}

	/**
	 * Reads the data from the specified {@link BufferedReader} into this
	 * {@link OrderedProperties} instance.
	 *
	 * @param reader the {@link BufferedReader} to read from.
	 * @throws IOException If an error occurs during the operation.
	 */
	public void load(@Nullable BufferedReader reader) throws IOException {
		if (reader != null) {
			doLoad(new LineReader(reader));
		}
	}

	/**
	 * Reads the data from the specified {@link LineReader} into this
	 * {@link OrderedProperties} instance.
	 *
	 * @param lineReader the {@link LineReader} to use.
	 * @throws IOException If an error occurs during the operation.
	 */
	protected void doLoad(@Nonnull LineReader lineReader) throws IOException {
		char[] convtBuf = new char[1024];
		int limit;
		int keyLen;
		int valueStart;
		char c;
		boolean hasSep;
		boolean precedingBackslash;

		while ((limit = lineReader.readLine()) >= 0) {
			c = 0;
			keyLen = 0;
			valueStart = limit;
			hasSep = false;

			precedingBackslash = false;
			while (keyLen < limit) {
				c = lineReader.lineBuf[keyLen];
				//need check if escaped.
				if ((c == '=' ||  c == ':') && !precedingBackslash) {
					valueStart = keyLen + 1;
					hasSep = true;
					break;
				} else if ((c == ' ' || c == '\t' ||  c == '\f') && !precedingBackslash) {
					valueStart = keyLen + 1;
					break;
				}
				if (c == '\\') {
					precedingBackslash = !precedingBackslash;
				} else {
					precedingBackslash = false;
				}
				keyLen++;
			}
			while (valueStart < limit) {
				c = lineReader.lineBuf[valueStart];
				if (c != ' ' && c != '\t' &&  c != '\f') {
					if (!hasSep && (c == '=' ||  c == ':')) {
						hasSep = true;
					} else {
						break;
					}
				}
				valueStart++;
			}
			String key = loadConvert(lineReader.lineBuf, 0, keyLen, convtBuf);
			String value = loadConvert(lineReader.lineBuf, valueStart, limit - valueStart, convtBuf);
			put(key, value);
		}
	}

	/**
	 * Converts encoded "&#92;uxxxx" to Unicode {@code char}s and changes
	 * special saved {@code char}s to their original form.
	 *
	 * @param in the {@code char} array to convert.
	 * @param off the offset to use.
	 * @param len the length to read.
	 * @param convertBuffer the conversion buffer to (re-)use.
	 * @return The converted {@link String}.
	 */
	protected static String loadConvert(@Nonnull char[] in, int off, int len, @Nonnull char[] convertBuffer) {
		if (convertBuffer.length < len) {
			int newLen = len * 2;
			if (newLen < 0) {
				newLen = Integer.MAX_VALUE;
			}
			convertBuffer = new char[newLen];
		}
		char aChar;
		char[] out = convertBuffer;
		int outLen = 0;
		int end = off + len;

		while (off < end) {
			aChar = in[off++];
			if (aChar == '\\') {
				aChar = in[off++];
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = in[off++];
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					out[outLen++] = (char) value;
				} else {
					if (aChar == 't') {
						aChar = '\t';
					} else if (aChar == 'r') {
						aChar = '\r';
					} else if (aChar == 'n') {
						aChar = '\n';
					} else if (aChar == 'f') {
						aChar = '\f';
					}
					out[outLen++] = aChar;
				}
			} else {
				out[outLen++] = aChar;
			}
		}
		return new String(out, 0, outLen);
	}

	/**
	 * Converts Unicode characters to encoded "&#92;uxxxx" and escapes special
	 * characters with a preceding "&#92;".
	 * <p>
	 * Changes from {@link Properties#saveConvert} are that {@code =, :, #} and
	 * {@code !} are only escaped in keys, and that a {@link StringBuilder} is
	 * used instead of a {@link StringBuffer} since there's no need for the
	 * added cost of synchronization.
	 *
	 * @param content the {@link String} to convert.
	 * @param isKey {@code true} if the {@link String} should be converted as a
	 *            key, {@code false} otherwise.
	 * @param escapeUnicode {@code true} if the {@link String} should have
	 *            Unicode characters converted to "&#92;uxxxx" notation,
	 *            {@code false} otherwise.
	 * @return The converted {@link String}.
	 */
	@Nullable
	protected static String saveConvert(@Nullable String content, boolean isKey, boolean escapeUnicode) {
		if (content == null) {
			return null;
		}
		int len = content.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuilder outBuffer = new StringBuilder(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = content.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if (aChar > 61 && aChar < 127) {
				if (aChar == '\\') {
					outBuffer.append('\\').append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				if (x == 0 || isKey) {
					outBuffer.append('\\');
				}
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\').append('t');
				break;
			case '\n':
				outBuffer.append('\\').append('n');
				break;
			case '\r':
				outBuffer.append('\\').append('r');
				break;
			case '\f':
				outBuffer.append('\\').append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				if (isKey) {
					outBuffer.append('\\');
				}
				outBuffer.append(aChar);
				break;
			default:
				if ((aChar < 0x0020 || aChar > 0x007e) & escapeUnicode) {
					outBuffer.append('\\').append('u').append(toHex((aChar >> 12) & 0xF))
						.append(toHex((aChar >> 8) & 0xF)).append(toHex((aChar >> 4) & 0xF))
						.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Writes a comment line to the specified {@link BufferedWriter}.
	 * <p>
	 * Changes from {@link Properties#writeComments} are that a space is added
	 * between the comment tag and the comment itself and that no date is added.
	 *
	 * @param bw the {@link BufferedWriter} to write to.
	 * @param comment the comment {@link String}.
	 * @param lineSeparator the line separator sequence to use.
	 * @throws IOException If an error occurs during the operation.
	 */
	protected static void writeComment(
		@Nonnull BufferedWriter bw,
		@Nullable String comment,
		@Nullable String lineSeparator
	) throws IOException {
		if (isBlank(comment)) {
			return;
		}

		bw.write("# ");
		int len = comment.length();
		int current = 0;
		int last = 0;
		char[] uu = new char[6];
		uu[0] = '\\';
		uu[1] = 'u';
		while (current < len) {
			char c = comment.charAt(current);
			if (c > '\u00ff' || c == '\n' || c == '\r') {
				if (last != current) {
					bw.write(comment.substring(last, current));
				}
				if (c > '\u00ff') {
					uu[2] = toHex((c >> 12) & 0xf);
					uu[3] = toHex((c >> 8) & 0xf);
					uu[4] = toHex((c >> 4) & 0xf);
					uu[5] = toHex(c & 0xf);
					bw.write(new String(uu));
				} else {
					writeNewLine(bw, lineSeparator);
					if (c == '\r' && current != len - 1 && comment.charAt(current + 1) == '\n') {
						current++;
					}
					if (current == len - 1 || comment.charAt(current + 1) != '#' && comment.charAt(current + 1) != '!') {
						bw.write("#");
					}
				}
				last = current + 1;
			}
			current++;
		}
		if (last != current) {
			bw.write(comment.substring(last, current));
		}
		writeNewLine(bw, lineSeparator);
	}

	/**
	 * Converts a nibble to a hexadecimal digit.
	 *
	 * @param nibble the nibble to convert.
	 * @return The hexadecimal digit.
	 * @throws ArrayIndexOutOfBoundsException If {@code nibble} isn't in the
	 *             range 0 to 16.
	 */
	public static char toHex(int nibble) {
		return HEX_DIGIT[nibble & 0xF];
	}

	/**
	 * Reads in a "logical line" from an {@link InputStream} or {@link Reader},
	 * skip all comment and blank lines and filter out those leading whitespace
	 * characters ( , and ) from the beginning of a "natural line". Method
	 * returns the char length of the "logical line" and stores the line in
	 * "lineBuf".
	 */
	protected static class LineReader {

		/**
		 * Creates a new instance using an {@link InputStream} source.
		 *
		 * @param inStream the source.
		 */
		public LineReader(@Nonnull InputStream inStream) {
			this.inStream = inStream;
			inByteBuf = new byte[8192];
		}

		/**
		 * Creates a new instance using a {@link Reader} source.
		 *
		 * @param reader the source.
		 */
		public LineReader(@Nonnull Reader reader) {
			this.reader = reader;
			inCharBuf = new char[8192];
		}

		private byte[] inByteBuf;
		private char[] inCharBuf;
		private char[] lineBuf = new char[1024];
		private int inLimit = 0;
		private int inOff = 0;
		private InputStream inStream;
		private Reader reader;

		int readLine() throws IOException {
			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean isCommentLine = false;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = inStream == null ? reader.read(inCharBuf) : inStream.read(inByteBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0 || isCommentLine) {
							return -1;
						}
						return len;
					}
				}
				if (inStream != null) {
					//The line below is equivalent to calling a ISO8859-1 decoder.
					c = (char) (0xff & inByteBuf[inOff++]);
				} else {
					c = inCharBuf[inOff++];
				}
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						isCommentLine = true;
						continue;
					}
				}

			if (c != '\n' && c != '\r') {
				lineBuf[len++] = c;
				if (len == lineBuf.length) {
					int newLength = lineBuf.length * 2;
					if (newLength < 0) {
						newLength = Integer.MAX_VALUE;
					}
					char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					//flip the preceding backslash flag
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
				} else {
					// reached EOL
					if (isCommentLine || len == 0) {
						isCommentLine = false;
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = (inStream == null) ?
							reader.read(inCharBuf) :
							inStream.read(inByteBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return len;
						}
					}
					if (precedingBackslash) {
						len -= 1;
						//skip the leading whitespace characters in following line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return len;
					}
				}
			}
		}
	}
}
