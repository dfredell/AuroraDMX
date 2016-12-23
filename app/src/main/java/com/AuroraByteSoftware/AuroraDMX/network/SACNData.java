package com.AuroraByteSoftware.AuroraDMX.network;

import android.os.Build;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author Dan Fredell
 *         April 26, 2014
 */
class SACNData {

    private byte universe = 1;
    private byte sequenceNumber = 0x00;
    private final byte[] message;

    /**
     * Initate the aSCN packet. Use message via pass by reference.
     *
     * @param a_message  the universe
     * @param a_universe the data
     * @throws UnsupportedEncodingException
     */
    public SACNData(byte[] a_message, int a_universe)
            throws UnsupportedEncodingException {
        universe = (byte) a_universe;
        message = a_message;
        addRootLayer();
        addFrameLayer();
        addDMPLayer();
    }

    private void addRootLayer() {
        // Preamble Size
        message[0] = (byte) 0x00;
        message[1] = (byte) 0x10;
        // Post-amble Size
        message[2] = (byte) 0x00;
        message[3] = (byte) 0x00;
        // ACN Packet Identifier
        message[4] = (byte) 0x41;
        message[5] = (byte) 0x53;
        message[6] = (byte) 0x43;
        message[7] = (byte) 0x2d;
        message[8] = (byte) 0x45;
        message[9] = (byte) 0x31;
        message[10] = (byte) 0x2e;
        message[11] = (byte) 0x31;
        message[12] = (byte) 0x37;
        message[13] = (byte) 0x00;
        message[14] = (byte) 0x00;
        message[15] = (byte) 0x00;
        // Flags and Length
        message[16] = ((byte) 0x72);
        message[17] = ((byte) 0x6e);
        // Vector
        message[18] = ((byte) 0x00);
        message[19] = ((byte) 0x00);
        message[20] = ((byte) 0x00);
        message[21] = ((byte) 0x04);
        // CID 22-37
        String serial = Build.SERIAL;
        serial = serial.length() > 16 ? serial.substring(0, 16) : serial;
        byte[] serialBytes = serial.getBytes();
        System.arraycopy(serialBytes, 0, message, 22, serialBytes.length);
    }

    private void addFrameLayer() throws UnsupportedEncodingException {
        // Flags and Length
        message[38] = (byte) 0x72;
        message[39] = (byte) 0x58;
        // Vector
        message[40] = (byte) 0x00;
        message[41] = (byte) 0x00;
        message[42] = (byte) 0x00;
        message[43] = (byte) 0x02;
        // Source Name 44-107
        String name = "AuroraDMX";
        byte[] nameBytes = name.getBytes("UTF-8");
        System.arraycopy(nameBytes, 0, message, 44, nameBytes.length);
        // Priority
        message[108] = (byte) 100;
        // Reserved
        message[109] = (byte) 0x00;
        message[110] = (byte) 0x00;
        // Sequence Number
        //message[111] = sequenceNumber++;
        // Options
        message[112] = (byte) 0x00;
        // Universe
        message[113] = (byte) 0x00;
        message[114] = universe;

    }

    private void addDMPLayer() {
        // Flags and Length
        message[115] = (byte) 0x72;
        message[116] = (byte) 0x0b;
        // Vector
        message[117] = (byte) 0x02;
        // Address Type & Data Type
        message[118] = (byte) 0xa1;
        // First Property Address
        message[119] = (byte) 0x00;
        message[120] = (byte) 0x00;
        // Address messagecrement
        message[121] = (byte) 0x00;
        message[122] = (byte) 0x01;
        // Address messagecrement
        message[123] = (byte) 0x02;
        message[124] = (byte) 0x01;
    }

    /**
     * DMX data in int array level range 0-255, array range 0-511
     * Array to channels are off by one
     *
     * @param levels 0-255 on each
     */
    public void addDMXData(int levels[]) {
        // Property values. START Code + Data
        for (int x = 0; x < 512 && x < levels.length; x++) {
            message[126 + x] = (byte) levels[x];
        }
        // Sequence Number
        message[111] = sequenceNumber++;
    }

}
