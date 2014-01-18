package units;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

import abilities.Ability;
import abilities.AbilityHandler;


/** The "Virus" class
 * A superclass for every virus type
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Virus extends Unit{

	protected Unit currentTarget;

	//Variables dealing with Movement
	protected int speed;
	protected double xVel;
	protected double yVel;

	protected int value;
	
	protected int damage;

	// List of possible targets for the virus to aim for.
	protected LinkedList <Unit> targets;

	protected Unit crystal;

	protected double closestDist;

	/** Creates a new virus
	 * @param virusHealth	the starting health of the virus
	 * @param spawnPoint	where it spawns
	 * @param sprite		the image to display for it
	 * @param targetCrystal	the crystal it aims for
	 */
	public Virus (int virusHealth, Point spawnPoint,Image sprite, Unit targetCrystal)
	{
		super (virusHealth,spawnPoint,sprite);
		crystal = targetCrystal;
		speed = 2;
		damage = 1;
		value = 10;
		targets = new LinkedList<Unit>();
		setDestination();
	}

	/** Adds a target to the current list of targets of the virus
	 * @param unit What unit to target
	 */
	public void addTarget (Unit unit)
	{
		if (!targets.contains(unit))
			targets.add(unit);
	}

	/** Looks through the current targets, finds the closest one,
	 *  then sets that as the current destination
	 */
	public void setDestination()
	{		

		closestDist = Integer.MAX_VALUE;
		// If there are no available targets, stay still.
		if (targets.isEmpty())
			currentTarget = null;
		else
		{
			// If there are targets, find the closest one and set that as the destination
			Iterator <Unit> targetItr = targets.iterator();
			while (targetItr.hasNext())
			{
				Unit unit = targetItr.next();
				if (!unit.isAlive())
					targetItr.remove();
				else if (!(unit == crystal))
				{
					double unitDistance = AbilityHandler.getDist(pos, unit.getPos());
					if (unitDistance < closestDist)
					{				
						currentTarget = unit;
						closestDist = unitDistance;
					}
				}
			}
			// The user may distract viruses from the crystal from a further distance,
			// because the viruses would be impossible to remove from the crystal otherwise 
			// (they are already at the closest range possible)
			if (closestDist > AbilityHandler.getDist(pos, crystal.getPos()) + 50)
				currentTarget = crystal;
		}
	}

	/** Calculates the angle between 2 points
	 * @param p1  	the first point
	 * @param p2	the second point
	 * @return		The angle between the two points from p1 to p2 in degrees
	 */
	public double calcAngle (Point p1, Point p2)
	{
		return AbilityHandler.calcAngle(p1, p2);
	}

	/** Moves the virus towards the current destination. 
	 */
	public void moveToDest ()
	{		
		angle = calcAngle (pos,currentTarget.getPos());		

		xVel = speed*Math.cos(Math.toRadians(angle));
		yVel = speed*Math.sin(Math.toRadians(angle));		

		pos.setLocation(pos.getX()+xVel,pos.getY()+yVel);
	}

	/** Runs all necessary methods for the virus
	 */
	public void run ()
	{
		setDestination();
		if (currentTarget!= null)
		{			
			if (currentTarget.collideWith(this))
				currentTarget.getDamaged(damage);
			else
				moveToDest();
		}
	}


	/** Checks if an ability has hit the virus
	 * @param ability
	 * @return true if the ability has hit the virus, false if not
	 */
	public boolean collideAbility (Ability ability)
	{
		if (ability.isInRange(this))
			return true;
		return false;
	}
	
	
	/** Lets the virus get damaged by an ability if it collides
	 * @param ability	the ability to check
	 */
	public void checkAbility (Ability ability)
	{
		if (collideAbility(ability))
			ability.damage(this);
	}

	/** Finds out how much points the virus gives when it is killed
	 * @return	the value of the virus
	 */
	public Integer getValue() {
		return value;
	}


}
