package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class  FileUtils {
	
	public static byte[] fileToByteArray(File f){
		byte[] buf = new byte[((Long)f.length()).intValue()];
		try {
			FileInputStream fin = new FileInputStream(f);
			try {
				fin.read(buf);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			finally{
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return buf;
	}
	
	public static void byteArrayToFile(String fullFilePath, byte[] arr){
		//fullFilePath - with name
		try {
			FileOutputStream fout = new FileOutputStream(fullFilePath);
			try {
				fout.write(arr);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			finally {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param f - {@link File}, which path must be returned
	 * @return The path of file (without file's name).
	 * <pre>
	 * <b>For example:</b> File's path is 'C:\system32\run.dll'
	 * 	then return value will be 'C:\system32'
	 * </pre> 
	 */
	public static String getFilePathOnly(File f){
		String fullPath = f.getPath();
		int lastIndex = fullPath.lastIndexOf("//");
		//nafsyaki
		if (lastIndex == -1){
			lastIndex = fullPath.lastIndexOf("\\");
		}
		if(lastIndex == -1){
			return "";
		} else{
			return fullPath.substring(0, lastIndex + 1);
		}
	}
}
