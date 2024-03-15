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
package org.digitalmediaserver.crowdin;

import static org.digitalmediaserver.crowdin.tool.Constants.*;
import static org.digitalmediaserver.crowdin.tool.StringUtil.isBlank;
import static org.digitalmediaserver.crowdin.tool.StringUtil.isNotBlank;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.digitalmediaserver.crowdin.configuration.PathPlaceholder;
import org.digitalmediaserver.crowdin.api.CrowdinAPI;
import org.digitalmediaserver.crowdin.api.FileType;
import org.digitalmediaserver.crowdin.configuration.Conversion;
import org.digitalmediaserver.crowdin.configuration.StatusFile;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.digitalmediaserver.crowdin.tool.FIFOProperties;
import org.digitalmediaserver.crowdin.tool.FileUtil;
import org.digitalmediaserver.crowdin.tool.GroupSortedProperties;
import org.digitalmediaserver.crowdin.tool.ISO639;
import org.digitalmediaserver.crowdin.tool.NSISUtil;
import org.digitalmediaserver.crowdin.tool.OrderedProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Transforms the downloaded files as needed and copies/writes them to their
 * configured location.
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.NONE)
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class DeployCrowdinMojo extends AbstractCrowdinMojo {

	@Override
	public void execute() throws MojoExecutionException {
		initializeParameters();
		TranslationFileSet.initialize(translationFileSets);
		StatusFile.initialize(statusFiles);
		doExecute();
	}

	/**
	 * Performs the task of this {@link Mojo}.
	 *
	 * @throws MojoExecutionException If an error occurs during the operation.
	 */
	public void doExecute() throws MojoExecutionException {
		if (translationFileSets == null || translationFileSets.isEmpty()) {
			throw new MojoExecutionException("No filesets are defined");
		}

		if (Files.isDirectory(downloadFolderPath)) {
			final Set<MatchInfo> fileSetMatches = buildFileSetMatches();
			final Path statusFile = downloadFolderPath.resolve(STATUS_DOWNLOAD_FILENAME);

			try {
				Files.walkFileTree(downloadFolderPath, new FileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						Path folderName = dir.getFileName();
						String folderNameStr = folderName == null ? null : folderName.toString();
						if (folderNameStr != null && folderNameStr.startsWith(".")) {
							getLog().debug("Skipping folder \"" + dir + "\"");
							return FileVisitResult.SKIP_SUBTREE;
						}
						getLog().debug("Checking folder \"" + dir + "\"");
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if (statusFile != null && statusFile.equals(file)) {
							deployStatusFiles(file);
							return FileVisitResult.CONTINUE;
						}

						ParseResult parseResult;
						try {
							parseResult = parseFileName(file, fileSetMatches);
						} catch (IOException e) {
							getLog().error("Unable to process file \"" + file.toAbsolutePath() + "\": " + e.getMessage());
							return FileVisitResult.CONTINUE;
						} catch (MojoExecutionException e) {
							throw new IOException(
								"An error occurred while processing file \"" + file.toAbsolutePath() + "\"",
								e
							);
						}
						if (parseResult == null) {
							getLog().warn("Couldn't parse \"" + file + "\" - skipping file");
							return FileVisitResult.CONTINUE;
						}
						TranslationFileSet fileSet = parseResult.getMatchInfo().getFileSet();

						// Include & exclude
						List<String> includes = fileSet.getIncludes();
						if (includes != null && includes.isEmpty()) {
							includes = null;
						}
						List<String> excludes = fileSet.getExcludes();
						if (excludes != null && excludes.isEmpty()) {
							excludes = null;
						}
						if (includes != null || excludes != null) {
							int downloadFolderLength = downloadFolderPath.getNameCount();
							if (file.getNameCount() - downloadFolderLength < 1) {
								throw new AssertionError(
									"Internal error in DeployCrowdinMojo.doExecute(), nameCount=" +
									file.getNameCount() + ", downloadFolderLength=" + downloadFolderLength
								);
							}
							String relativeFile = FileUtil.formatPath(
								file.subpath(downloadFolderLength, file.getNameCount()),
								false
							);
							Path fileNamePath = file.getFileName();
							String fileName = fileNamePath == null ? null : fileNamePath.toString();
							if (includes != null) {
								boolean found = false;
								for (String include : includes) {
									Pattern pattern = createFilterPattern(include);
									if (
										pattern != null && (
											pattern.matcher(relativeFile.toString()).matches() ||
											pattern.matcher(fileName).matches()
									)) {
										found = true;
										break;
									}
								}
								if (!found) {
									getLog().debug(
										"Skipping file \"" + file + "\"because it's not included in fileset \"" + fileSet + "\""
									);
									return FileVisitResult.CONTINUE;
								}
							}
							if (excludes != null && !excludes.isEmpty()) {
								for (String exclude : excludes) {
									Pattern pattern = createFilterPattern(exclude);
									if (
										pattern != null && (
											pattern.matcher(relativeFile.toString()).matches() ||
											pattern.matcher(fileName).matches()
									)) {
										getLog().debug(
											"Skipping file \"" + file + "\"because it is excluded in fileset \"" + fileSet + "\""
										);
										return FileVisitResult.CONTINUE;
									}
								}
							}
						}

						Path targetFile = fileSet.getLanguageFilesFolder().toPath().resolve(parseResult.getTargetFile());
						Path targetFolder = targetFile.getParent();
						if (targetFolder != null && !Files.exists(targetFolder)) {
							getLog().info("Creating folder \"" + targetFolder + "\"");
							Files.createDirectories(targetFolder);
						}
						getLog().info("Deploying file \"" + targetFile.toAbsolutePath() + "\" from \"" + file + "\"");

						String commentHeader;
						if (Boolean.TRUE.equals(fileSet.getAddComment())) {
							if (isBlank(fileSet.getComment())) {
								commentHeader = isBlank(comment) ? DEFAULT_COMMENT : comment;
							} else {
								commentHeader = fileSet.getComment();
							}
						} else {
							commentHeader = null;
						}
						String currentLineSeparator = fileSet.getLineSeparator() != null ? fileSet.getLineSeparator() : lineSeparator;
						if (currentLineSeparator != null) {
							currentLineSeparator = currentLineSeparator.replace("\\r", "\r").replace("\\n", "\n");
						}

						if (fileSet.getType() == FileType.properties) {
							OrderedProperties orderedProperties = Boolean.TRUE.equals(fileSet.getSortLines()) ?
								new GroupSortedProperties() :
								new FIFOProperties();
							try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
								orderedProperties.load(reader);
							}
							try (BufferedWriter writer = Files.newBufferedWriter(targetFile, fileSet.getCharset())) {
								orderedProperties.store(
									writer,
									commentHeader,
									currentLineSeparator,
									!Boolean.FALSE.equals(fileSet.getEscapeUnicode())
								);
							}
						} else {
							if (Boolean.TRUE.equals(fileSet.getSortLines())) {
								throw new IOException("Invalid option", new MojoExecutionException(
									"Option \"sortLines\" isn't supported for " + fileSet.getType() + " files"
								));
							}
							if (Boolean.TRUE.equals(fileSet.getEscapeUnicode())) {
								throw new IOException("Invalid option", new MojoExecutionException(
									"Option \"escapeUnicode\" isn't supported for " + fileSet.getType() + " files"
								));
							}

							if (
								!fileSet.getCharset().equals(StandardCharsets.UTF_8) ||
								Boolean.TRUE.equals(fileSet.getAddComment()) ||
								currentLineSeparator != null ||
								fileSet.getType() == FileType.nsh ||
								Boolean.TRUE.equals(fileSet.getWriteBOM())
							) {
								// "Manual" copy
								try (
									BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
									BufferedWriter writer = Files.newBufferedWriter(targetFile, fileSet.getCharset());
								) {
									if (Boolean.TRUE.equals(fileSet.getWriteBOM())) {
										writer.write("\ufeff");
									}
									if (Boolean.TRUE.equals(fileSet.getAddComment())) {
										if (fileSet.getType() == FileType.html || fileSet.getType() == FileType.xml) {
											writer.write("<!-- ");
											writer.write(commentHeader);
											writer.write(" -->");
										} else {
											writer.write(fileSet.getCommentTag());
											writer.write(' ');
											writer.write(commentHeader);
										}
										OrderedProperties.writeNewLine(writer, currentLineSeparator);
										OrderedProperties.writeNewLine(writer, currentLineSeparator);
									}

									for (String line = reader.readLine(); line != null; line = reader.readLine()) {
										if (fileSet.getType() == FileType.nsh) {
											line = NSISUtil.convertLineToNSIS(line);
										}
										writer.write(line);
										OrderedProperties.writeNewLine(writer, currentLineSeparator);
									}
									writer.flush();
								}
							} else {
								// Filesystem copy
								copyFile(file, targetFile, true);
							}
						}
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
						getLog().error("Could not process file \"" + file.toAbsolutePath() + "\": " + exc.getMessage());
						throw exc;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						getLog().debug("Finished checking folder " + dir.toAbsolutePath());
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				if (e.getCause() instanceof MojoExecutionException) {
					throw (MojoExecutionException) e.getCause();
				}
				throw new MojoExecutionException("An I/O error occurred while deploying translations: " + e.getMessage(), e);
			}
		} else {
			if (!Files.exists(downloadFolderPath)) {
				throw new MojoExecutionException("Crowdin download folder (" + downloadFolderPath + ") doesn't exist. Call fetch first.");
			}
			throw new MojoExecutionException("Crowdin download folder (" + downloadFolderPath + ") isn't a folder.");
		}
	}

	@Nullable
	private ParseResult parseFileName(@Nullable Path path, Set<MatchInfo> fileSetMatchInfos) throws MojoExecutionException, IOException {
		if (path == null || !Files.isRegularFile(path)) {
			throw new IOException(path == null ?
				"File path is null" :
				"File path isn't a regular file: \"" + path.toAbsolutePath() + "\""
			);
		}
		if (!path.startsWith(downloadFolderPath)) {
			throw new MojoExecutionException(
				"Internal error: downloadFolderPath \"" + downloadFolderPath.toAbsolutePath() +
				"\" isn't part of the file path \"" + path.toAbsolutePath() + "\""
			);
		}

		int downloadFolderLength = downloadFolderPath.getNameCount();
		String crowdinCode = path.getName(downloadFolderLength).toString();
		if (path.getNameCount() - downloadFolderLength < 2) {
			// File is not inside a Crowdin-code subfolder, so it's not a translation file
			return null;
		}
		String fileName = FileUtil.formatPath(path.subpath(downloadFolderLength + 1, path.getNameCount()), false);
		Matcher matcher = null;
		MatchInfo matchedfileSetMatchInfo = null;

		for (MatchInfo fileSetMatchInfo : fileSetMatchInfos) {
			Matcher m = fileSetMatchInfo.getPattern().matcher(fileName);
			if (m.matches()) {
				matchedfileSetMatchInfo = fileSetMatchInfo;
				matcher = m;
				break;
			}
		}
		if (matcher == null || matchedfileSetMatchInfo == null) {
			throw new IOException("Unable to match file \"" + path.toAbsolutePath() + "\" to any translation file set");
		}

		StringBuilder targetFileName = new StringBuilder();
		List<Conversion> conversions = matchedfileSetMatchInfo.getFileSet().getConversions();
		if (isBlank(matchedfileSetMatchInfo.getFileSet().getTargetFileName())) {
			// Convert placeholders
			int groupCount = matcher.groupCount();
			if (groupCount > 0) {
				int literalStart, literalEnd;
				for (int group = 1; group <= groupCount; group++) {
					literalStart = group == 1 ? 0 : matcher.end(group - 1);
					literalEnd = matcher.start(group);
					if (literalEnd - literalStart > 0) {
						targetFileName.append(fileName.substring(literalStart, literalEnd));
					}
					targetFileName.append(convertPlaceholder(matcher.group(group), conversions));
				}
				targetFileName.append(fileName.substring(matcher.end(groupCount)));
			} else {
				targetFileName.append(fileName);
			}
			String crowdinPath = FileUtil.formatPath(matchedfileSetMatchInfo.getFileSet().getCrowdinPath(), true);
			if (crowdinPath != null && targetFileName.toString().startsWith(crowdinPath)) {
				targetFileName = new StringBuilder(targetFileName.substring(crowdinPath.length()));
			}
		} else {
			String remaining = matchedfileSetMatchInfo.getFileSet().getTargetFileName();
			while (remaining.length() > 0) {
				Matcher m = PLACEHOLDER_PATTERN.matcher(remaining);
				if (m.find()) {
					if (m.start() > 0) {
						targetFileName.append(remaining.substring(0, m.start()));
					}
					String replacement = null;
					PathPlaceholder placeholder = PathPlaceholder.typeOf(m.group());
					if (placeholder != null) {
						for (int i = 0; i < matchedfileSetMatchInfo.getPlaceHolders().size(); i++) {
							if (placeholder == matchedfileSetMatchInfo.getPlaceHolders().get(i)) {
								replacement = convertPlaceholder(matcher.group(i + 1), conversions);
								break;
							}
						}
					}
					if (replacement == null) {
						String group = placeholder == null ? m.group().toLowerCase(Locale.ROOT) : null;
						if (
							placeholder == PathPlaceholder.LANGUAGE ||
							placeholder == PathPlaceholder.TWO_LETTER ||
							placeholder == PathPlaceholder.THREE_LETTER ||
							"%shortest_iso639_code%".equals(group)
						) {
							ISO639 language = getLanguageFromCrowdinCode(crowdinCode);
							if (language == null) {
								throw new IOException(
									"Unable to resolve ISO639 instance for Crowdin code \"" + crowdinCode + "\""
								);
							}
							if (placeholder == PathPlaceholder.LANGUAGE) {
								replacement = convertPlaceholder(language.getName(), conversions);
							} else if (placeholder == PathPlaceholder.TWO_LETTER) {
								replacement = convertPlaceholder(language.get2LetterCode(), conversions);
							} else if (placeholder == PathPlaceholder.THREE_LETTER) {
								replacement = convertPlaceholder(language.getPart2T(), conversions);
							} else {
								// %shortest_iso639_code%
								replacement = convertPlaceholder(language.getShortestCode(), conversions);
							}
						} else if (placeholder != null) {
							throw new MojoExecutionException(
								"targetFileName refers to placeholder \"" + placeholder.getIdentifier() +
								"\" not found in the export pattern \"" +
								matchedfileSetMatchInfo.getFileSet().getExportPattern() + "\""
							);
						} else if ("%crowdin_code%".equals(group)) {
							replacement = convertPlaceholder(crowdinCode, conversions);
						} else if ("%crowdin_code_with_underscore%".equals(group)) {
							replacement = convertPlaceholder(crowdinCode, conversions).replace('-', '_');
							replacement = convertPlaceholder(replacement, conversions);
						} else {
							throw new MojoExecutionException("Unknown placeholder \"" + m.group() + "\"");
						}
					}
					targetFileName.append(replacement);
					remaining = remaining.substring(m.end());
				} else {
					targetFileName.append(remaining);
					remaining = "";
				}
			}

		}
		if (isBlank(targetFileName)) {
			throw new IOException("Resolved target filename for file \"" + path.toAbsolutePath() + "\" is blank");
		}
		return new ParseResult(Paths.get(targetFileName.toString()), matchedfileSetMatchInfo);
	}

	private void deployStatusFiles(@Nonnull Path file) throws IOException {
		// Translations status
		if (statusFiles != null && !statusFiles.isEmpty()) {
			Gson gson = CrowdinAPI.getGsonInstance();
			JsonElement document;
			try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				document = gson.fromJson(reader, JsonElement.class);
			} catch (JsonParseException e) {
				throw new IOException("Could not parse JSON file \"" + file + "\"", e);
			}
			for (StatusFile fileSet : statusFiles) {

				getLog().info("Deploying status file \"" + fileSet.getTargetFile() + "\" from \"" + file  + "\"");

				String commentHeader = null;
				String currentLineSeparator = fileSet.getLineSeparator() != null ? fileSet.getLineSeparator() : lineSeparator;
				if (currentLineSeparator != null) {
					currentLineSeparator = currentLineSeparator.replace("\\r", "\r").replace("\\n", "\n");
				}

				if (fileSet.getType() == FileType.properties) {
					// Properties status file
					if (Boolean.TRUE.equals(fileSet.getAddComment())) {
						if (isBlank(fileSet.getComment())) {
							commentHeader = isBlank(comment) ? DEFAULT_COMMENT : comment;
						} else {
							commentHeader = fileSet.getComment();
						}
					}

					OrderedProperties statusProperties = Boolean.TRUE.equals(fileSet.getSortLines()) ?
						new GroupSortedProperties() :
						new FIFOProperties();
					JsonElement element;
					JsonObject childObject, object;
					String languageTag;
					try {
						for (JsonElement child : document.getAsJsonArray()) {
							childObject = child.getAsJsonObject();
							element = childObject.get("languageId");
							if (element != null) {
								languageTag = convertPlaceholder(element.getAsString(), fileSet.getConversions());
								element = childObject.get("language");
								if (element != null) {
									statusProperties.put(languageTag + ".name", element.getAsJsonObject().get("name").getAsString());
								}
								element = childObject.get("phrases");
								if (element != null) {
									object = element.getAsJsonObject();
									statusProperties.put(languageTag + ".phrases", object.get("total").getAsString());
									statusProperties.put(languageTag + ".phrases.translated", object.get("translated").getAsString());
									statusProperties.put(languageTag + ".phrases.approved", object.get("approved").getAsString());
								}
								element = childObject.get("words");
								if (element != null) {
									object = element.getAsJsonObject();
									statusProperties.put(languageTag + ".words", object.get("total").getAsString());
									statusProperties.put(languageTag + ".words.translated", object.get("translated").getAsString());
									statusProperties.put(languageTag + ".words.approved", object.get("approved").getAsString());
								}
								element = childObject.get("translationProgress");
								if (element != null) {
									statusProperties.put(languageTag + ".progress.translated", element.getAsString());
								}
								element = childObject.get("approvalProgress");
								if (element != null) {
									statusProperties.put(languageTag + ".progress.approved", element.getAsString());
								}
							}
						}
					} catch (IllegalStateException | UnsupportedOperationException e) {
						throw new IOException("Unable to parse status file \"" + file + "\": " + e.getMessage(), e);
					}
					try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileSet.getTargetFile()), fileSet.getCharset())) {
						statusProperties.store(
							writer,
							commentHeader,
							currentLineSeparator,
							!Boolean.FALSE.equals(fileSet.getEscapeUnicode())
						);
					}
				} else if (fileSet.getType() == FileType.json) {
					// JSON status file
					if (Boolean.TRUE.equals(fileSet.getSortLines())) {
						throw new IOException("Invalid option", new MojoExecutionException(
							"Option \"sortLines\" isn't supported for " + fileSet.getType() + " files"
						));
					}
					if (Boolean.TRUE.equals(fileSet.getEscapeUnicode())) {
						throw new IOException("Invalid option", new MojoExecutionException(
							"Option \"escapeUnicode\" isn't supported for " + fileSet.getType() + " files"
						));
					}

					if (Boolean.TRUE.equals(fileSet.getAddComment())) {
						throw new IOException("Invalid option", new MojoExecutionException(
							"Option \"addComment\" isn't supported for " + fileSet.getType() + " files"
						));
					}

					Charset charset = fileSet.getCharset();
					if (charset == null) {
						charset = StandardCharsets.UTF_8;
					}
					if (!charset.toString().startsWith("UTF")) {
						throw new IOException("Invalid charset", new MojoExecutionException(
							"Only Unicode character sets are supported for " + fileSet.getType() + " files"
						));
					}

					List<Conversion> conversions = fileSet.getConversions();
					if (conversions != null && !conversions.isEmpty() && document.isJsonArray()) {
						JsonElement element;
						JsonObject childObject;
						String currentCode, convertedCode;
						for (JsonElement child : document.getAsJsonArray()) {
							if (child.isJsonObject()) {
								childObject = child.getAsJsonObject();
								element = childObject.get("languageId");
								if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
									convertedCode = convertPlaceholder(
										currentCode = element.getAsJsonPrimitive().getAsString(),
										conversions
									);
									if (!convertedCode.equals(currentCode)) {
										childObject.add("languageId", new JsonPrimitive(convertedCode));
									}
								}
							}
						}
					}

					String formattedDocument = new GsonBuilder()
						.setPrettyPrinting()
						.disableHtmlEscaping()
						.create()
						.toJson(document);

					// Gson always outputs \n (next version is expected to change that), so replace them if necessary
					if (currentLineSeparator == null) {
						currentLineSeparator = System.lineSeparator();
					}
					if (!"\n".equals(currentLineSeparator)) {
						formattedDocument = formattedDocument.replace("\n", currentLineSeparator);
					}

					try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileSet.getTargetFile()), charset)) {
						writer.write(formattedDocument);
					}
				} else {
					throw new IOException("Invalid file type \"" + fileSet.getType() + "\" for status file \"" + file + "\"");
				}
			}
		}
	}

	@Nullable
	private static Pattern createFilterPattern(@Nullable String filter) {
		if (isBlank(filter)) {
			return null;
		}
		Matcher matcher = FILTER_DELIMITERS.matcher(filter);

		StringBuilder sb = new StringBuilder(filter.length() * 2);
		int next = 0;
		String literal;
		while (matcher.find()) {
			literal = filter.substring(next, matcher.start());
			if (!literal.isEmpty()) {
				sb.append(Pattern.quote(literal));
			}
			switch (matcher.group()) {
				case "?":
					sb.append(".");
					break;
				case "*":
					sb.append(".*");
					break;
				case "\\":
					sb.append("/");
					break;
				default:
					throw new AssertionError("Broken code in createFilterPattern");
			}
			next = matcher.end();
		}

		literal = filter.substring(next);
		if (!literal.isEmpty()) {
			sb.append(Pattern.quote(literal));
		}

		return Pattern.compile(sb.toString());
	}

	@Nonnull
	private Set<MatchInfo> buildFileSetMatches() throws MojoExecutionException {
		HashSet<MatchInfo> fileSetMatches = new HashSet<>();
		for (TranslationFileSet fileSet : translationFileSets) {
			if (isBlank(fileSet.getExportPattern())) {
				getLog().warn(
					"Can't deploy translation file set \"" + fileSet.getTitle() +
					"\" because \"exportPattern\" is missing"
				);
				continue;
			}
			StringBuilder sb = new StringBuilder();
			String pushFolder = FileUtil.getPushFolder(fileSet, true);
			if (isNotBlank(pushFolder)) {
				sb.append(Pattern.quote(pushFolder));
			}
			String remaining = fileSet.getExportPattern();
			List<PathPlaceholder> matchPlaceHolders = new ArrayList<>();
			while (remaining.length() > 0) {
				Matcher matcher = PLACEHOLDER_PATTERN.matcher(remaining);
				if (matcher.find()) {
					if (matcher.start() > 0) {
						sb.append(Pattern.quote(remaining.substring(0, matcher.start())));
					}
					PathPlaceholder placeholder = PathPlaceholder.typeOf(matcher.group());
					if (placeholder == null) {
						throw new MojoExecutionException("Unknown placeholder \"" + matcher.group() + "\"");
					}
					sb.append(placeholder.getPattern());
					matchPlaceHolders.add(placeholder);
					remaining = remaining.substring(matcher.end());
				} else {
					sb.append(Pattern.quote(remaining));
					remaining = "";
				}
			}
			fileSetMatches.add(new MatchInfo(fileSet, Pattern.compile(sb.toString()), matchPlaceHolders));
		}
		return fileSetMatches;
	}

	@Nullable
	private static ISO639 getLanguageFromCrowdinCode(String crowdinCode) {
		if (isBlank(crowdinCode)) {
			return null;
		}
		int hyphen = crowdinCode.indexOf('-');
		if (hyphen > 0) {
			crowdinCode = crowdinCode.substring(0, hyphen);
		}
		return ISO639.getCode(crowdinCode);
	}

	@Nonnull
	private static String convertPlaceholder(
		@Nonnull String placeholderContent,
		@Nullable List<Conversion> conversions
	) {
		if (placeholderContent == null) {
			throw new IllegalArgumentException("placeholder cannot be null");
		}

		if (conversions == null || conversions.isEmpty()) {
			return placeholderContent;
		}

		for (Conversion conversion : conversions) {
			if (placeholderContent.equals(conversion.getFrom())) {
				return conversion.getTo();
			}
		}
		return placeholderContent;
	}

	private static void copyFile(
		@Nonnull Path sourceFile,
		@Nonnull Path destinationFile,
		boolean overwrite
	) throws IOException {
		if (overwrite) {
			Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.copy(sourceFile, destinationFile);
		}
	}

	/**
	 * A class holding the result from parsing a file.
	 *
	 * @author Nadahar
	 */
	@Immutable
	public static class ParseResult {

		@Nonnull
		private final Path targetFile;

		@Nonnull
		private final MatchInfo matchInfo;

		/**
		 * Creates a new instance with the specified values.
		 *
		 * @param targetFile the target {@link Path}.
		 * @param matchInfo the {@link MatchInfo}.
		 */
		public ParseResult(@Nonnull Path targetFile, @Nonnull MatchInfo matchInfo) {
			this.targetFile = targetFile;
			this.matchInfo = matchInfo;
		}

		/**
		 * @return The resolved target {@link Path}.
		 */
		@Nonnull
		public Path getTargetFile() {
			return targetFile;
		}

		/**
		 * @return The matching {@link MatchInfo}.
		 */
		@Nonnull
		public MatchInfo getMatchInfo() {
			return matchInfo;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ParseResult [targetFile=").append(targetFile)
				.append(", matchInfo=").append(matchInfo).append("]");
			return builder.toString();
		}
	}

	/**
	 * A class holding information for matching a file path to a
	 * {@link TranslationFileSet}.
	 *
	 * @author Nadahar
	 */
	@Immutable
	public static class MatchInfo {

		@Nonnull
		private final TranslationFileSet fileSet;

		@Nonnull
		private final Pattern pattern;

		@Nonnull
		private final List<PathPlaceholder> placeHolders;

		/**
		 * Create a new instance with the specified values.
		 *
		 * @param fileSet the {@link TranslationFileSet}.
		 * @param pattern the matching {@link Pattern}.
		 * @param placeHolders the {@link List} of {@link PathPlaceholder}s.i
		 */
		public MatchInfo(
			@Nonnull TranslationFileSet fileSet,
			@Nonnull Pattern pattern,
			@Nonnull List<PathPlaceholder> placeHolders
		) {
			this.fileSet = fileSet;
			this.pattern = pattern;
			this.placeHolders = placeHolders;
		}

		/**
		 * @return The {@link TranslationFileSet}.
		 */
		@Nonnull
		public TranslationFileSet getFileSet() {
			return fileSet;
		}

		/**
		 * @return The match {@link Pattern}.
		 */
		@Nonnull
		public Pattern getPattern() {
			return pattern;
		}

		/**
		 * @return The ordered {@link List} of {@link PathPlaceholder}s.
		 */
		@Nonnull
		public List<PathPlaceholder> getPlaceHolders() {
			return placeHolders;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MatchInfo [fileSet=").append(fileSet)
				.append(", pattern=\"").append(pattern)
				.append("\", placeHolders=").append(placeHolders).append("]");
			return builder.toString();
		}
	}
}
