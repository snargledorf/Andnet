package com.theeste.andnet.IO;

abstract class StreamOperationResult implements IStreamOperationResult, IStreamOperationRunnable {

	private Stream m_Stream;
	private Object m_State;
	
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
}
