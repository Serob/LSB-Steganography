package com.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ImageFilter extends FileFilter{

	@Override
	public boolean accept(File f) {
		 if (f.isDirectory()) {
	            return true;
	     }

        String extension = ImageUtils.getExtension(f);
        if (extension != null) {
        	 if (extension.equals(ImageUtils.BMP)){
        		 return true;
        	 } else{
        		 return false;
        	 }
        }
		return false;
	}

	@Override
	public String getDescription() {
		
		return "*.bmp";
	}

}
