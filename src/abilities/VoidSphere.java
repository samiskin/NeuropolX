package abilities;

import java.awt.*;
import units.Unit;



/** The "VoidSphere" class
 * A circular ball of energy that damages a unit it hits and is then destroyed
 * @author Shiranka Miskin
 * @version January 2012
 */
public class VoidSphere extends Ability {

	private Point pos;
	private boolean active;
	private double angle;
	private double speed;
	private double xVel;
	private double yVel;
	private static int [] size = {7,10,15,25};
	private static int [] damage = {150,250,400,550};
	private int level;
	
	private Image sprite = Toolkit.getDefaultToolkit().getImage("Sprites/VoidSphere.png");
	
	/** Creates a new void sphere
	 * @param pos 	the sphere's starting position on the screen
	 * @param angle the angle it is traveling at
	 * @param level	the level of the sphere, determining its size and damage
	 */
	public VoidSphere (Point pos, double angle, int level)
	{
		this.pos = new Point (pos);
		this.angle = angle;
		this.level = level;
		active = true;
		speed = 20;
	}
	
	
	/* (non-Javadoc)
	 * @see abilities.Ability#isInRange(units.Unit)
	 */
	public boolean isInRange (Unit targetUnit)
	{
		return targetUnit.collideWith(pos,size[level]);
	}
	
	/** Moves the sphere
	 */
	public void move()
	{
		xVel = speed*Math.cos(Math.toRadians(angle));
		yVel = speed*Math.sin(Math.toRadians(angle));		
		
		pos.setLocation(pos.getX()+xVel,pos.getY()+yVel);
	}
	
	/* (non-Javadoc)
	 * @see abilities.Ability#draw(java.awt.Graphics, java.awt.Container)
	 */
	public void draw (Graphics g, Container container)
	{
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		double angleInRadians = Math.toRadians(angle);

        // Rotate the graphic context, draw the image and then rotate back
        g2D.rotate (angleInRadians, pos.x, pos.y);
        g.drawImage (sprite, pos.x-size[level], pos.y - size[level],size[level]*2,size[level]*2, container);
        g2D.rotate (-angleInRadians, pos.x,pos.y);
		
	}
	
	
	/* (non-Javadoc)
	 * @see abilities.Ability#isInEffect()
	 */
	public boolean isInEffect() {
		return active;
	}

	/* (non-Javadoc)
	 * @see abilities.Ability#run()
	 */
	public void run() {
		move ();		
	}
	
	/* (non-Javadoc)
	 * @see abilities.Ability#damage(units.Unit)
	 */
	public void damage (Unit unit)
	{
		unit.getDamaged(damage[level]);
		active = false;
	}

	
}
