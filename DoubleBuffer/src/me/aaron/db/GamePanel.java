package me.aaron.db;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener
{
	private static final long serialVersionUID = 3169723523582860586L;
	private Graphics bufferGraphics;
	private Graphics2D g2;
	private BufferedImage offscreen;
	private Timer timer;

	private int enemyAttackTime = 0;

	private static long startPause = 0;
	private static long endPause = 0;
	private static long totalPause = 0;

	public static double totalAttacks = 0.0, oldTotal = 0.0;
	private static double missedAttacks = 0.0, oldMissed = 0.0;
	private static long startTime = 0, endTime = 0;

	private static Object keyLock = new Object();
	private static TreeSet<Integer> keysDown = new TreeSet<Integer>();
	private static LinkedList<Character> keysTyped = new LinkedList<Character>();

	public static TreeSet<Attack> attacks = new TreeSet<Attack>();
	public static TreeSet<Enemy> enemies = new TreeSet<Enemy>();
	public static TreeSet<EnemyAttack> eAttacks = new TreeSet<EnemyAttack>();
	public static TreeSet<Powerup> powerups = new TreeSet<Powerup>();

	private Random random = new Random();
	private static Player player = new Player(Color.yellow, 3, 300, 600, 10, 50);

	private static boolean paused = false;
	private static boolean win = false;

	private static Boss boss;

	public GamePanel()
	{
		this.setBounds(0, 0, 700, 700);
		this.setBackground(Color.black);

		offscreen = new BufferedImage(700, 700, BufferedImage.TYPE_INT_RGB);
		bufferGraphics = offscreen.getGraphics();

		g2 = (Graphics2D) bufferGraphics;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		spawnEnemies();
		spawnBoss();
		startTime = System.currentTimeMillis();

		timer = new Timer(10, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				repaint();
			}
		});

		timer.start();

		setFocusable(true);
		requestFocusInWindow();
		addKeyListener(this);

		Thread thread = new Thread(new Painter());
		thread.start();
	}

	public static void spawnBoss()
	{
		boss = new Boss(new Color(2, 110, 250), 4, 200, 100, 100, 500);
	}

	public static void spawnEnemies()
	{
		spawnEnemyRow(Color.blue, 60, 20, 15, 1, Enemy.LONG_DELAY, 10);
		spawnEnemyRow(Color.blue, 60, 50, 15, 1, Enemy.LONG_DELAY, 10);
		spawnEnemyRow(Color.blue, 60, 80, 15, 1, Enemy.LONG_DELAY, 10);
		spawnEnemyRow(Color.blue, 60, 110, 15, 1, Enemy.LONG_DELAY, 10);
		spawnEnemyRow(Color.green, 150, 140, 8, 2, Enemy.LONG_DELAY, 20);
		spawnEnemyRow(Color.orange, 250, 170, 5, 3, Enemy.SHORT_DELAY, 40);
	}

	public static void clearAll()
	{
		enemies.clear();
		eAttacks.clear();
		attacks.clear();
		powerups.clear();

		oldTotal = totalAttacks;
		oldMissed = missedAttacks;

		totalAttacks = 0.0;
		missedAttacks = 0.0;

		paused = false;
	}

	public static void newGame()
	{
		clearAll();
		win = false;
		player.setHitPointsFull();
		player.setRemovalFlag(false);

		startTime = System.currentTimeMillis();
		endTime = 0;
		totalPause = 0;
		startPause = 0;
		endPause = 0;

		spawnEnemies();
		spawnBoss();
	}
	
	/**
	 * <b> cleanupAll </b>
	 * <p>
	 * {@code void cleanupAll()}
	 * <p>
	 * Iterates through all of the sets holding entities, and removes those which are flagged for removal.
	 */
	private synchronized void cleanupAll()
	{
		for (Iterator<Attack> it = attacks.iterator(); it.hasNext();)
		{
			if (it.next().getRemovalFlag() == true) it.remove();
		}
		
		for (Iterator<Enemy> it = enemies.iterator(); it.hasNext();)
		{
			if (it.next().getRemovalFlag() == true) it.remove();
		}
		
		for (Iterator<EnemyAttack> it = eAttacks.iterator(); it.hasNext();)
		{
			if (it.next().getRemovalFlag() == true) it.remove();
		}
		
		for (Iterator<Powerup> it = powerups.iterator(); it.hasNext();)
		{
			if (it.next().getRemovalFlag() == true) it.remove();
		}
		
		try
		{
			
			for(Clip clip : Entity.audioClips)
			{
				if(clip.isRunning() && paused == true)
				{
					Entity.keepClips.add(clip);
					clip.stop();
				}
			}
			
			if(paused == false)
			{
				for(Clip clip : Entity.keepClips)
				{
					clip.start();
				}
				
				Entity.keepClips.clear();
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void spawnEnemyRow(Color color, int x, int y, int amount, int moveSpeed, int delay, int hp)
	{
		for (int i = 0; i < amount; i++)
		{
			enemies.add(new Enemy(color, moveSpeed, x, y, 15, delay, hp));
			x += 40;
		}
	}

	public void paint(Graphics g)
	{
		g.drawImage(offscreen, 0, 0, this);
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	private class Painter implements Runnable
	{

		@Override
		public void run()
		{
			Timer timer = new Timer(10, new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					bufferGraphics = offscreen.getGraphics();
					bufferGraphics.clearRect(0, 0, 700, 700);

					g2 = (Graphics2D) bufferGraphics;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

					if ((isKeyPressed(KeyEvent.VK_P) || isKeyPressed(KeyEvent.VK_ESCAPE)) && paused == false && win == false)
					{
						paused = true;
					}

					if (isKeyPressed(KeyEvent.VK_R) && paused == true)
					{
						paused = false;
					}

					if (player.getRemovalFlag() == false && win == false)
					{
						player.drawSprite(bufferGraphics);

						// Draw the lives counter
						int y = 620;
						for (int i = 0; i < player.getHP(); i++)
						{
							bufferGraphics.setColor(Color.yellow);
							bufferGraphics.fillRect(670, y, 5, 5);
							y -= 10;
						}

						// Draw the boss health counter

						if (boss.getActivated() == true)
						{
							y = 625;
							for (int i = 0; i < boss.getHP(); i++)
							{
								bufferGraphics.setColor(Color.red);
								bufferGraphics.fillRect(660, y, 5, 1);
								y -= 1;
							}
						}

						// Draw all of the attacks, and set the removal flags
						// for cleanup later
						for (Attack attack : attacks)
						{
							if (isCollide(boss, attack) && boss.getActivated() == true)
							{
								boss.decrementHitPoints(attack.getDamage());
								boss.playSound();
								attack.setRemovalFlag(true);
							}
							else if (attack.getY() < 0 && attack.getRemovalFlag() == false)
							{
								missedAttacks++;
								attack.setRemovalFlag(true);
							}
							else
							{
								attack.update(paused == false);
								attack.drawSprite(bufferGraphics);
							}
						}

						player.update(paused == false);
						player.drawSprite(bufferGraphics);
					}

					if (boss.getRemovalFlag() == false && boss.getActivated() == true)
					{
						// Draw the boss and check to see if the player has
						// collided with it
						boss.update(paused == false);
						boss.drawSprite(bufferGraphics);

						if (isCollide(boss, player))
						{
							player.decrementHP(1);
						}
					}

					// See if any current attacks hit an enemy, and if they
					// do mark them for removal
					// Also draws all of the enemies that are still alive
					for (Enemy enemy : enemies)
					{
						if (isCollide(enemy, player))
						{
							player.decrementHP(1);
						}

						for (Attack attack : attacks)
						{
							if (isCollide(enemy, attack))
							{
								enemy.decrementHitPoints(attack.getDamage());
								attack.setRemovalFlag(true);
							}
						}

						enemy.update(paused == false);
						enemy.drawSprite(bufferGraphics);
					}

					// Draw enemy attacks, and decrement player health if
					// the player is hit
					for (EnemyAttack eAttack : eAttacks)
					{
						if (isCollide(eAttack, player))
						{
							player.decrementHP(eAttack.getDamage());
							eAttack.setRemovalFlag(true);
						}

						if (eAttack.getY() > 700) eAttack.setRemovalFlag(true);

						eAttack.update(paused == false);
						eAttack.drawSprite(bufferGraphics);
					}

					for (Powerup powerup : powerups)
					{
						if (isCollide(powerup, player))
						{
							powerup.setRemovalFlag(true);
							powerup.playSound();
							player.applyPowerup(powerup);
						}

						powerup.update(paused == false);
						powerup.drawSprite(bufferGraphics);
					}

					// Code for spacing out the timing of enemy attacks so
					// they don't happen too often
					if (enemyAttackTime % 20 == 0 && enemies.size() > 0 && paused == false)
					{
						int r1 = random.nextInt(enemies.size()), r2 = random.nextInt(enemies.size());
						Object[] arr = enemies.toArray();
						Enemy one = (Enemy) arr[r1];
						Enemy two = (Enemy) arr[r2];
						eAttacks.add(new EnemyAttack(Color.red, 3, one.getX() + 6, one.getY() + 15, 6, 5, 0, true));
						eAttacks.add(new EnemyAttack(Color.red, 3, two.getX() + 6, two.getY() + 15, 6, 5, 0, true));
					}

					enemyAttackTime++;

					if (enemyAttackTime % 500 == 0 && paused == false && win == false)
					{
						powerups.add(new Powerup(random.nextInt(2)));
					}

					

					if (enemies.isEmpty() && boss.getActivated() == false && player.getRemovalFlag() == false)
					{
						boss.playStageSound(1);
						boss.setActivated(true);
					}

					if (player.getRemovalFlag() == true && boss.getHP() > 0)
					{
						bufferGraphics.setColor(Color.red);
						bufferGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 120));
						bufferGraphics.drawString("GAME", 160, 300);

						bufferGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 110));
						bufferGraphics.drawString("OVER", 190, 400);

						player.setHitPointsFull();
						boss.setRemovalFlag(true);
						boss.setActivated(false);
						clearAll();

						player.setX(300);
						player.setY(400);
					}
					else if (enemies.isEmpty() && boss.getHP() <= 0)
					{
						win = true;
						DecimalFormat formatter = new DecimalFormat("#.##");

						if (attacks.isEmpty() == false) clearAll();

						player.setX(300);
						player.setY(400);

						bufferGraphics.setColor(Color.green);
						bufferGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 72));
						bufferGraphics.drawString("YOU WIN!", 160, 200);

						// Attack information breakdown
						bufferGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 30));
						bufferGraphics.drawString("Total Attacks: " + (int) oldTotal, 180, 300);
						bufferGraphics.drawString("Missed Attacks: " + (int) oldMissed, 180, 330);
						bufferGraphics.drawString("Percentage: " + formatter.format((1.0 - oldMissed / oldTotal) * 100) + "%", 180, 360);
						bufferGraphics.drawString("You were hit " + (player.getOriginalHitPoints() - player.getHP()) / 5 + " times.", 180, 390);

						int totalTime = (int) ((endTime - startTime - totalPause) / 1000);
						if (totalTime / 60 > 1) bufferGraphics.drawString("You took " + totalTime / 60 + " minutes and " + (totalTime % 60) + " seconds.", 180, 420);
						else if(totalTime / 60 == 1) bufferGraphics.drawString("You took " + totalTime / 60 + " minute and " + (totalTime % 60) + " seconds.", 180, 420);
						else bufferGraphics.drawString("You took " + (totalTime % 60) + " seconds.", 180, 420);

						bufferGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 72));
						bufferGraphics.drawString("YOUR SCORE: " + determineScore(oldTotal, oldMissed), 80, 550);
					}
					else if (paused == true)
					{
						bufferGraphics.setColor(Color.red);
						bufferGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 72));
						bufferGraphics.drawString("PAUSED", 200, 300);
						bufferGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 30));
						bufferGraphics.drawString("Press R to resume.", 210, 330);
					}
					
					// Takes care of removing attacks that are outside the
					// visible drawn window, or attacks
					// that land on a player or an enemy
					cleanupAll();
					bufferGraphics.dispose();
				}
			});

			timer.start();
		}
	}

	private String determineScore(double total, double missed)
	{
		if (endTime == 0) endTime = System.currentTimeMillis() - totalPause;
		double time = (double) endTime - startTime;
		time /= 1000;

		double percentage;
		if (missed == 0) percentage = 100;
		else percentage = (1.0 - missed / total) * 100;

		if (time <= 60)
		{
			if (percentage >= 75) return "A+";
			else if (percentage < 75 && percentage >= 50) return "A";
			else if (percentage < 50) return "A-";
		}

		else if (time > 60 && time <= 120)
		{
			if (percentage >= 75) return "B+";
			else if (percentage < 75 && percentage >= 50) return "B";
			else if (percentage < 50) return "B-";
		}

		else if (time > 120 && time <= 180)
		{
			if (percentage >= 75) return "C+";
			else if (percentage < 75 && percentage >= 50) return "C";
			else if (percentage < 50) return "C-";
		}

		return "D";
	}

	
	private boolean isCollide(Entity one, Entity two)
	{
		if(!(one instanceof Boss) && !(two instanceof Boss))
		{
			if (Math.abs(one.getX() - two.getX()) >= 50 || Math.abs(one.getY() - two.getY()) >= 50) return false;
		}
		
		ArrayList<Pair> onePairs = one.getEdges();
		ArrayList<Pair> twoPairs = two.getEdges();

		for (Pair op : onePairs)
		{
			for (Pair tp : twoPairs)
			{
				if (Math.abs(op.getX() - tp.getX()) <= 1 && Math.abs(op.getY() - tp.getY()) <= 1) return true;
			}
		}

		return false;
	}

	/*************************************************************************
	 * Keyboard interactions.
	 *************************************************************************/

	/**
	 * Has the user typed a key?
	 * 
	 * @return true if the user has typed a key, false otherwise
	 */
	public static boolean hasNextKeyTyped()
	{
		synchronized (keyLock)
		{
			return !keysTyped.isEmpty();
		}
	}

	/**
	 * What is the next key that was typed by the user? This method returns a
	 * Unicode character corresponding to the key typed (such as 'a' or 'A'). It
	 * cannot identify action keys (such as F1 and arrow keys) or modifier keys
	 * (such as control).
	 * 
	 * @return the next Unicode key typed
	 */
	public static char nextKeyTyped()
	{
		synchronized (keyLock)
		{
			return keysTyped.removeLast();
		}
	}

	/**
	 * Is the keycode currently being pressed? This method takes as an argument
	 * the keycode (corresponding to a physical key). It can handle action keys
	 * (such as F1 and arrow keys) and modifier keys (such as shift and
	 * control). See <a href =
	 * "http://download.oracle.com/javase/6/docs/api/java/awt/event/KeyEvent.html"
	 * >KeyEvent.java</a> for a description of key codes.
	 * 
	 * @return true if keycode is currently being pressed, false otherwise
	 */

	public static boolean isKeyPressed(int keycode)
	{
		synchronized (keyLock)
		{
			return keysDown.contains(keycode);
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{
		synchronized (keyLock)
		{
			keysTyped.addFirst(e.getKeyChar());
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		synchronized (keyLock)
		{
			keysDown.add(e.getKeyCode());
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		synchronized (keyLock)
		{
			keysDown.remove(e.getKeyCode());
		}
	}

}
