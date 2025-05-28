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

package library;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import application.Console;
import application.Shutter;

public class EXIFTOOL extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static String exifDate;
public static String exifHours;
public static String creationDate;
public static String creationHours;
public static String exifWidth;
public static String exifHeight;
private static boolean scanOrientation = true;
private static Boolean horizontal = true;

	public static void run(final String cmd) {
				
		error = false;
		scanOrientation = true;
		horizontal = true;
		exifDate = "0000:00:00";
		exifHours = "00:00:00";
		creationDate = "0000:00:00";
		creationHours = "00:00:00";
		exifWidth = "";
		exifHeight = "";
		
		if (cmd != '"' + "" + '"')
		{
			Console.consoleEXIFTOOL.append(System.lineSeparator());
			Console.consoleEXIFTOOL.append(Shutter.language.getProperty("command") + " " + cmd);
		}
			
		//Watermark scaling
		FFPROBE.previousImageWidth = FFPROBE.imageWidth;
		 
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				
				try {
					
					String PathToEXIFTOOL;
					ProcessBuilder processEXIFTOOL;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToEXIFTOOL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToEXIFTOOL = PathToEXIFTOOL.substring(1,PathToEXIFTOOL.length()-1);
						PathToEXIFTOOL = '"' + PathToEXIFTOOL.substring(0,(int) (PathToEXIFTOOL.lastIndexOf("/"))).replace("%20", " ")  + "/Library/exiftool.exe" + '"';
						processEXIFTOOL = new ProcessBuilder(PathToEXIFTOOL + " " + cmd);
					}
					else
					{
						PathToEXIFTOOL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToEXIFTOOL = PathToEXIFTOOL.substring(0,PathToEXIFTOOL.length()-1);
						PathToEXIFTOOL = PathToEXIFTOOL.substring(0,(int) (PathToEXIFTOOL.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/exiftool";
						processEXIFTOOL = new ProcessBuilder("/bin/bash", "-c" , PathToEXIFTOOL + " " + cmd);
					}
											
					isRunning = true;	
					Process process = processEXIFTOOL.start();
					
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        
					String line;
					
					if (cmd != '"' + "" + '"')
					{
						Console.consoleEXIFTOOL.append(System.lineSeparator());
					}
					
					//Analyse des données	
					while ((line = br.readLine()) != null)
					{						
						  Console.consoleEXIFTOOL.append(line + System.lineSeparator());		
					    
						  if (line.contains("Error"))
						  {
							  error = true;							  
						  }
						  
						  if (line.contains("Date/Time Original"))
						  {
							  String l = line.substring(line.indexOf(":") + 2);
							  String f[] = l.split(" ");
							  exifDate = f[0]; //2018:04:06
							  exifHours = f[1]; //12:30:00
						  }
						  
						  if (line.contains("Create Date") && line.contains("+") == false)
						  {								  													  
							  String l = line.substring(line.indexOf(":") + 2);							 
							  String f[] = l.split(" ");								  
							  creationDate = f[0].toString(); //2018:04:06
							  
							  String time[]	= f[1].split(":"); //12:30:00
							  creationHours = time[0] + ":" + time[1] + ":" + time[2];
						  }	
						  
						  if (line.contains("Image Width"))
							  exifWidth = line.substring(line.indexOf(":") + 2);
						  
						  if (line.contains("Image Height"))
							  exifHeight = line.substring(line.indexOf(":") + 2);	
						  
						  if (line.contains("Orientation") && scanOrientation)
						  {
							  if (line.substring(line.indexOf(":") + 2).contains("Horizontal") == false && line.substring(line.indexOf(":") + 2).contains("Rotate 180") == false)
							  {
								  horizontal = false;
							  }
							  
							  scanOrientation = false;
						  }
					}				
					process.waitFor();
					
					//Si il n'y a pas d'exif on lit la date de création système
					if (exifDate == "" && exifHours == "")
					{
						exifDate = creationDate;
						exifHours = creationHours;
					}
					
					//On inject la résolution dans FFPROBE
					if (exifWidth != "" && exifHeight != "")
					{
						if (horizontal)
						{
							FFPROBE.imageResolution = exifWidth + "x" + exifHeight; 
							FFPROBE.imageWidth = Integer.parseInt(exifWidth);
							FFPROBE.imageHeight = Integer.parseInt(exifHeight);							
						}
						else
						{
							FFPROBE.imageResolution = exifHeight + "x" + exifWidth; 
					    	FFPROBE.imageWidth = Integer.parseInt(exifHeight);
					    	FFPROBE.imageHeight = Integer.parseInt(exifWidth);
						}
						
						FFPROBE.imageRatio = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;
					}
											
					} catch (Exception e) {
						error = true;
					} finally {
						isRunning = false;
					}
						
			}				
		});		
		runProcess.start();
	}
}