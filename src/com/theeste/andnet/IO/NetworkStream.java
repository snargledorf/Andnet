package com.theeste.andnet.IO;

import java.io.IOException;
import java.net.Socket;

public class NetworkStream extends StreamWrapper {

	public NetworkStream(Socket socket) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream());
	}
	
}
