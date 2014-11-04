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
package fr.azelart.artnetstack.domain.controller;

/**
 * Good input.
 * @author Corentin Azelart.
 *
 */
public class ControllerGoodInput {

	/** Input receive data in error. */
	private Boolean receivedDataError;

	/** Input is disabled. */
	private Boolean disabled;

	/** This input accept DMX Text packets. */
	private Boolean includeDMXTextPackets;

	/** This input accept DMX SIPs packets. */
	private Boolean includeDMXSIPsPackets;

	/** This input accept DMX test packets. */
	private Boolean includeDMXTestPackets;

	/** This input receive data. */
	private Boolean dataReceived;

	/**
	 * Constructor.
	 */
	public ControllerGoodInput() {
		super();
	}

	/**
	 * @return the receivedDataError
	 */
	public final Boolean getReceivedDataError() {
		return receivedDataError;
	}

	/**
	 * @param receivedDataError the receivedDataError to set
	 */
	public final void setReceivedDataError(final Boolean receivedDataError) {
		this.receivedDataError = receivedDataError;
	}

	/**
	 * @return the disabled
	 */
	public final Boolean getDisabled() {
		return disabled;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(final Boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @return the includeDMXTextPackets
	 */
	public final Boolean getIncludeDMXTextPackets() {
		return includeDMXTextPackets;
	}

	/**
	 * @param includeDMXTextPackets the includeDMXTextPackets to set
	 */
	public final void setIncludeDMXTextPackets(Boolean includeDMXTextPackets) {
		this.includeDMXTextPackets = includeDMXTextPackets;
	}

	/**
	 * @return the includeDMXSIPsPackets
	 */
	public final Boolean getIncludeDMXSIPsPackets() {
		return includeDMXSIPsPackets;
	}

	/**
	 * @param includeDMXSIPsPackets the includeDMXSIPsPackets to set
	 */
	public final void setIncludeDMXSIPsPackets(Boolean includeDMXSIPsPackets) {
		this.includeDMXSIPsPackets = includeDMXSIPsPackets;
	}

	/**
	 * @return the includeDMXTestPackets
	 */
	public final Boolean getIncludeDMXTestPackets() {
		return includeDMXTestPackets;
	}

	/**
	 * @param includeDMXTestPackets the includeDMXTestPackets to set
	 */
	public final void setIncludeDMXTestPackets(Boolean includeDMXTestPackets) {
		this.includeDMXTestPackets = includeDMXTestPackets;
	}

	/**
	 * @return the dataReceived
	 */
	public final Boolean getDataReceived() {
		return dataReceived;
	}

	/**
	 * @param dataReceived the dataReceived to set
	 */
	public final void setDataReceived(final Boolean dataReceived) {
		this.dataReceived = dataReceived;
	}
}
