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
package fr.azelart.artnetstack.domain.arttimecode;

import fr.azelart.artnetstack.domain.artnet.ArtNetObject;

/**
 * ArtTimeCode.
 * @author Corentin Azelart.
 *
 */
public class ArtTimeCode extends ArtNetObject {

	/** Frames time. 0 - 29 depending on mode. */
	private int frameTime;

	/** Seconds. 0 - 59. */
	private int seconds;

	/** Minutes. 0 - 59. */
	private int minutes;

	/** Hours. 0 - 23. */
	private int hours;

	/** Type of ArtTimeCode. */
	private ArtTimeCodeType artTimeCodeType;

	/**
	 * Constructor.
	 */
	public ArtTimeCode() {
		super();
	}

	/**
	 * @return the frameTime
	 */
	public final int getFrameTime() {
		return frameTime;
	}

	/**
	 * @param frameTimecode the frameTime to set
	 */
	public final void setFrameTime(final int frameTimecode) {
		this.frameTime = frameTimecode;
	}

	/**
	 * @return the seconds
	 */
	public final int getSeconds() {
		return seconds;
	}

	/**
	 * @param secondsTimecode the seconds to set
	 */
	public final void setSeconds(final int secondsTimecode) {
		this.seconds = secondsTimecode;
	}

	/**
	 * @return the minutes
	 */
	public final int getMinutes() {
		return minutes;
	}

	/**
	 * @param minutesTimecode the minutes to set
	 */
	public final void setMinutes(final int minutesTimecode) {
		this.minutes = minutesTimecode;
	}

	/**
	 * @return the hours
	 */
	public final int getHours() {
		return hours;
	}

	/**
	 * @param hoursTimecode the hours to set
	 */
	public final void setHours(final int hoursTimecode) {
		this.hours = hoursTimecode;
	}

	/**
	 * @return the artTimeCodeType
	 */
	public final ArtTimeCodeType getArtTimeCodeType() {
		return artTimeCodeType;
	}

	/**
	 * @param timeCodeType the artTimeCodeType to set
	 */
	public final void setArtTimeCodeType(final ArtTimeCodeType timeCodeType) {
		this.artTimeCodeType = timeCodeType;
	}

	/**
	 * ToString.
	 * @return A Human String of current timecode
	 */
	public final String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("f(").append(frameTime).append(") ").
		append("s(").append(seconds).append(") ").
		append("m(").append(minutes).append(") ").
		append("h(").append(hours).append(")");
		return sb.toString();
	}
}
