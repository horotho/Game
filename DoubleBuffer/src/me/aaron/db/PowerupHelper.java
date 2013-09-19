package me.aaron.db;

public class PowerupHelper
{
	private PowerupItem[] currentPowerups = new PowerupItem[100];
	private int index = 0;
	private Player player;

	public PowerupHelper(Player p)
	{
		this.player = p;
	}

	public void addPowerup(Powerup p)
	{
		currentPowerups[index] = new PowerupItem(System.currentTimeMillis(), p);
		index++;
		
		switch(p.getID())
		{
			case 0:
				player.setInvincible(true);
				player.playSound("invinc.wav", -10.0f);
				break;
			case 1:
				player.damageModify(true);
				break;
		}
	}

	public void decrementPowerups()
	{
		int[] IDs = new int[5];
		
		for(int i = 0; i < index; i++)
		{
			Powerup p = currentPowerups[i].getPowerup();
			//System.out.println(System.currentTimeMillis() - currentPowerups[i].getStartTime());
			
			if(System.currentTimeMillis() - currentPowerups[i].getStartTime() == p.getLength())
			{
				System.out.println("pwerup ended");
			}
		}
	}
	
	private class PowerupItem
	{
		private long startTime;
		private Powerup p;
		
		public PowerupItem(long startTime, Powerup p)
		{
			this.startTime = startTime;
			this.p = p;
		}
		
		public Powerup getPowerup()
		{
			return p;
		}
		
		public long getStartTime()
		{
			return startTime;
		}
	}

}
