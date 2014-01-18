package units;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import abilities.Ability;
import abilities.AbilityHandler;

/** The "Worm" class
 * A virus that snakes around attacking with ranged spit balls
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Worm extends Spitter{

	private ArrayList <WormBody> bodySegments;

	private int chainSize;
	private int minBodySize;

	/** Creates a new worm
	 * @param spawnPoint	where the worm starts off at
	 * @param targetCrystal	the crystal to destroy
	 */
	public Worm(Point spawnPoint, Unit targetCrystal) 
	{
		super(120, spawnPoint, Toolkit.getDefaultToolkit().getImage("Sprites/WormHead.png"), 
				targetCrystal, Toolkit.getDefaultToolkit().getImage("Sprites/WormSpit.png"));
		damage = 10;
		range = 300;
		health = 1000;
		value = 50;
		spitCooldown = 0;
		maxCooldown = 50;
		chainSize = 6;
		minBodySize = 10;
		bodySegments = new ArrayList <WormBody>();
		// Assign the first segment to follow the head
		bodySegments.add(new WormBody (this, minBodySize + chainSize * 2 + 2));
		// Make all others follow the previous segment
		for (int segment = 1; segment < chainSize; segment++)
			bodySegments.add(0, new WormBody (bodySegments.get(0), minBodySize+2*(chainSize-segment)));		
	}

	/* (non-Javadoc)
	 * @see units.Virus#run()
	 */
	public void run ()
	{
		super.run();
		for (WormBody bodyPart : bodySegments)
			bodyPart.run();
	}

	/* (non-Javadoc)
	 * @see units.Unit#draw(java.awt.Graphics, java.awt.Container)
	 */
	public void draw (Graphics g, Container container)
	{
		if (health > 0)
			for (WormBody bodyPart : bodySegments)
				bodyPart.draw(g, container);

		super.draw(g, container);	
	}

	/* (non-Javadoc)
	 * @see units.Unit#collideWith(java.awt.Point, int)
	 */
	public boolean collideWith (Point targetPos, int targetRadius)
	{
		// If the unit is dead, but spit balls are still moving, 
		// stop the player from being able to collide with the
		// invisible worm
		if (health <= 0)
			return false;
		
		if (super.collideWith (targetPos,targetRadius))
			return true;

			for (WormBody bodyPart : bodySegments)
				if (bodyPart.collideWith (targetPos,targetRadius))
					return true;

		return false;
	}

	/* (non-Javadoc)
	 * @see units.Virus#collideAbility(abilities.Ability)
	 */
	public boolean collideAbility (Ability ability)
	{
		if (super.collideAbility(ability))
			return true;

		for (WormBody bodyPart : bodySegments)
			if (bodyPart.collideAbility(ability))
				return true;

		return false;		
	}

	/** The "WormBody" class
	 * Each body segment of the worm
	 * @author Shiranka Miskin
	 * @version January 2012
	 */
	private class WormBody extends Virus
	{
		// The parent body part it follows
		private Virus parent;
		private int size;

		/** Creates a new body segment for the worm
		 * @param parent	the part it follows
		 * @param size		the size of the body part
		 */
		public WormBody(Virus parent, int size) {
			super(1, new Point (parent.getPos()), Toolkit.getDefaultToolkit().getImage("Sprites/WormBody.png"), null);
			this.parent = parent;
			this.size = size;
		}

		/** Moves the body part towards its parent
		 */
		public void move ()
		{
			angle = calcAngle (pos,parent.getPos());		

			xVel = speed*Math.cos(Math.toRadians(angle));
			yVel = speed*Math.sin(Math.toRadians(angle));		

			pos.setLocation(pos.getX()+xVel,pos.getY()+yVel);			
		}

		/* (non-Javadoc)
		 * @see units.Virus#run()
		 */
		public void run ()
		{
			if (AbilityHandler.getDist(pos,parent.getPos()) >= 15)
			{
				move();
			}
		}		

		/* (non-Javadoc)
		 * @see units.Unit#draw(java.awt.Graphics, java.awt.Container)
		 */
		public void draw (Graphics g, Container container)
		{
			Graphics2D g2D = (Graphics2D)g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			double angleInRadians = Math.toRadians(angle-90);

			// Rotate the graphic context, draw the image and then rotate back
			g2D.rotate (angleInRadians, pos.x, pos.y);
			g.drawImage (sprite, pos.x-size / 2, pos.y - size / 2,size,size, container);
			g2D.rotate (-angleInRadians, pos.x,pos.y);
		}
	}

}
