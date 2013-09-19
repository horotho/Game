package me.aaron.db;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.Timer;

public class HomingAttack extends EnemyAttack
{
	private int endX, endY, originalX, originalY, updateTime;
	private double timestep = 0;
	private Timer destroyTimer;

	public HomingAttack(Color color, int x, int y, int size, int damage)
	{
		super(color, 0, x, y, size, damage);
		this.originalX = x;
		this.originalY = y;
		updateTime = 0;

		destroyTimer = new Timer(0, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				setRemovalFlag(true);
			}
		});
		
		destroyTimer.setRepeats(false);
		destroyTimer.setInitialDelay(2200);
		destroyTimer.start();
	}
	
	public void drawSprite(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform old = g2.getTransform();
		
		AffineTransform at = new AffineTransform();
		double theta = 0.0;
		
		Pair tip = new Pair(getX() + 5, getY() + 5);
		double xdist = endX + 5 - tip.getX(), ydist = endY + 5 - tip.getY();
		
		theta = -Math.atan2(xdist, ydist);
		at.rotate(theta, getX(), getY());
		g2.setTransform(at);
		
		g.setColor(this.getColor());
		int[] xPoints = {getX(), getX() + 7, getX() + 7, getX() + 3, getX()};
		int[] yPoints = {getY(), getY(), getY() + 7, getY() + 10, getY() + 7};
		
		g.fillPolygon(xPoints, yPoints, 5);
		
		g2.setTransform(old);
	}

	@Override
	public void update(boolean shouldUpdate)
	{
		if (shouldUpdate == false) return;

		int x = getX(), y = getY();

		if (updateTime % 2 == 0)
		{
			endX = GamePanel.player.getX();
			endY = GamePanel.player.getY();

			originalX = getX();
			originalY = getY();

			x = (int) (originalX + (endX - originalX) * timestep);
			y = (int) (originalY + (endY - originalY) * timestep);

		}

		if (updateTime % 10 == 0)
		{
			timestep += 0.01;
		}

		setX(x);
		setY(y);

		updateTime += 1;

	}

}
