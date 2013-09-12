package me.aaron.db;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

public class EnemyAttack extends Entity
{
	private int sway, damage;
	private Timer timer;
	private Random random = new Random();

	public EnemyAttack(Color color, int move, int x, int y, int size, int damage, int sway, boolean randomSway)
	{
		super(color, move, x, y, size);
		this.damage = damage;
		this.sway = sway;
		
		if(randomSway == true)
		{
			double value = random.nextDouble();
			if(value >= 0 && value <= 0.08) sway = -1;
			else if(value > 0.08 && value <= 0.16) sway = -2;
			else if(value > 0.16 && value <= 0.24) sway = -3;
			else if(value > 0.24 && value <= 0.32) sway = -4;
			else if(value > 0.32 && value <= 0.4) sway = -5;
			else if(value > 0.4 && value <= 0.48) sway = -6;
			else if(value > 0.48 && value <= 0.56) sway = 5;
			else if(value > 0.56 && value <= 0.64) sway = 4;
			else if(value > 0.64 && value <= 0.72) sway = 3;
			else if(value > 0.72 && value <= 0.8) sway = 2;
			else if(value > 0.8  && value <= 0.88) sway = 1;
			else if(value > 0.88 && value <= 0.96) sway = 0;
			else sway = 0;
		}
		
		//start(); 
	}
	
	public void update(boolean shouldUpdate)
	{
		if(shouldUpdate == false) return;
		
		setY(getY() + getMoveSpeed());
		setX(getX() + sway);
	}
	
	public void start()
	{
		timer = new Timer(20, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				setY(getY() + getMoveSpeed());
				setX(getX() + sway);
			}
		});

		timer.start();
	}
	
	public void stop()
	{
		timer.stop();
	}

	public int getDamage()
	{
		return damage;
	}


}
