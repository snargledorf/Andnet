package com.theeste.andnet.System.Net.Sockets;

import java.io.IOException;
import java.net.Socket;

import com.theeste.andnet.AndroidHelpers.JavaStreamWrapper;

public class NetworkStream extends JavaStreamWrapper {

	public NetworkStream(Socket socket) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream());
	}
	
}
