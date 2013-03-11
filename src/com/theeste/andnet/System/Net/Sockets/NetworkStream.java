package com.theeste.andnet.System.Net.Sockets;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.theeste.andnet.AndroidHelpers.JavaStreamWrapper;
import com.theeste.andnet.System.IAsyncResult;
import com.theeste.andnet.System.IO.FileAccess;
import com.theeste.andnet.System.IO.IAsyncReadReceiver;
import com.theeste.andnet.System.IO.IAsyncWriteReceiver;
import com.theeste.andnet.System.IO.SeekOrigin;
import com.theeste.andnet.System.IO.Stream;
import com.theeste.andnet.System.IO.StreamReadOperation;
import com.theeste.andnet.System.IO.StreamWriteOperation;

public class NetworkStream extends Stream {
	
	private ExecutorService m_ThreadPool;
	private JavaStreamWrapper m_Stream;
	private Socket m_Socket = null;
	private boolean m_OwnsSocket = false;
	
	public NetworkStream(Socket socket) throws IOException {
		this(socket, FileAccess.ReadWrite, false);
	}
	
	public NetworkStream(Socket socket, boolean ownsSocket) throws IOException {
		this(socket, FileAccess.ReadWrite, ownsSocket);
	}
	
	public NetworkStream(Socket socket, FileAccess access) throws IOException {
		this(socket, access, false);
	}
	
	public NetworkStream(Socket socket, FileAccess access, boolean ownsSocket) throws IOException {

		m_Socket = socket;
		
		m_OwnsSocket = ownsSocket;
		
		m_Stream = new JavaStreamWrapper();
		
		if (access == FileAccess.Read || access == FileAccess.ReadWrite) {
			m_Stream.setInputStream(socket.getInputStream());
		}
		
		if (access == FileAccess.Write || access == FileAccess.ReadWrite) {
			m_Stream.setOutputStream(socket.getOutputStream());
		}
		
		m_ThreadPool = Executors.newCachedThreadPool();
	}
	
	@Override
	public int readByte() throws IOException, UnsupportedOperationException {
		return m_Stream.readByte();
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException,
			UnsupportedOperationException {
		return m_Stream.read(buffer, offset, length);
	}

	@Override
	public void setReadTimeout(int timeout) {
		super.setReadTimeout(timeout);
		try {
			if (m_Socket != null)
				m_Socket.setSoTimeout(timeout);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeByte(int oneByte) throws IOException,
			UnsupportedOperationException {
		m_Stream.writeByte(oneByte);
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		m_Stream.write(buffer, offset, count);
	}

	@Override
	public boolean canTimeout() {
		return true;
	}

	@Override
	public void flush() throws IOException {
		m_Stream.flush();
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
	
	public boolean dataAvailable() throws IOException {
		return m_Stream.getInputStream().available() > 0;
	}

	@Override
	public long length() {
		return m_Stream.length();
	}

	@Override
	public long position() {
		return m_Stream.position();
	}

	@Override
	public void seek(long offset, SeekOrigin origin) throws IOException {
		m_Stream.seek(offset, origin);
	}

	@Override
	public void position(long newPosition) throws IOException {
		m_Stream.position(newPosition);
	}

	@Override
	public void setLength(long value) throws IOException {
		m_Stream.setLength(value);
	}

	@Override
	public IAsyncResult beginRead(byte[] buffer, int offset, int count,
			IAsyncReadReceiver receiver, Object state)
			throws UnsupportedOperationException, IOException,
			InterruptedException {

		StreamReadOperation readOp = new StreamReadOperation(buffer, offset, count, this, receiver, state);
		
		m_ThreadPool.submit(readOp);		
		
		return readOp;
	}

	@Override
	public IAsyncResult beginWrite(byte[] buffer, int offset, int count,
			IAsyncWriteReceiver receiver, Object state)
			throws UnsupportedOperationException, IOException {
		
		StreamWriteOperation writeOp = new StreamWriteOperation(buffer, offset, count, this, receiver, state);
		
		m_ThreadPool.submit(writeOp);
		
		return writeOp;		
	}

	@Override
	public void dispose() {
		m_Stream.close();

		try {
			if (m_OwnsSocket && m_Socket != null)
				m_Socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
