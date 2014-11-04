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
 * OpCodes file.
 * @author Corentin Azelart.
 */
public final class OpCodeConstants {

	/**
	 * Private constructor.
	 */
	private OpCodeConstants() {
		super();
	}

	/**
	 * This is an ArtPoll packet,
	 * no other data is contained in this UDP packet.
	 */
	public static final int OPPOLL = 2000;

	/**
	 * This is an ArtPollReply packet,.
	 */
	public static final int OPPOLLREPLY = 2100;

	/**
	 * This is an ArtTimeCode packet.
	 * It is used to transport time code over the network.
	 */
	public static final int OPTIMECODE = 9700;

	/**
	 * This is an ArtDMX packet.
	 * It is used to transport DMX over the network.
	 */
	public static final int OPOUTPUT = 5000;

	/**
	 * This is an ArtAddress packet.
	 * It contains remote programming information for a Node.
	 */
	public static final int ARTADDRESS = 6000;
}
