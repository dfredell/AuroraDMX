package com.AuroraByteSoftware.AuroraDMX.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import fr.azelart.artnetstack.domain.artnet.ArtNetObject;
import fr.azelart.artnetstack.domain.artpoll.ArtPoll;
import fr.azelart.artnetstack.domain.artpollreply.ArtPollReply;
import fr.azelart.artnetstack.domain.controller.Controller;
import fr.azelart.artnetstack.utils.ArtNetPacketDecoder;
import fr.azelart.artnetstack.utils.ArtNetPacketEncoder;

public class SendArtnetPoll extends Thread {
    private Context superContext = null;
    private static final String TAG = "AuroraDMX";

    @Override
    public void run() {

        MainActivity.foundServers.clear();//forget old servers
        WifiManager.MulticastLock multicastLock = null;
        try {
            if (MainActivity.clientSocket != null)
                MainActivity.clientSocket.close();

            WifiManager wifi = (WifiManager) superContext.getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("net.inside.broadcast");
            multicastLock.acquire();

            if (MainActivity.clientSocket == null || MainActivity.clientSocket.isClosed()) {
                MainActivity.clientSocket = new DatagramSocket(6454);
                MainActivity.clientSocket.setReuseAddress(true);
                //System.out.println("OPENING Socket");
            }
            Log.i(TAG, "Starting SendArtnetPoll");
            Controller cont = new Controller();
            byte[] out = ArtNetPacketEncoder.encodeArtPollPacket(cont);
            DatagramPacket sendPacket = new DatagramPacket(out, out.length, InetAddress.getByName("255.255.255.255"), 6454);
            MainActivity.clientSocket.setBroadcast(true);
            MainActivity.clientSocket.send(sendPacket);
            MainActivity.clientSocket.send(sendPacket);
            MainActivity.clientSocket.send(sendPacket);
            MainActivity.clientSocket.send(sendPacket);
            MainActivity.clientSocket.send(sendPacket);
            MainActivity.clientSocket.send(sendPacket);
            MainActivity.clientSocket.setSoTimeout(1000);

            long timeOut = System.currentTimeMillis() + 1000;
            while (timeOut > System.currentTimeMillis()) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                MainActivity.clientSocket.receive(packet);

                ArtNetObject artNet = ArtNetPacketDecoder.decodeArtNetPacket(buf, packet.getAddress());
                if (artNet instanceof ArtPollReply) {
                    ArtPollReply reply = (ArtPollReply) artNet;
//					System.out.println(reply.getSubNet());
//					System.out.println(reply.getEsta());
//					System.out.println(reply.getSubSwitch());
                    Log.i(TAG, String.format("Found ArtNet: '%1$s' '%2$s' %3$s:%4$s:%5$s %6$s", reply.getShortName(), reply.getEsta(), reply.getIp(), reply.getSubNet(), reply.getSubSwitch(), reply.getOemHexa()));
                    if (!MainActivity.foundServers.contains(packet.getAddress().getHostAddress())) {
                        MainActivity.foundServers.add(reply.getIp());
                        MainActivity.foundServers.add(reply.getShortName());
                    }
                }else if(artNet instanceof ArtPoll){
                    Log.i(TAG, String.format("Did NOT Found ArtNet: '%1$s' '%2$s'", artNet, artNet.getClass().toString()));
                }else{
                    Log.i(TAG, String.format("Did NOT Found ArtNet: %1$s", artNet));
                }
            }
        } catch (SocketTimeoutException ste) {
            //This is good
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (null != MainActivity.clientSocket) {
                MainActivity.clientSocket.disconnect();
                MainActivity.clientSocket.close();
            }
            if (null != MainActivity.progressDialog)
                MainActivity.progressDialog.dismiss();
            if (null != multicastLock)
                multicastLock.release();
        }
    }

    public void setContext(Context context) {
        // TODO Auto-generated method stub
        superContext = context;
    }
}
