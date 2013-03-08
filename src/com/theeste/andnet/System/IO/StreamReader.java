package com.theeste.andnet.System.IO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.CharBuffer;
public class StreamReader extends TextReader {
	
	private Stream m_Stream;
	public Stream baseStream() {
		return m_Stream;
	}
	
	public boolean endOfStream() {
		try {
			return m_Stream.available() == 0;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return true;
	}
	
	public StreamReader(Stream stream) {
		m_Stream = stream;
	}
	
	public StreamReader(String path) throws FileNotFoundException, IOException {
		this(File.open(path, FileMode.Open, FileAccess.Read));
	}

	@Override
	public int read() throws UnsupportedOperationException, IOException {
		return m_Stream.readByte();
	}

	@Override
	public int read(char[] buffer, int index, int count) throws Exception {
		int totalCount = 0;
		
		byte[] temp = new byte[count];
		
		totalCount = m_Stream.read(temp, 0, temp.length);
		
		CharBuffer.wrap(buffer, index, count).put(new String(temp, 0, totalCount));
		
		return totalCount;
	}

	@Override
	public void close() {
		m_Stream.close();
	}

	@Override
	public int peek() {

		if (m_Stream.canSeek()) {
		
			try {
				if (m_Stream.available() > 0) {

					int lastPosition = m_Stream.position();
					int nextByte = m_Stream.readByte();
					m_Stream.seek(lastPosition);
					return nextByte;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		return -1;
	}
}
