package me.aaron.db;

import java.awt.Color;

public class Enemy extends Entity
{
	private Color originalColor;
	private int initialDelay, initialX;
	private boolean flashFlag = false;
	private boolean countUp = true, countDown = true;
	private int hitPoints, damageTime = 0;
	
	public static final int STANDARD_DELAY = 50;
	public static final int LONG_DELAY = 500;
	public static final int SHORT_DELAY = 0;
	public static final int MEDIUM_DELAY = 75;
	
	public Enemy(Color c, int move, int x, int y, int size, int initialDelay, int hitPoints)
	{
		super(c, move, x, y, size);
		this.originalColor = c;
		this.hitPoints = hitPoints;
		this.initialDelay = initialDelay;
		initialX = x;
	}
	
	public void update(boolean shouldUpdate)
	{
		if(shouldUpdate == false) return;
		
		if(flashFlag == true)
		{
			setColor(Color.red);
			damageTime++;
		}
		
		if(damageTime == 10)
		{
			damageTime = 0;
			flashFlag = false;
		}
		
		if(flashFlag == false) setColor(originalColor);
		
		if(countDown == true)
		{
			setX(getX() - getMoveSpeed());
			countUp = false;
			
			if(getX() == initialX - 20 * getMoveSpeed())
			{
				countUp = true;
				countDown = false;
			}
		}
		else if(countUp == true)
		{
			setX(getX() + getMoveSpeed());
			countDown = false;
			
			if(getX() == initialX + 20 * getMoveSpeed()) countDown = true;
		}
	}
	
	public int getHitPoints()
	{
		return hitPoints;
	}
	
	public boolean getFlashFlag()
	{
		return flashFlag;
	}
	
	public int getDamageTime()
	{
		return damageTime;
	}
	
	public void setDamageTime(int time)
	{
		damageTime = time;
	}
	
	public Color getOriginalColor()
	{
		return originalColor;
	}
	
	public void setFlashFlag(boolean flag)
	{
		flashFlag = flag;
	}
	
	public void setHitPoints(int amount)
	{
		hitPoints = amount;
	}
	
	public void decrementHitPoints(int amount)
	{
		hitPoints -= amount;
		flashFlag = true;
		
		if(hitPoints <= 0)
		{
			setRemovalFlag(true);
			super.playSound("enemy_hit3.wav", -20.0f);
		}
	}

}
