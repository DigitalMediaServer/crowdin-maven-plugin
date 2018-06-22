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
import static org.digitalmediaserver.crowdin.tool.CrowdinAPI.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.client.HttpClient;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Server;
import org.digitalmediaserver.crowdin.configuration.TranslationFileSet;
import org.jdom2.Element;


/**
 * This is a utility class for working with the "Crowdin file system". Most
 * methods operates on a cache of {@link Element}s retrieved by using the
 * {@code "info"} function in the Crowdin API.
 *
 * @author Nadahar
 */
public class CrowdinFileSystem {

	/**
	 * Not to be instantiated.
	 */
	private CrowdinFileSystem() {
	}

	/**
	 * Checks if the given {@link Element} contains the specified file.
	 *
	 * @param currentElement the {@link Element} to check.
	 * @param fileName the file name to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return {@code true} if the file exists in {@code files}, {@code false}
	 *         otherwise.
	 */
	public static boolean containsFile(
		@Nullable Element currentElement,
		@Nullable String fileName,
		@Nullable Log logger
	) {
		return containsFile(currentElement, fileName, EnumSet.of(ItemType.FILE), logger);
	}

	/**
	 * Checks if the given {@link Element} contains the specified folder.
	 *
	 * @param currentElement the {@link Element} to check.
	 * @param folderName the folder name to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return {@code true} if the folder exists in {@code files}, {@code false}
	 *         otherwise.
	 */
	public static boolean containsFolder(
		@Nullable Element currentElement,
		@Nullable String folderName,
		@Nullable Log logger
	) {
		return containsFile(currentElement, folderName, EnumSet.of(ItemType.FOLDER), logger);
	}

	/**
	 * Checks if the given {@link Element} contains the specified branch.
	 *
	 * @param currentElement the {@link Element} to check.
	 * @param branchName the file name to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return {@code true} if the branch exists in {@code files}, {@code false}
	 *         otherwise.
	 */
	public static boolean containsBranch(
		@Nullable Element currentElement,
		@Nullable String branchName,
		@Nullable Log logger
	) {
		return containsFile(currentElement, branchName, EnumSet.of(ItemType.BRANCH), logger);
	}

	/**
	 * Checks if the given {@link Element} contains the specified file, folder
	 * or branch.
	 *
	 * @param currentElement the {@link Element} to check.
	 * @param name the file name to look for.
	 * @param filter the {@link ItemType}s to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return {@code true} if the file, folder or branch exists in
	 *         {@code files}, {@code false} otherwise.
	 */
	public static boolean containsFile(
		@Nullable Element currentElement,
		@Nullable String name,
		@Nullable EnumSet<ItemType> filter,
		@Nullable Log logger
	) {
		if (logger != null) {
			logger.debug("Checking if Crowdin project contains " + name);
		}
		Element file = getFile(currentElement, name, filter, logger);
		return file != null;
	}

	/**
	 * Extracts the folder {@link Element} with the specified name.
	 *
	 * @param items the {@link List} of {@link Element}s to extract from.
	 * @param folderName the folder name.
	 * @return The matching {@link Element} or {@code null}.
	 */
	@Nullable
	public static Element getFolder(@Nullable List<Element> items, @Nullable String folderName) {
		return getFile(items, folderName, EnumSet.of(ItemType.FOLDER));
	}

	/**
	 * Extracts the specified folder {@link Element} from {@code files}.
	 *
	 * @param currentElement the {@link Element} to look in.
	 * @param folderName the folder name to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The requested {@link Element} or {@code null} if it doesn't
	 *         exist.
	 */
	@Nullable
	public static Element getFolder(
		@Nullable Element currentElement,
		@Nullable String folderName,
		@Nullable Log logger
	) {
		return getFile(currentElement, folderName, EnumSet.of(ItemType.FOLDER), logger);
	}

	/**
	 * Extracts the branch {@link Element} with the given name.
	 *
	 * @param items the {@link List} of {@link Element}s to extract from.
	 * @param branchName the branch name.
	 * @return The matching {@link Element} or {@code null}.
	 */
	@Nullable
	public static Element getBranch(@Nullable List<Element> items, @Nullable String branchName) {
		return getFile(items, branchName, EnumSet.of(ItemType.BRANCH));
	}

	/**
	 * Extracts the specified branch {@link Element} from {@code files}.
	 *
	 * @param currentElement the {@link Element} to look in.
	 * @param branchName the branch name to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The requested {@link Element} or {@code null} if it doesn't
	 *         exist.
	 */
	@Nullable
	public static Element getBranch(
		@Nullable Element currentElement,
		@Nullable String branchName,
		@Nullable Log logger
	) {
		return getFile(currentElement, branchName, EnumSet.of(ItemType.BRANCH), logger);
	}

	/**
	 * Extracts the file {@link Element} with the given name.
	 *
	 * @param items the {@link List} of {@link Element}s to extract from.
	 * @param fileName the file name.
	 * @return The matching {@link Element} or {@code null}.
	 */
	@Nullable
	public static Element getFile(@Nullable List<Element> items, @Nullable String fileName) {
		return getFile(items, fileName, EnumSet.of(ItemType.FILE));
	}

	/**
	 * Extracts the file, folder or branch {@link Element} with the specified name.
	 *
	 * @param items the {@link List} of {@link Element}s to extract from.
	 * @param name the file, branch or folder name.
	 * @param filter the {@link ItemType}s to look for.
	 * @return The matching {@link Element} or {@code null}.
	 */
	@Nullable
	public static Element getFile(
		@Nullable List<Element> items,
		@Nullable String name,
		@Nullable EnumSet<ItemType> filter
	) {
		if (items == null || items.isEmpty() || isBlank(name)) {
			return null;
		}
		for (Element item : items) {
			if (name.equals(item.getChildText("name"))) {
				if (filter != null && !filter.isEmpty()) {
					ItemType itemType = ItemType.typeOf(item);
					if (itemType == null) {
						return null;
					}
					return filter.contains(itemType) ? item : null;
				}
				return item;
			}
		}
		return null;
	}

	/**
	 * Extracts the specified file {@link Element} from {@code files}.
	 *
	 * @param currentElement the {@link Element} to look in.
	 * @param fileName the file name to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The requested {@link Element} or {@code null} if it doesn't
	 *         exist.
	 */
	@Nullable
	public static Element getFile(
		@Nullable Element currentElement,
		@Nullable String fileName,
		@Nullable Log logger
	) {
		return getFile(currentElement, fileName, EnumSet.of(ItemType.FILE), logger);
	}

	/**
	 * Extracts the specified file, folder or branch {@link Element} from
	 * {@code files}.
	 *
	 * @param currentElement the {@link Element} to look in.
	 * @param name the file name to look for.
	 * @param filter the {@link ItemType}s to look for.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The requested {@link Element} or {@code null} if it doesn't
	 *         exist.
	 */
	@Nullable
	public static Element getFile(
		@Nullable Element currentElement,
		@Nullable String name,
		@Nullable EnumSet<ItemType> filter,
		@Nullable Log logger
	) {
		if (currentElement == null || isBlank(name)) {
			return null;
		}
		if (!"files".equals(currentElement.getName())) {
			currentElement = currentElement.getChild("files");
			if (currentElement == null) {
				return null;
			}
		}
		if (logger != null) {
			logger.debug("Looking for \"" + name + "\" in Crowdin project");
		}
		List<Element> items = currentElement.getChildren("item");
		int slash = name.indexOf('/');
		if (slash == -1) {
			for (Element item : items) {
				if (name.equals(item.getChildText("name"))) {
					ItemType itemType = ItemType.typeOf(item);
					if (filter != null && !filter.isEmpty()) {
						if (itemType == null) {
							if (logger != null) {
								logger.warn(
									"Found \"" + name + "\" with an unknown type, but it isn't " +
									(filter.size() == 1 ? "the expected type: " : "among the expected types: ") + filter
								);
							}
							return null;
						}
						if (!filter.contains(itemType)) {
							if (logger != null) {
								logger.warn(
									"Found " + itemType.toString().toLowerCase(Locale.ROOT) + " \"" + name + "\" but it isn't " +
									(filter.size() == 1 ? "the expected type: " : "among the expected types: ") + filter
								);
							}
							return null;
						}
					}
					if (logger != null) {
						logger.debug("Found " + itemType.toString().toLowerCase(Locale.ROOT) + " \"" + name + "\"");
					}
					return item;
				}
			}
		} else {
			String folderName = name.substring(0, slash);
			String subPath = name.substring(slash + 1);
			Element folderElement = getFile(items, folderName, EnumSet.of(ItemType.BRANCH, ItemType.FOLDER));
			if (folderElement != null) {
				return getFile(folderElement, subPath, filter, logger);
			}
		}

		if (logger != null) {
			if (filter != null && filter.size() == 1) {
				logger.debug(
					"Couldn't find " + filter.iterator().next().toString().toLowerCase(Locale.ROOT) + " \"" + name + "\""
				);
			} else {
				logger.debug("Couldn't find \"" + name + "\"");
			}
		}
		return null;
	}

	/**
	 * Checks whether the specified {@link Element} is a Crowdin file.
	 *
	 * @param item the {@link Element} to check.
	 * @return {@code true} if {@code item} is a Crowdin file, {@code false}
	 *         otherwise.
	 */
	public static boolean isFile(@Nullable Element item) {
		return ItemType.typeOf(item) == ItemType.FILE;
	}

	/**
	 * Checks whether the specified  {@link Element} is a Crowdin folder.
	 *
	 * @param item the {@link Element} to check.
	 * @return {@code true} if {@code item} is a Crowdin folder, {@code false}
	 *         otherwise.
	 */
	public static boolean isFolder(@Nullable Element item) {
		return ItemType.typeOf(item) == ItemType.FOLDER;
	}

	/**
	 * Checks whether the specified {@link Element} is a Crowdin branch.
	 *
	 * @param item the {@link Element} to check.
	 * @return {@code true} if {@code item} is a Crowdin branch, {@code false}
	 *         otherwise.
	 */
	public static boolean isBranch(@Nullable Element item) {
		return ItemType.typeOf(item) == ItemType.BRANCH;
	}

	/**
	 * Checks whether the specified {@link Element} is the Crowdin
	 * "root folder".
	 *
	 * @param item the {@link Element} to check.
	 * @return {@code true} if {@code item} is the Crowdin "root folder",
	 *         {@code false} otherwise.
	 */
	public static boolean isRoot(@Nullable Element item) {
		if (item == null) {
			return false;
		}
		if ("files".equals(item.getName())) {
			item = item.getParentElement();
		}
		return item == null ? false : "info".equals(item.getName());
	}

	/**
	 * Gets the parent file or folder for the specified Crowdin file or folder
	 * item. Handles the fact that there's two levels between each item because
	 * all children is wrapped in a {@code <files>} {@link Element}.
	 *
	 * @param item the {@link Element} for which to get the parent;
	 * @return The parent file or folder item.
	 */
	@Nullable
	public static Element getParent(@Nullable Element item) {
		if (item == null) {
			return null;
		}
		if ("files".equals(item.getName())) {
			item = item.getParentElement();
		}
		item = item == null ? null : item.getParentElement();
		item = item == null ? null : item.getParentElement();
		return item;
	}

	/**
	 * Determines and returns the push file folder by taking
	 * {@link TranslationFileSet#crowdinPath} (optionally) and any folders in
	 * {@link TranslationFileSet#baseFileName} into account.
	 *
	 * @param fileSet the {@link TranslationFileSet} for which to find the
	 *            Crowdin push folder.
	 * @param includeCrowdinPath {@code true} to include
	 *            {@link TranslationFileSet#crowdinPath} in the returned path,
	 *            {@code false} otherwise.
	 * @return The Crowdin push folder path or an empty string if the
	 *         {@link TranslationFileSet} is placed in the Crowdin root.
	 */
	@Nonnull
	public static String getPushFolder(@Nonnull TranslationFileSet fileSet, boolean includeCrowdinPath) {
		ArrayList<String> folders = new ArrayList<String>();
		if (includeCrowdinPath && !isBlank(fileSet.getCrowdinPath())) {
			folders.addAll(Arrays.asList(fileSet.getCrowdinPath().split("/")));
		}
		if (!isBlank(fileSet.getBaseFileName())) {
			folders.addAll(Arrays.asList(fileSet.getBaseFileName().split("/")));
		}
		if (folders.size() > 0) {
			folders.remove(folders.size() - 1);
		}
		StringBuilder sb = new StringBuilder();
		for (String folder : folders) {
			if (sb.length() > 0) {
				sb.append("/");
			}
			sb.append(folder);
		}
		return sb.toString();
	}

	/**
	 * Creates folders on Crowdin as needed until the specified path exists.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param currentElement the file or branch item to resolve {@code path}
	 *            from.
	 * @param path the path to make sure exists relative to
	 *            {@code currentElement}.
	 * @param logger the {@link Log} instance to use for logging.
	 * @return The target folder.
	 * @throws IOException If an error occurs during the operation.
	 */
	@Nonnull
	public static Element createFolders(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull Element currentElement,
		String path,
		@Nullable Log logger
	) throws IOException {
		if (isBlank(path)) {
			return currentElement;
		}
		if (currentElement != null && "files".equals(currentElement.getName())) {
			currentElement = currentElement.getParentElement();
		}
		if (currentElement == null) {
			throw new IllegalArgumentException("currentElement is null or invalid");
		}
		if (!isFolder(currentElement) && !isBranch(currentElement)) {
			throw new IllegalArgumentException("currentElement must be a branch or folder");
		}

		String[] folders = path.split("\\\\|/");

		List<String> parents = new ArrayList<>();
		Element parentElement = currentElement;
		while (parentElement != null && !isRoot(parentElement)) {
			parents.add(0, parentElement.getChildText("name"));
			parentElement = getParent(parentElement);
		}

		StringBuilder currentFolder = new StringBuilder();
		for (int i = 0; i < parents.size(); i++) {
			if (i > 0) {
				currentFolder.append("/");
			}
			currentFolder.append(parents.get(i));
		}

		for (int i = 0; i < folders.length; i++) {
			String folder = folders[i];
			if (currentFolder.length() > 0) {
				currentFolder.append("/");
			}
			currentFolder.append(folder);
			if (containsFile(currentElement, folder, EnumSet.of(ItemType.BRANCH, ItemType.FOLDER), logger)) {
				currentElement = getFile(currentElement, folder, EnumSet.of(ItemType.BRANCH, ItemType.FOLDER), null);
			} else {
				createFolder(httpClient, server, currentFolder.toString(), logger);
				Element rootFilesElement = getFiles(httpClient, server, null, null, null);
				currentElement = getFile(
					rootFilesElement,
					currentFolder.toString(),
					EnumSet.of(ItemType.BRANCH, ItemType.FOLDER),
					null
				);
			}
			if (currentElement == null || isFile(currentElement)) {
				throw new IOException(
					"Internal error, couldn't find recently created branch or folder \"" +
					currentFolder.toString() + "\""
				);
			}
		}
		return currentElement;
	}

	/**
	 * Creates a new folder at Crowdin.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param folderName the name of the new folder.
	 * @param logger the {@link Log} instance to use for logging.
	 * @throws IOException If an error occurs during the operation.
	 * @throws IllegalArgumentException If {@code folderName} is blank.
	 */
	public static void createFolder(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull String folderName,
		@Nullable Log logger
	) throws IOException {
		if (isBlank(folderName)) {
			throw new IllegalArgumentException("folderName cannot be blank");
		}
		if (logger != null) {
			logger.info("Creating folder \"" + folderName + "\" on Crowdin");
		}
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", folderName);
		requestPostDocument(httpClient, server, "add-directory", parameters, null, true, logger);
	}

	/**
	 * Creates a new branch at Crowdin.
	 *
	 * @param httpClient the {@link HttpClient} to use.
	 * @param server the {@link Server} to use for Crowdin credentials.
	 * @param branchName the name of the new branch.
	 * @param logger the {@link Log} instance to use for logging.
	 * @throws IOException If an error occurs during the operation.
	 * @throws IllegalArgumentException If {@code branchrName} is blank.
	 */
	public static void createBranch(
		@Nonnull HttpClient httpClient,
		@Nonnull Server server,
		@Nonnull String branchName,
		@Nullable Log logger
	) throws IOException {
		if (isBlank(branchName)) {
			throw new IllegalArgumentException("branchName cannot be blank");
		}
		if (logger != null) {
			logger.info("Creating branch \"" + branchName + "\" on Crowdin");
		}
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", branchName);
		parameters.put("is_branch", "1");
		requestPostDocument(httpClient, server, "add-directory", parameters, null, true, logger);
	}

	/**
	 * Formats a {@link Path} by converting backslashes to slashes and
	 * optionally appends a slash to the end of the {@link Path}.
	 *
	 * @param path the {@link Path} to format.
	 * @param appendSeparator if {@code true} a slash will be appended to the
	 *            {@link Path}.
	 * @return The formatted file path.
	 */
	@Nonnull
	public static String formatPath(@Nonnull Path path, boolean appendSeparator) {
		return formatPath(path.toString(), appendSeparator);
	}

	/**
	 * Formats a file path by converting backslashes to slashes and optionally
	 * appends a slash to the end of the path if it's not already present.
	 *
	 * @param path the file path to format.
	 * @param appendSeparator if {@code true} a slash will be appended to the
	 *            path if one isn't already there.
	 * @return The formatted file path.
	 */
	@Nonnull
	public static String formatPath(@Nonnull String path, boolean appendSeparator) {
		path = path.replace('\\', '/');
		if (appendSeparator && !path.isEmpty() && !path.endsWith("/")) {
			path += "/";
		}
		return path;
	}

	/**
	 * This {@code enum} represents the item types in the "Crowdin file system".
	 */
	public enum ItemType {

		/** A special type of folder that represents a Crowdin branch */
		BRANCH,

		/** A Crowdin file */
		FILE,

		/** A Crowdin folder */
		FOLDER;

		/**
		 * Returns the {@link ItemType} of the specified Crowdin item or
		 * {@code null}.
		 *
		 * @param item the item whose {@link ItemType} to find.
		 * @return The {@link ItemType} or {@code null}.
		 */
		@Nullable
		public static ItemType typeOf(@Nullable Element item) {
			if (item != null && "files".equals(item.getName())) {
				item = item.getParentElement();
			}
			if (item == null) {
				return null;
			}
			String nodeType = item.getChildText("node_type");
			if (nodeType == null) {
				return null;
			}
			switch (nodeType) {
				case "branch":
					return BRANCH;
				case "directory":
					return FOLDER;
				case "file":
					return FILE;
				default:
					return null;
			}
		}
	}
}
