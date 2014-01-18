package units;

import java.awt.Point;
import java.awt.Toolkit;

/** The "DDoS" class
 * A basic virus that simply moves and attacks
 * @author Shiranka Miskin
 * @version January 2012
 */
public class DDoS extends Virus {
	
	public DDoS(Point spawnPoint, Unit targetCrystal) {
		super(250, spawnPoint, Toolkit.getDefaultToolkit().getImage("Sprites/DDoS.png"), targetCrystal);		
		damage = 1;
		health = 200;
	}

}
