package com.theeste.andnet.System.IO;

import java.io.IOException;

public class StreamWriteOperation extends StreamOperationAsyncResult {
	
	private IAsyncWriteReceiver m_Receiver;
	private byte[] m_BufferToWrite;
	private int m_Offset;
	private int m_Count;
	private Exception m_Exception;
	
	public StreamWriteOperation(byte[] buffer, int offset, int count, Stream stream, IAsyncWriteReceiver receiver, Object state) {
		super(stream, state);
		
		m_Receiver = receiver;
		m_BufferToWrite = buffer;
		m_Offset = offset;
		m_Count = count;
	}
	
	void write() throws UnsupportedOperationException, IOException {
		
		this.stream().write(m_BufferToWrite, m_Offset, m_Count);
		
		this.asyncWaitHandle().set();
		
		if (m_Receiver != null)
			m_Receiver.endWrite(this);
	}
	
	void endWrite() throws Exception {
		
		this.asyncWaitHandle().waitOne();
		
		if (m_Exception != null)
			throw m_Exception;
	}

	@Override
	public void run() {
		try {
			this.write();
		} catch (Exception e) {
			this.m_Exception = e;
		}
	}
}
