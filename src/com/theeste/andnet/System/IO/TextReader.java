package com.theeste.andnet.System.IO;

public abstract class TextReader {
	
	public void close() {
		
	}
	
	public int read() throws Exception {
		return -1;
	}
	
	public int read(char[] buffer, int index, int count) throws Exception {
		return 0;
	}
	
	public int readBlock(char[] buffer, int index, int count) throws Exception {
		int totalCount = 0;

		for (int i = 0; i < count; i++) {
			int nextChar = this.read();
			if (nextChar > -1) {
				
				totalCount++;
				
				buffer[i + index] = (char)nextChar;
			} else {
				break;
			}
		}
		
		return totalCount;
	}
	
	public String readLine() throws Exception {
		
		String line = "";
		
		int readChar = -1;
		while (true) {
			
			readChar = this.read();
			
			if (readChar <= 0)
				return null;
			
			char c = (char)readChar;
			
			boolean foundCarriageReturn = false;
			if (c == '\r') {				
				foundCarriageReturn  = true;
				continue;
			} else if (c == '\n' || foundCarriageReturn) {
				break;
			}

			line += c;		
		}
		
		return line;		
	}
	
	public String readToEnd() throws Exception {
		String text = "";
		
		int readChar = -1;
		while (true) {
			
			readChar = this.read();
			
			if (readChar <= -1)
				break;
			
			char c = (char)readChar;
			
			boolean foundCarriageReturn = false;
			if (c == '\r') {				
				foundCarriageReturn  = true;
				continue;
			} else if (c == '\n' || foundCarriageReturn) {
				break;
			}

			text += c;		
		}
		
		return text;		
	}
	
	public int peek() {
		return -1;
	}
}
