package me.aaron.db;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

public class Player extends Entity
{
	private int hitPoints, originalHitPoints, currentDamage, originalDamage, currentSize, originalSize;
	private int timeSinceLastAttack = 0, hitTimer = 0;
	boolean isInvincible, isHit;
	PowerupHelper powerupHelper = new PowerupHelper(this);

	public Player(Color color, int moveSpeed, int x, int y, int size, int hp)
	{
		super(color, moveSpeed, x, y, size);

		isInvincible = false;
		isHit = false;

		hitPoints = hp;
		originalHitPoints = hitPoints;

		currentDamage = 5;
		originalDamage = currentDamage;

		currentSize = 5;
		originalSize = currentSize;
	}
	
	public void update(boolean shouldUpdate)
	{
		if (GamePanel.isKeyPressed(KeyEvent.VK_W) && getY() >= 5)   moveUp();
		if (GamePanel.isKeyPressed(KeyEvent.VK_S) && getY() <= 625) moveDown();
		if (GamePanel.isKeyPressed(KeyEvent.VK_A) && getX() >= 5)   moveLeft();
		if (GamePanel.isKeyPressed(KeyEvent.VK_D) && getX() <= 670) moveRight();
		
		if (GamePanel.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceLastAttack >= 15)
		{
			spawnAttack();
			timeSinceLastAttack = 0;
		}

		timeSinceLastAttack++;
	
		if (isHit == true)
		{
			setColor(Color.red);
			hitTimer++;
		}

		if (hitTimer == 10)
		{
			setColor(Color.yellow);
			isHit = false;
			hitTimer = 0;
		}
		
		powerupHelper.decrementPowerups();
	}

	public void spawnAttack()
	{
		GamePanel.attacks.add(new Attack(Color.red, 4, getX() + 2, getY() - 8, currentSize, currentDamage, 0));
		GamePanel.attacks.add(new Attack(Color.red, 4, getX() + 8, getY() - 8, currentSize, currentDamage, 1));
		GamePanel.attacks.add(new Attack(Color.red, 4, getX() - 5, getY() - 8, currentSize, currentDamage, -1));
		GamePanel.totalAttacks += 3;
	}

	public void moveUp()
	{
		this.setY(this.getY() - this.getMoveSpeed());
	}

	public void moveDown()
	{
		this.setY(this.getY() + this.getMoveSpeed());
	}

	public void moveLeft()
	{
		this.setX(this.getX() - this.getMoveSpeed());
	}

	public void moveRight()
	{
		this.setX(this.getX() + this.getMoveSpeed());
	}

	public int getHP()
	{
		return hitPoints;
	}

	public void applyPowerup(Powerup powerup)
	{
		powerupHelper.addPowerup(powerup);
	}
	
	public void decrementHP(int amount)
	{
		if (isInvincible == false)
		{
			hitPoints -= amount;
			isHit = true;
			super.playSound("hit.wav", -5.0f);
		}

		if (hitPoints <= 0)
		{
			this.setRemovalFlag(true);
			super.playSound("gameover.wav", -10.0f);
		}
	}

	public int getOriginalHitPoints()
	{
		return originalHitPoints;
	}

	public void setHitPointsFull()
	{
		hitPoints = originalHitPoints;
	}
	
	public void setInvincible(boolean inv)
	{
		isInvincible = inv;
	}
	
	public void damageModify(boolean shouldModify)
	{
		if(shouldModify == true)
		{
			currentDamage += 5;
			currentSize += 5;
		}
		else
		{
			currentDamage -= 5;
			currentSize -= 5;
			
			if(currentDamage < originalDamage) currentDamage = originalDamage;
			if(currentSize < originalSize) currentSize = originalSize;
		}
	}

}
