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
package fr.azelart.artnetstack.listeners;

import java.util.EventListener;

import fr.azelart.artnetstack.domain.artaddress.ArtAddress;
import fr.azelart.artnetstack.domain.artdmx.ArtDMX;
import fr.azelart.artnetstack.domain.artnet.ArtNetObject;
import fr.azelart.artnetstack.domain.artpoll.ArtPoll;
import fr.azelart.artnetstack.domain.artpollreply.ArtPollReply;
import fr.azelart.artnetstack.domain.arttimecode.ArtTimeCode;

/**
 * The list of availables methods on recept ArtNetPacket.
 * @author Corentin Azelart.
 */
public interface ArtNetPacketListener extends EventListener {

	/**
	 * We have receive an ArtNet object (this is a ArtNet packet).
	 * @param artNetObject is the artNet object
	 */
	void onArt(ArtNetObject artNetObject);

	/**
	 * We have receive an ArtPoll packet.
	 * @param artPoll is the object
	 */
	void onArtPoll(ArtPoll artPoll);

	/**
	 * We have receive an ArtPolLReply packet.
	 * @param artPollReply is the artPollReply object
	 */
	void onArtPollReply(ArtPollReply artPollReply);

	/**
	 * We have receive an ArtTimeCode packet.
	 * @param artTimeCode is the artTimeCode object
	 */
	void onArtTimeCode(ArtTimeCode artTimeCode);

	/**
	 * We have receive an ArtDMX packet.
	 * @param artDMX is the artDMX packet
	 */
	void onArtDMX(ArtDMX artDMX);
	
	/**
	 * We have receive an ArtAddress packet.
	 * @param artAddress is the ArtAddress packet
	 */
	void onArtAddress(ArtAddress artAddress);
}
