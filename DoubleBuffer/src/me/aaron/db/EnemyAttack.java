package me.aaron.db;

import java.awt.Color;

public class EnemyAttack extends Entity
{
	private int damage;

	public EnemyAttack(Color color, int move, int x, int y, int size, int damage)
	{
		super(color, move, x, y, size);
		this.damage = damage;
	}
	
	public void update(boolean shouldUpdate)
	{
		if(shouldUpdate == false) return;
		
		setY(getY() + getMoveSpeed());
	}
	
	public int getDamage()
	{
		return damage;
	}
}
