package com.theeste.andnet.System.IO;

import java.io.FileNotFoundException;
import java.io.IOException;

public class StreamWriter extends TextWriter {

	private Stream m_Stream;
	
	public StreamWriter(Stream stream) {
		m_Stream = stream;
	}
	
	public StreamWriter(String path) throws FileNotFoundException, IOException {
		this(File.open(path, FileMode.Create, FileAccess.Write));
	}

	@Override
	public void flush() throws IOException {
		m_Stream.flush();
	}

	@Override
	public void close() {
		super.close();
		m_Stream.close();
	}

	@Override
	public void write(char value) throws Exception {
		super.write(value);
		
		m_Stream.write(value);
	}
}
