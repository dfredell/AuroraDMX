package com.AuroraByteSoftware.AuroraDMX.network;

import android.app.Activity;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.AuroraNetwork;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.SettingsActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.TimerTask;

import fr.azelart.artnetstack.utils.ArtNetPacketEncoder;

public class SendArtnetUpdate extends TimerTask {

    private final Activity activity;
    private String serverIp = "";
    private int artnetPort = 0;
    private int[] previousMessage = new int[0];
    private int previousMessageSentAgo = 0;
    private DatagramSocket clientSocket;

    public SendArtnetUpdate(final Activity activity, DatagramSocket datagramSocket) {
        this.activity = activity;
        this.clientSocket = datagramSocket;
        final String serverPref = MainActivity.getSharedPref().getString(SettingsActivity.serveraddress.trim(), "");
        String[] splitServer = serverPref.split(":");
        if (splitServer.length == 2) {
            serverIp = splitServer[0];
            try {
                artnetPort = Integer.parseInt(splitServer[1].trim());
            } catch (NumberFormatException e) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, String.format(activity.getString(R.string.serverUnknown), serverPref), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            serverIp = serverPref;
        }
    }

    @Override
    public void run() {

        InetAddress IPAddress;
        try {
            clientSocket = AuroraNetwork.getArtnetSocket();
            IPAddress = InetAddress.getByName(serverIp);
        } catch (Exception t) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, String.format(activity.getString(R.string.serverUnknown), serverIp + ":" + artnetPort), Toast.LENGTH_LONG).show();
                }
            });
            t.printStackTrace();
            this.cancel();
            return;
        }

        int[] buffer = MainActivity.getCurrentDimmerLevels();
        //Only transmit unchanging channels every second
        if (Arrays.equals(previousMessage, buffer) && previousMessageSentAgo < 10 || buffer == null) {
            previousMessageSentAgo++;
            return;
        }
        previousMessageSentAgo = 0;

        byte[] data = {};
        try {
            data = ArtNetPacketEncoder.encodeArtDmxPacket(artnetPort, 0, buffer);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, AuroraNetwork.ART_NET_PORT);
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        previousMessage = buffer;
    }
}
