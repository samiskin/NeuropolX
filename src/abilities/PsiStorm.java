package abilities;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import units.Unit;


/** The "PsiStorm" class
 *  A storm that damages all units in its area
 * @author Shiranka Miskin
 * @version January 2012
 */
public class PsiStorm extends Ability {

	private Point pos;
	private static int [] size = {35,45,60,75};
	private static int [] damage = {15,30,45,60};
	private static int [] duration = {100,125,160,200};
	private static int [] range = {350,450,550,700};
	private double angle;
	
	private Image sprite;
	
	private int timeLeft;
	private int level;
	
	/** Creates a new storm
	 * @param location	The center of the storm
	 * @param level		The level of the storm
	 */
	public PsiStorm (Point location, int stormLevel)
	{
		pos = location;
		level = stormLevel;
		timeLeft = duration[level];
		sprite = Toolkit.getDefaultToolkit().getImage("Sprites/Storm.png");
		angle = 90;
	}
	
	/* (non-Javadoc)
	 * @see abilities.Ability#isInRange(units.Unit)
	 */
	public boolean isInRange (Unit unit)
	{
		if (AbilityHandler.getDist(unit.getPos(), pos) <= size[level] + unit.getSize())
			return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see abilities.Ability#isInEffect()
	 */
	boolean isInEffect() {
		if (timeLeft <= 0)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see abilities.Ability#run()
	 */
	void run() {
		angle+= 15;
		timeLeft--;
	}

	/* (non-Javadoc)
	 * @see abilities.Ability#draw(java.awt.Graphics, java.awt.Container)
	 */
	void draw(Graphics g, Container container) {
	
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		double angleInRadians = Math.toRadians(angle-90);

        // Rotate the graphic context, draw the image and then rotate back
        g2D.rotate (angleInRadians, pos.x, pos.y);
        g.drawImage (sprite, pos.x-size[level], pos.y - size[level],size[level]*2,size[level]*2, container);
        g2D.rotate (-angleInRadians, pos.x,pos.y);
		
	}
	
	/* (non-Javadoc)
	 * @see abilities.Ability#damage(units.Unit)
	 */
	public void damage (Unit unit)
	{
		unit.getDamaged(damage[level]);
	}

	/**	Gets the range of the storm
	 * @param level the level of the storm
	 * @return	the range of the storm at the specified level
	 */
	public static int getRange(int level) {
		return range[level];
	}
	
	
	
	
}
