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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import fr.azelart.artnetstack.constants.Constants;
import fr.azelart.artnetstack.constants.MagicNumbers;
import fr.azelart.artnetstack.domain.arttimecode.ArtTimeCode;
import fr.azelart.artnetstack.domain.controller.Controller;

/**
 * Encoder for ArtNet Packets.
 * @author Corentin Azelart
 *
 */
public final class ArtNetPacketEncoder {

	/** ArtPollCounter. */
	private static Integer artPollCounter = 1;
	
	/** ArtDmxCounter. */
	private static Integer artDmxCounter = 1;

	/**
	 * Private constructor to respect checkstyle and protect class.
	 */
	private ArtNetPacketEncoder() {
		super();
	}

	/**
	 * Encode an ArtPoll packet.
	 * @param controller is the controller
	 * @throws IOException is the OutputStream have problem
	 * @return the ArtPollPacket in array
	 */
	public static byte[] encodeArtPollPacket(final Controller controller) throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_32);
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		byteArrayOutputStream.write(new Integer(Constants.ART_NET_VERSION).byteValue());
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_6);		// TalkToMe
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);	// Filler
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Encode an ArtTimeCode packet.
	 * @param artTimeCode is timecode informations
	 * @throws IOException in error with byte array
	 * @return the ArtTimeCode in array
	 */
	public static byte[] encodeArtTimeCodePacket(final ArtTimeCode artTimeCode) throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		// ID.
		byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);

		// OpTimeCode
		byteArrayOutputStream.write(ByteUtilsArt.in16toByte(38656));

		// Version
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		byteArrayOutputStream.write(new Integer(Constants.ART_NET_VERSION).byteValue());

		// Filler 1 and 2
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);

		// Frame
		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getFrameTime()));

		// Seconds
		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getSeconds()));

		// Minutes
		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getMinutes()));

		// Hours
		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getHours()));

		// Type
		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getArtTimeCodeType().ordinal()));

		return byteArrayOutputStream.toByteArray();
	}
	
	/**
	 * Encode a ArtDMX packet.
	 * @param univers is the universe
	 * @param network is the network
	 * @param dmx is the 512 DMX parameters
	 * @throws IOException in error with byte array
	 * @return the ArtDmxCode in array
	 */
	public static byte[] encodeArtDmxPacket(final String univers, final String network, final int dmx[] ) throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		// Prepare next trame
		artDmxCounter++;
		
		// ID.
		byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		
		// OpOutput
		byteArrayOutputStream.write(ByteUtilsArt.in16toByte(20480));

		// Version
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		byteArrayOutputStream.write(Constants.ART_NET_VERSION);
		
		
		// Sequence
		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artDmxCounter));
		
		// Physical
		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
		
		// Net Switch
		byteArrayOutputStream.write(Integer.parseInt(univers, MagicNumbers.MAGIC_NUMBER_16));
		byteArrayOutputStream.write(Integer.parseInt(network, MagicNumbers.MAGIC_NUMBER_16));
		
		// DMX data Length
		byteArrayOutputStream.write(ByteUtilsArt.in16toBit(dmx.length));
		
		byte bdmx;
		for(int i=0; i!=Constants.DMX_512_SIZE; i++) {
			if(dmx.length>i) {
				bdmx = (byte) dmx[i];
				byteArrayOutputStream.write(ByteUtilsArt.in8toByte(bdmx));
			} else {
				byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
			}
		}
		
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Encode an ArtPollReply packet.
	 * @param controller is the controller
	 * @param inetAdress is the address informations
	 * @param port is the port information
	 * @throws IOException in error with byte array
	 * @return the ArtTimeCode in array
	 */
	public static byte[] encodeArtPollReplyPacket(final Controller controller, final InetAddress inetAdress, final int port) throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();return null;}

//		// Prepare newt trame
//		artPollCounter++;
//
//		// ID.
//		byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
//		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
//
//		// ArtPollReply
//		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
//		byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_33);
//
//		// IP
//		byteArrayOutputStream.write(inetAdress.getAddress());
//
//		// Port
//		byteArrayOutputStream.write(ByteUtilsArt.in16toByte(port));
//
//		// Version Hight
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(Constants.VERSION_LIB_HIGHT));
//
//		// Version Low
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(Constants.VERSION_LIB_LOW));
//
//		// Net Switch
//		byteArrayOutputStream.write(Integer.parseInt(controller.getNetwork(), MagicNumbers.MAGIC_NUMBER_16));
//		byteArrayOutputStream.write(Integer.parseInt(controller.getSubNetwork(), MagicNumbers.MAGIC_NUMBER_16));
//
//		// Oem and UBEA
//		byteArrayOutputStream.write(ByteUtilsArt.hexStringToByteArray(("0x00ff")));
//
//		// Status1
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_199));
//
//		// Manufactor code
//		byteArrayOutputStream.write(ByteUtils.toByta("CZ"));
//
//		// ShotName
//		byteArrayOutputStream.write(ByteUtils.toByta(encodeString(Constants.SHORT_NAME, Constants.MAX_LENGTH_SHORT_NAME)));
//
//		// LongName
//		byteArrayOutputStream.write(ByteUtils.toByta(encodeString(Constants.LONG_NAME, Constants.MAX_LENGTH_LONG_NAME)));
//
//		//Node report
//		final int vArtPollCounter = artPollCounter + 1;
//		final StringBuffer nodeReport = new StringBuffer();
//		nodeReport.append("#").append("0x0000");	// Debug mode, see table 3
//		nodeReport.append("[").append(vArtPollCounter).append("]");
//		nodeReport.append("ok");
//		byteArrayOutputStream.write(ByteUtils.toByta(encodeString(nodeReport.toString(), Constants.MAX_LENGTH_NODE_REPORT)));
//
//		// NumPortHi (0, future evolution of ArtNet protocol)
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//
//		// NumPortLo (Between 0 and 4, max is 4)
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_4));
//
//		// Port Type
//		final Map<Integer, ControllerPortType> portsTypesMap = controller.getPortTypeMap();
//		ControllerPortType controlerPortType = null;
//		BitSet bitSet = null;
//		for (int i = 0; i != Constants.MAX_PORT; i++) {
//			controlerPortType = portsTypesMap.get(i);
//			// No port
//			if (controlerPortType == null) {
//				byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//			} else {
//				bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
//				// First 4 bits (PROCOTOL)
//				if (controlerPortType.getType().equals(PortTypeEnum.DMX512)) {
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_4, false);	// DMX
//				} else if (controlerPortType.getType().equals(PortTypeEnum.MIDI)) {
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_3, false);	// MIDI
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, true);										// MIDI
//				} else if (controlerPortType.getType().equals(PortTypeEnum.AVAB)) {
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_2, false);	// AVAB
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, true);										// AVAB
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, false);										// AVAB
//				} else if (controlerPortType.getType().equals(PortTypeEnum.COLORTRANCMX)) {
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_2, false);	// COLORTRAN
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, MagicNumbers.MAGIC_NUMBER_BIT_4, true);		// COLORTRAN
//				} else if (controlerPortType.getType().equals(PortTypeEnum.ADB)) {
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);	// ADB
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, true);										// ADB
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, MagicNumbers.MAGIC_NUMBER_BIT_4, false);	// ADB
//				} else if (controlerPortType.getType().equals(PortTypeEnum.ARTNET)) {
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);	// ARTNET
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, true);										// ARTNET
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, false);										// ARTNET
//					bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, true);										// ARTNET
//				}
//				// Set if this channel can input onto the Art-NetNetwork
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_5, controlerPortType.isInputArtNet());
//				// Set is this channel can output data from the Art-Net Network
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_6, controlerPortType.isOutputArtNet());
//				byteArrayOutputStream.write(bitSet.toByteArray());
//			}
//		}
//
//		// Good Input
//		final Map<Integer, ControllerGoodInput> portsGoodInputsMap = controller.getGoodInputMapping();
//		ControllerGoodInput controlerGoodInput = null;
//		for (int i = 0; i != Constants.MAX_PORT; i++) {
//			controlerGoodInput = portsGoodInputsMap.get(i);
//			// No port
//			if (controlerGoodInput == null) {
//				byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//			} else {
//				bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);	// Unused and transmitted as zero
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, controlerGoodInput.getReceivedDataError());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, controlerGoodInput.getDisabled());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, controlerGoodInput.getIncludeDMXTextPackets());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_5, controlerGoodInput.getIncludeDMXSIPsPackets());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_6, controlerGoodInput.getIncludeDMXTestPackets());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_7, controlerGoodInput.getDataReceived());
//				byteArrayOutputStream.write(bitSet.toByteArray());
//			}
//		}
//
//		// Good Ouput
//		final Map<Integer, ControllerGoodOutput> portsGoodOutputsMap = controller.getGoodOutputMapping();
//		ControllerGoodOutput controlerGoodOutput = null;
//		for (int i = 0; i != Constants.MAX_PORT; i++) {
//			controlerGoodOutput = portsGoodOutputsMap.get(i);
//			// No port
//			if (controlerGoodOutput == null) {
//				byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//			} else {
//				bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);	// Unused and transmitted as zero
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_1, controlerGoodOutput.getMergeLTP());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, controlerGoodOutput.getOutputPowerOn());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, controlerGoodOutput.getOutputmergeArtNet());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, controlerGoodOutput.getIncludeDMXTextPackets());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_5, controlerGoodOutput.getIncludeDMXSIPsPackets());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_6, controlerGoodOutput.getIncludeDMXTestPackets());
//				bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_7, controlerGoodOutput.getDataTransmited());
//				byteArrayOutputStream.write(bitSet.toByteArray());
//			}
//		}
//
//		// Directions
//		BitSet bitSetIn;
//		BitSet bitSetOut;
//		final ByteArrayOutputStream byteArrayInTempOutputStream = new ByteArrayOutputStream();
//		final ByteArrayOutputStream byteArrayOutTempOutputStream = new ByteArrayOutputStream();
//		for (int i = 0; i != Constants.MAX_PORT; i++) {
//			controlerPortType = portsTypesMap.get(i);
//			bitSetIn = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
//			bitSetOut = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
//
//			// No port
//			if (controlerPortType == null || controlerPortType.getDirection() == null) {
//				bitSetIn.set(i, false);
//				bitSetOut.set(i, false);
//			} else if (controlerPortType.getDirection().equals(PortInputOutputEnum.INPUT)) {
//				bitSetIn.set(i, true);
//			} else if (controlerPortType.getDirection().equals(PortInputOutputEnum.OUTPUT)) {
//				bitSetOut.set(i, true);
//			} else if (controlerPortType.getDirection().equals(PortInputOutputEnum.BOTH)) {
//				bitSetIn.set(i, true);
//				bitSetOut.set(i, true);
//			} else {
//				bitSetIn.set(i, false);
//				bitSetOut.set(i, false);
//			}
//
//
//			if (bitSetIn.isEmpty()) {
//				byteArrayInTempOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//			} else {
//				byteArrayInTempOutputStream.write(bitSetIn.toByteArray());
//			}
//
//			if (bitSetOut.isEmpty()) {
//				byteArrayOutTempOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//			} else {
//				byteArrayOutTempOutputStream.write(bitSetOut.toByteArray());
//			}
//		}
//		byteArrayOutputStream.write(byteArrayInTempOutputStream.toByteArray());
//		byteArrayOutputStream.write(byteArrayOutTempOutputStream.toByteArray());
//
//		// Screen
//		bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
//		if (controller.getScreen()) {
//			// Ethernet data display
//			bitSet.set(1, true);
//			byteArrayOutputStream.write(bitSet.toByteArray());
//
//		} else {
//			// Local data display
//			byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		}
//
//		// SwMacro (not implemented)
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//
//		// SwRemote (not implemented)
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//
//		// Spare (1+2+3), Not used, set to zero
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//
//		// Style
//		// TODO
//
//		// MAC
//		// TODO : Implement it.
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//		byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
//
//		return byteArrayOutputStream.toByteArray();
//	}

	/**
	 * Encode string with finals white spaces.
	 * @param text is text
	 * @param size is max size
	 * @return the string
	 */
	private static String encodeString(final String text, final int size) {
		final StringBuffer sb = new StringBuffer();
		sb.append(text);
		for (int i = text.length(); i != size; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

}
