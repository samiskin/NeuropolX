package abilities;

import java.awt.*;
import java.util.*;

import units.*;


/** The "Scapegoat" class
 *  An ability that acts as a distraction for viruses
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Scapegoat extends Unit{

	private int level;
	private static int [] maxHealths = {150,250,500,1000};
	private static int [] duration = {150,300,500,1000};	
	private int durationLeft;
	private static int [] ranges = {100,250,500,1000};	
	private LinkedList <Virus> viruses;
	
	/** Creates a new scapegoat
	 * @param pos		the position to create it at
	 * @param sprite	the image to display
	 * @param level		the level of the scapegoat
	 * @param virusHandler	the virusHandler to interact with
	 */
	public Scapegoat (Point pos, int level, VirusHandler virusHandler)
	{
		super (maxHealths[level],pos,Toolkit.getDefaultToolkit().getImage("Sprites/Scapegoat.png"));
		viruses = virusHandler.getVirusList();
		durationLeft = duration[level];
	}
	
	/** Determines if a circular area is in range of the scapegoat's effect
	 * @param location	the location of the target
	 * @param unitSize	the size of it
	 * @return true if it is in range, false if not
	 */
	public boolean isInRange (Point location, int unitSize)
	{
		if (AbilityHandler.getDist(location, pos) <= ranges[level] + unitSize)
			return true;
		return false;
	}
	
	/** Damages the scapegoat
	 * @param damage	how much to damage it by
	 */
	public void damage (int damage)
	{
		health -= damage;
	}	

	/** Adds the scapegoat to the virus's target lists
	 *  Lowers the duration the scapegoat has left
	 */
	public void run() {
		for (Virus targetVirus : viruses)
			if (isInRange(targetVirus.getPos(),targetVirus.getSize()))
				targetVirus.addTarget(this);
		if (durationLeft > 0)
			durationLeft--;		
	}
	
	/** Finds out if the scapegoat is still alive
	 * @return true if the scapegoat is still alive, false if not
	 */
	public boolean isInEffect ()
	{
		if (isAlive() && durationLeft > 0)
			return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see units.Unit#draw(java.awt.Graphics, java.awt.Container)
	 */
	public void draw (Graphics g, Container container)
	{
		super.draw (g, container);
		drawHealthBar(g,container);
	}
	
	
}
