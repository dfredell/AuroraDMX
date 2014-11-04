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
package fr.azelart.artnetstack.domain.artdmx;

import fr.azelart.artnetstack.constants.Constants;
import fr.azelart.artnetstack.domain.artnet.ArtNetObject;

/**
 * This is an ArtDMX packet.
 * @author Corentin Azelart
 *
 */
public class ArtDMX extends ArtNetObject {

	/**
	 * The sequence number is used to ensure that ArtDmx
	 * packets are used in the correct order. When Art-Net is
	 * carried over a medium such as the Internet, it is
	 * possible that ArtDmx packets will reach the receiver
	 * out of order.
	 */
	private int sequence;

	/**
	 * The Sequence field is set to 0x00 to disable this feature.
	 */
	private boolean sequenceEnabled;

	/**
	 * The physical input port from which DMX512 data was
	 * input. This field is for information only. Use Universe
	 * for data routing.
	 */
	private int physicalPort;

	/** Network. */
	private String subNet;

	/** Adress in network. */
	private String subSwitch;

	/**
	 * The length of the DMX512 data array. This value
	 * should be an even number in the range 2 - 512.
	 * It represents the number of DMX512 channels encoded
	 * in packet. NB: Products which convert Art-Net to
	 * DMX512 may opt to always send 512 channels.
	 * High Byte
	 */
	private int lengthHi;

	/**
	 * Low Byte of above.
	 */
	private int length;

	/**
	 * An variable length array of DMX512 lighting data.
	 */
	private int[] data;

	/**
	 * Construct an Art DMX packet.
	 */
	public ArtDMX() {
		super();
	}

	/**
	 * ToString method.
	 * @return a textual representation
	 */
	@Override
	public final String toString() {
		final StringBuilder vSb = new StringBuilder();
		vSb.append("ArtDMX[sequence=");
		vSb.append(sequence);
		vSb.append(",port=");
		vSb.append(physicalPort);
		vSb.append(",length=");
		vSb.append(lengthHi);
		if (data.length >= 1) {
			vSb.append(",C0=");
			vSb.append(data[0]);
		}
		if (data.length >= Constants.DMX_512_SIZE) {
			vSb.append(",C512=");
			vSb.append(data[Constants.DMX_512_SIZE - 1]);
		}
		vSb.append("]");
		return vSb.toString();
	}

	/**
	 * @return the sequence
	 */
	public final int getSequence() {
		return sequence;
	}

	/**
	 * @param pSequence the sequence to set
	 */
	public final void setSequence(final int pSequence) {
		this.sequence = pSequence;
	}

	/**
	 * @return the sequenceEnabled
	 */
	public final boolean isSequenceEnabled() {
		return sequenceEnabled;
	}

	/**
	 * @param pSequenceEnabled the sequenceEnabled to set
	 */
	public final void setSequenceEnabled(final boolean pSequenceEnabled) {
		this.sequenceEnabled = pSequenceEnabled;
	}

	/**
	 * @return the physicalPort
	 */
	public final int getPhysicalPort() {
		return physicalPort;
	}

	/**
	 * @param pPhysicalPort the physicalPort to set
	 */
	public final void setPhysicalPort(final int pPhysicalPort) {
		this.physicalPort = pPhysicalPort;
	}

	/**
	 * @return the subNet
	 */
	public final String getSubNet() {
		return subNet;
	}

	/**
	 * @param pSubNet the subNet to set
	 */
	public final void setSubNet(final String pSubNet) {
		this.subNet = pSubNet;
	}

	/**
	 * @return the subSwitch
	 */
	public final String getSubSwitch() {
		return subSwitch;
	}

	/**
	 * @param pSubSwitch the pSubSwitch to set
	 */
	public final void setSubSwitch(final String pSubSwitch) {
		this.subSwitch = pSubSwitch;
	}

	/**
	 * @return the lengthHi
	 */
	public final int getLengthHi() {
		return lengthHi;
	}

	/**
	 * @param pLengthHi the lengthHi to set
	 */
	public final void setLengthHi(final int pLengthHi) {
		this.lengthHi = pLengthHi;
	}

	/**
	 * @return the length
	 */
	public final int getLength() {
		return length;
	}

	/**
	 * @param pLength the length to set.
	 */
	public final void setLength(final int pLength) {
		this.length = pLength;
	}

	/**
	 * @return the data
	 */
	public final int[] getData() {
		return data;
	}

	/**
	 * @param pData the data to set.
	 */
	public final void setData(final int[] pData) {
		this.data = pData;
	}
}
