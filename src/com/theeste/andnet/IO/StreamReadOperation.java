package com.theeste.andnet.IO;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.theeste.andnet.Threading.ManualResetEvent;

class StreamReadOperation extends StreamOperationResult implements Runnable {
	
	private ByteBuffer m_ByteBuffer;	
	private IAsyncReadReceiver m_Receiver;
	private int m_FirstByte;
	private ManualResetEvent m_ReadCompleteEvent = new ManualResetEvent(false);
		
	StreamReadOperation(byte[] buffer, int offset, int count, Stream stream, IAsyncReadReceiver receiver, Object state) {
		super(stream, state);
		
		m_ByteBuffer = ByteBuffer.wrap(buffer, offset, count);
		m_Receiver = receiver;
	}
	
	int read() throws IOException, InterruptedException {
		
		int totalBytesRead = -1;
		
		m_ReadCompleteEvent.reset();
		m_ReadCompleteEvent.waitOne();
		
		this.stop();
		
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

	@Override
	public void run() {
		
		try {
			
			if (this.isMarkedToStop() == false && Thread.currentThread().isInterrupted() == false) {
				
				m_FirstByte = this.getStream().read();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		m_ReadCompleteEvent.set();
		
		if (this.isMarkedToStop() == false && m_Receiver != null)
			m_Receiver.endRead(this);
	}
}
