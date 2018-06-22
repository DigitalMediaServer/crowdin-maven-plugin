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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * A {@link org.apache.maven.plugin.Mojo} configuration class describing a
 * placeholder conversion consisting of a "from" and a "to" value.
 *
 * @author Nadahar
 */
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class Conversion {

	/**
	 * The placeholder value to convert from.
	 *
	 * @parameter
	 * @required
	 */
	protected String from;

	/**
	 * The placeholder value to convert to.
	 *
	 * @parameter
	 * @required
	 */
	protected String to;

	/**
	 * @return The placeholder value to convert from.
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @return The placeholder value to convert to.
	 */
	public String getTo() {
		return to;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": \"" + from + "\" -> \"" + to + "\"";
	}
}
