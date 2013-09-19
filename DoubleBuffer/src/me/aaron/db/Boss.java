package me.aaron.db;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Boss extends Entity
{
	private boolean countUp = true;
	private int attackTime = 0, hitPoints = 0, damageTime = 0, shrinkTime = 0;
	private boolean shrinkFlag = false, flashFlag = false;
	private boolean activated = false;
	private boolean stageOneSound = false, stageTwoSound = false;
	private Color originalColor;
	private int timesKilled = 0, attackSeparation = 30;

	public Boss(Color c, int moveSpeed, int x, int y, int size, int hitPoints)
	{
		super(c, moveSpeed, x, y, size);
		this.hitPoints = hitPoints;
		this.originalColor = c;
		activated = false;

		//start();
	}

	public void playSound()
	{
		if(timesKilled == 1) super.playSound("enemy_hit3.wav", -15.0f);
		else super.playSound("boss_shield_hit.wav", -15.0f);
	}
	
	public void playStageSound(int stage)
	{
		switch(stage)
		{
			case 1:
				if(stageOneSound == false)
				{
					playSound("boss_stage1.wav", -20.0f);
					stageOneSound = true;
				}
				break;
			case 2:
				if(stageTwoSound == false)
				{
					playSound("boss_stage2.wav", 0.0f);
					stageTwoSound = true;
				}
				break;
			default:
				break;
		}
	}

	public ArrayList<Pair> getEdges()
	{
		ArrayList<Pair> allPairs = new ArrayList<Pair>();
		int x = this.getX(), y = this.getY(), size = this.getSize();
		int halfSize = size / 2, quarterSize = size / 4;

		if (timesKilled == 1)
		{
			allPairs.addAll(getEdges(x, y, size));
			allPairs.addAll(getEdges(x - quarterSize, y - quarterSize, halfSize));
			allPairs.addAll(getEdges(x - quarterSize, y + getSize() - quarterSize, halfSize));
			allPairs.addAll(getEdges(x + getSize() - quarterSize, y - quarterSize, halfSize));
			allPairs.addAll(getEdges(x + getSize() - quarterSize, y + getSize() - quarterSize, halfSize));
		}
		else
		{
			allPairs.addAll(getCircleEdges(x + 50, y + 50, 120, 500));
		}

		return allPairs;
	}

	public void drawSprite(Graphics g)
	{
		int x = this.getX(), y = this.getY(), size = this.getSize();
		int halfSize = this.getSize() / 2, quarterSize = this.getSize() / 4;

		if (shrinkFlag == false)
		{
			g.setFont(new Font("Segoe UI", Font.PLAIN, 32));
			g.setColor(Color.red);
			g.drawString("Boss Stage " + (timesKilled + 1), 10, 30);
		}

		g.setColor(this.getColor());
		g.fillRect(x, y, size, size);

		g.setColor(Color.green);
		g.fillRect(x - quarterSize, y - quarterSize, halfSize, halfSize);
		g.fillRect(x - quarterSize, y + getSize() - quarterSize, halfSize, halfSize);
		g.fillRect(x + getSize() - quarterSize, y - quarterSize, halfSize, halfSize);
		g.fillRect(x + getSize() - quarterSize, y + getSize() - quarterSize, halfSize, halfSize);
		
		if (timesKilled == 0)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(5));
			g.setColor(Color.white);
			if (flashFlag == false) this.drawCircle(g, x + 50, y + 50, 120);
			else this.fillCircle(g, x + 50, y + 50, 120);
			g2.setStroke(new BasicStroke(1));
		}
	}

	public void spawnForwardAttacks()
	{
		Pair bottomLeftCorner = new Pair(getX(), getY() + getSize());
		Pair bottomRightCorner = new Pair(getX() + getSize(), getY() + getSize());
		
		Player player = GamePanel.player;

		GamePanel.eAttacks.add(new LineAttack(Color.green, 0.008, bottomLeftCorner.getX(), bottomLeftCorner.getY(), 10, 10, player.getX(), player.getY()));
		GamePanel.eAttacks.add(new LineAttack(Color.orange, 0.008, bottomRightCorner.getX(), bottomRightCorner.getY(), 10, 10, player.getX(), player.getY()));
	}
	
	public void spawnAftAttacks()
	{
		Pair topLeftCorner = new Pair(getX(), getY());
		Pair topRightCorner = new Pair(getX() + getSize(), getY());
		
		Player player = GamePanel.player;
		
		for(int i = 0; i < 10; i++)
		{
			GamePanel.eAttacks.add(new LineAttack(Color.red, 0.003, topLeftCorner.getX() + i, topLeftCorner.getY(), 10, 10, i * 100, 700));
			GamePanel.eAttacks.add(new LineAttack(Color.blue, 0.003, topRightCorner.getX() + i, topRightCorner.getY(), 10, 10, i * 100, 700));
		}
	}
	
	public void update(boolean shouldUpdate)
	{
		if(shouldUpdate == false) return;
		
		if (activated == true)
		{
			if (flashFlag == true)
			{
				setColor(Color.white);
				damageTime++;
			}

			if (damageTime == 10)
			{
				damageTime = 0;
				flashFlag = false;
			}

			if (shrinkFlag == true)
			{
				shrinkTime++;
				if (getSize() > 0 && shrinkTime % 2 == 0) setSize(getSize() - 1);
				else if(getSize() <= 0) setRemovalFlag(true);
			}

			if (flashFlag == false) setColor(originalColor);

			if (countUp == true)
			{
				setX(getX() + getMoveSpeed());
			}
			else if (countUp == false)
			{
				setX(getX() - getMoveSpeed());
			}

			if (getX() >= 600 - getSize()) countUp = false;
			if (getX() <= getSize()) countUp = true;

			if (shrinkFlag == false)
			{
				if(attackTime % attackSeparation == 0) spawnForwardAttacks();
				if(attackTime % (attackSeparation + 500) == 0) spawnAftAttacks();
				if(attackTime % (attackSeparation + 200) == 0 && timesKilled == 1)
				{
					GamePanel.eAttacks.add(new HomingAttack(Color.magenta, getSize() + getSize()/2, getSize() + getSize()/2, 10, 10));
				}
			}
			

			attackTime++;
		}
	}

	public int getHP()
	{
		return hitPoints;
	}

	public void decrementHitPoints(int amount)
	{
		hitPoints -= amount;
		flashFlag = true;

		if (hitPoints <= 0 && timesKilled == 0 && activated == true)
		{
			hitPoints = 500;
			timesKilled++;
			attackSeparation -= 5;
		}

		else if (hitPoints <= 0 && timesKilled == 1)
		{
			shrinkFlag = true;
			activated = false;
			super.playSound("boss_beat2.wav", -10.0f);
		}
	}

	public boolean getActivated()
	{
		return activated;
	}

	public void setActivated(boolean activated)
	{
		this.activated = activated;
	}
}
