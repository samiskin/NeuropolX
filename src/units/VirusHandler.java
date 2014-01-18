package units;
import gui.GameScreen;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;


/** The "VirusHandler" class
 * An object that manages all viruses in play
 * @author Shiranka Miskin
 * @version January 2012
 */
public class VirusHandler {


	private LinkedList <Virus> virusList;
	private Player player;
	private Unit crystal;
	private GameScreen game;
	
	/** Starts up a new virus handler
	 * @param player		the main player
	 * @param crystal		the crystal to target
	 * @param gameScreen	the menu that holds all the game stats
	 */
	public VirusHandler (Player gamePlayer, Unit gameCrystal, GameScreen gameScreen)
	{
		virusList = new LinkedList<Virus>();
		player = gamePlayer;
		crystal = gameCrystal;
		game = gameScreen;
	}
	
	/** Creates a new virus
	 * @param loc	Where the virus spawns
	 * @param type	What type of virus to spawn
	 */
	public void newVirus (Point loc, int type)
	{
		Virus newVirus;
		switch (type)
		{
		case 1 : newVirus = new Worm (loc,crystal);
				 break;
		case 2 : newVirus = new Rootkit (loc,crystal);
				 break;
		case 3 : newVirus = new BruteForce (loc, crystal);
				break;
		default: newVirus = new DDoS(loc, crystal);
		}
		newVirus.addTarget(crystal);
		newVirus.addTarget (player);
		virusList.add(newVirus);
		
	}
	
	/** Checks if a unit is colliding with any of the viruses in play
	 * @param unit	the unit to check
	 * @return	true if they collide, false if not
	 */
	public boolean contains (Unit unit)
	{
		return contains(unit.getPos(),unit.getSize());
	}
	
	/** Checks if a circle collides with any viruses in play
	 * @param targetPos		the center of the circle
	 * @param targetRadius	the radius of the circle
	 * @return 	true if they collide, false if not
	 */
	public boolean contains (Point targetPos, int targetRadius)
	{
		for (Virus virus : virusList)
		{
			if (virus.collideWith(targetPos,targetRadius))
				return true;
		}
		return false;
	}
	
	/** Runs every virus in play and deletes any that are dead 
	 */
	public void run()
	{
		Iterator<Virus> virusItr = virusList.iterator();
		while (virusItr.hasNext())
		{
			Virus virus = virusItr.next();
			virus.run();
			if (!virus.isAlive())
			{
				game.increaseScore(virus.getValue());
				virusItr.remove();
			}
		}		
	}
	
	/** Draws all the viruses
	 * @param g			the graphic context
	 * @param container	the container to draw on
	 */
	public void draw(Graphics g, Container container)
	{
		for (Virus virus : virusList)
		{
			virus.draw(g, container);
		}
	}
	
	/** Damages all viruses in play
	 * @param damage
	 */
	public void damageAll (int damage)
	{
		for (Virus virus : virusList)
		{
			virus.getDamaged(damage);
		}
	}

	/** Lets something interact with all the viruses
	 * @return	the list of viruses in play
	 */
	public LinkedList<Virus> getVirusList() {
		return virusList;
	}

	/** Clears all viruses in play
	 */
	public void clear() {
		virusList.clear();		
	}
	

	/** Finds out if there are no viruses currently in play
	 * @return	true if there aren't any, false if there are
	 */
	public boolean isEmpty ()
	{
		return virusList.isEmpty();
	}
	
}
