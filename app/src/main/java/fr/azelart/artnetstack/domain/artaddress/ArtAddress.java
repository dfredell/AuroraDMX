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
package fr.azelart.artnetstack.domain.artaddress;

import fr.azelart.artnetstack.domain.artnet.ArtNetObject;


/**
 * This is an ArtAddress packet.
 * @author Corentin Azelart
 *
 */
public class ArtAddress extends ArtNetObject {

	/**
	 * Short name to program.
	 */
	private String shortName;
	
	/**
	 * Long name to program.
	 */
	private String longName;

	/**
	 * @return the shortName
	 */
	public final String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public final void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the longName
	 */
	public final String getLongName() {
		return longName;
	}

	/**
	 * @param longName the longName to set
	 */
	public final void setLongName(String longName) {
		this.longName = longName;
	}
}
