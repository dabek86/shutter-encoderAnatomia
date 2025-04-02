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

package application;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JProgressBar;
import javax.swing.JDialog;


@SuppressWarnings("serial")
public class ReducedWindow extends JDialog {

	public static JDialog frame;
	private JPanel panel;
	private JLabel lblEnCours;
	private JLabel pourcentage;
	private JLabel iconImage;
	private JProgressBar progressBar;
	private JLabel lblTempsRestant;
	private ImageIcon icon;
	private boolean drag = false;

	private static int MousePositionY;
	
	public ReducedWindow() {
		
		frame = new JDialog();
		frame.setContentPane(new MiniWindowBackground());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	
		frame.setTitle("Shutter Encoder");
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(290, 94);
		frame.setResizable(false);		
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			frame.setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
			//frame.setShape(new AntiAliasedRoundRectangle(0, 0, 320, 94, 30, 30));
			frame.setAlwaysOnTop(true);
			frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		}
		
		GraphicsConfiguration config = frame.getGraphicsConfiguration();
		GraphicsDevice myScreen = config.getDevice();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] allScreens = env.getScreenDevices();
		int screenIndex = -1;
		for (int i = 0; i < allScreens.length; i++) {
		    if (allScreens[i].equals(myScreen))
		    {
		    	screenIndex = i;
		        break;
		    }
		}
		
		if (System.getProperty("os.name").contains("Windows"))
			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDefaultConfiguration().getBounds().width - frame.getSize().width, Shutter.MiniWindowY);	
		else
			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDisplayMode().getWidth() - frame.getSize().width, Shutter.MiniWindowY);	
		
		panel = new JPanel();	
		panel.setLayout(null);
		panel.setBounds(6, 3, 88, 88);
		panel.setBackground(new Color(30,30,35));
		frame.getContentPane().add(panel);
		
		pourcentage = new JLabel("0%");
		pourcentage.setHorizontalAlignment(SwingConstants.RIGHT);
		pourcentage.setBounds(29, 36, 33, 16);
		pourcentage.setVisible(false);
		pourcentage.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		pourcentage.setBackground(new Color(0,0,0));
		panel.add(pourcentage);
		
		ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png"));
		icon = new ImageIcon(imageIcon.getImage().getScaledInstance(panel.getSize().width - 5, panel.getSize().height - 5, Image.SCALE_AREA_AVERAGING));			
        iconImage = new JLabel(icon);	
        iconImage.setBounds(0, 0, 88,88);
		panel.add(iconImage);		
				
		lblEnCours = new JLabel(Shutter.language.getProperty("lblEnCours"));
		lblEnCours.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnCours.setForeground(Color.LIGHT_GRAY);
		lblEnCours.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblEnCours.setBounds(100, 6, 178, 16);		
		frame.getContentPane().add(lblEnCours);
		
		progressBar = new JProgressBar();		
		progressBar.setBounds(100, 24, 178, 24);
		frame.getContentPane().add(progressBar);
		
		Shutter.caseRunInBackground.setBounds(100, 53, Shutter.caseRunInBackground.getPreferredSize().width, 16);	
		frame.getContentPane().add(Shutter.caseRunInBackground);
						
		lblTempsRestant = new JLabel(Shutter.language.getProperty("tempsRestant") + " ");
		lblTempsRestant.setVisible(false);
		lblTempsRestant.setHorizontalAlignment(SwingConstants.LEFT);
		lblTempsRestant.setForeground(Utils.themeColor);
		lblTempsRestant.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		lblTempsRestant.setBounds(100, 72, 178, 16);		
		frame.getContentPane().add(lblTempsRestant);
				
		//Right_to_left
		if (Shutter.getLanguage.contains(Locale.of("ar").getDisplayLanguage()))
		{
			//Frame
			for (Component c : frame.getContentPane().getComponents())
			{
				if (c instanceof JPanel)
				{						
					for (Component p : ((JPanel) c).getComponents())
					{
						if (p instanceof JCheckBox)
						{
							p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
						}
					}
				}
			}		
		}
		
		frame.setVisible(true);
				
		//Mise à jour
		TimerTask task = new TimerTask()
		{
			@Override
			public void run() 
			{
				//lblEncodage
				if (Shutter.lblCurrentEncoding.getText().equals(Shutter.language.getProperty("lblEncodageEnCours")) == false)
				{
					lblEnCours.setText(Shutter.lblCurrentEncoding.getText());
					if (Shutter.progressBar1.isIndeterminate() == false)
						pourcentage.setVisible(true);
					else
						pourcentage.setVisible(false);
				}
				else
				{
					pourcentage.setVisible(false);
					lblEnCours.setText(Shutter.language.getProperty("lblEnCours"));
				}
				lblEnCours.setForeground(Shutter.lblCurrentEncoding.getForeground());
											
				//TempsRestant
				if (Shutter.tempsRestant.isVisible())
					lblTempsRestant.setVisible(true);		
				else
					lblTempsRestant.setVisible(false);	
				
				lblTempsRestant.setText(Shutter.tempsRestant.getText());				
				
				//ProgressBar
				if (Shutter.progressBar1.isIndeterminate())
					progressBar.setIndeterminate(true);
				else
				{
					SwingUtilities.invokeLater(new Runnable()
					{
			           @Override
			           public void run() {
			        	   Shutter.progressBar1.setIndeterminate(false);
			           }
					});
				}
				
				progressBar.setMaximum(Shutter.progressBar1.getMaximum());
				progressBar.setValue(Shutter.progressBar1.getValue());
				
				//RotateIcon
				if (Shutter.progressBar1.isIndeterminate() == false && progressBar.getValue() != 0)
					rotateIcon(( (long) progressBar.getValue() * 360) / ( (long) progressBar.getMaximum()));		
				 
				//Pourcentage
				if (Shutter.progressBar1.isIndeterminate() == false && progressBar.getValue() != 0)
					pourcentage.setText(String.valueOf(( (long) progressBar.getValue() * 100) / ( (long) progressBar.getMaximum()) + "%"));
			}	
		};    			
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 100); 		
		
		frame.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
				GraphicsConfiguration config = Shutter.frame.getGraphicsConfiguration();
				GraphicsDevice myScreen = config.getDevice();
				GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice[] allScreens = env.getScreenDevices();
				int screenIndex = -1;
				for (int i = 0; i < allScreens.length; i++) {
				    if (allScreens[i].equals(myScreen))
				    {
				    	screenIndex = i;
				        break;
				    }
				}

				int screenHeight = allScreens[screenIndex].getDisplayMode().getHeight();	
				
				int position;
				if (MouseInfo.getPointerInfo().getLocation().y - MousePositionY < 0)
				{
					position = 0;
					frame.setLocation(frame.getLocation().x, position);	
				}
				else if ( MouseInfo.getPointerInfo().getLocation().y - MousePositionY > screenHeight - frame.getSize().height)
				{
					position = screenHeight - frame.getSize().height;
					frame.setLocation(frame.getLocation().x, position);	
				}
				else if (drag)
				{				
					frame.setLocation(frame.getLocation().x, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);		
				}
							
			}

			@Override
			public void mouseMoved(MouseEvent e) {	
			}
			
		});
		
		frame.addMouseListener(new MouseListener(){

			@SuppressWarnings("static-access")
			@Override
			public void mouseClicked(MouseEvent e) {	
				
				if (drag == false)				 
				{
					frame.setVisible(false);
					
					Shutter.frame.setState(Shutter.frame.NORMAL);
					Shutter.caseRunInBackground.setBounds(6, 64, Shutter.caseRunInBackground.getPreferredSize().width, 23);		
					Shutter.grpProgression.add(Shutter.caseRunInBackground);	
				}
			}

			@Override
			public void mousePressed(MouseEvent down) {
				drag = true;
				MousePositionY = down.getPoint().y;				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				drag = false;
				Shutter.MiniWindowY = frame.getLocation().y;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
								
				if (drag == false && frame.getOpacity() < 1f)
				{	
					do {		
						
						try {			
							
							frame.setOpacity(frame.getOpacity() + 0.01f);
							
							long startTime = System.nanoTime();
							
							//Animate size
							Shutter.animateSections(startTime, true);	
							
						 } catch (Exception e1) {}
							
					} while (frame.getOpacity() < 1f);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {

				if (drag == false)
				{
					do {		
						
						try {		
							
							frame.setOpacity(frame.getOpacity() - 0.01f);
							
							long startTime = System.nanoTime();
							
							//Animate size
							Shutter.animateSections(startTime, true);	
													
						 } catch (Exception e1) {}
							
					} while (frame.getOpacity() > 0.5f);		
				}
			}
			
		});
			
		frame.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				
				do {		
					
					try {		
						
						frame.setOpacity(frame.getOpacity() - 0.01f);
						
						long startTime = System.nanoTime();
						
						//Animate size
						Shutter.animateSections(startTime, true);
						
					 } catch (Exception e1) {}
						
				} while (frame.getOpacity() > 0.5f);
				
			}
			
		});
	}
		
	private void rotateIcon(long l) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        int type = BufferedImage.TRANSLUCENT;
        BufferedImage image = new BufferedImage(h, w, type);
        Graphics2D g2 = image.createGraphics();
        double x = (h - w)/2.0;
        double y = (w - h)/2.0;
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(Math.toRadians(l), w/2.0, h/2.0);
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2.setRenderingHints(qualityHints);
        g2.drawImage(icon.getImage(), at, iconImage);
        g2.dispose();
        iconImage.setIcon(new ImageIcon(image));
    }
	
}

//Background
@SuppressWarnings("serial")
class MiniWindowBackground extends JPanel {
    public void paintComponent(Graphics g){
	  	  RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	  	  qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	  	  Graphics2D g1 = (Graphics2D)g.create();
	  	  g1.setComposite(AlphaComposite.SrcIn.derive(0.0f));
	  	  g1.setRenderingHints(qualityHints);
	  	  g1.setColor(new Color(0,0,0));
	  	  g1.fillRect(0,0,ReducedWindow.frame.getWidth() + 14, ReducedWindow.frame.getHeight() + 7);
  	  
 		  Graphics2D g2 = (Graphics2D)g.create();
 		  g2.setRenderingHints(qualityHints);
 		  g2.setColor(new Color(30,30,35));
 		  g2.fillRoundRect(0, 0,320, 94,30,30);
     }
}