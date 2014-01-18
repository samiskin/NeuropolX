package abilities;

import gui.Screen;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;

import units.Player;
import units.Unit;
import units.Virus;
import units.VirusHandler;

/** The "AbilityHandler" class
 * A class that handles all the abilities in play
 * @author Shiranka Miskibn
 * @version January 2012
 */
public class AbilityHandler {

	// Basic objects that either hold abilities or interact with them
	private Player player;
	private VirusHandler virusHandler;
	private LinkedList <Ability> abilitiesInPlay;
	private LinkedList <Scapegoat> scapeGoats;
	private boolean sound;

	// The image that is used to display the range of an ability
	private Image rangeDisplay = Toolkit.getDefaultToolkit().getImage("Sprites/rangeDisplay.png");


	/* Ability index guide
	 * 0. Void Sphere
	 * 1. Heal
	 * 2. Laser
	 * 3. Blink
	 * 4. Overshield
	 * 5. Storm
	 * 6. Scapegoat
	 * 7. Nuke
	 */
	private int [] abilityLevels;	// The level of each ability
	private static int [][] cooldownLevels = {	
		{5,6,7,8},  // Void Sphere
		{500,600,700,800},  // Heal
		{20,15,10,5},  // Laser
		{200,150,100,0},  // Blink
		{300,400,600,800},  // Overshield
		{40,50,100,150},  // Storm
		{500,600,700,800},  // Scapegoat
		{1000,2000,3000,4000}};// Nuke
	private int [] cooldowns;
	private int activeAbility;

	// Stats for abilities that don't have their own object
	private static int [] shieldAmounts = {250,500,750,1000};
	private static int [] blinkDistances = {200,350,500,1000};
	private static int [] healAmounts = {100,250,500,1000};
	private static int [] nukeDamages = {250,500,750,1000};
		
	// All the sound effects for each ability
	private AudioClip voidSphereFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Voidsphere.wav"));
	private AudioClip healFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Heal.wav"));
	private AudioClip laserFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Laser.wav"));	
	private AudioClip blinkFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Blink.wav"));
	private AudioClip shieldFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Overshield.wav"));
	private AudioClip stormFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Storm.wav"));
	private AudioClip scapeGoatFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Scapegoat.wav"));
	private AudioClip nukeFX = Applet.newAudioClip(Screen.getCompleteURL ("Sound/Nuke.wav"));


	/** Constructor for the ability handler
	 * @param player 	The player that uses the abilities
	 * @param viruses	The handler for the viruses to interact with
	 */
	public AbilityHandler (Player mainPlayer, VirusHandler viruses)
	{
		player = mainPlayer;		
		virusHandler = viruses;
		abilitiesInPlay = new LinkedList <Ability> ();	
		scapeGoats = new LinkedList <Scapegoat>();
		clearLevels();
		refreshCooldowns();
		activeAbility = -1;
		sound = true;		
	}

	/** Resets the levels of all the abilities
	 */
	public void clearLevels()
	{
		abilityLevels = new int [8];
	}

	/** Resets the cooldowns of all the abilities
	 */
	public void refreshCooldowns ()
	{
		cooldowns = new int [8];
	}

	/** Gets the distance between two points in a 2D plane
	 * @param p1	The first point
	 * @param p2	The second point
	 * @return		The distance between the two points
	 */
	public static double getDist (Point p1, Point p2)
	{
		double distance = Math.sqrt((p1.x - p2.x)*(p1.x - p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
		return distance;
	}

	/** Gets the distance between two units on a 2D plane
	 * @param unit1		The first unit
	 * @param unit2		The second unit
	 * @return			The distance between the two
	 */
	public static double getDist (Unit unit1, Unit unit2)
	{
		return getDist (unit1.getPos(), unit2.getPos());
	}

	/** Determines if two circles overlap each other
	 * @param p1	The center of the first circle
	 * @param p2	The center of the second circle
	 * @param r1	The radius of the first circle
	 * @param r2	The radius of the second circle
	 * @return true if the two circles overlap, false if they do not
	 */
	public static boolean circleCollision (Point p1, Point p2, int r1, int r2)
	{
		int deltaX = p2.x - p1.x;
		int deltaY = p2.y - p1.y;
		int sumOfRadii = r1+r2;

		if (deltaX*deltaX + deltaY*deltaY <= sumOfRadii*sumOfRadii)
			return true;
		return false;
	}

	/** Determines the angle between two points
	 * @param p1	The first point
	 * @param p2	The second point
	 * @return The angle of the line segment from p1 to p2
	 */
	public static double calcAngle (Point p1, Point p2)
	{
		double angle = Math.atan2(p2.y-p1.y,p2.x-p1.x)*180/Math.PI;
		return angle;
	}

	/** Determines the distance between a point and a line 
	 * @param location 	The place to test
	 * @param p1		The first point of the line segment
	 * @param p2		The second point of the line segment		
	 * @return	the distance between the location and the line segment
	 */
	public static double distancePointLine (Point location, Point p1, Point p2)
	{
		int A = p1.y - p2.y;
		int B = p2.x - p1.x;
		int C = p1.x*p2.y-p2.x*p1.y;

		// d = |Ax+By+C| / sqrt(A^2+B^2)
		double d = Math.abs(A*(location.x)+B*location.y+C) / (Math.sqrt(A*A+B*B));
		return d;
	}



	/** Runs every ability in play and deletes any that shouldn't exist
	 */
	public void run ()
	{
		// Lowers the cooldown of every ability
		for (int ability = 0; ability < 8; ability++)
		{
			if (cooldowns[ability] > 0)
				cooldowns[ability]--;
		}

		// Runs each ability and removes any that are complete
		Iterator<Ability> abilityItr = abilitiesInPlay.iterator();
		while (abilityItr.hasNext())
		{
			Ability ability = abilityItr.next();
			ability.run();
			if (!ability.isInEffect())
				abilityItr.remove();
		}

		// Runs every scapegoat and removes them if they are dead
		Iterator <Scapegoat> scapeGoatItr = scapeGoats.iterator();
		while (scapeGoatItr.hasNext())
		{
			Scapegoat scapeGoat = scapeGoatItr.next();
			scapeGoat.run();
			if (!scapeGoat.isInEffect())
				scapeGoatItr.remove();
		}

		// Lets every ability interact with every virus
		for (Virus targetVirus : virusHandler.getVirusList())
		{
			for (Ability ability : abilitiesInPlay)
			{
				targetVirus.checkAbility(ability);
			}
		}	
		
	}

	/** Draws every ability on the screen
	 * @param g  the graphics context
	 * @param container  the container to draw on
	 */
	public void draw (Graphics g, Container container)
	{

		// Draws range displays for Blink and Storm
		if (activeAbility == 3)
		{
			int blinkRange = blinkDistances[abilityLevels[3]];
			g.drawImage(rangeDisplay,player.getPos().x - blinkRange,player.getPos().y - blinkRange,blinkRange*2,blinkRange*2,container);
		}
		else if (activeAbility == 5)
		{
			int stormRange = PsiStorm.getRange(abilityLevels[5]);
			g.drawImage(rangeDisplay,player.getPos().x - stormRange,player.getPos().y - stormRange,stormRange*2,stormRange*2,container);
		}

		// Draws all the abilities and scapegoats
		for (Ability ability : abilitiesInPlay)
		{
			ability.draw(g, container);
		}
		for (Scapegoat scapeGoat : scapeGoats)
		{
			scapeGoat.draw(g,container);
		}

	}

	/** Sets the current active ability
	 * @param ability The ability to set it to
	 */
	public void setActiveAbility(int ability) {
		if (ability >= 0 && ability <= 7)
			activeAbility = ability;
		else
			activeAbility = -1;
	}

	/** Starts the cooldown of an ability
	 * @param ability The ability to start cooling down
	 */
	private void startCooldown (int ability)
	{
		cooldowns[ability] = cooldownLevels[ability][abilityLevels[ability]];
	}

	/** Finds out what ability is currently active
	 * @return the active ability
	 */
	public int getActiveAbility()
	{
		return activeAbility;
	}
	
	/**	Gets the maximum cooldown of an ability
	 * @param ability	which ability to check
	 * @param level		the level of that ability
	 * @return	the maximum time it takes before the player can use the ability again
	 */
	public static int getMaxCooldown(int ability, int level)
	{
		return cooldownLevels[ability][level];
	}

	/** Handles key input
	 * @param event 	the key event
	 */
	public void keyInput (KeyEvent event)
	{

		int ability = Integer.MAX_VALUE;

		switch (event.getKeyCode())
		{
		// Activate the proper ability based on the key code
		case KeyEvent.VK_Q: ability = 0;
		break;
		case KeyEvent.VK_W: ability = 1;
		break;
		case KeyEvent.VK_E: ability = 2;
		break;
		case KeyEvent.VK_R: ability = 3;
		break;
		case KeyEvent.VK_A:  ability = 4;
		break;
		case KeyEvent.VK_S: ability = 5;
		break;
		case KeyEvent.VK_D:  ability = 6;
		break;
		case KeyEvent.VK_F:  ability = 7;
		break;
		case KeyEvent.VK_ESCAPE:  ability = -1;
		break;		
		}

		// If no ability has been activated, leave
		if (ability == Integer.MAX_VALUE)
			return;

		if (ability == -1)
		{
			setActiveAbility(-1);
			return;
		}
		
		// Only set the active ability if it isn't on cooldown
		if (cooldowns[ability] <= 0)
		{
			// For the abilities that don't need a secondary mouse activation,
			// activate it immediately.
			switch (ability)
			{
			case 1: if (player.getHealth() < player.getMaxHealth())
					{
						player.heal(healAmounts[abilityLevels[1]]);
						playSoundFX(1);
						startCooldown(1);
					}
					break;
			case 4: player.addShield(shieldAmounts[abilityLevels[4]]);
					playSoundFX(4);
					startCooldown (4);
					break;
			case 7:	virusHandler.damageAll(nukeDamages[abilityLevels[7]]);
					playSoundFX(7);
					startCooldown(7);
					break;
			default: setActiveAbility(ability);
					 break;
			}

		}
	}
	
	/** Plays the sound effects for an ability
	 * @param ability	the ability that is being used
	 */
	private void playSoundFX(int ability)
	{
		// If the game is muted, don't play the sound
		if (!sound)
			return;
		switch (ability)
		{
		case 0: voidSphereFX.play();
				break;
		case 1: healFX.play();
				break;
		case 2: laserFX.play();
				break;
		case 3: blinkFX.play();
				break;
		case 4: shieldFX.play();
				break;
		case 5: stormFX.play();
				break;
		case 6: scapeGoatFX.play();
				break;
		case 7: nukeFX.play();
				break;
		}
	}

	/** Casts an ability if it is further activated from the mouse
	 * @param event 	The mouse event
	 */
	public void castAbility(MouseEvent event) {

		if (activeAbility >= 0 && cooldowns[activeAbility] <= 0)
		{
			// Only restart the cooldown if the ability has been casted	
			// Certain methods that depend on the player return a boolean
			// to denote if it has been successfully casted or not
			boolean hasCasted = false;

			switch (activeAbility)
			{
			case 0 : abilitiesInPlay.add(new VoidSphere (player.getPos(), calcAngle(player.getPos(), event.getPoint()), abilityLevels[activeAbility]));
					playSoundFX(0);
					hasCasted = true;
					break;
			
			case 2 : abilitiesInPlay.add(new Laser (player.getPos(),calcAngle(player.getPos(),event.getPoint()),abilityLevels[activeAbility]));;
					playSoundFX(2);
					hasCasted = true; 
					break;
			
			case 3 : if (player.blink(event.getPoint(), blinkDistances[abilityLevels[activeAbility]]))
					{
						playSoundFX(3);
						hasCasted=true;
					}
					break;
			
			case 5 :if (getDist(player.getPos(),event.getPoint()) <= PsiStorm.getRange(abilityLevels[5]))
					{
						abilitiesInPlay.add(new PsiStorm(event.getPoint(), abilityLevels[5]));
						playSoundFX(5);
						hasCasted = true;
					}
					break;
			
			case 6 : Scapegoat scapeGoat = new Scapegoat (event.getPoint(), abilityLevels[6], virusHandler);
					if (!virusHandler.contains(scapeGoat))
					{
						scapeGoats.add(scapeGoat);
						playSoundFX(6);
						hasCasted = true;
					}
					break;
			}
			
			// Start the cooldown if necessary, then reset the active ability
			if (hasCasted)
				startCooldown(activeAbility);
			
			// The ability is reset every time so that the user
			// is disinclined to spam abilities too much.  
			activeAbility = -1;
		}

	}

	/** Clears all the variables to reset the object
	 */
	public void clear() {

		abilitiesInPlay.clear();
		scapeGoats.clear();
		clearLevels();
		refreshCooldowns();

	}

	/** Gets the level of the specified ability
	 * @param ability	The ability to check
	 * @return 	the level of the ability
	 */
	public int getLevel (int ability)
	{
		return abilityLevels[ability];
	}

	/** Upgrades an ability
	 * @param ability	the ability to upgrade
	 */
	public void upgradeAbility(int ability) {
		abilityLevels[ability] ++;
	}

	/** Finds out the current amount of cooldown for an ability
	 * @param ability	the ability to check
	 * @return	the amount of cooldown of the ability
	 */
	public int getCooldown(int ability) {
		return cooldowns[ability];
	}

	/** Switches sound on/off
	 */
	public void toggleSound() {
		sound = !sound;		
	}



}
