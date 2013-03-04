package com.theeste.andnet.IO;

abstract class StreamOperationResult implements IAsyncStreamOperationResult, Runnable {

	private Stream m_Stream;
	private Object m_State;
	private boolean m_ShouldStop = false;
	private Thread m_RunningThread;
		
	@Override
	public Stream getStream() {
		// TODO Auto-generated method stub
		return m_Stream;
	}
	
	@Override
	public Object getState() {
		// TODO Auto-generated method stub
		return m_State;
	}
	
	StreamOperationResult(Stream stream, Object state) {
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
