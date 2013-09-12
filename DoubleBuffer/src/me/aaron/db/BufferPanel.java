package me.aaron.db;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

public class BufferPanel extends JPanel
{

	private static final long serialVersionUID = 4098917684220708733L;
	private Graphics bufferGraphics;
	private Graphics2D g2;
	private BufferedImage offscreen;
	private int xPos, yPos;
	private int xInd = 1, yInd = 1;
	private int xArr[] = new int[500];
	private int yArr[] = new int[500];
	
	
	public BufferPanel()
	{
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) 
			{
				if(Math.abs(arg0.getX() - xPos) == 3 || Math.abs(arg0.getY() - yPos) == 10)
				{
					xArr[xInd] = arg0.getX();
					yArr[yInd] = arg0.getY();
					
					xInd++;
					yInd++;
				}
				xPos = arg0.getX();
				yPos = arg0.getY();
				repaint();
			}
			@Override
			public void mouseDragged(MouseEvent arg0) 
			{
				if(Math.abs(arg0.getX() - xPos) == 3 || Math.abs(arg0.getY() - yPos) == 10)
				{
					xArr[xInd] = arg0.getX();
					yArr[yInd] = arg0.getY();
					
					xInd++;
					yInd++;
				}
				
				xPos = arg0.getX();
				yPos = arg0.getY();
				repaint();
			}
		});
		
		this.setBounds(0, 0, 700, 700);
		this.setBackground(Color.black);
		offscreen = new BufferedImage(700, 700, BufferedImage.TYPE_INT_ARGB);
		bufferGraphics = offscreen.getGraphics();
		g2 = (Graphics2D) bufferGraphics;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(3));
		
		xArr[0] = 100;
		yArr[0] = 100;

	}
	
	public void paint(Graphics g)
	{
		
		bufferGraphics.clearRect(0, 0, 700, 700);
		bufferGraphics.drawRect(0, 0, 700, 700);
		
		for(int i = 0; i < xArr.length; i++)
		{
			bufferGraphics.drawLine(xPos, yPos, xArr[i], yArr[i]);
		}
		
		bufferGraphics.drawString("X: " + xPos + " Y: " + yPos, 350, 600);
		
		g.drawImage(offscreen, 0, 0, this);
	}
	
	public void update(Graphics g)
	{
		paint(g);
	}

}
