package com.theeste.andnet.System.IO;

import java.io.IOException;
import java.nio.ByteBuffer;

public class StreamReadOperation extends StreamOperationAsyncResult {
	
	private ByteBuffer m_ByteBuffer;	
	private IAsyncReadReceiver m_Receiver;
	private Exception m_Exception;	
	private int m_BytesRead = 0;
	
	public StreamReadOperation(byte[] buffer, int offset, int count, Stream stream, IAsyncReadReceiver receiver, Object state) {
		super(stream, state);
		
		m_ByteBuffer = ByteBuffer.wrap(buffer, offset, count);
		m_Receiver = receiver;
	}
	
	protected void read() throws IOException, InterruptedException {
				
		byte[] buffer = new byte[m_ByteBuffer.limit()];
		
		int byteCount = this.stream().read(buffer, 0, buffer.length);
			
		if (byteCount > 0) { // Just in case one of our stream classes returns something less than 0.
			
			m_BytesRead += byteCount;
			
			m_ByteBuffer.put(buffer, 0, byteCount);			
		}
		
		this.asyncWaitHandle().set();
		
		if (m_Receiver != null)
			m_Receiver.endRead(this);
	}
	
	int endRead() throws Exception {
		
		this.asyncWaitHandle().waitOne();
		
		if (this.m_Exception != null)
			throw this.m_Exception;
		
		return m_BytesRead;
	}

	@Override
	public void run() {
		try {
			this.read();
		} catch (Exception e) {
			this.m_Exception = e;
		}
	}
}
