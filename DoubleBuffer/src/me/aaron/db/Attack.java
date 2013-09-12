package me.aaron.db;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Attack extends Entity
{
	private int damage, sway;

	public Attack(Color color, int move, int x, int y, int size, int damage, int sway)
	{
		super(color, move, x, y, size);
		this.damage = damage;
		this.sway = sway;
		
		playSound("lazer.wav", -15.0f);
	}
	
	@Override
	public void drawSprite(Graphics g)
	{
		g.setColor(this.getColor());
		fillCircle(g, this.getX(), this.getY(), this.getSize()/2);
		
		g.setColor(Color.yellow);
		drawCircle(g, this.getX(), this.getY(), this.getSize()/2 + 1);
	}
	
	@Override
	public ArrayList<Pair> getEdges()
	{
		return super.getCircleEdges(getX(), getY(), getSize()/2, 50);
	}
	
	public void update(boolean shouldUpdate)
	{
		if(shouldUpdate == false) return;
		
		setY(getY() - getMoveSpeed());
		setX(getX() + sway);
	}

	public int getDamage()
	{
		return damage;
	}

}
