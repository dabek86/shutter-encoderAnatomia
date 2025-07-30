/*******************************************************************************************
* Copyright (C) 2025 PACIFICO PAUL
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
* 
********************************************************************************************/

package settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import application.Shutter;
import library.FFPROBE;

public class Timecode extends Shutter {

	public static String setTimecode(File file) {
		
   		String dropFrame = ":";
        if (isDropFrame())
    	{
        	dropFrame = ";";
    	}
        
   		if (Shutter.caseConform.isSelected())
   		{
   			if (Shutter.comboFPS.getSelectedItem().toString().equals("29,97") || Shutter.comboFPS.getSelectedItem().toString().equals("59,94"))
   			{
   				dropFrame = ";";
   			}
   			else 
   				dropFrame = ":";
   		}

		if (caseGenerateFromDate.isSelected())
		{
			try {		
				
	            BasicFileAttributes attrs = Files.readAttributes(Paths.get(file.toString()), BasicFileAttributes.class);
	            Instant creationTime = attrs.creationTime().toInstant();
	            String date = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault()).format(creationTime);
	            
	            String s[] = date.split(":");
				
				return " -timecode " + '"' + s[0] + ":" + s[1] + ":" + s[2] + dropFrame + "00" + '"';
				
	        } catch (IOException e) {}		
		}		
		else if (caseSetTimecode.isSelected())
		{
			return " -timecode " + '"' + TCset1.getText() + ":" + TCset2.getText() + ":" + TCset3.getText() + dropFrame + TCset4.getText() + '"';
		}
		else if (FFPROBE.timecode1 != "")
		{						
			return " -timecode " + '"' + FFPROBE.timecode1 + ":" + FFPROBE.timecode2 + ":" + FFPROBE.timecode3 + dropFrame + FFPROBE.timecode4 + '"';
		}
		
		return "";
	}
	
	public static boolean isDropFrame() {
		
		if (FFPROBE.dropFrameTC.equals(":") == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f))
     	{
			return true;
     	}
		else
			return false;
	}
	
	public static boolean isNonDropFrame() {
		
		if (FFPROBE.dropFrameTC.equals(":") && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f) || FFPROBE.currentFPS == 23.98f)
     	{
			return true;
     	}
		else
			return false;
	}
	
	public static double setNTSCtimecode(double currentFrame) {
				
		//NTSC framerates => remove a frame to reach round framerate
		if (currentFrame > 0)
		{							
			double currentTime = currentFrame * ((double) 1000 / FFPROBE.currentFPS);
			
			if (FFPROBE.currentFPS == 23.98f)
			{
				currentFrame -= (currentTime * 0.024 / 1000) - 1;
			}
			else if (FFPROBE.currentFPS == 29.97f)
			{
				currentFrame -= (currentTime * 0.03 / 1000) - 1;
			}
			else if (FFPROBE.currentFPS == 59.94f)
			{					
				currentFrame -= (currentTime * 0.06 / 1000) - 1;
			}
		}
		
		return (float) Math.floor(currentFrame);	
	}
	
	public static double getNTSCtimecode(double currentFrame) {
		
		//Allows to set the current seeking values		
		double currentTime = currentFrame * ((double) 1000 / FFPROBE.currentFPS);
			
		if (FFPROBE.currentFPS == 23.98f)
		{
			currentFrame += (currentTime * 0.024 / 1000);
		}
		else if (FFPROBE.currentFPS == 29.97f)
		{
			currentFrame += (currentTime * 0.03 / 1000);
		}
		else if (FFPROBE.currentFPS == 59.94f)
		{
			currentFrame += (currentTime * 0.06 / 1000);
		}
				
		return (float) Math.floor(currentFrame);		
	}
	
}
