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
 * Controler Input Good.
 * @author Corentin Azelart.
 *
 */
public class ControllerGoodOutput {

	/** The merge mode is LTP. */
	private Boolean mergeLTP;

	/** DMX output short detected on power up. */
	private Boolean outputPowerOn;

	/** Output is merging ArtNet data. */
	private Boolean outputmergeArtNet;

	/** This output accept DMX Text packets. */
	private Boolean includeDMXTextPackets;

	/** This output accept DMX SIPs packets. */
	private Boolean includeDMXSIPsPackets;

	/** This output accept DMX test packets. */
	private Boolean includeDMXTestPackets;

	/** This output transmit data. */
	private Boolean dataTransmited;

	/**
	 * Constructor.
	 */
	public ControllerGoodOutput() {
		super();
	}

	/**
	 * @return the mergeLTP
	 */
	public final Boolean getMergeLTP() {
		return mergeLTP;
	}

	/**
	 * @param mergeLTP the mergeLTP to set
	 */
	public final void setMergeLTP(Boolean mergeLTP) {
		this.mergeLTP = mergeLTP;
	}

	/**
	 * @return the outputPowerOn
	 */
	public final Boolean getOutputPowerOn() {
		return outputPowerOn;
	}

	/**
	 * @param outputPowerOn the outputPowerOn to set
	 */
	public final void setOutputPowerOn(Boolean outputPowerOn) {
		this.outputPowerOn = outputPowerOn;
	}

	/**
	 * @return the outputmergeArtNet
	 */
	public final Boolean getOutputmergeArtNet() {
		return outputmergeArtNet;
	}

	/**
	 * @param outputmergeArtNet the outputmergeArtNet to set
	 */
	public final void setOutputMergeArtNet(final Boolean outputmergeArtNet) {
		this.outputmergeArtNet = outputmergeArtNet;
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
	public final void setIncludeDMXTextPackets(final Boolean includeDMXTextPackets) {
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
	public final void setIncludeDMXTestPackets(final Boolean includeDMXTestPackets) {
		this.includeDMXTestPackets = includeDMXTestPackets;
	}

	/**
	 * @return the dataTransmited
	 */
	public final Boolean getDataTransmited() {
		return dataTransmited;
	}

	/**
	 * @param dataTransmited the dataTransmited to set
	 */
	public final void setDataTransmited(final Boolean dataTransmited) {
		this.dataTransmited = dataTransmited;
	}
}
