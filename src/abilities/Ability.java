package abilities;

import java.awt.Container;
import java.awt.Graphics;
import units.Unit;


/** The Ability class
 * Abstract framework for every ability object
 * @author Shiranka Miskin
 * @version January 2012
 */
public abstract class Ability {
	
	/** Finds out whether the enemy unit is in range of the ability
	 * @param unit What unit to check
	 * @return true if the unit is in range, false if it is not
	 */
	public abstract boolean isInRange(Unit unit);
	
	/** Whether the ability is still active (should not be deleted yet)
	 * @return true if it is still active, false if not
	 */
	abstract boolean isInEffect();
	
	/** Runs all operations for the ability (like moving, or lowering the duration)
	 */
	abstract void run();
	
	/** Draws the ability on the screen
	 * @param g 		The graphic context
	 * @param container The container to draw on
	 */
	abstract void draw(Graphics g, Container container);
	
	/** Damages the target unit
	 * @param unit	The target unit
	 */
	public abstract void damage (Unit unit);
	
	
}
