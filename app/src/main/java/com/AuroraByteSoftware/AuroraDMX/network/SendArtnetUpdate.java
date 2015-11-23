package com.AuroraByteSoftware.AuroraDMX.network;

import android.widget.Toast;

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

    private final MainActivity mainActivity;
    private String server = "";
    private int[] previousMessage = new int[0];
    private int previousMessageSentAgo = 0;

    public SendArtnetUpdate(MainActivity a_mainActivity) {
        this.mainActivity = a_mainActivity;
        server = MainActivity.getSharedPref().getString(SettingsActivity.serveraddress.trim(), "");
    }

    @Override
    public void run() {

        InetAddress IPAddress;
        try {
            if (MainActivity.clientSocket == null || MainActivity.clientSocket.isClosed()) {
                MainActivity.clientSocket = new DatagramSocket(6454);
                MainActivity.clientSocket.setReuseAddress(true);
            }
            IPAddress = InetAddress.getByName(server);
        } catch (Throwable t) {
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mainActivity, String.format(mainActivity.getResources().getString(R.string.serverUnknown), server), Toast.LENGTH_LONG).show();
                }
            });
            t.printStackTrace();
            this.cancel();
            return;
        }

        int[] buffer = MainActivity.getCurrentDimmerLevels();
        if (Arrays.equals(previousMessage, buffer) && previousMessageSentAgo < 10) {
            previousMessageSentAgo++;
            return;
        }
        previousMessageSentAgo = 0;

        byte[] data = {};
        try {
            data = ArtNetPacketEncoder.encodeArtDmxPacket("00", "00", buffer);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 6454);
        try {
            if (!MainActivity.clientSocket.isClosed()) {
                MainActivity.clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        previousMessage = buffer;
    }
}
