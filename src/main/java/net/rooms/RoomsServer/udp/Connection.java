package net.rooms.RoomsServer.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class Connection {

	private static final DatagramSocket socket;
	private static final byte[] buffer = new byte[4096];

	static {
		try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

	public static void send(String message, InetAddress address, int port) throws IOException {
		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
		socket.send(packet);
	}

	public static DatagramPacket receive() throws IOException {
		Arrays.fill(buffer, (byte) 0);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return packet;
	}


}
