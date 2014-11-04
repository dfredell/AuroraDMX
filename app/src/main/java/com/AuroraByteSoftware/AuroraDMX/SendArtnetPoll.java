package com.AuroraByteSoftware.AuroraDMX;

import android.content.Context;
import android.net.wifi.WifiManager;
import fr.azelart.artnetstack.domain.artnet.ArtNetObject;
import fr.azelart.artnetstack.domain.artpollreply.ArtPollReply;
import fr.azelart.artnetstack.domain.controller.Controller;
import fr.azelart.artnetstack.utils.ArtNetPacketDecoder;
import fr.azelart.artnetstack.utils.ArtNetPacketEncoder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

class SendArtnetPoll extends Thread {
	private Context superContext=null;
	@Override
	public void run() {

		MainActivity.foundServers.clear();//forget old servers
		WifiManager.MulticastLock multicastLock = null;
		try {
			if(MainActivity.clientSocket!=null)
				MainActivity.clientSocket.close();
			
			WifiManager wifi= (WifiManager)superContext.getSystemService(Context.WIFI_SERVICE);
			multicastLock=wifi.createMulticastLock("net.inside.broadcast");			
			multicastLock.acquire();
			
			if (MainActivity.clientSocket == null || MainActivity.clientSocket.isClosed()) {
				MainActivity.clientSocket = new DatagramSocket(6454);
				MainActivity.clientSocket.setReuseAddress(true);
				//System.out.println("OPENING Socket");
			}
			//System.out.println("Starting SendArtnetPoll");
			Controller cont=new Controller();
			byte[] out = ArtNetPacketEncoder.encodeArtPollPacket(cont);
			DatagramPacket sendPacket = new DatagramPacket(out, out.length,
					InetAddress.getByName("255.255.255.255"), 6454);
			MainActivity.clientSocket.setBroadcast(true);
			MainActivity.clientSocket.send(sendPacket);
			Thread.sleep(100);
			MainActivity.clientSocket.send(sendPacket);
			Thread.sleep(100);
			MainActivity.clientSocket.send(sendPacket);
			Thread.sleep(100);
			MainActivity.clientSocket.setSoTimeout(1000);

			long timeOut = System.currentTimeMillis() + 1000;
			while (timeOut > System.currentTimeMillis()) {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				MainActivity.clientSocket.receive(packet);
				
				ArtNetObject artNet=ArtNetPacketDecoder.decodeArtNetPacket(buf, packet.getAddress());
				if(artNet instanceof ArtPollReply && !MainActivity.foundServers.contains(packet.getAddress().getHostAddress())){
					ArtPollReply reply=(ArtPollReply)artNet;
//					System.out.println(reply.getLongName());
//					System.out.println(reply.getIp());
//					System.out.println(reply.getShortName());
//					System.out.println(reply.getSubNet());
//					System.out.println(reply.getEsta());
//					System.out.println(reply.getSubSwitch());
//					System.out.println(reply.getOemHexa());

					MainActivity.foundServers.add(reply.getIp());
					MainActivity.foundServers.add(reply.getShortName());
				
				}
			}
		} catch (SocketTimeoutException ste) {
			//This is good
		} catch (Throwable e) {
			e.printStackTrace();
		} finally{
			if(null != MainActivity.clientSocket){
				MainActivity.clientSocket.disconnect();
				MainActivity.clientSocket.close();
			}
			if(null != MainActivity.progressDialog)
				MainActivity.progressDialog.dismiss();
			if(null != multicastLock)
				multicastLock.release();
		}
	}

	public void setContext(Context context) {
		// TODO Auto-generated method stub
		superContext=context;
	}
}
