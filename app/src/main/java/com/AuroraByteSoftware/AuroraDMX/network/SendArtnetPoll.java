package com.AuroraByteSoftware.AuroraDMX.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.AuroraByteSoftware.AuroraDMX.AuroraNetwork;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fr.azelart.artnetstack.domain.artnet.ArtNetObject;
import fr.azelart.artnetstack.domain.artpoll.ArtPoll;
import fr.azelart.artnetstack.domain.artpollreply.ArtPollReply;
import fr.azelart.artnetstack.domain.controller.Controller;
import fr.azelart.artnetstack.utils.ArtNetPacketDecoder;
import fr.azelart.artnetstack.utils.ArtNetPacketEncoder;

public class SendArtnetPoll extends Thread {
    private static final int TIMEOUT = 1500;
    private Context superContext = null;
    private static final String TAG = "AuroraDMX";
    private DatagramSocket clientSocket = null;

    @Override
    public void run() {

        MainActivity.foundServers.clear();//forget old servers
        WifiManager.MulticastLock multicastLock = null;
        final Map<InetAddress, byte[]> networkResponse = new HashMap<>();
        Timer sendPacketTimer = new Timer();

        try {

            WifiManager wifi = (WifiManager) superContext.getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("com.AuroraByteSoftware.AuroraDMX");
            multicastLock.acquire();

            clientSocket = AuroraNetwork.getArtnetSocket();
            Log.i(TAG, "Starting SendArtnetPoll");
            Controller cont = new Controller();
            byte[] out = ArtNetPacketEncoder.encodeArtPollPacket(cont);
            final DatagramPacket sendPacket = new DatagramPacket(out, out.length, InetAddress.getByName("255.255.255.255"), AuroraNetwork.ART_NET_PORT);
            clientSocket.setBroadcast(true);
            clientSocket.setSoTimeout(TIMEOUT);

            //Send a few Poll questions
            final TimerTask runnable = new TimerTask() {
                @Override
                public void run() {
                    try {
                        clientSocket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            sendPacketTimer.scheduleAtFixedRate(runnable, 0, 200);


            //Receive the poll answers
            long timeOut = System.currentTimeMillis() + TIMEOUT;
            while (timeOut > System.currentTimeMillis()) {
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                clientSocket.receive(packet);
                Log.i(TAG, "Packet Received " + packet.getAddress());
                Log.i(TAG, "Packet Received " + Arrays.toString(packet.getData()));
                networkResponse.put(packet.getAddress(), packet.getData());
            }

        } catch (SocketTimeoutException ste) {
            //This is good
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (null != clientSocket) {
                clientSocket.disconnect();
                clientSocket.close();
            }
            if (null != MainActivity.progressDialog)
                MainActivity.progressDialog.dismiss();
            if (null != multicastLock && multicastLock.isHeld())
                multicastLock.release();
            sendPacketTimer.cancel();
        }

        //Parse the bytes into ArtPollReplies
        for (InetAddress address : networkResponse.keySet()) {
            try {
                ArtNetObject artNet = ArtNetPacketDecoder.decodeArtNetPacket(networkResponse.get(address), address);
                if (artNet instanceof ArtPollReply) {
                    ArtPollReply reply = (ArtPollReply) artNet;
                    Log.i(TAG, String.format("Found ArtNet: '%1$s' '%2$s' '%3$s'", reply.getShortName(), reply.getIp(), Arrays.toString(address.getAddress())));
                    for (Iterator<ArtPollReply> iterator = MainActivity.foundServers.iterator(); iterator.hasNext(); ) {
                        ArtPollReply foundServer = iterator.next();
                        if (foundServer.getIp().equals(reply.getIp())) {
                            iterator.remove();
                        }
                    }
                    MainActivity.foundServers.add(reply);
                } else if (artNet instanceof ArtPoll) {
                    Log.i(TAG, String.format("Found ArtPoll: '%1$s' '%2$s'", artNet, artNet.getClass().toString()));
                } else {
                    Log.i(TAG, String.format("Did NOT Found ArtNet: %1$s", artNet));
                }
            } catch (RuntimeException e){
                Log.w(TAG,"Unable to parse art poll replies ", e);
            }
        }
        clientSocket.disconnect();
        clientSocket.close();
        //Sort the ArtNet servers by ip
        Collections.sort(MainActivity.foundServers, new Comparator<ArtPollReply>() {
            @Override
            public int compare(ArtPollReply lhs, ArtPollReply rhs) {
                return lhs.getIp().compareTo(rhs.getIp());
            }
        });
    }

    public void setContext(Context context) {
        // TODO Auto-generated method stub
        superContext = context;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        clientSocket.disconnect();
        clientSocket.close();
    }
}
