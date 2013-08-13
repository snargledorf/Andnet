package com.theeste.andnet.System.Threading;

public class Thread 
{

	private java.lang.Thread m_Thread;
	
	private ParameterizedThreadStart m_ParameterizedThreadStart;
	private ThreadStart m_ThreadStart;
	
	private ThreadState m_ThreadState;
	public ThreadState getThreadState()
	{
		return m_ThreadState;
	}
	
	public boolean isAlive()
	{
		return m_Thread != null && m_Thread.isAlive();
	}
	
	public boolean isBackground()
	{
		return m_Thread != null && m_Thread.isDaemon();
	}
	
	public boolean isThreadPoolThread()
	{
		return false; // TODO Currently not sure how to check this...
	}
	
	private String m_Name = "";
	public String getName()
	{
		return m_Name;
	}
	
	public void setName(String threadName)
	{
		m_Name = threadName;
		if (m_Thread != null)
		{
			m_Thread.setName(threadName);
		}
	}
	
	private ThreadPriority m_Priority = ThreadPriority.Normal;
	public ThreadPriority getPriority()
	{
		if (m_Thread != null)
		{
			int priority = m_Thread.getPriority();
			m_Priority = ThreadPriority.get(priority);
		}
		
		return m_Priority;
	}
	
	public void setPriority(ThreadPriority threadPriority)
	{		
		m_Priority = threadPriority;
		
		if (m_Thread != null)
		{
			m_Thread.setPriority(threadPriority.getValue());
		}
	}
	
	private Thread()
	{
		m_ThreadState = ThreadState.Unstarted;
	}
	
	public Thread(ThreadStart threadStart) 
	{
		this();
		
		m_ThreadStart = threadStart;
	}
	
	public Thread(ParameterizedThreadStart parameterizedThreadStart)
	{
		this();
		
		m_ParameterizedThreadStart = parameterizedThreadStart;
	}
	
	public void start()
	{
		if (m_ParameterizedThreadStart != null)
		{
			start(new ParameterizedThreadStartRunnable(m_ParameterizedThreadStart, null));
		} 
		else
		{
			start(new ThreadStartRunnable(m_ThreadStart));
		}
	}
	
	public void start(Object parameter)
	{
		if (m_ThreadStart != null)
		{
			start(new ThreadStartRunnable(m_ThreadStart));
		}
		else
		{
			start(new ParameterizedThreadStartRunnable(m_ParameterizedThreadStart, parameter));
		}
	}
	
	private void start(Runnable runnable)
	{
		m_Thread = new java.lang.Thread(runnable, getName());
		
		m_Thread.setPriority(getPriority().getValue());
		
		m_Thread.start();
		
		m_ThreadState = ThreadState.Running;
	}
	
	private class ThreadStartRunnable implements Runnable
	{
		private ThreadStart m_ThreadStart;
		
		public ThreadStartRunnable(ThreadStart threadStart)
		{
			m_ThreadStart = threadStart;
		}

		@Override
		public void run() {

			m_ThreadStart.run();
		}
		
	}
	
	private class ParameterizedThreadStartRunnable implements Runnable
	{
		private Object m_Parameter;		
		private ParameterizedThreadStart m_ParameterizedThreadStart;
		
		public ParameterizedThreadStartRunnable(ParameterizedThreadStart parameterizedThreadStart, Object parameter)
		{
			m_Parameter = parameter;
			m_ParameterizedThreadStart = parameterizedThreadStart;
		}
		
		@Override
		public void run() {
			m_ParameterizedThreadStart.run(m_Parameter);
		}
		
	}
	
}
