package units;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import abilities.AbilityHandler;

/** The "Spitter" class
 * A type of virus that spits projectiles instead of using melee attacks
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Spitter extends Virus{

	private LinkedList <Spit> spitBalls;
	protected int range;

	protected int spitCooldown;
	protected int maxCooldown;
	
	private Image spitImage;
	
	/** Creates a new spitter unit
	 * @param virusHealth	how much health 
	 * @param spawnPoint	where it starts off at
	 * @param sprite		the image to use
	 * @param targetCrystal	the crystal to destroy
	 */
	public Spitter(int virusHealth, Point spawnPoint, Image sprite,Unit targetCrystal, Image spit) {
		super(virusHealth, spawnPoint, sprite, targetCrystal);		
		spitImage = spit;

		spitBalls = new LinkedList <Spit>();		
	}
	
	/* (non-Javadoc)
	 * @see units.Unit#isAlive()
	 */
	public boolean isAlive()
	{
		// Keep the spitter "alive" so that the spitballs still 
		// run and display if there are any left
		if (super.isAlive())
			return true;
		return (!spitBalls.isEmpty());
	}
	
	/* (non-Javadoc)
	 * @see units.Unit#collideWith(java.awt.Point, int)
	 */
	public boolean collideWith (Point targetPos, int targetRadius)
	{
		// If the unit is dead, but spit balls are still moving, 
		// stop the player from being able to collide with the
		// invisible unit
		if (health <= 0)
			return false;
			
		return super.collideWith(targetPos, targetRadius);
	}
	
	/* (non-Javadoc)
	 * @see units.Virus#run()
	 */
	public void run ()
	{
		setDestination();
		if (currentTarget!= null)
		{		
			// Fire spitball if possible
			if (health > 0 && spitCooldown <= 0 && AbilityHandler.getDist(this,currentTarget) <= range)
			{
				angle = AbilityHandler.calcAngle(pos,currentTarget.getPos());
				spitBalls.add(new Spit(pos,angle, spitImage));
				spitCooldown = maxCooldown;
			}			
			else
				spitCooldown--;

			// Run every spitball
			Iterator <Spit> spitItr = spitBalls.iterator();
			while (spitItr.hasNext())
			{
				Spit spitBall = spitItr.next();
				
				// Check if any of the possible targets have been hit by the 
				// spit ball (otherwise the spit will just travel through 
				// anything but the current target it fired at)				
				boolean hasCollided = false;
				
				// Check if its gone off the screen.  The screen is slightly larger than the actual
				// one to account for the size of the spit balls
				Rectangle screen = new Rectangle(new Point (-10,-10),new Dimension (1020,720));
				if (!screen.contains(spitBall.getPos()))
					hasCollided = true;
				
				for (Unit possibleTarget : targets)
				{
					if (!hasCollided && possibleTarget.collideWith(spitBall.getPos(),spitBall.getSize()))
					{
						attack(possibleTarget);
						hasCollided = true;
					}
				}
				if (hasCollided)
					spitItr.remove();
				else
					spitBall.run();
			}
			// If not in range, move towards the enemy
			if (AbilityHandler.getDist(this,currentTarget) > range)
				moveToDest();
		}
	}
	
	/** Attacks a unit
	 * @param target	who to attack
	 */
	private void attack(Unit target)
	{
		target.getDamaged(damage);
	}
	
	/* (non-Javadoc)
	 * @see units.Unit#draw(java.awt.Graphics, java.awt.Container)
	 */
	public void draw (Graphics g, Container container)
	{
		for (Spit spitBall : spitBalls)
			spitBall.draw(g,container);
		if (health > 0)
			super.draw(g, container);	
	}

	/** The "Spit" class
	 * Class for the spit that is emitted by the Worm and Rootkit viruses
	 * @author Shiranka Miskin
	 * @version January 2012
	 */
	private class Spit
	{
		private Point pos;
		private double angle;
		private double speed;
		private double xVel;
		private double yVel;
		private Image sprite;
		private int size;

		/** Creates a new Spit ball
		 * @param pos 	the sphere's starting position on the screen
		 * @param angle the angle it is travelling at
		 */
		public Spit (Point pos, double angle, Image sprite)
		{
			this.pos = new Point (pos);
			this.angle = angle;
			speed = 5;
			size = 8;
			this.sprite = sprite;
		}

		/** Moves the spit ball
		 */
		public void run() {
			move();			
		}
		
		/** Gets the position of the spitball
		 * @return	the position of the spit ball
		 */
		public Point getPos ()
		{
			return pos;
		}

		/** Moves the spit ball
		 */
		public void move()
		{
			xVel = speed*Math.cos(Math.toRadians(angle));
			yVel = speed*Math.sin(Math.toRadians(angle));		

			pos.setLocation(pos.getX()+xVel,pos.getY()+yVel);
		}

		/** Draws the spit ball
		 * @param g			the graphic context
		 * @param container	the container to draw on
		 */
		public void draw (Graphics g, Container container)
		{
			g.drawImage (sprite,pos.x-size,pos.y-size,size*2,size*2, container);
		}

		/** Gets the size of the spit ball
		 * @return the size of the spit ball
		 */
		public int getSize() {
			return size;
		}


	}
	
}
