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
package fr.azelart.artnetstack.domain.artpollreply;

/**
 * Style enumeration.
 * @author Corentin Azelart.
 *
 */
public enum ArtPollReplyStyle {

	/** A DMX to / from Art-Net device. */
	ST_NODE,

	/** A lighting console. */
	ST_CONTROLLER,

	/** A Media Server. */
	ST_MEDIA,

	/** A network routing device. */
	ST_ROUTE,

	/** A backup device. */
	ST_BACKUP,

	/** A configuration or diagnostic tool. */
	ST_CONFIG,

	/** A visualiser. */
	ST_VISUAL
}
