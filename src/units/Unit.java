package units;

import java.awt.*;

/** The "Unit" class
 *  Superclass for all units (player, viruses, scapegoats)
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Unit {
	
	protected int health;
	protected int maxHealth;
	protected Point pos;
	protected Point spawnPos;
	protected int radius;
	protected double angle;
	protected int rooted;
	
	protected Image sprite;
	
	/** Creates a new unit
	 * @param health	the starting health of the unit
	 * @param pos		the starting position of the unit
	 * @param sprite	the image of the unit
	 */
	public Unit (int health, Point spawnPoint, Image unitSprite)
	{		
		maxHealth = health;
		pos = spawnPoint;
		spawnPos = new Point (pos);
		sprite = unitSprite;
		reset();
		
		// The collision detection is based off a circle around the unit.  
		// the circle is as big as the height of the image as every image is 
		// a perfect square, and the contents are usually circular
        radius = sprite.getHeight(new Container ())/2;
		
	}
	
	/** Resets the unit values
	 */
	public void reset ()
	{
		health = maxHealth;
		angle = 270;
		rooted = 0;
		pos = new Point(spawnPos);
	}
	
	/** Roots the unit in place
	 * @param duration the duration of the root effect
	 */
	public void root (int duration)
	{
		rooted = duration;
	}
	
	/** Damages the unit
	 * @param amountOfDamage how much the unit is damaged by
	 */
	public void getDamaged (int amountOfDamage)
	{
		health -= amountOfDamage;
	}
	
	/** Lets other classes learn the position of the unit
	 * @return 	the position of the unit
	 */
	public Point getPos ()
	{
		return pos;
	}
	
	/** Tells other classes the radius of the unit
	 * @return	the radius of the unit
	 */
	public int getRadius()
	{
		return radius;
	}
	
	/** Tells other classes the current health of the unit
	 * @return	the current health of the unit
	 */
	public int getHealth()
	{
		return health;
	}
	
	/** Tells other classes the maximum health of the unit
	 * @return	the maximum health of the unit
	 */
	public int getMaxHealth()
	{
		return maxHealth;
	}
	
	/** Tells if the unit is still alive on the field
	 * @return	true if the unit is alive, false if it is dead
	 */
	public boolean isAlive ()
	{
		if (health > 0)
			return true;
		return false;
	}
	
	/** Finds if a unit collides with another unit
	 * @param unit	the other unit to check
	 * @return	true if they collide, false if not
	 */
	public boolean collideWith (Unit unit)
	{
		return collideWith (unit.getPos(),unit.radius);
	}
	
	/** Finds out if the unit collides with a circular object
	 * @param targetPos		the center of the circle
	 * @param targetRadius	the radius of the circle
	 * @return	true if they collide, false if not
	 */
	public boolean collideWith (Point targetPos, int targetRadius)
	{
		int distanceX = targetPos.x - pos.x;
		int distanceY = targetPos.y - pos.y;
		int sumOfRadii = radius+targetRadius;
		
		if (distanceX*distanceX + distanceY*distanceY < sumOfRadii*sumOfRadii)
			return true;
		return false;
	}
		

	/** Heals the unit to maximum health
	 */
	public void fullHeal ()
	{
		health = maxHealth;
	}
	
	/** Draws a health bar above the unit
	 * @param g			the graphic context
	 * @param container	the container to draw on
	 */
	public void drawHealthBar(Graphics g, Container container)
	{
        g.setColor(Color.red);
        g.fillRect(pos.x-radius-5, pos.y-radius-10, (int)((1.0*health / maxHealth)*(radius*2+10)), 5);      
        g.setColor(Color.black); 
        g.drawRect(pos.x-radius-5, pos.y-radius-10, radius*2+10, 5);
	}
	
	/** Draws the unit
	 * @param g			the graphic context
	 * @param container	the container to draw on
	 */
	public void draw (Graphics g, Container container)
	{
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		double angleInRadians = Math.toRadians(angle-90);
		radius = Math.max(sprite.getWidth(container), sprite.getHeight(container))/2 - 5;

        // Rotate the graphic context, draw the image and then rotate back
        g2D.rotate (angleInRadians, pos.x, pos.y);
        g.drawImage (sprite, pos.x-radius - 5, pos.y - radius - 5, container);
        g2D.rotate (-angleInRadians, pos.x,pos.y);
	}

	/** Gets the size of the unit
	 * @return	the size of the unit
	 */
	public int getSize() {
		return radius;
	}

}
