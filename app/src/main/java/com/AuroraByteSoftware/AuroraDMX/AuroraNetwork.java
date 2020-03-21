package com.AuroraByteSoftware.AuroraDMX;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.AuroraByteSoftware.AuroraDMX.network.SendArtnetUpdate;
import com.AuroraByteSoftware.AuroraDMX.network.SendSacnUpdate;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;

/**
 * Network management
 * Created by furtchet on 5/24/17.
 */

public class AuroraNetwork {

    //Network timers
    private static Timer ArtNet;
    private static Timer SACN;
    private static Timer SACNUnicast;
    private static DatagramSocket clientSocket = null;
    private static DatagramSocket artnetSocket = null;
    public static final int ART_NET_PORT = 6454;

    private AuroraNetwork() {
    }

    public static void setUpNetwork(Activity activity) {
        SharedPreferences sharedPref = MainActivity.getSharedPref();
        if (null == sharedPref){
            return;
        }
        String protocol = sharedPref.getString("select_protocol", "");
        Log.i("AuroraNetwork", "Starting Network " + protocol);
        stopNetwork();

        if ("SACNUNI".equals(protocol)) {
            SACNUnicast = new Timer();
            SACNUnicast.scheduleAtFixedRate(new SendSacnUpdate(activity, clientSocket), 200, 100);
        } else if ("SACN".equals(protocol)) {
            SACN = new Timer();
            SACN.scheduleAtFixedRate(new SendSacnUpdate(activity, clientSocket), 200, 100);
        } else {
            ArtNet = new Timer();
            ArtNet.scheduleAtFixedRate(new SendArtnetUpdate(activity, clientSocket), 200, 100);
        }

    }

    public static void stopNetwork() {
        if (ArtNet != null) {
            ArtNet.cancel();
        }
        if (SACN != null) {
            SACN.cancel();
        }
        if (SACNUnicast != null) {
            SACNUnicast.cancel();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    public static DatagramSocket getClientSocket() {
        return clientSocket;
    }

    public static void setClientSocket(DatagramSocket clientSocket) {
        AuroraNetwork.clientSocket = clientSocket;
    }

    public static DatagramSocket getArtnetSocket() throws SocketException {
        if (artnetSocket == null || artnetSocket.isClosed()) {
            artnetSocket = new DatagramSocket(ART_NET_PORT);
            artnetSocket.setReuseAddress(true);
        }
        return artnetSocket;
    }
}
