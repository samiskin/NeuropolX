package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import units.*;
import abilities.AbilityHandler;



/** The "GameScreen" class
 * The menu that the game runs on
 * @author Shiranka Miskin
 * @version January 2012
 */
public class GameScreen extends Menu{

	// Variables to hold the game objects
	private Player player;		
	private Unit crystal;
	private VirusHandler virusHandler;
	private AbilityHandler abilityHandler;
	private LinkedList <AbilityBox> abilityBoxes;

	// Variables for the statistics of the current game
	private int score;
	private int experiance;
	
	private PauseMenu pauseMenu;
	
	private int skillPoints;
	private int difficulty;
	private int levelWait;
	private int levelWaitTime;

	// The images that are used in the game
	private Image abilityMenu = Toolkit.getDefaultToolkit().getImage("Sprites/AbilityBoxes.png");
	private Image scoreMenu = Toolkit.getDefaultToolkit().getImage("Sprites/ScoreMenu.png");
	private Image playerImage = Toolkit.getDefaultToolkit().getImage("Sprites/norton.png");
	private Image healthBar = Toolkit.getDefaultToolkit().getImage("Sprites/HealthBar.png");
	// The width of the health bar in the picture
	private int healthBarWidth = 715;
	private Font abilityFont = new Font ("Arial", Font.BOLD,15);
	private Font scoreFont = new Font ("Arial", Font.BOLD,20);
	private int level;
	

	/** Creates a new game screen and adds everything necessary for it
	 * @param container	The container to run on
	 * @param sound 	the boolean that handles if the sound is on or off
	 */
	public GameScreen (Container container)
	{
		// Starts a menu that displays the background image
		super (Toolkit.getDefaultToolkit().getImage("Backgrounds/GameBackground.jpg"),container);

		// Initializes all the game objects
		crystal = new Unit (5000, new Point (500,600),Toolkit.getDefaultToolkit().getImage("Sprites/Crystal.png"));
		player = new Player (new Point (500,450),playerImage,virusHandler, crystal);
		virusHandler = new VirusHandler (player, crystal, this);
		player.setVirusHandler(virusHandler);
		abilityHandler = new AbilityHandler (player, virusHandler);	
		abilityBoxes = new LinkedList <AbilityBox>();
		abilityBoxes.add(new AbilityBox (0,new Point (14,554), new Dimension (61,61)));
		abilityBoxes.add(new AbilityBox (1,new Point (86,554), new Dimension (61,61)));
		abilityBoxes.add(new AbilityBox (2,new Point (158,554), new Dimension (61,61)));
		abilityBoxes.add(new AbilityBox (3,new Point (230,554), new Dimension (61,61)));
		abilityBoxes.add(new AbilityBox (4,new Point (38,625), new Dimension (61,61)));
		abilityBoxes.add(new AbilityBox (5,new Point (110,625), new Dimension (61,61)));
		abilityBoxes.add(new AbilityBox (6,new Point (182,625), new Dimension (61,61)));
		abilityBoxes.add(new AbilityBox (7,new Point (254,625), new Dimension (61,61)));
		restart();
		
	}

	/** Switches sound effects on/off
	 */
	public void toggleSound()
	{
		abilityHandler.toggleSound();
	}

	/** Resets all the objects on the screen
	 */
	public void clearObjects()
	{
		player.reset();
		crystal.reset();
		virusHandler.clear();
		abilityHandler.clear();		
	}


	/** Restarts the game
	 */
	public void restart() {
		clearObjects();
		player.reset();
		score = 0;
		experiance = 0;
		difficulty = 0;
		skillPoints = 0;
		level = 1;
		levelWait = 0;
		levelWaitTime = 100;
	}

	/** Sets the proper pause menu
	 * @param pauseMenu	the menu to show up when the game is paused
	 */
	public void setPauseMenu (PauseMenu pauseMenu)
	{
		this.pauseMenu = pauseMenu;
	}

	/** Draws the content to the screen
	 */
	public void draw (Graphics g)
	{
		super.draw (g);
		crystal.draw(g,container);
		if (levelWait <= 0)
			virusHandler.draw (g,container);
		abilityHandler.draw(g, container);
		player.draw(g, container);
		
		// Draw the outline if the box can be upgraded
			for (AbilityBox box : abilityBoxes)
				if (skillPoints > 0)
				{
					if (abilityHandler.getLevel(box.getAbility()) < 3)
						box.drawOutline(g, Color.green);
				}
				// Draws an outline around the active ability
				else if (abilityHandler.getActiveAbility() == box.getAbility())
					box.drawOutline(g, Color.cyan);

		// Draw all the boxes and their level
		g.drawImage(abilityMenu,0,0,container);
		g.setColor (Color.cyan);
		g.setFont(abilityFont);
		for (AbilityBox box : abilityBoxes)
		{
			g.drawString(""+(abilityHandler.getLevel(box.getAbility())+1),box.x+box.width-10,box.y+box.height-5);
			box.drawCooldown (g,container,abilityHandler);
		}
		
		// Draw the score background and health bar
		g.drawImage(scoreMenu,0,0,container);
		g.setColor (Color.red);
		g.fillRect (142,28,(int)(1.0*crystal.getHealth() / crystal.getMaxHealth()*healthBarWidth),52-28);
		g.drawImage (healthBar,0,0,container);
		

		// Display the score
		g.setColor (Color.white);
		g.setFont(scoreFont);
		g.drawString ("" + score, 452,83);
	}

	/** Runs all the objects on the screen that need to be ran
	 */
	public void run ()
	{
		if (levelWait <= 0)
			virusHandler.run();
		else
			levelWait--;
		if (virusHandler.isEmpty())
		{
			difficulty++;
			spawnUnits();
			levelWait = levelWaitTime;
		}
		abilityHandler.run();
		player.run();
		if (crystal.getHealth() <= 0 || player.getHealth() <= 0)
		{
			completed = true;
			pauseMenu.setGameOver(true);
			nextMenu = pauseMenu;
		}
	}
	
	/** Increases the score and the exp 
	 * @param amount	how much to increase them by
	 */
	public void increaseScore(int amount)
	{
		score+=amount;
		experiance += amount;
		
		// If the player has enough experience to level up, 
		// reset the experience amount and increase their
		// skill points and level
		if (experiance >= 200 * level)
		{
			skillPoints++;			
			experiance -= 200 * level;
			level++;
		}
	}
	
	/** Gets the current score of the game
	 * @return	the score of the game
	 */
	public int getScore ()
	{
		return score;
	}

	/* (non-Javadoc)
	 * @see gui.Menu#getKeyInput(java.awt.event.KeyEvent)
	 */
	public void getKeyInput (KeyEvent event)
	{
		if (abilityHandler.getActiveAbility() == -1 && event.getKeyCode() == KeyEvent.VK_ESCAPE)
			pause();
		else
			abilityHandler.keyInput(event);
	}
	
	/** Pauses the game, goes to the Pause Menu
	 */
	public void pause()
	{
		completed = true;
		nextMenu = pauseMenu;
	}

	/** Spawns the proper amount of units based on the current 
	 *  difficulty level of the game
	 */
	private void spawnUnits() {		
		// Spawn DDoS
		
		int clusterRadius = 100;
		for (int cluster = 0; cluster < difficulty / 2; cluster++)
		{
			int clusterLocation = (int)(Math.random() * 2400);
			Point center = spawnUnit (clusterLocation, 0);
			for (int clusterMember = 0; clusterMember < difficulty / 2; clusterMember++)
			{
				spawnUnit (new Point ((int)(center.x+(Math.random()-0.5)*clusterRadius),(int)(center.y+(Math.random()-0.5)*clusterRadius)), 0);
			}			
		}
		
		// Spawn Worm
		for (int virus = 0; difficulty > 3 && virus < difficulty / 5;virus++)
		{
			int virusLocation = (int)(Math.random() * 2400);
			spawnUnit (virusLocation,1);
		}

		// Spawn Rootkit
		for (int virus = 0; difficulty > 8 && virus < difficulty / 8; virus++)
		{
			int virusLocation = (int)(Math.random() * 2400);
			spawnUnit (virusLocation,2);
		}
		
		// Spawn Brute Force
		for (int virus = 0; difficulty > 6 && virus < difficulty / 6; virus++)
		{
			int virusLocation = (int)(Math.random() * 2400);
			spawnUnit (virusLocation,3);
		}
		
	}
	
	/** Spawns a unit at a certain point on the border of the screen
	 * @param location	the place on the border to place on
	 * @param virusType	the type of virus to spawn	
	 * @return	the location on the board it has spawned
	 */
	private Point spawnUnit (int location, int virusType)
	{
		Point spawnLocation = new Point ();
		
		// If its on the left border
		if (location < 700)
			spawnLocation.y = location;
		// If its on the top border
		else if (location < 1700)
			spawnLocation.x = location - 700;
		// If its on the right border
		else if (location < 2400)
		{
			spawnLocation.x = 1000;
			spawnLocation.y = location-1700;
		}
		virusHandler.newVirus(spawnLocation, virusType);
		return spawnLocation;
	}
	
	/**	Spawns a unit at any location on the map
	 * @param spawnLocation	where to spawn it
	 * @param virusType		the type of virus to spawn
	 */
	private void spawnUnit (Point spawnLocation, int virusType)
	{
		virusHandler.newVirus(spawnLocation, virusType);
	}
	

	/* (non-Javadoc)
	 * @see gui.Menu#getMouseInput(java.awt.event.MouseEvent)
	 */
	public void getMouseInput (MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON1)
		{
			// If the user has pressed the pause button in the top right, pause the game
			if (event.getX() >= 965 && event.getX() <= 991 && event.getY() >= 7 && event.getY() <= 35)
			{
				pause();
				return;
			}
			
			// If the player is trying to cast an ability, cast it,
			// otherwise check if the player is trying to upgrade
			// an ability in the ability boxes in the bottom left
			if (abilityHandler.getActiveAbility() > -1)
			{
				abilityHandler.castAbility (event);
			}
			else if (skillPoints > 0)
			{
				int clickedAbility = checkAbilityBox(event.getPoint());
				if (clickedAbility > -1 && abilityHandler.getLevel(clickedAbility) < 3)
				{
					abilityHandler.upgradeAbility(clickedAbility);
					skillPoints--;
				}
			}
		}
		// If the player right clicks, move the player to that location
		else if (event.getButton () == MouseEvent.BUTTON3)
		{
			abilityHandler.setActiveAbility(-1);
			player.moveToMouse(event);
		}
	}

	/** Checks what ability the player has clicked on
	 * @param location	the area the player clicked
	 * @return	the ability the player clicked on, -1 if no ability has been clicked
	 */
	public int checkAbilityBox (Point location)
	{
		// Checks every current ability box
		for (AbilityBox box : abilityBoxes)
		{
			if (box.contains(location))
			{
				return box.getAbility();
			}
		}
		return -1;
	}


	/** The "AbilityBox" class
	 * An area that has the value of a certain ability
	 * @author Shiranka Miskin
	 * @version January 2012
	 */
	private class AbilityBox extends Rectangle
	{
		private int abilityIndex;
		private Image cooldownIMG = Toolkit.getDefaultToolkit().getImage("Sprites/abilityCD.png");

		/**	Creates a new ability box
		 * @param ability	the ability it corresponds to
		 * @param pos		the top left corner of it
		 * @param dim		the size of it
		 */
		public AbilityBox (int ability, Point pos, Dimension dim)
		{
			super (pos, dim);
			abilityIndex = ability;
		}

		/** Gets the ability it links to
		 * @return	the ability it corresponds to
		 */
		public int getAbility ()
		{
			return abilityIndex;
		}

		/** Draws the outline around a box
		 * @param g the graphic context
		 */
		public void drawOutline (Graphics g, Color color)
		{
			g.setColor(color);
			g.fillRect(x,y,width,height);
		}

		/** Draws the current cooldown on the ability
		 * @param g		the graphic context
		 * @param container		the container to draw on
		 * @param abilityHandler	the ability handler to access for cooldowns
		 */
		public void drawCooldown (Graphics g, Container container, AbilityHandler abilityHandler)
		{
			int maxCooldown = AbilityHandler.getMaxCooldown(abilityIndex,abilityHandler.getLevel(abilityIndex));
			int cooldown = abilityHandler.getCooldown(abilityIndex);
			double percentage = 1.0*cooldown / maxCooldown;

			g.drawImage(cooldownIMG,x,y,(int)(percentage*width),height,container);

		}

	}







}
