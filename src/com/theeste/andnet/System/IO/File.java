package com.theeste.andnet.System.IO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public final class File {

	private File() {
		throw new AssertionError();
	}
	
	public static FileStream openRead(String path) throws IOException, FileNotFoundException {
		return File.open(path, FileMode.Open, FileAccess.Read);
	}
	
	public static FileStream openWrite(String path) throws FileNotFoundException, IOException {
		return File.open(path, FileMode.Open, FileAccess.Write);
	}
	
	public static FileStream open(String path, FileMode mode) throws IOException {
		return File.open(path, mode, FileAccess.ReadWrite);
	}
	
	public static FileStream open(String path, FileMode mode, FileAccess access) throws IOException, FileNotFoundException {
		
		java.io.File file = new java.io.File(path);
		
		if (file.exists()) {
			if (file.isFile() == false) {
				throw new IOException("Path does not point to a file");
			}
		}
		
		FileStream fs = null;
		
		switch (mode) {
			case CreateNew:
				if (file.exists()) throw new IOException("File already exists");
			case Create:
				file.delete(); // Delete the file if it already exists
				if (file.exists()) throw new IOException("Could not overwrite existing file"); // File wasn't deleted
			default:
			case OpenOrCreate:
				file.createNewFile();
			case Open:
				if (file.exists() == false) throw new FileNotFoundException();
				fs = new FileStream(file, access);
				break;
		}
		
		return fs;
	}
	
	public static void copy(String sourceFileName, String destFileName) throws IOException, FileNotFoundException {
		File.copy(sourceFileName, destFileName, false);
	}
	
	public static void copy(String sourceFileName, String destFileName, boolean overwrite) throws IOException, FileNotFoundException {
		
		if (File.exists(sourceFileName) == false) {
			throw new FileNotFoundException("Source file does not exist");
		}

		if (overwrite == false && File.exists(destFileName))
			throw new IOException("Destination file exists and overwrite is set to false");
		
		Stream sourceFileStream = File.open(sourceFileName, FileMode.Open, FileAccess.Read);	
		
		Stream destFileStream = File.open(destFileName, FileMode.Create, FileAccess.Write);
		
		int byteCount = 0;
		byte[] buffer = new byte[1024];
		while ((byteCount = sourceFileStream.read(buffer )) > 0) {
			destFileStream.write(buffer, 0, byteCount);
		}
		
		destFileStream.close();
		sourceFileStream.close();
	}
	
	public static void Move(String sourceFileName, String destFileName) throws FileNotFoundException, IOException {
		File.copy(sourceFileName, destFileName);
		File.delete(sourceFileName);
	}
	
	private static boolean delete(String path) {
		return new java.io.File(path).delete();
	}

	public static boolean exists(String path) {
		return new java.io.File(path).exists();
	}
	
	public static byte[] readAllBytes(String path) throws FileNotFoundException, IOException {
		
		FileStream fs = File.open(path, FileMode.Open, FileAccess.Read);
		
		MemoryStream ms = new MemoryStream();
		
		int byteCount = 0;
		byte[] buffer = new byte[1024];
		while ((byteCount = fs.read(buffer)) > 0) {
			ms.write(buffer, 0, byteCount);
		}

		fs.close();
		
		return ms.toArray();
	}
	
	public static String readAllText(String path) throws FileNotFoundException, IOException {
		byte[] fileData = File.readAllBytes(path);
		return new String(fileData, 0, fileData.length);
	}
	
	public static String[] readAllLines(String path) throws FileNotFoundException, IOException {
		ArrayList<String> lines = new ArrayList<String>();
		
		String fileText = File.readAllText(path);
		
		boolean foundCarriageReturn = false;
		String line = "";
		for (int i = 0, length = fileText.length(); i < length; i++) {
			
			char c = fileText.charAt(i);
			
			if (c == '\r') {				
				foundCarriageReturn = true;
				continue;
			} else if (c == '\n' || foundCarriageReturn) {
				
				lines.add(line);
				line = "";
				foundCarriageReturn = false;
				
				if (c == '\n')
					continue;
			}

			line += c;
		}
		
		return (String[]) lines.toArray();
	}
	
	public static void writeAllBytes(String path, byte[] bytes) throws FileNotFoundException, IOException {
		FileStream fs = File.open(path, FileMode.Create, FileAccess.Write);
		
		fs.write(bytes);
		
		fs.close();
	}
	
	public static void writeAllText(String path, String contents) throws FileNotFoundException, IOException {
		File.writeAllBytes(path, contents.getBytes());
	}
	
	public static void writeAllLines(String path, String[] contents) throws FileNotFoundException, IOException {
		String fullText = "";
		for (String line : contents) {
			fullText += line + "\r\n";
		}
		File.writeAllText(path, fullText);
	}
}
