package units;

import java.awt.Point;
import java.awt.Toolkit;

/** The "Rootkit" class
 * A virus that shoots out projectiles that damage and stun the player
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Rootkit extends Spitter{
	
	private int rootDuration;
	
	/** Creates a new Rootkit virus
	 * @param spawnPoint	Where the rootkit starts at
	 * @param targetCrystal	The crystal it means to destroy
	 */
	public Rootkit (Point spawnPoint, Unit targetCrystal) 
	{
		super(120, spawnPoint, Toolkit.getDefaultToolkit().getImage("Sprites/Rootkit.png"),
				targetCrystal, Toolkit.getDefaultToolkit().getImage("Sprites/RootSpit.png"));
		damage = 10;
		range = 300;
		health = 400;
		value = 50;
		maxCooldown = 50;
		spitCooldown = 0;
		rootDuration = 25;
	}
	
	/** Damages and stuns a target
	 * @param target	the unit to attack
	 */
	public void attack (Unit target)
	{
		target.getDamaged(damage);
		target.root(rootDuration);
	}

	/* (non-Javadoc)
	 * @see units.Unit#collideWith(java.awt.Point, int)
	 */
	public boolean collideWith (Point targetPos, int targetRadius)
	{
		if (super.collideWith (targetPos,targetRadius))
			return true;
		return false;
	}

	
}
