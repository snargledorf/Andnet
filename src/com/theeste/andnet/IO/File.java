package com.theeste.andnet.IO;

import java.io.FileNotFoundException;

public final class File {

	private File() {
		throw new AssertionError();
	}
	
	public static FileStream Open(String filename) throws FileNotFoundException {
		
		java.io.File file = new java.io.File(filename);
		
		return new FileStream(file);
	}
}
