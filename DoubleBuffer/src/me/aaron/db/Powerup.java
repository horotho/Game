package me.aaron.db;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

public class Powerup extends Entity
{
	private Timer expireTimer;
	private ArrayList<Pair> circleEdges, currentPairs;
	private int index = 0, ID;
	private static Random random;

	public Powerup(int ID)
	{
		super(Color.black, 0, 0, 0, 10);
		random = new Random();

		int x = random.nextInt(600) + 50, y = random.nextInt(200) + 300;
		setX(x);
		setY(y);
		
		circleEdges = this.getCircleEdges(x + 4, y + 4, 20, 500);
		currentPairs = new ArrayList<Pair>();
		this.ID = ID;
		
		if(ID == 0) setColor(Color.white);
		if(ID == 1) setColor(new Color(0, 191, 255));

		expireTimer = new Timer(1, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setRemovalFlag(true);
			}
		});

		// Set the powerup to expire in 10 seconds
		expireTimer.setInitialDelay(8 * 1000);
		expireTimer.setRepeats(false);
		expireTimer.start();

		start();
	}

	public void start()
	{
		expireTimer.start();
	}
	
	public void update(boolean shouldUpdate)
	{
		if(shouldUpdate == false) return;
		
		currentPairs.clear();
		currentPairs.add(circleEdges.get(index % circleEdges.size()));
		currentPairs.add(circleEdges.get((index + 100) % circleEdges.size()));
		currentPairs.add(circleEdges.get((index + 200) % circleEdges.size()));
		currentPairs.add(circleEdges.get((index + 300) % circleEdges.size()));
		currentPairs.add(circleEdges.get((index + 400) % circleEdges.size()));
		index += 5;
	}

	public void stop()
	{
		expireTimer.stop();
	}

	public void drawSprite(Graphics g)
	{
		g.setColor(this.getColor());
		g.fillRect(getX(), getY(), getSize(), getSize());

		for (Pair pair : currentPairs)
		{
			g.fillRect(pair.getX(), pair.getY(), getSize() / 2, getSize() / 2);
		}
	}

	public int getID()
	{
		return ID;
	}
	
	public int getLength()
	{
		switch(ID)
		{
			case 0:
				return 7100;
			case 1:
				return 4400;
			default:
				return 0;
		}
	}

	
	public void playSound()
	{
		switch(ID)
		{
			case 0:
				super.playSound("powerup.wav", -10.0f);
				break;
			case 1:
				super.playSound("powerup2.wav", -10.0f);
				break;
			default:
				break;
		}
		
	}

}
