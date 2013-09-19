package me.aaron.db;

import java.awt.Color;

public class LineAttack extends EnemyAttack
{
	private int endX, endY, originalX, originalY;
	private double timestep = 0, timestepValue = 0;
	
	public LineAttack(Color color, double timestepValue, int x, int y, int size, int damage, int ex, int ey)
	{
		super(color, 0, x, y, size, damage);
		this.endX = ex;
		this.endY = ey;
		this.originalX = x;
		this.originalY = y;
		this.timestepValue = timestepValue;
	}
	
	@Override
	public void update(boolean shouldUpdate)
	{
		if(shouldUpdate == false) return;
		
		int x = (int) (originalX + (endX - originalX) * timestep);
		int y = (int) (originalY + (endY - originalY) * timestep);
		
		timestep += timestepValue;
		
		setX(x);
		setY(y);
	}

}
