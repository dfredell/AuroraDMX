package com.AuroraByteSoftware.AuroraDMX.network;

import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.TimerTask;

/**
 * User Guide http://www.doityourselfchristmas.com/wiki/index.php?title=E1
 * .31_(Streaming-ACN)_Protocol Specs
 * http://tsp.plasa.org/tsp/documents/published_docs.php
 * 
 * @author furtchet
 * 
 */
public class SendSacnUpdate extends TimerTask {

	private SACNData sacnPacket = null;
	private final byte[] sacnMessage = new byte[638];
	private int universe = 1;
	private String server = null;
	private final MainActivity mainActivity;

	public SendSacnUpdate(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		String univ = MainActivity.sharedPref.getString("protocol_sacn_universe", "1").trim();
		String protocol = MainActivity.sharedPref.getString("select_protocol", "");
		if ("SACNUNI".equals(protocol))
			server = MainActivity.sharedPref.getString("protocol_sacn_unicast_ip", "239.255.0." + universe).trim();
		System.out.println("unicast "+server);
		try {
			universe = Integer.parseInt(univ);
		} catch (NullPointerException e) {
			// ignore
			universe = 1;
		} catch (NumberFormatException e) {
			//Just default to universe 1
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
			        Toast.makeText(mainActivity, String.format(mainActivity.getResources().getString(R.string.serverUnknown),server), Toast.LENGTH_LONG).show();
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
		} catch (SocketException e) {
			// if (null != e && e.getCause().toString().contains("ENETUNREACH"))
			// {
			// MainActivity.clientSocket.close();
			// e.printStackTrace();
			// } else
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
