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

import application.Shutter;
import application.VideoPlayer;
import library.FFPROBE;

public class Colorimetry extends Shutter {

	public static int allR = 0;
    public static int allG = 0;
    public static int allB = 0;
    public static int highR = 0;
    public static int highG = 0;
    public static int highB = 0;
    public static int mediumR = 0;
    public static int mediumG = 0;
    public static int mediumB = 0;
    public static int lowR = 0;
    public static int lowG = 0;
    public static int lowB = 0;
    public static String balanceAll = "";
    public static String balanceHigh = "";
    public static String balanceMedium = "";
    public static String balanceLow = "";
    public static int vibranceValue = 0;
    public static int vibranceR = 0;
    public static int vibranceG = 0;
    public static int vibranceB = 0;
	
	public static String setColor(String filterComplex) {
				
		if (grpColorimetry.isVisible() && Shutter.caseEnableColorimetry.isSelected() && setEQ(true).equals("") == false)
		{			
			if (filterComplex != "") filterComplex += ",";
			
			//Important
			setEQ(true);
						
			filterComplex += colorimetryValues;	
		}

		return filterComplex;
	}
	
	public static String setLevels(String filterComplex) {
		
		if ((grpColorimetry.isVisible() || VideoPlayer.fullscreenPlayer) && caseLevels.isSelected())
		{			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "scale=in_range=" + comboInLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full") + ":out_range=" + comboOutLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full");		
		}

		return filterComplex;
	}
	
	public static String setColormatrix(String filterComplex) {
		
		if ((grpColorimetry.isVisible() || VideoPlayer.fullscreenPlayer) && caseColormatrix.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
			
			if (comboInColormatrix.getSelectedItem().equals("HDR"))
			{		
				String pathToLuts;
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				{
					pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
					pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/HDR-to-SDR.cube";
				}
				else
					pathToLuts = "LUTs/HDR-to-SDR.cube";

				filterComplex += "lut3d=file=" + pathToLuts;	
			}
			else
				filterComplex += "colorspace=iall=" + Shutter.comboInColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("601", "601-6-625") + ":all=" + Shutter.comboOutColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("601", "601-6-625");
		}

		return filterComplex;
	}
		
	public static String setColorspace() {
		
		if (grpColorimetry.isVisible() && caseColorspace.isSelected())
		{
			if (comboColorspace.getSelectedItem().toString().contains("Rec. 709"))
			{
				return " -color_primaries bt709 -color_trc bt709 -colorspace bt709";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 PQ"))
			{
				return " -color_primaries bt2020 -color_trc smpte2084 -colorspace bt2020nc";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 HLG"))
			{
				return " -color_primaries bt2020 -color_trc arib-std-b67 -colorspace bt2020nc";
			}
		}
		
		return "";
	}
	
	public static String setMetadata(String filterComplex) {
		
		if (grpColorimetry.isVisible() && caseColorspace.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
						
			if (comboColorspace.getSelectedItem().toString().contains("Rec. 709"))
			{
				filterComplex += "setparams=color_primaries=bt709:color_trc=bt709:colorspace=bt709";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 PQ"))
			{
				filterComplex += "setparams=color_primaries=bt2020:color_trc=smpte2084:colorspace=bt2020nc";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 HLG"))
			{
				filterComplex += "setparams=color_primaries=bt2020:color_trc=arib-std-b67:colorspace=bt2020nc";
			}
		}
		
		return filterComplex;
	}

	public static String setInputCodec(String extension) {
		
		if (extension.toLowerCase().equals(".exr"))
		{
			return " -apply_trc iec61966_2_1";
		}
		else if (FFPROBE.videoCodec != null)
		{
			//Preserve the alpha channel
			if (FFPROBE.videoCodec.contains("vp9") && FFPROBE.hasAlpha)
			{
				return " -c:v libvpx-vp9";
			}
			else if (FFPROBE.videoCodec.contains("vp8"))
			{
				return " -c:v libvpx";
			}
		}
		
		return "";	
	}
	
	public static String setLUT(String filterComplex) {
		
		if ((grpColorimetry.isVisible() || VideoPlayer.fullscreenPlayer) && caseLUTs.isSelected())
		{			
			String pathToLuts;
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			{
				pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
				pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/";
			}
			else
				pathToLuts = "LUTs/";
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "lut3d=file=" + pathToLuts + Shutter.comboLUTs.getSelectedItem().toString();	
		}
		
		return filterComplex;
	}

	public static String setGrain(String eq) {
	
		if (Shutter.sliderGrain.getValue() != 0)
		{		
			if (eq != "")
				eq += ",";

			if (Shutter.sliderGrain.getValue() > 0)
				eq += "unsharp=la=" + (float) Shutter.sliderGrain.getValue() / 50;
			else
				eq += "bm3d=sigma=" + (float) (0 - Shutter.sliderGrain.getValue()); 
		}
		
		return eq;
	}
		
	public static String setAngle(String eq) {

		if (Shutter.sliderAngle.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			float angle;
			if (Shutter.sliderAngle.getValue() > 0)
				angle = (float) ((float) ((float) Shutter.sliderAngle.getValue() / 10) * Math.PI) / 180;
			else
				angle = (float) ((float) (0 - (float) Shutter.sliderAngle.getValue() / 10) * Math.PI) / 180;
			
			float ratio = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;
			float h = (float) ( (float) FFPROBE.imageHeight / ( ( (float) ratio * Math.sin(angle) ) + Math.cos(angle) ) );
			float w = (float) h * ratio;
			if (ratio < 1)
			{
				ratio = (float) FFPROBE.imageHeight / FFPROBE.imageWidth;
				w = (float) ( (float) FFPROBE.imageWidth / ( ( (float) ratio * Math.sin(angle) ) + Math.cos(angle) ) );
				h = (float) w * ratio;
			}
			
			w = (float) (2 - ((float) FFPROBE.imageWidth / w));
			h = (float) (2 - ((float) FFPROBE.imageHeight / h));			
						
			if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
			{
				eq += "rotate=" + ((float) Shutter.sliderAngle.getValue() / 10) + "*PI/180:ow=iw*" + w + ":oh=ih*" + h + ",scale=" + FFPROBE.imageWidth + ":" + FFPROBE.imageHeight; 
			}
			else
				eq += "rotate=" + ((float) Shutter.sliderAngle.getValue() / 10) + "*PI/180:ow=iw*" + w + ":oh=ih*" + h; 
		}
		
		return eq;
	}
	
	public static String setZoom(String eq, boolean finalEQ) {

		if (sliderZoom.getValue() != 0)
		{			
			if (eq != "" && finalEQ)
				eq += ",";
			
			float zoomValue = (float) 1 - ((float) sliderZoom.getValue() / 2 / 100);
			
			eq += "crop=iw*" + zoomValue + ":ih*" + zoomValue; 
		}
		
		return eq;
	}
	
	public static String setVignette(String eq) {
		
		if (Shutter.sliderVignette.getValue() != 0)
		{		
			if (eq != "")
				eq += ",";

			if (Shutter.sliderVignette.getValue() > 0)
				eq += "vignette=PI/" + (float) (100 - Shutter.sliderVignette.getValue()) / 5 + ":mode=backward"; 
			else
				eq += "vignette=PI/" + (float) (100 + Shutter.sliderVignette.getValue()) / 5;				
		}
		
		return eq;
	}

	public static String setVibrance(String eq) {
		
		if (vibranceValue != 0)
		{
			if (eq != "")
				eq += ",";			

			eq += "vibrance=" + (float) (vibranceValue) / 50 + ":rbal=" + (float) (100 + vibranceR) / 100 + ":gbal=" + (float) (100 + vibranceG) / 100 + ":bbal=" + (float) (100 + vibranceB) / 100;
		}
		
		return eq;
	}
	
	public static String setSaturation(String eq) {
		
		if (Shutter.sliderSaturation.getValue() != 0)
		{
			if (eq != "")
				eq += ",";			
			
			eq += "eq=saturation=" + ((float) (Shutter.sliderSaturation.getValue() + 100) / 100);
		}
		
		return eq;
	}

	public static String setBalance(String eq) {		
		
		float r = (float) Shutter.sliderRED.getValue() / 400;
		float g = (float) Shutter.sliderGREEN.getValue() / 400;
		float b = (float) Shutter.sliderBLUE.getValue() / 400;
		
		if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
			balanceAll = "rs="+r+":gs="+g+":bs="+b+":rm="+r+":gm="+g+":bm="+b+":rh="+r+":gh="+g+":bh="+b;			
			
		if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
			balanceLow = "rs="+r+":gs="+g+":bs="+b;	
		
		else if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
			balanceMedium = "rm="+r+":gm="+g+":bm="+b;	
		
		else if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
			balanceHigh = "rh="+r+":gh="+g+":bh="+b;
		
		if (balanceAll != "" && balanceAll.equals("rs=0.0:gs=0.0:bs=0.0:rm=0.0:gm=0.0:bm=0.0:rh=0.0:gh=0.0:bh=0.0") == false)
		{
			if (eq != "")
				eq += ",";
			
			eq += "colorbalance=" + balanceAll;
		}
		
		//Permet de compléter tout l'eq à chaque fois
		if (balanceLow != "" || balanceMedium != "" || balanceHigh != "")
		{
			if (balanceAll != "" && balanceAll.equals("rs=0.0:gs=0.0:bs=0.0:rm=0.0:gm=0.0:bm=0.0:rh=0.0:gh=0.0:bh=0.0") == false)
				eq += ",colorbalance=";
			else if (eq != "")
				eq += ",colorbalance=";
			else
				eq = "colorbalance=";
			
			if (balanceLow == "")
				balanceLow = "rs=0:gs=0:bs=0";
			
			if (balanceMedium == "")
				balanceMedium = "rm=0:gm=0:bm=0";
			
			if (balanceHigh == "")
				balanceHigh = "rh=0:gh=0:bh=0";
			
			eq += balanceLow + ":" + balanceMedium + ":" + balanceHigh;
		}


		return eq;
	}

	public static String setContrast(String eq) {
		
		if (Shutter.sliderContrast.getValue() != 0)
		{
			if (eq != "")
				eq += ",";

			eq += "eq=contrast=" + (1 + (float) Shutter.sliderContrast.getValue() / 100); 
		}
		
		return eq;
	}
	
	public static String setWB(String eq) {

		if (Shutter.sliderBalance.getValue() != 6500)
		{
			if (eq != "")
				eq += ",";

			eq += "colortemperature=" + Shutter.sliderBalance.getValue() + ":pl=1"; 
		}
		
		return eq;
	}
	
	public static String setHUE(String eq) {

		if (Shutter.sliderHUE.getValue() != 0)
		{
			if (eq != "")
				eq += ",";

			eq += "hue=h=" + (0 - Shutter.sliderHUE.getValue()); 
		}
		
		return eq;
	}
	
	public static String setWhite(String eq) {

		if (Shutter.sliderWhite.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
				
			if (Shutter.sliderWhite.getValue() > 0)
			{
				float value = 1 - (float) Shutter.sliderWhite.getValue() / 200;				
				eq += "colorlevels=rimax=" + value + ":gimax=" + value + ":bimax=" + value; 
			}
			else
			{
				float value = 1 + (float) Shutter.sliderWhite.getValue() / 200;
				eq += "colorlevels=romax=" + value + ":gomax=" + value + ":bomax=" + value; 
			}
		}
		
		return eq;
	}
	
	public static String setBlack(String eq) {

		if (Shutter.sliderBlack.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
				
			if (Shutter.sliderBlack.getValue() > 0)
			{
				float value = (float) Shutter.sliderBlack.getValue() / 200;				
				eq += "colorlevels=romin=" + value + ":gomin=" + value + ":bomin=" + value; 				 
			}
			else
			{
				float value = 0 - (float) Shutter.sliderBlack.getValue() / 200;
				eq += "colorlevels=rimin=" + value + ":gimin=" + value + ":bimin=" + value;
			}
				
		}
		
		return eq;
	}

	public static String setShadows(String eq) {

		if (Shutter.sliderShadows.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
										
			if (Shutter.sliderShadows.getValue() > 0)
				eq += "curves=master=" + "'" + "0/0 0.25/" + (0.25f - (float) (0 - (float) Shutter.sliderShadows.getValue() / 500)) + " 0.5/0.5 0.75/0.75 0.875/0.875 1/1'"; 
			else
				eq += "curves=master=" + "'0/0 " + (0.25f - (float) Shutter.sliderShadows.getValue() / 500) + "/0.25 0.5/0.5 0.625/0.625 0.75/0.75 0.875/0.875 1/1" + "'"; 
		}
		
		return eq;
	}
	
	public static String setMediums(String eq) {

		if (Shutter.sliderMediums.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
					
			if (Shutter.sliderMediums.getValue() > 0)
				eq += "curves=master=" + "'" + "0/0 " + (0.5 - (float) Shutter.sliderMediums.getValue() / 400) + "/" + (0.5 + (float) Shutter.sliderMediums.getValue() / 400) + " 1/1" + "'"; 										
			else
				eq += "curves=master=" + "'" + "0/0 " + (0.5 + (float) (0 - (float) Shutter.sliderMediums.getValue() / 400)) + "/" + (0.5 - (float) (0 - (float) Shutter.sliderMediums.getValue() / 400)) + " 1/1" + "'"; 
		}
		
		return eq;
	}

	public static String setHighlights(String eq) {

		if (Shutter.sliderHighlights.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			if (Shutter.sliderHighlights.getValue() > 0)
				eq += "curves=master=" + "'" + "0/0 0.125/0.125 0.25/0.25 0.375/0.375 0.5/0.5 " + (0.75f - (float) Shutter.sliderHighlights.getValue() / 500) + "/0.75 1/1" + "'"; 										
			else
				eq += "curves=master=" + "'" + "0/0 0.125/0.125 0.25/0.25 0.375/0.375 0.5/0.5 0.75/" + (0.75f - (float) (0 - (float) Shutter.sliderHighlights.getValue() / 500)) + " 1/1'"; 
		}
		
		return eq;
	}

	public static String setExposure(String eq) {

		if (Shutter.sliderExposure.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			eq += "exposure=" + (float) ((float) Shutter.sliderExposure.getValue() / 100) * 3; 
		}
		
		return eq;
	}
	
	public static String setGamma(String eq) {		

		if (Shutter.sliderGamma.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			eq += "eq=gamma=" + (1 + (float) Shutter.sliderGamma.getValue() / 100); 
		}
		
		return eq;
	}
	
	public static String setEQ(boolean finalEQ) {
		
		String eq = "";
		
		//Highlights 
		eq = setHighlights(eq);
		
		//Mediums 
		eq = setMediums(eq);
		
		//Shadows 
		eq = setShadows(eq);
		
		//Exposure
		eq = setExposure(eq);
		
		//Gamma
		eq = setGamma(eq);
		
		//Contrast
		eq = setContrast(eq);
		
		//White
		eq = setWhite(eq);

		//Black
		eq = setBlack(eq);
				
		//White Balance 
		eq = setWB(eq);
		
		//Hue
		eq = setHUE(eq);
				
		//Balance
		eq = setBalance(eq);
		
		//Saturation
		eq = setSaturation(eq);
		
		//Vibrance
		eq = setVibrance(eq);
				
		//Grain
		eq = setGrain(eq);
				
		//Angle
		eq = setAngle(eq);
		
		//Vignette
		eq = setVignette(eq);
		
		//FinalEQ
		Shutter.colorimetryValues = eq.replace("\"", "'");
		
		return eq;
	}
	

}
