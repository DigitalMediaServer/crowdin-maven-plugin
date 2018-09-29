package org.digitalmediaserver.crowdin.tool;

import static org.digitalmediaserver.crowdin.AbstractCrowdinMojo.isBlank;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * A utility class for handling NSIS files. Since Crowdin's handling of these is
 * rudimentary, some conversion is needed both when uploading and downloading
 * NSIS files.
 *
 * @author Nadahar
 */
public class NSISUtil {

	/** A {@link Pattern} for finding NSIS comments */
	public static final Pattern COMMENT_TAG = Pattern.compile("^[^'`\"]*(?:#|;)");

	/** A {@link Pattern} for splitting a single quoted string */
	public static final Pattern SINGLE_QUOTED_STRING = Pattern.compile("^([^']*)'(.*)'([^']*)$");

	/** A {@link Pattern} for splitting a backtick quoted string */
	public static final Pattern BACKTICK_QUOTED_STRING = Pattern.compile("^([^`]*)`(.*)`([^`]*)$");

	/** A {@link Pattern} for splitting a double quoted string */
	public static final Pattern DOUBLE_QUOTED_STRING = Pattern.compile("^([^\"]*)\"(.*)\"([^\"]*)$");

	/** A {@link Pattern} for finding backslashes not preceded by a dollar sign */
	public static final Pattern ESCAPES = Pattern.compile("(?<!\\$)\\\\");

	/** A {@link Pattern} for finding dollar-backslash escape sequences */
	public static final Pattern DOLLAR_ESCAPES = Pattern.compile("\\$\\\\");

	/**
	 * A {@link Pattern} for finding dollar signs that's not part of a
	 * dollar-backslash escape sequence
	 */
	public static final Pattern DOLLAR = Pattern.compile("\\$(?!\\\\)");

	/**
	 * A {@link Pattern} for finding double dollar signs that's not part of a
	 * dollar-backslash escape sequence
	 */
	public static final Pattern DOUBLE_DOLLAR = Pattern.compile("\\$\\$(?!\\\\)");

	/** The buffer size */
	protected static final int BUFFER_SIZE = 4096;

	/**
	 * Not to be instantiated.
	 */
	private NSISUtil() {
	}

	/**
	 * Converts the specified line from NSIS format to the slightly modified
	 * format used to store NSIS files on Crowdin.
	 *
	 * @param inputLine the line to convert.
	 * @return The converted line or {@code null} if {@code input} is
	 *         {@code null}.
	 */
	@Nullable
	public static String convertLineFromNSIS(@Nullable String inputLine) {
		if (inputLine == null) {
			return null;
		}
		return convertLine(inputLine, true);
	}

	/**
	 * Converts the specified line from the slightly modified format used to
	 * store NSIS files on Crowdin to "proper" NSIS format.
	 *
	 * @param inputLine the line to convert.
	 * @return The converted line.
	 */
	@Nonnull
	public static String convertLineToNSIS(@Nonnull String inputLine) {
		return convertLine(inputLine, false);
	}

	/**
	 * Converts the specified line between "proper" NSIS format and the slightly
	 * modified format used to store NSIS files on Crowdin.
	 *
	 * @param inputLine the line to convert.
	 * @param from if {@code true} the input is expected to be in NSIS format,
	 *            if {@code false} the output will be in NSIS format.
	 * @return The converted line.
	 */
	@Nonnull
	protected static String convertLine(@Nonnull String inputLine, boolean from) {
		if (isBlank(inputLine)) {
			return inputLine;
		}

		Matcher matcher = COMMENT_TAG.matcher(inputLine);
		String content, comment;
		if (matcher.find()) {
			content = inputLine.substring(0, matcher.start());
			comment = inputLine.substring(matcher.start());
		} else {
			content = inputLine;
			comment = null;
		}
		if (isBlank(content)) {
			return inputLine;
		}

		int firstSingleQuote = inputLine.indexOf('\'');
		int firstBackTick = inputLine.indexOf('`');
		int firstQuote = inputLine.indexOf('"');
		if (firstSingleQuote > -1 && firstBackTick > -1) {
			firstSingleQuote = Math.min(firstSingleQuote, firstBackTick);
		} else {
			firstSingleQuote = Math.max(firstSingleQuote, firstBackTick);
		}
		if (firstSingleQuote > -1 && firstQuote > -1) {
			firstQuote = Math.min(firstSingleQuote, firstQuote);
		} else {
			firstQuote = Math.max(firstSingleQuote, firstQuote);
		}
		if (firstQuote < 0) {
			return inputLine;
		}

		char quote = inputLine.charAt(firstQuote);
		Pattern pattern = quote == '"' ? DOUBLE_QUOTED_STRING : quote == '`' ? BACKTICK_QUOTED_STRING : SINGLE_QUOTED_STRING;
		matcher = pattern.matcher(content);
		if (!matcher.find()) {
			return inputLine;
		}

		String text = matcher.group(2);
		if (isBlank(text)) {
			return inputLine;
		}

		if (from) {
			// Replace $\ with \
			text = DOLLAR_ESCAPES.matcher(text).replaceAll("\\\\");

			// Replace $$ with $
			text = DOUBLE_DOLLAR.matcher(text).replaceAll("\\$");
		} else {
			// Replace $ with $$
			text = DOLLAR.matcher(text).replaceAll("\\$\\$");

			// Replace \ with $\
			text = ESCAPES.matcher(text).replaceAll("\\$\\\\");

			// Escape quotes
			text = text.replaceAll("(?<!\\$\\\\)" + quote, "\\$\\\\" + quote);
		}

		// Rebuild string
		StringBuilder sb = new StringBuilder(inputLine.length() + 10);
		sb.append(matcher.group(1)).append(quote).append(text).append(quote).append(matcher.group(3));
		if (comment != null) {
			sb.append(comment);
		}

		return sb.toString();
	}

	/**
	 * Converts a {@link String} to a {@code UTF-8} byte array while handling
	 * {@code null}.
	 *
	 * @param string the {@link String} to convert.
	 * @return The {@code UTF-8} byte array or {@code null}.
	 */
	@Nullable
	protected static byte[] getUTF8ByteArray(@Nullable String string) {
		if (string == null) {
			return null;
		}
		return string.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * An {@link InputStream} implementation that automatically converts the
	 * content from NSIS format to the slightly modified format used to store
	 * NSIS files on Crowdin.
	 *
	 * @author Nadahar
	 */
	public static class NSISInputStream extends InputStream {

		@Nonnull
		private InputStream inputStream;
		private byte[] line;
		private int linePos;
		private final byte[] buffer = new byte[BUFFER_SIZE];
		private int pos;
		private int lim = -1;
		private boolean eof;
		private String lastLineSeparator;
		private boolean remainingLineFeed;

		/**
		 * Creates a new instance.
		 *
		 * @param file the NSIS source file.
		 * @throws IOException If an error occurs during the operation.
		 */
		public NSISInputStream(@Nonnull Path file) throws IOException {
			if (file == null) {
				throw new IllegalArgumentException("file cannot be null");
			}
			this.inputStream = Files.newInputStream(file);
		}

		@Override
		public int read() throws IOException {
			if (remainingLineFeed) {
				// Return the second byte of CRLF
				remainingLineFeed = false;
				return 10;
			}
			if (line == null) {
				if (eof) {
					return -1;
				}
				line = getUTF8ByteArray(convertLineFromNSIS(getNextLine()));
				linePos = 0;
				if (line == null) {
					return -1;
				}
			}
			if (linePos == line.length) {
				if (lastLineSeparator == null) {
					return -1;
				}
				byte[] separatorBytes = lastLineSeparator.getBytes(StandardCharsets.UTF_8);
				if (separatorBytes.length > 1) {
					remainingLineFeed = true;
				}
				line = getUTF8ByteArray(convertLineFromNSIS(getNextLine()));
				linePos = 0;
				return separatorBytes[0];
			}
			return line[linePos++] & 0xFF;
		}

		/**
		 * Returns the next line from the source, recycling the buffer as
		 * needed.
		 *
		 * @return The next line.
		 * @throws IOException If an error occurs during the operation.
		 */
		@Nullable
		protected String getNextLine() throws IOException {
			if (bufferRemaining() == 0 && eof) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			int nextLineSeparator = getNextLineSeparatorIndex();
			while (nextLineSeparator < 0 || nextLineSeparator == lim) {
				if (bufferRemaining() > 0) {
					if (nextLineSeparator == lim) {
						sb.append(new String(buffer, pos, nextLineSeparator - pos, StandardCharsets.UTF_8));
						pos = nextLineSeparator;
					} else {
						int lastBoundary = findLastBoundary();
						sb.append(new String(buffer, pos, lastBoundary - pos + 1, StandardCharsets.UTF_8));
						pos = lastBoundary + 1;
					}
				}
				fillBuffer();
				if (bufferRemaining() == 0 && eof) {
					lastLineSeparator = null;
					return sb.toString();
				}
				nextLineSeparator = getNextLineSeparatorIndex();
			}
			if (bufferRemaining() > 0) {
				sb.append(new String(buffer, pos, nextLineSeparator - pos, StandardCharsets.UTF_8));
				pos = nextLineSeparator;
			}
			if (buffer[pos] == 10) {
				pos++;
				lastLineSeparator = "\n";
			} else if (buffer[pos] == 13) {
				if (pos == lim) {
					pos++;
					fillBuffer();
					if (bufferRemaining() == 0 || buffer[pos] != 10) {
						lastLineSeparator = "\r";
						pos++;
					} else {
						lastLineSeparator = "\r\n";
						pos += 2;
					}
				} else if (buffer[pos + 1] == 10) {
					lastLineSeparator = "\r\n";
					pos += 2;
				} else {
					lastLineSeparator = "\r";
					pos++;
				}
			} else {
				throw new AssertionError("Logical flaw in getNextLine()");
			}
			return sb.toString();
		}

		/**
		 * @return The index of the last byte of the last {@code UTF-8} code
		 *         point before or at {@code lim}.
		 */
		protected int findLastBoundary() {
			if (lim < 1) {
				return lim;
			}
			int lastBoundary = lim;
			for (; lastBoundary >= 0; lastBoundary--) {
				if (buffer[lastBoundary] >= 0) {
					// ASCII
					return lastBoundary;
				}
				if ((buffer[lastBoundary] & 0x40) > 0) {
					// UTF-8 start byte
					int remainingBytes = 0;
					if ((buffer[lastBoundary] & 0xE0) == 0xC0) {
						remainingBytes = 1;
					} else if ((buffer[lastBoundary] & 0xF0) == 0xE0) {
						remainingBytes = 2;
					} else if ((buffer[lastBoundary] & 0xF8) == 0xF0) {
						remainingBytes = 3;
					}
					if (remainingBytes > 0 && lastBoundary + remainingBytes <= lim) {
						// Valid, complete UTF-8 sequence
						return lastBoundary + remainingBytes;
					}
				}
			}
			return lastBoundary;
		}

		/**
		 * Refills the buffer from the source, discarding anything that is
		 * before {@code pos}.
		 *
		 * @throws IOException If an error occurs during the operation.
		 */
		protected void fillBuffer() throws IOException {
			if (pos > 0 && pos <= lim) {
				System.arraycopy(buffer, pos, buffer, 0, lim + 1 - pos);
				lim -= pos;
			} else {
				lim = -1;
			}
			pos = 0;
			while (lim < buffer.length - 1) {
				int count = inputStream.read(buffer, lim + 1, buffer.length - lim - 1);
				if (count == -1) {
					eof = true;
					return;
				}
				lim += count;
			}
		}

		/**
		 * @return The index of the first byte of the next (relative to
		 *         {@code pos}) line separator in the buffer, or {@code -1} if
		 *         the buffer doesn't contain any further line separators.
		 */
		protected int getNextLineSeparatorIndex() {
			int value;
			for (int i = pos; i <= lim; i++) {
				value = buffer[i];
				if (value == 10 || value == 13) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * @return The number unread bytes remaining in the current buffer.
		 */
		protected int bufferRemaining() {
			return lim - pos + 1;
		}

		@Override
		public void close() throws IOException {
			inputStream.close();
		}
	}
}
