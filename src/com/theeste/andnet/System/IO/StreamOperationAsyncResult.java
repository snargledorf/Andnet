package com.theeste.andnet.System.IO;

import com.theeste.andnet.System.IAsyncResult;


abstract class StreamOperationAsyncResult implements IAsyncResult, Runnable {

	private Stream m_Stream;
	private boolean m_ShouldStop = false;
	private Thread m_RunningThread;
		
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
	
	public synchronized void stop() {
		m_ShouldStop = true;
		
		if (m_RunningThread != null) {
			Thread temp = m_RunningThread;
			m_RunningThread = null;
			temp.interrupt();
		}
	}

	private synchronized void setRunningThread(Thread thread) {
		m_RunningThread = thread;
	}
	
	public synchronized boolean isMarkedToStop() {
		return m_ShouldStop;
	}	

	@Override
	public void run() {		
		this.setRunningThread(Thread.currentThread());
	}
}
