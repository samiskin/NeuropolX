package abilities;

import java.awt.*;

import units.Unit;


/** The "Laser" class
 * A laser that damages units in a line
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Laser extends Ability {

	// Variables for where the line (that is the laser) starts and ends
	private Point pos;
	private Point end;
	
	// If the laser is still on the screen
	private boolean inEffect;
	
	// The size of the laser (since it gets smaller and smaller
	private int currentSize;
	
	private static int [] damage = {25,35,50,65};
	private static int [] size = {15,25,35,50};
	private int level;
	
	/** Creates a new laser on the map
	 * @param location	The starting location of the laser
	 * @param angle		The angle its aiming towards
	 * @param level		The level of the laser
	 */
	public Laser (Point location,double angle, int level)
	{
		pos = new Point (location);
		this.level = level;
		currentSize = size[level]*2;
		inEffect = true;
		
		end = new Point ((int)(pos.x + 2000*Math.cos(Math.toRadians(angle))), (int)(pos.y+2000*Math.sin(Math.toRadians(angle))));
	}
	
	/* (non-Javadoc)
	 * @see abilities.Ability#isInRange(units.Unit)
	 */
	public boolean isInRange (Unit unit)
	{
		// If the unit is on the same side as where the line is travelling and 
		// the distance between that unit and the line is less than the range,
		// the unit is being hit by the laser.  Without the first part units
		// behind the player would be hit as well. 
		if (((unit.getPos().x > pos.x && end.x > pos.x) || (unit.getPos().x < pos.x && end.x < pos.x)) && 
			AbilityHandler.distancePointLine(new Point (unit.getPos()), pos, end) <= currentSize/2 + unit.getSize())
			return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see abilities.Ability#damage(units.Unit)
	 */
	public void damage (Unit unit)
	{
		unit.getDamaged(damage[level]);
	}

	/* (non-Javadoc)
	 * @see abilities.Ability#isInEffect()
	 */
	boolean isInEffect() {
		return inEffect;
	}

	/* (non-Javadoc)
	 * @see abilities.Ability#run()
	 */
	void run() {
		
		if (inEffect)
		{
		currentSize -= size[level]/5;
		if (currentSize < 0)
			inEffect = false;	
		}
		
	}

	/* (non-Javadoc)
	 * @see abilities.Ability#draw(java.awt.Graphics, java.awt.Container)
	 */
	void draw(Graphics g, Container container) {
		
		if (inEffect)
		{
			g.setColor (Color.CYAN);
			g.fillOval(pos.x-currentSize, pos.y-currentSize,currentSize*2,currentSize*2);
			Graphics2D g2 = (Graphics2D)g;
			g2.setStroke(new BasicStroke(currentSize));
			g2.drawLine(pos.x, pos.y, end.x, end.y);
			g2.setStroke(new BasicStroke(0));
		}
		
	}
		
	
	
}
