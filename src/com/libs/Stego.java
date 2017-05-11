package com.libs;

import java.io.File;

import com.exceptions.ContainerSizeException;
import com.utils.FileUtils;


public class Stego {
	
	private static final int  ENTRY_POINT = 0x36; 
	private static int containerIndex = ENTRY_POINT;		//shows in which byte we are 
	
	/**
	 * Hides data of one file into another
	 * @param container - File, in which must be hidden information
	 * @param secret - File, which is must be hidden
	 * @return Byte array of full container file
	 */
	public static byte[] hide(File container, File secret) throws ContainerSizeException {
		byte[] containerBytes = FileUtils.fileToByteArray(container);
		byte[] secretBytes = FileUtils.fileToByteArray(secret);
		final int secretFileLength = (int)secret.length();

		containerIndex = ENTRY_POINT;
		
		byte[] secretsName = secret.getName().getBytes();
		int secretsNameLength  = secretsName.length;
		//fayln ira anunov pti texavorvi 
		//11 byte _ chap@ grelu hamar(mi int-@) => 2*11 _ fayli chap@ + anuni chap@, 
		//(secretsNameLength*8)/3 +1 _ Te file-i anunn inchqan tex kzbaxacni container-um , +1-n el nafsyaki verji biteri hamar(hnaravor korox byte)
		if((0.375*container.length() - 2*11 - 54  - ((secretsNameLength*8)/3 +1)) >= secret.length()){

			//write name size
			hideInt(containerBytes, secretsNameLength);
			
			//write file size
			hideInt(containerBytes, secretFileLength);
			
			//write name
			hideBytes(containerBytes, secretsName);
			
			//write file
			hideBytes(containerBytes, secretBytes);

		} else{
			throw new ContainerSizeException("Secret file\'s size must be " + ((int)(0.375*container.length()) - 2*11 - 54 - ((secretsNameLength*8)/3 + 1)) + " bytes maximum");
		}
		
		return containerBytes;
	 
	}
	
	/**
	 * Extracts data from container
	 * @param container - Full container, with the secret data (file)
	 * @param name - Return value, which keeps the name of hidden file
	 * @return byte array of hidden file
	 */
	public static byte[] extract (File container, StringBuilder name) {
		byte[] containerBytes = FileUtils.fileToByteArray(container);
		name.delete(0, name.length());
		containerIndex = ENTRY_POINT;
		int secretsNameLength;
		int secretFileLength;
		
		secretsNameLength = extractInt(containerBytes);
		secretFileLength = extractInt(containerBytes);

		
		name.append(new String(extractBytes(containerBytes, secretsNameLength)));

		return extractBytes(containerBytes, secretFileLength);
	}
	
	private static void hideBytes(byte[] containerBytes, byte[] hiddenBytes){
		long mask;
		int containerShiftSize = 2;
		byte currentBit;
		byte secretMask = 0x1;
		
		for(int i = 0; i < hiddenBytes.length; i++){
			mask = 0;
			mask ++;
			mask <<= 7;
			
			//running on bits
			for(int j = 0; j < 8; j ++, containerShiftSize--){
				if(containerShiftSize < 0){
					containerShiftSize = 2;
					containerIndex ++;
				}
				currentBit = (byte)((mask & hiddenBytes[i])>>(8-j-1));
				mask >>= 1;
				
				containerBytes[containerIndex] &= ~(secretMask<<containerShiftSize);
				containerBytes[containerIndex] |= currentBit<<containerShiftSize;
			}
		}
		//Apahovoum a en pah@, vor yete anvan yerkarutyun@ bajanvum a 3-i mi hat avel byte pahenq chisht extract-i hamar
		containerIndex ++;
	}
	
	
	private static void hideInt(byte[] containerBytes, int hiddenInt){
		long mask = 0;
		mask ++;
		mask <<= 31;
		
		int containerShiftSize = 2;
		byte currentBit;
		byte secretMask = 0x1;

		int secretShiftSize = 31;
		while (secretShiftSize > 0) {
			for(containerShiftSize = 2; containerShiftSize >= 0 && secretShiftSize >= 0; containerShiftSize--){
				currentBit = (byte)((mask & hiddenInt)>>secretShiftSize--);
				mask >>= 1;

				containerBytes[containerIndex] &= ~(secretMask<<containerShiftSize);
				containerBytes[containerIndex] |= currentBit<<containerShiftSize;
			}
			containerIndex ++;
		}
	}
	
	private static byte[] extractBytes(byte[] containerBytes, int size){

		int containerShiftSize = 2;
		int currentBit;
		byte secretMask = 0x1;
		byte[] secretBytes;
		
		try{
			secretBytes = new byte[size];
		} catch (OutOfMemoryError e){
			throw (e);
		}
		
		for(int i = 0; i < size; i++){
		
			//running on bits
			for(int j = 0; j < 8; j ++, containerShiftSize--){
				if(containerShiftSize < 0){
					containerShiftSize = 2;
					containerIndex ++;
				}
				currentBit = ((containerBytes[containerIndex] & secretMask<<containerShiftSize)>>containerShiftSize);
				
				currentBit <<= (8-j-1);
				secretBytes[i] &= ~currentBit;
				secretBytes[i] |= currentBit;
				
			}
		}
		containerIndex ++;
		return secretBytes;
	}
	
	private static int extractInt(byte[] containerBytes){
		int containerShiftSize = 2;
		int currentBit;
		byte secretMask = 0x1;
		int secretInt = 0;
		
		for(int i = 0; i < 32; i++, containerShiftSize--){
			if(containerShiftSize < 0){
				containerShiftSize = 2;
				containerIndex ++;
			}
			currentBit = ((containerBytes[containerIndex] & secretMask<<containerShiftSize)>>containerShiftSize);
			
			currentBit <<= (32-i-1);
			secretInt &= ~currentBit;
			secretInt |= currentBit;
		}
		containerIndex ++;
		return secretInt;
	}
}
