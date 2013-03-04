package com.theeste.andnet.IO;

import java.io.IOException;

class StreamWriteOperation extends StreamOperationResult implements Runnable{
	
	private IAsyncWriteReceiver m_Receiver;
	private byte[] m_BufferToWrite;
	private int m_Offset;
	private int m_Count;
	
	StreamWriteOperation(byte[] buffer, int offset, int count, Stream stream, IAsyncWriteReceiver receiver, Object state) {
		super(stream, state);
		
		m_Receiver = receiver;
		m_BufferToWrite = buffer;
		m_Offset = offset;
		m_Count = count;
	}

	@Override
	public void run() {
		super.run();
		if (this.isMarkedToStop() == false && m_Receiver != null)
			m_Receiver.endWrite(this);
	}
	
	void write() throws UnsupportedOperationException, IOException {
		this.getStream().write(m_BufferToWrite, m_Offset, m_Count);
	}
}
