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
package fr.azelart.artnetstack.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import fr.azelart.artnetstack.constants.Constants;
import fr.azelart.artnetstack.constants.MagicNumbers;
import fr.azelart.artnetstack.constants.OpCodeConstants;
import fr.azelart.artnetstack.domain.artaddress.ArtAddress;
import fr.azelart.artnetstack.domain.artdmx.ArtDMX;
import fr.azelart.artnetstack.domain.artnet.ArtNetObject;
import fr.azelart.artnetstack.domain.artpoll.ArtPoll;
import fr.azelart.artnetstack.domain.artpollreply.ArtPollReply;
import fr.azelart.artnetstack.domain.artpollreply.ArtPollReplyStatus;
import fr.azelart.artnetstack.domain.arttimecode.ArtTimeCode;
import fr.azelart.artnetstack.domain.arttimecode.ArtTimeCodeType;
import fr.azelart.artnetstack.domain.enums.IndicatorStateEnum;
import fr.azelart.artnetstack.domain.enums.NetworkCommunicationTypeEnum;
import fr.azelart.artnetstack.domain.enums.UniverseAddressProgrammingAuthorityEnum;

/**
 * ArtNetPacket decoder.
 * @author Corentin Azelart.
 */
public class ArtNetPacketDecoder {

	/**
	 * Private constructor.
	 */
	private ArtNetPacketDecoder() {
		super();
	}

	/**
	 * Decode an ArtNet packet.
	 * @param packet is the packet.
	 * @param ip is the ip of source.
	 * @return a ArtNetObject than be cast in correct format
	 */
	public static ArtNetObject decodeArtNetPacket(final byte[] packet, final InetAddress ip) {

		// The ArtNetPacket.
		final ArtNetObject artNetObject = null;

		// Set generals infos
		final String hexaBrut = byteArrayToHex(packet);
		final String id = new String(packet, 0, 7);
		
		// Extract OpCode
		int opCode = ((packet[9] & 0xFF) << 8) | (packet[8] & 0xFF);
		opCode = Integer.parseInt(Integer.toHexString(opCode));

		// Yes, it's a ArtNetPacket
		if (!"Art-Net".equals(id)) {
			return null;
		}

		/*
		 * Dicover the type of the packet.
		 * Please refer to OpcodeTable.
		 */
		if (OpCodeConstants.OPPOLL == opCode) {
			/*
			 * ArtPollPacket : This is an ArtPoll packet,
			 * no other data is contained in this UDP packet
			 */
			if (!checkVersion(packet, hexaBrut)) {
				return null;
			}
			return decodeArtPollPacket(packet, hexaBrut);
		} else if (OpCodeConstants.OPTIMECODE == opCode) {
			/*
			 * ArtTimePacket : OpTimeCode
			 * This is an ArtTimeCode packet.
			 * It is used to transport time code over the network.
			 */
			if (!checkVersion(packet, hexaBrut)) {
				return null;
			}
			return decodeArtTimeCodePacket(packet, hexaBrut);
		} else if (OpCodeConstants.OPPOLLREPLY == opCode) {
			// ArtPollReply : This is a ArtPollReply packet.
			return decodeArtPollReplyPacket(packet, hexaBrut, ip);
		} else if (OpCodeConstants.OPOUTPUT == opCode) {
			// ArtDMX
			return decodeArtDMXPacket(packet, hexaBrut);
		} else if (OpCodeConstants.ARTADDRESS == opCode) {
			// ArtAddress
			return decodeArtAddressPacket(packet, hexaBrut);
		}

		return artNetObject;
	}

	/**
	 * Decode an artPollReplyPacket.
	 * @param bytes is the packet data
	 * @param hexaBrut is the text data
	 * @param ip is the address ip
	 * @return ArtPollReply
	 */
	private static ArtPollReply decodeArtPollReplyPacket(final byte[] bytes, final String hexaBrut, final InetAddress ip) {
		final ArtPollReply artPollReply = new ArtPollReply();

		// IP Adress (4*8)
		final byte[] address = new byte[MagicNumbers.MAGIC_NUMBER_4];
		System.arraycopy(bytes, MagicNumbers.MAGIC_NUMBER_10, address, MagicNumbers.MAGIC_NUMBER_ZERO, MagicNumbers.MAGIC_NUMBER_4);
		try {
			final InetAddress inetAdress = InetAddress.getByAddress(address);
			artPollReply.setIp(inetAdress.getHostAddress());
		} catch (final UnknownHostException e) {
			artPollReply.setIp(null);
		}

		// Port (2*8)
		artPollReply.setPort(ByteUtilsArt.byte2toIn(bytes, MagicNumbers.MAGIC_NUMBER_14));

		// Version High (1*8)
		artPollReply.setVersionH(bytes[MagicNumbers.MAGIC_NUMBER_16]);

		// Version Low (1*8)
		artPollReply.setVersionL(bytes[MagicNumbers.MAGIC_NUMBER_15]);

		// Subnet (1*8) and subswtich (1*8)
		artPollReply.setSubNet(String.format("%02X", bytes[MagicNumbers.MAGIC_NUMBER_18]));
		artPollReply.setSubSwitch(String.format("%02X", bytes[MagicNumbers.MAGIC_NUMBER_19]));

		// Oem Hi (1*8) + Oem (1*8)
		artPollReply.setOemHexa(String.format("%02X", bytes[MagicNumbers.MAGIC_NUMBER_20]) + String.format("%02X", bytes[MagicNumbers.MAGIC_NUMBER_21]));

		// UBEA Version (1*8) / 0 if not programmed
		artPollReply.setUbeaVersion(bytes[MagicNumbers.MAGIC_NUMBER_22]);

		// Status area
		final ArtPollReplyStatus artPollReplyStatus = new ArtPollReplyStatus();
		artPollReplyStatus.setUbeaPresent(ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , 0));
		artPollReplyStatus.setRdmCapable(ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , 1));
		artPollReplyStatus.setBootRom(ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , 2));

		if (ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_5) && ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_4)) {
			artPollReplyStatus.setProgrammingAuthority(UniverseAddressProgrammingAuthorityEnum.NOT_USED);
		} else if (!ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_5) && ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_4)) {
			artPollReplyStatus.setProgrammingAuthority(UniverseAddressProgrammingAuthorityEnum.FRONT_PANEL);
		} else if (ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_5) && !ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_4)) {
			artPollReplyStatus.setProgrammingAuthority(UniverseAddressProgrammingAuthorityEnum.NETWORK);
		} else if (!ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_5) && !ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_4)) {
			artPollReplyStatus.setProgrammingAuthority(UniverseAddressProgrammingAuthorityEnum.UNKNOW);
		}

		if (ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_7) && ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_6)) {
			artPollReplyStatus.setIndicatorState(IndicatorStateEnum.NORMAL_MODE);
		} else if (!ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_7) && ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_6)) {
			artPollReplyStatus.setIndicatorState(IndicatorStateEnum.LOCATE_MODE);
		} else if (ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_7) && !ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_6)) {
			artPollReplyStatus.setIndicatorState(IndicatorStateEnum.MUTE_MODE);
		} else if (!ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_7) && !ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_23] , MagicNumbers.MAGIC_NUMBER_6)) {
			artPollReplyStatus.setIndicatorState(IndicatorStateEnum.UNKNOW);
		}

		artPollReply.setArtPollReplyStatus(artPollReplyStatus);

		// EstaManHi (1*8) + EstaManLow (1*8)
		artPollReply.setEsta(new String(bytes, MagicNumbers.MAGIC_NUMBER_24, 2));

		// Short Name
		artPollReply.setShortName(new String(bytes, MagicNumbers.MAGIC_NUMBER_26, MagicNumbers.MAGIC_NUMBER_18));

		// Long Name
		artPollReply.setLongName(new String(bytes, MagicNumbers.MAGIC_NUMBER_44, MagicNumbers.MAGIC_NUMBER_64));

		// Real ip
		artPollReply.setPhysicalIp(ip.getHostAddress());

		return artPollReply;
	}

	/**
	 * Decode an artTimeCodePacket.
	 * @param bytes is the packet data
	 * @param hexaBrut is the text packet
	 * @return the ArtPollPacketObject
	 */
	private static ArtTimeCode decodeArtTimeCodePacket(final byte[] bytes, final String hexaBrut) {
		final ArtTimeCode artTimeCode = new ArtTimeCode();
		artTimeCode.setFrameTime(bytes[MagicNumbers.MAGIC_NUMBER_14]);
		artTimeCode.setSeconds(bytes[MagicNumbers.MAGIC_NUMBER_15]);
		artTimeCode.setMinutes(bytes[MagicNumbers.MAGIC_NUMBER_16]);
		artTimeCode.setHours(bytes[MagicNumbers.MAGIC_NUMBER_17]);
		final int typeTimecode = bytes[MagicNumbers.MAGIC_NUMBER_18];

		if (typeTimecode == 0) {
			artTimeCode.setArtTimeCodeType(ArtTimeCodeType.FILM);
		} else if (typeTimecode == 1) {
			artTimeCode.setArtTimeCodeType(ArtTimeCodeType.EBU);
		} else if (typeTimecode == 2) {
			artTimeCode.setArtTimeCodeType(ArtTimeCodeType.DF);
		} else if (typeTimecode == MagicNumbers.MAGIC_NUMBER_3) {
			artTimeCode.setArtTimeCodeType(ArtTimeCodeType.SMPTE);
		}
		return artTimeCode;
	}

	/**
	 * Decode an artPollPacket.
	 * @param bytes is the packet data
	 * @param hexaBrut is the text packet
	 * @return the ArtPollPacketObject
	 */
	private static ArtPoll decodeArtPollPacket(final byte[] bytes, final String hexaBrut) {
		final ArtPoll artPoll = new ArtPoll();

		artPoll.setArtPollReplyWhenConditionsChanges(ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_12], 1));
		artPoll.setSendMeDiagnosticsMessage(ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_12], 2));

		if (ByteUtilsArt.bitIsSet(bytes[MagicNumbers.MAGIC_NUMBER_12], MagicNumbers.MAGIC_NUMBER_3)) {
			artPoll.setNetworkCommunicationTypeDiagnosticsMessages(NetworkCommunicationTypeEnum.UNICAST);
		} else {
			artPoll.setNetworkCommunicationTypeDiagnosticsMessages(NetworkCommunicationTypeEnum.BROADCAST);
		}

		return artPoll;
	}

	/**
	 * Decode an artDMX packet.
	 * @param bytes is the packet data
	 * @param hexaBrut is hexa packet
	 * @return an ArtDMX packet.
	 */
	private static ArtDMX decodeArtDMXPacket(final byte[] bytes, final String hexaBrut) {
		final ArtDMX artDMX = new ArtDMX();

		// Sequence (1*8)
		artDMX.setSequence(bytes[MagicNumbers.MAGIC_NUMBER_12] & Constants.INT_ESCAP);

		// Physical (1*8)
		artDMX.setPhysicalPort(bytes[MagicNumbers.MAGIC_NUMBER_13] & Constants.INT_ESCAP);

		// Subnet (1*8) and subswtich (1*8)
		artDMX.setSubNet(String.format("%02X", bytes[MagicNumbers.MAGIC_NUMBER_14]));
		artDMX.setSubSwitch(String.format("%02X", bytes[MagicNumbers.MAGIC_NUMBER_15]));

		// Length of DMX data (1*8)
		artDMX.setLengthHi(bytes[MagicNumbers.MAGIC_NUMBER_16] & Constants.INT_ESCAP);

		// Low Byte of above. (1*8)
		artDMX.setLength(bytes[MagicNumbers.MAGIC_NUMBER_17] & Constants.INT_ESCAP);

		// An variable length array of DMX512 lighting data
		final byte[] dmx = new byte[Constants.DMX_512_SIZE];
		System.arraycopy(bytes, MagicNumbers.MAGIC_NUMBER_18, dmx, 0, Constants.DMX_512_SIZE);
		artDMX.setData(byteArrayToIntArray(dmx));

		return artDMX;
	}
	
	/**
	 * Decode an ArtAddress packet, we can only set :
	 * - Short name
	 * - Long name
	 * @param bytes is the packet.
	 * @param hexaBrut is the packet on hexa format.
	 * @return an ArtAddress
	 */
	private static ArtAddress decodeArtAddressPacket(final byte[] bytes, final String hexaBrut) {
		final ArtAddress artAddress = new ArtAddress();
		
		
		
		return artAddress;
	}
	

	/**
	 * Check the version of artnet.
	 * @param packet is the packet
	 * @param hexaBrut is the text packet
	 * @return true if the is the correct version of ArtNet protocol
	 */
	private static boolean checkVersion(final byte[] packet, final String hexaBrut) {
		final int version = packet[MagicNumbers.MAGIC_NUMBER_11];
		return (version >= Constants.ART_NET_VERSION);
	}

	/**
	 * Convert byte to hexa.
	 * @param barray is the byte array.
	 * @return the String in hexa value
	 */
	private static String byteArrayToHex(final byte[] barray) {
		final char[] c = new char[barray.length * 2];
		byte b;
		for (int i = 0; i < barray.length; ++i) {
			b = ((byte) (barray[i] >> MagicNumbers.MAGIC_NUMBER_4));
			c[i * 2] = (char) (b > MagicNumbers.MAGIC_NUMBER_9 ? b + 0x37 : b + 0x30);
			b = ((byte) (barray[i] & 0xF));
			c[i * 2 + 1] = (char) (b > 9 ? b + 0x37 : b + 0x30);
		}

		return new String(c);
	}

	/**
	 * Transform a byte array to int array.
	 * @param in a array in bytes
	 * @return a array in int
	 */
	private static int[] byteArrayToIntArray(final byte[] in) {
		final int[] output = new int[in.length];
		for (int i = 0; i != in.length; i++) {
			output[i] = in[i] & Constants.INT_ESCAP;
		}
		return output;
	}
}
