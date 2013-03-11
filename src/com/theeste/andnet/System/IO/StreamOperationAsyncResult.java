package com.theeste.andnet.System.IO;

import com.theeste.andnet.System.IAsyncResult;
import com.theeste.andnet.System.Threading.ManualResetEvent;


abstract class StreamOperationAsyncResult implements IAsyncResult, Runnable {
	
	private ManualResetEvent m_AsyncWaitHandle = new ManualResetEvent(false);
	@Override
	public ManualResetEvent asyncWaitHandle() {
		return m_AsyncWaitHandle;
	}

	private Stream m_Stream;
		
	public Stream stream() {
		return m_Stream;
	}

	private Object m_State;
	@Override
	public Object asyncState() {
		return m_State;
	}
	
	public StreamOperationAsyncResult(Stream stream, Object state) {
		m_Stream = stream;
		m_State = state;
	}
}
