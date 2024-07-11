package net.rooms.RoomsServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.DatagramPacket;

import static net.rooms.RoomsServer.udp.Connection.receive;
import static net.rooms.RoomsServer.udp.Connection.send;

@SpringBootApplication
public class RoomsServerApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(RoomsServerApplication.class, args);
		DatagramPacket packet = receive();
		System.out.println(new String(packet.getData()));
		packet = receive();
		System.out.println(new String(packet.getData()));
		send("Blah", packet.getAddress(), packet.getPort());
	}
}
