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

		GamePanel.eAttacks.add(new EnemyAttack(Color.blue, 3, bottomLeftCorner.getX() + 1, bottomLeftCorner.getY(), 10, 10, -1, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.green, 3, bottomRightCorner.getX() + 1, bottomRightCorner.getY(), 10, 10, -1, false));
	}
	
	public void spawnAftAttacks()
	{
		Pair topLeftCorner = new Pair(getX(), getY());
		Pair topRightCorner = new Pair(getX() + getSize(), getY());
		
		GamePanel.eAttacks.add(new EnemyAttack(Color.red, 3, topLeftCorner.getX() + 1, topLeftCorner.getY(), 10, 10, -2, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.red, 3, topLeftCorner.getX() + 2, topLeftCorner.getY(), 10, 10, -1, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.red, 3, topLeftCorner.getX() + 3, topLeftCorner.getY(), 10, 10, 0, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.red, 3, topLeftCorner.getX() + 4, topLeftCorner.getY(), 10, 10, 1, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.red, 3, topLeftCorner.getX() + 5, topLeftCorner.getY(), 10, 10, 2, false));
		
		GamePanel.eAttacks.add(new EnemyAttack(Color.orange, 3, topRightCorner.getX() + 1, topRightCorner.getY(), 10, 10, -2, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.orange, 3, topRightCorner.getX() + 2, topRightCorner.getY(), 10, 10, -1, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.orange, 3, topRightCorner.getX() + 3, topRightCorner.getY(), 10, 10, -0, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.orange, 3, topRightCorner.getX() + 4, topRightCorner.getY(), 10, 10,  1, false));
		GamePanel.eAttacks.add(new EnemyAttack(Color.orange, 3, topRightCorner.getX() + 5, topRightCorner.getY(), 10, 10,  2, false));
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

			if (attackTime % attackSeparation == 0 && shrinkFlag == false)
			{
				spawnForwardAttacks();
			}
			
			if(attackTime % (attackSeparation + 80) == 0 && shrinkFlag == false)
			{
				spawnAftAttacks();
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
