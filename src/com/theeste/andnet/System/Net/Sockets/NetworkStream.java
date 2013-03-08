package com.theeste.andnet.System.Net.Sockets;

import java.io.IOException;
import java.net.Socket;

import com.theeste.andnet.AndroidHelpers.JavaStreamWrapper;
import com.theeste.andnet.System.IO.Stream;

public class NetworkStream extends Stream {

	private JavaStreamWrapper m_Stream;
	
	public NetworkStream(Socket socket) throws IOException {
		m_Stream = new JavaStreamWrapper(socket.getInputStream(), socket.getOutputStream());
	}

	@Override
	public void flush() throws IOException {
		m_Stream.flush();
	}

	@Override
	public int available() throws IOException {
		return m_Stream.available();
	}

	@Override
	public boolean canSeek() {
		return false;
	}

	@Override
	public boolean canRead() {
		return m_Stream.canRead();
	}

	@Override
	public boolean canWrite() {
		return m_Stream.canWrite();
	}	
}
