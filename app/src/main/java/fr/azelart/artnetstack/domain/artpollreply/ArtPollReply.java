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

import fr.azelart.artnetstack.domain.artnet.ArtNetObject;

/**
 * This is an ArtPollReply object.
 * @author Corentin Azelart
 *
 */
public class ArtPollReply extends ArtNetObject {

	/** Adress IP. */
	private String ip;

	/** Port. */
	private int port;

	/** Version High. */
	private int versionH;

	/** Version Low. */
	private int versionL;

	/** Hexa Oem. */
	private String oemHexa;

	/** UBEA Version. */
	private int ubeaVersion;

	/** ArtPollReply status. */
	private ArtPollReplyStatus artPollReplyStatus;

	/** Network. */
	private String subNet;

	/** Adress in network. */
	private String subSwitch;

	/** Real ip address. */
	private String physicalIp;

	/** The ESTA manufacturer code. These codes are used to
	 * represent equipment manufacturer. They are assigned
	 * by ESTA. This field can be interpreted as two ASCII
	 * bytes representing the manufacturer initials.
	 */
	private String esta;

	/**
	 * The array represents a null terminated short name for
	 * the Node. The Controller uses the ArtAddress packet
	 * to program this string. Max length is 17 characters
	 * plus the null. This is a fixed length field, although the
	 * string it contains can be shorter than the field.
	 */
	private String shortName;

	/**
	 * The array represents a null terminated long name for
	 * the Node. The Controller uses the ArtAddress packet
	 * to program this string. Max length is 63 characters
	 * plus the null. This is a fixed length field, although the
	 * string it contains can be shorter than the field.
	 */
	private String longName;

	/**
	 * The Style code defines the equipment style of the
	 * device. See Table 4 for current Style codes.
	 */
	private ArtPollReplyStyle artPollReplyStyle;

	/**
	 * toString.
	 * @return a representation of this packet
	 */
	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ArtPollReply[ip=").append(ip).append(",longName").append(longName).append("]");
		return sb.toString();
	}

	/**
	 * @return the artPollReplyStyle
	 */
	public final ArtPollReplyStyle getArtPollReplyStyle() {
		return artPollReplyStyle;
	}

	/**
	 * @param artPollReplyStyle the artPollReplyStyle to set
	 */
	public final void setArtPollReplyStyle(final ArtPollReplyStyle artPollReplyStyle) {
		this.artPollReplyStyle = artPollReplyStyle;
	}

	/**
	 * @return the ip
	 */
	public final String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public final void setIp(final String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public final void setPort(final int port) {
		this.port = port;
	}

	/**
	 * @return the versionH
	 */
	public final int getVersionH() {
		return versionH;
	}

	/**
	 * @param versionH the versionH to set
	 */
	public final void setVersionH(final int versionH) {
		this.versionH = versionH;
	}

	/**
	 * @return the versionL
	 */
	public final int getVersionL() {
		return versionL;
	}

	/**
	 * @param versionL the versionL to set
	 */
	public final void setVersionL(final int versionL) {
		this.versionL = versionL;
	}

	/**
	 * @return the oemHexa
	 */
	public final String getOemHexa() {
		return oemHexa;
	}

	/**
	 * @param oemHexa the oemHexa to set
	 */
	public final void setOemHexa(final String oemHexa) {
		this.oemHexa = oemHexa;
	}

	/**
	 * @return the ubeaVersion
	 */
	public final int getUbeaVersion() {
		return ubeaVersion;
	}

	/**
	 * @param ubeaVersion the ubeaVersion to set
	 */
	public final void setUbeaVersion(final int ubeaVersion) {
		this.ubeaVersion = ubeaVersion;
	}

	public final ArtPollReplyStatus getArtPollReplyStatus() {
		return artPollReplyStatus;
	}

	public final void setArtPollReplyStatus(final ArtPollReplyStatus artPollReplyStatus) {
		this.artPollReplyStatus = artPollReplyStatus;
	}

	/**
	 * @return the subNet
	 */
	public final String getSubNet() {
		return subNet;
	}

	/**
	 * @param subNet the subNet to set
	 */
	public final void setSubNet(final String subNet) {
		this.subNet = subNet;
	}

	/**
	 * @return the subSwitch
	 */
	public final String getSubSwitch() {
		return subSwitch;
	}

	/**
	 * @param subSwitch the subSwitch to set
	 */
	public final void setSubSwitch(final String subSwitch) {
		this.subSwitch = subSwitch;
	}

	/**
	 * @return the shortName
	 */
	public final String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public final void setShortName(final String shortName) {
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
	public final void setLongName(final String longName) {
		this.longName = longName;
	}

	/**
	 * @return the esta
	 */
	public final String getEsta() {
		return esta;
	}

	/**
	 * @param esta the esta to set
	 */
	public final void setEsta(final String esta) {
		this.esta = esta;
	}


	/**
	 * @return the physicalIp
	 */
	public final String getPhysicalIp() {
		return physicalIp;
	}

	/**
	 * @param physicalIp the physicalIp to set
	 */
	public final void setPhysicalIp(final String physicalIp) {
		this.physicalIp = physicalIp;
	}
}
