/*
 * Copyright 2012 Corentin Azelart.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.azelart.artnetstack.constants;

/**
 * Styles contants.
 * @author Corentin Azelart
 */
public final class StyleConstants {

	/**
	 * Private constructor.
	 */
	private StyleConstants() {
		super();
	}

	/** A DMX to / from Art-Net device. */
	public static final  int ST_NODE = 0;

	/** A lighting console. */
	public static final int ST_CONTROLLER = 1;

	/** A Media Server. */
	public static final int ST_MEDIA = 2;

	/** A network routing device. */
	public static final int ST_ROUTE = 3;

	/** A backup device. */
	public static final int ST_BACKUP = 4;

	/** A configuration or diagnostic tool. */
	public static final int ST_CONFIG = 5;

	/** A visualiser. */
	public static final int ST_VISUAL = 6;
}
