package units;

import java.awt.Point;
import java.awt.Toolkit;
import abilities.AbilityHandler;


/** The "BruteForce" class
 * A virus that charges at the enemy and can hook them
 * @author Shiranka Miskin
 * @version January 2012
 */
public class BruteForce extends Virus{

	private boolean charging;
	private Point target;

	private int chargeRange = 1000;
	private int maxCooldown = 250;
	private int chargeCooldown = 5;

	/** Creates a new BruteForce virus
	 * @param spawnPoint	Where the virus will spawn
	 * @param targetCrystal	the crystal to target
	 */
	public BruteForce (Point spawnPoint, Unit targetCrystal)
	{
		super (1000,spawnPoint, Toolkit.getDefaultToolkit().getImage("Sprites/BruteForce.png"), targetCrystal);
		speed = 3;
		value = 100;
		target = new Point ();
	}

	/** Looks through the current targets, finds the closest one,
	 *  then sets that as the current destination
	 */
	public void setDestination()
	{			
		// If the virus is charging, check if its reached its destination
		if (charging)
			if (AbilityHandler.getDist(pos,target) < radius + currentTarget.getSize())
				charging = false;
			else
				return;

		// Move normally if not charging
		super.setDestination();

		// If the virus can charge again, find a point to charge at and 
		// start attacking
		if (!charging && chargeCooldown <= 0 && currentTarget != crystal && closestDist < chargeRange)
		{
			charging = true;
			chargeCooldown = maxCooldown;
			// Set the charge point further than the target, to show that its
			// recklessly charging at you and it can't stop easily
			double chargeAngle = calcAngle (pos,currentTarget.getPos());	
			target.x = currentTarget.getPos().x+(int)(100*Math.cos(Math.toRadians(chargeAngle)));
			target.y = currentTarget.getPos().y+(int)(100*Math.sin(Math.toRadians(chargeAngle)));
		}
	}


	/** Moves the virus towards the current destination. 
	 */
	public void chargeToDest (Point chargeTarget)
	{		
		angle = calcAngle (pos,chargeTarget);		

		// Move 4 times faster than normal towards the target
		int chargeSpeed = speed*4;

		xVel = chargeSpeed*Math.cos(Math.toRadians(angle));
		yVel = chargeSpeed*Math.sin(Math.toRadians(angle));		

		pos.setLocation(pos.getX()+xVel,pos.getY()+yVel);
	}

	/** Runs all necessary methods for the virus
	 */
	public void run ()
	{
		setDestination();
		if (currentTarget!= null)
		{			
			// If the virus has hit its target, stop moving and attack
			if (currentTarget.collideWith(this))
			{
				currentTarget.getDamaged(damage);
				charging = false;
			}
			else
			{
				if (charging)
					chargeToDest (target);
				else
					moveToDest();
			}
		}
		// Cool down its charge ability
		if (chargeCooldown > 0)
			chargeCooldown--;
	}

}
