package com.theeste.andnet.IO;

import java.io.IOException;
import java.nio.ByteBuffer;

class StreamReadOperation extends StreamOperationResult {
	
	private ByteBuffer m_ByteBuffer;	
	private IAsyncReadReceiver m_Receiver;
	private Stream m_Stream;
	private Thread m_RunningThread;
	private boolean m_ShouldStop = false;
	private int m_FirstByte;
		
	StreamReadOperation(byte[] buffer, int offset, int count, Stream stream, IAsyncReadReceiver receiver, Object state) {
		super(stream, state);
		
		m_ByteBuffer = ByteBuffer.wrap(buffer, offset, count);
		m_Receiver = receiver;
		m_Stream = stream;
	}
	
	int read() throws IOException {
		
		int totalBytesRead = -1;
		
		if (this.m_FirstByte != -1) {
			
			m_ByteBuffer.put((byte)m_FirstByte);
			totalBytesRead = 1;
			
			if (this.getStream().available() > 0) {
			
				int limit = m_ByteBuffer.limit();
				
				int bytesLeft = limit - totalBytesRead;
				
				while (bytesLeft > 0) {
					
					byte[] buffer = new byte[bytesLeft];
					
					int byteCount = this.getStream().read(buffer, 0, buffer.length);
						
					if (byteCount > 0) {
						
						totalBytesRead += byteCount;
						
						m_ByteBuffer.put(buffer, 0, byteCount);
						
					} else if (byteCount < 0) {
						break;
					}
					
					if (this.getStream().available() <= 0)
						break;
					
					bytesLeft = limit - byteCount;
					
					if (bytesLeft <= 0)
						break;
				}
			}
		}
			
		return totalBytesRead;
	}
	
	private synchronized void setRunningThread(Thread thread) {
		m_RunningThread = thread;
	}
	
	@Override
	public synchronized void stop() {
		
		m_ShouldStop = true;
		
		if (m_RunningThread != null) {
			Thread temp = m_RunningThread;
			m_RunningThread = null;
			temp.interrupt();
		}
	}
	
	@Override
	public synchronized boolean isMarkedToStop() {
		return m_ShouldStop;
	}

	@Override
	public void run() {
		
		this.setRunningThread(Thread.currentThread());
		
		try {
			
			if (this.isMarkedToStop() == false && Thread.currentThread().isInterrupted() == false) {
				
				m_FirstByte = m_Stream.read();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
			
		m_Receiver.endRead(this);
	}
}
