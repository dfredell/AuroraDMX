package com.AuroraByteSoftware.AuroraDMX.network;

import android.util.Log;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.TimerTask;

/**
 * User Guide http://www.doityourselfchristmas.com/wiki/index.php?title=E1
 * .31_(Streaming-ACN)_Protocol Specs
 * http://tsp.plasa.org/tsp/documents/published_docs.php
 *
 * @author furtchet
 */
public class SendSacnUpdate extends TimerTask {

    private SACNData sacnPacket = null;
    private final byte[] sacnMessage = new byte[638];
    private int universe = 1;
    private String server = null;
    private final MainActivity mainActivity;
    private static final String TAG = "AuroraDMX";
    private int previousMessageSentAgo = 0;
    private int[] previousMessage = new int[0];

    public SendSacnUpdate(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        String univ = MainActivity.getSharedPref().getString("protocol_sacn_universe", "1").trim();
        String protocol = MainActivity.getSharedPref().getString("select_protocol", "");
        if ("SACNUNI".equals(protocol))
            server = MainActivity.getSharedPref().getString("protocol_sacn_unicast_ip", "239.255.0." + universe).trim();
        Log.v(TAG, "unicast " + server);
        try {
            universe = Integer.parseInt(univ);
        } catch (NullPointerException | NumberFormatException e) {
            // ignore
            universe = 1;
        }

        try {
            sacnPacket = new SACNData(sacnMessage, universe);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int[] levels = MainActivity.getCurrentDimmerLevels();
        //Only transmit unchanging channels every second
        if (Arrays.equals(previousMessage, levels) && previousMessageSentAgo < 10 || levels == null) {
            previousMessageSentAgo++;
            return;
        }
        previousMessageSentAgo = 0;

        sacnPacket.addDMXData(levels);
        InetAddress address;
        try {
            if (MainActivity.clientSocket == null || MainActivity.clientSocket.isClosed()) {
                MainActivity.clientSocket = new DatagramSocket(6454);
                MainActivity.clientSocket.setReuseAddress(true);
            }
            if (server != null)
                address = InetAddress.getByName(server);
            else
                address = InetAddress.getByName("239.255.0." + universe);
        } catch (Throwable e1) {
            e1.printStackTrace();
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mainActivity, String.format(mainActivity.getResources().getString(R.string.serverUnknown), server), Toast.LENGTH_LONG).show();
                }
            });
            this.cancel();
            return;
        }

        DatagramPacket sendPacket = new DatagramPacket(sacnMessage, sacnMessage.length, address, 5568);
        try {
            if (!MainActivity.clientSocket.isClosed()) {
                MainActivity.clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        previousMessage = levels;
    }
}
