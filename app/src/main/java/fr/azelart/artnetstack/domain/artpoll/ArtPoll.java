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
package fr.azelart.artnetstack.domain.artpoll;

import fr.azelart.artnetstack.domain.artnet.ArtNetObject;
import fr.azelart.artnetstack.domain.enums.NetworkCommunicationTypeEnum;
import fr.azelart.artnetstack.domain.enums.PriorityCodesEnum;

/**
 * This is an ArtPoll.
 * @author Corentin Azelart.
 *
 */
public class ArtPoll extends ArtNetObject {

	/**
	 * Send ArtPollReply whenever Node conditions
	 * change. This selection allows the Controller to be
	 * informed of changes without the need to continuously poll.
	 * If false :
	 * Only send ArtPollReply in response to an ArtPoll or ArtAddress.
	 */
	private Boolean artPollReplyWhenConditionsChanges;

	/**
	 * Send me diagnostics messages.
	 */
	private Boolean sendMeDiagnosticsMessage;

	/**
	 * Diagnostics messages are broadcast or unicast.
	 */
	private NetworkCommunicationTypeEnum networkCommunicationTypeDiagnosticsMessages;

	/**
	 * Prority codes.
	 */
	private PriorityCodesEnum priorityCodes;


	/**
	 * Constructor.
	 */
	public ArtPoll() {
		super();
	}


	/**
	 * @return the artPollReplyWhenConditionsChanges
	 */
	public final Boolean getArtPollReplyWhenConditionsChanges() {
		return artPollReplyWhenConditionsChanges;
	}


	/**
	 * @param artPollReplyWhenConditionsChanges the artPollReplyWhenConditionsChanges to set
	 */
	public final void setArtPollReplyWhenConditionsChanges(
			final Boolean artPollReplyWhenConditionsChanges) {
		this.artPollReplyWhenConditionsChanges = artPollReplyWhenConditionsChanges;
	}


	/**
	 * @return the sendMeDiagnosticsMessage
	 */
	public final Boolean getSendMeDiagnosticsMessage() {
		return sendMeDiagnosticsMessage;
	}


	/**
	 * @param sendMeDiagnosticsMessage the sendMeDiagnosticsMessage to set
	 */
	public final void setSendMeDiagnosticsMessage(final Boolean sendMeDiagnosticsMessage) {
		this.sendMeDiagnosticsMessage = sendMeDiagnosticsMessage;
	}


	/**
	 * @return the networkCommunicationTypeDiagnosticsMessages
	 */
	public final NetworkCommunicationTypeEnum getNetworkCommunicationTypeDiagnosticsMessages() {
		return networkCommunicationTypeDiagnosticsMessages;
	}


	/**
	 * @param networkCommunicationTypeDiagnosticsMessages the networkCommunicationTypeDiagnosticsMessages to set
	 */
	public final void setNetworkCommunicationTypeDiagnosticsMessages(
			final NetworkCommunicationTypeEnum networkCommunicationTypeDiagnosticsMessages) {
		this.networkCommunicationTypeDiagnosticsMessages = networkCommunicationTypeDiagnosticsMessages;
	}


	/**
	 * @return the priorityCodes
	 */
	public final PriorityCodesEnum getPriorityCodes() {
		return priorityCodes;
	}


	/**
	 * @param priorityCodes the priorityCodes to set
	 */
	public final void setPriorityCodes(final PriorityCodesEnum priorityCodes) {
		this.priorityCodes = priorityCodes;
	}
}
