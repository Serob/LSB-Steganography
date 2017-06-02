package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class  FileUtils {
	
	public static byte[] fileToByteArray(File f){
		byte[] buf = new byte[((Long)f.length()).intValue()];
		try(FileInputStream fin = new FileInputStream(f)) {
			fin.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf;
	}
	
	public static void byteArrayToFile(String fullFilePath, byte[] arr){
		//fullFilePath - with name
		try (FileOutputStream fout = new FileOutputStream(fullFilePath)) {
				fout.write(arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
