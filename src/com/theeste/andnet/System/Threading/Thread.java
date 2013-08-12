package com.theeste.andnet.System.Threading;

public class Thread {

	private ThreadStart m_ThreadStart;
	
	public Thread(ThreadStart threadStart) {
		m_ThreadStart = threadStart;
	}
	
	public void start()
	{
		new java.lang.Thread(m_ThreadStart).start();
	}
	
}
