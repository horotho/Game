package me.aaron.db;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Entity implements Comparable<Entity>
{
	private Color color;
	private int x, y, moveSpeed, size;
	private boolean removalFlag = false;
	public static ArrayList<Clip> audioClips = new ArrayList<Clip>();
	public static ArrayList<Clip> keepClips = new ArrayList<Clip>();

	public Entity(Color c, int moveSpeed, int x, int y, int size)
	{
		this.color = c;
		this.x = x;
		this.y = y;
		this.moveSpeed = moveSpeed;
		this.size = size;
	}

	public abstract void update(boolean shouldUpdate);

	public ArrayList<Pair> getEdges()
	{
		return getEdges(this.getX(), this.getY(), this.getSize());
	}

	/**
	 * Assumes that the Entity is a square, to make calculation easier.
	 * <p>
	 * 
	 * @param x
	 *            x point of the square object
	 * @param y
	 *            y point of the square object
	 * @param size
	 *            the length/width of the square
	 * @return 
	 * 			  an ArrayList of Pairs containing all the points around the edge
	 *            of the object
	 */
	public ArrayList<Pair> getEdges(int x, int y, int size)
	{
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		pairs.add(new Pair(x, y));

		for (int i = 1; i <= size; i++)
		{
			pairs.add(new Pair(x, y + i));
			pairs.add(new Pair(x + size, y + i));
			pairs.add(new Pair(x + i, y));
			pairs.add(new Pair(x + i, y + size));
		}

		return pairs;
	}

	/**
	 * @param x
	 *            x point of the center of the circle
	 * @param y
	 *            y point of the center of the circle
	 * @param radius
	 *            radius of the circle
	 * @param n
	 *            the amount of points around the circle returned
	 * @return an ArrayList of x, y Pairs of length n, evenly spaced around a
	 *         circle
	 */
	public ArrayList<Pair> getCircleEdges(int x, int y, int radius, int n)
	{
		ArrayList<Pair> pairs = new ArrayList<Pair>();

		double alpha = (Math.PI * 2) / n;
		int i = 0;

		while (i < n)
		{
			double theta = alpha * i;
			pairs.add(new Pair((int) (x + Math.cos(theta) * radius), (int) (y + Math.sin(theta) * radius)));
			i++;
		}

		return pairs;
	}

	/**
	 * Draws a circle outline with the given parameters.
	 * <p>
	 * 
	 * @param g
	 *            the Graphics object with which to draw
	 * @param x
	 *            x point of the center of the circle
	 * @param y
	 *            y point of the center of the circle
	 * @param radius
	 *            radius of the circle
	 */
	public void drawCircle(Graphics g, int x, int y, int radius)
	{
		g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}

	/**
	 * Draws a filled in a circle with the given parameters.
	 * <p>
	 * 
	 * @param g
	 *            the Graphics object with which to draw
	 * @param x
	 *            x point of the center of the circle
	 * @param y
	 *            y point of the center of the circle
	 * @param radius
	 *            radius of the circle
	 */
	public void fillCircle(Graphics g, int x, int y, int radius)
	{
		g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}

	/**
	 * Takes from the sound file package me/aaron/sounds, and plays the audio
	 * clip, as well as adding it to an ArrayList of running audio clips for
	 * starting/stopping playing them.
	 * 
	 * @param fileName
	 *            the name of the file to play including extension
	 * @param gain
	 *            a float that allows for the volume of the sound to be tuned
	 */
	public void playSound(String fileName, float gain)
	{
		try
		{
			// Open an audio input stream.
			URL url = GamePanel.class.getResource("/me/aaron/sounds/" + fileName);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();

			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);

		    audioClips.add(clip);
			
			//System.out.println("Adding audio clip: " + fileName + " with length " + clip.getMicrosecondLength() / (Math.pow(10, 6)));

			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(gain);

			clip.start();
		}
		catch (UnsupportedAudioFileException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (LineUnavailableException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	public void drawSprite(Graphics g)
	{
		g.setColor(color);
		g.fillRect(x, y, size, size);
	}

	public boolean getRemovalFlag()
	{
		return removalFlag;
	}

	public void setRemovalFlag(boolean removalFlag)
	{
		this.removalFlag = removalFlag;
	}

	public Color getColor()
	{
		return color;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getMoveSpeed()
	{
		return moveSpeed;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public void setColor(Color c)
	{
		this.color = c;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	@Override
	public int compareTo(Entity e)
	{
		return new CompareToBuilder()
		.append(x, e.getX())
		.append(y, e.getY())
		.append(moveSpeed, e.getMoveSpeed())
		.append(size, e.getSize())
		.toComparison();
	}
}
