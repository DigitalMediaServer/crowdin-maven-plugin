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
package org.digitalmediaserver.crowdin.configuration;


/**
 * An {@code enum} representing the {@code updateOption} parameter in the
 * {@code update-file} API method. //TODO: (Nad) Fix JAvaDoc
 *
 * @author Nadahar
 */
public enum UpdateOption {

	/** Delete translations of changed strings */
	delete_translations,

	/**
	 * Preserve translations of changed strings but remove validations of those
	 * translations if they exist
	 */
	update_as_unapproved,

	/** Preserve translations and validations of changed strings */
	update_without_changes;
}
