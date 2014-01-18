package units;
import java.awt.*;
import java.awt.event.*;
import abilities.AbilityHandler;




/** The "Player" class
 * A class for the user-controlled player
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Player extends Unit
{
	private Point dest;
	
	// Velocities
	private double xVel;
	private double yVel;
	
	// Speed and deceleration rate
	private double speed;
	private double decel;
	
	private int shieldAmount;
	
	private VirusHandler virusHandler;
	private Unit crystal;
	
	/** Creates a new player
	 * @param spawnPoint	Where the player starts
	 * @param sprite		the image of the player
	 * @param viruses		the list of viruses the player faces
	 * @param gameCrystal	the crystal the player must protect
	 */
	public Player (Point spawnPoint, Image sprite, VirusHandler viruses, Unit gameCrystal)
	{
		super (1000,spawnPoint,sprite);	
		spawnPos = new Point(spawnPoint);
		dest = new Point(spawnPoint);
		virusHandler = viruses;
		speed = 0;
		decel = 0.5;
		crystal = gameCrystal;
		shieldAmount = 0;
		rooted = 0;
	}
	
	/* (non-Javadoc)
	 * @see units.Unit#reset()
	 */
	public void reset ()
	{
		super.reset();
		dest = new Point(spawnPos);
		shieldAmount = 0;
		speed = 0;
	}
	
	/** Sets the proper virus handler for the player
	 * @param viruses	the virus handler to use
	 */
	public void setVirusHandler(VirusHandler viruses)
	{
		virusHandler = viruses;
	}
	
	
	/**	Starts the player moving towards a mouse point
	 * @param event	the mouse event
	 */
	public void moveToMouse (MouseEvent event)
	{
		dest = event.getPoint();
		speed = 10;
	}
	
	
	/** Attempts to blink (teleport) the player to a location
	 * @param location	where to aim for
	 * @param range		how far the player can blink
	 * @return			true if the player can blink there, false if not 
	 */
	public boolean blink (Point location, int range)
	{
		// If the target is inside a virus or the crystal, the player can't blink there
		if (virusHandler.contains(location, radius) || crystal.collideWith(location, radius) || AbilityHandler.getDist(pos, location) > range)
		{
			return false;
		}
		pos.setLocation(location);
		dest = pos;
		// Free the player from any root effects
		rooted = 0;
		return true;
	}
	
	/** Lowers shields and moves the player
	 */
	public void run ()
	{
		// Lower shields
		if (shieldAmount > 0)
			shieldAmount -= 1;
	
		// Start slowing down if near the destination
		double distToDest = AbilityHandler.getDist(pos, dest);
		if (distToDest <= 100)
			decelerate();

		if (rooted > 0)
			rooted--;		
		else if (distToDest >= 5)
			move();
						
	}
	
	/** Moves the player to its current destination
	 */
	public void move ()
	{
		angle = AbilityHandler.calcAngle (pos,dest);
		
		xVel = speed*Math.cos(Math.toRadians(angle));
		yVel = speed*Math.sin(Math.toRadians(angle));
		
		// Move the player, then check if the player can exist there.  
		// If the player can't exist there, move it back to where it used to be
		Point oldPosition = new Point (pos);
        pos.setLocation (pos.x+xVel,pos.y+yVel);
        
        if (virusHandler.contains(this) || crystal.collideWith(this))
		{
			pos.setLocation(oldPosition);
			dest = pos;
		}			
	}
	
	
	/** Slows the player down
	 */
	private void decelerate() {
		
		speed -= decel;
		if (speed < 0)
			speed = 0;		
	}


	/**	Increases the shields of a player
	 * @param amount	how much to increase the shields by
	 */
	public void addShield (int amount)
	{
		shieldAmount += amount;
	}
	
	/** Heals the player
	 * @param amount	how much to heal by
	 */
	public void heal (int amount)
	{
		health += amount;
		if (health > maxHealth)
			health = maxHealth;
	}
	
	/** Sets the player at a certain position
	 * @param location	where to place the player
	 */
	public void setPosition (Point location)
	{
		pos = location;
	}
	
	/* (non-Javadoc)
	 * @see Units.Unit#getDamaged(int)
	 */
	public void getDamaged (int amountOfDamage)
	{
		// The shield halves the damage done
		if (shieldAmount > amountOfDamage / 2)
			shieldAmount -= amountOfDamage / 2;
		else
		{
			// Lower the damage by the amount of shield reduces it by
			amountOfDamage -= 2*shieldAmount;
			shieldAmount = 0;
			// Lower the health by whats left
			health -= amountOfDamage;
		}
		if (health < 0)
			health = 0;
	}
	
	/* (non-Javadoc)
	 * @see units.Unit#draw(java.awt.Graphics, java.awt.Container)
	 */
	public void draw (Graphics g, Container container)
	{
		// Draw the shield if it still exists
		if (shieldAmount > 0)
		{
			g.setColor(Color.green);
			g.drawOval(pos.x-radius, pos.y-radius, radius*2, radius*2);
		}
		
		super.draw (g, container);
		drawHealthBar (g,container);
        
	}
	
}
