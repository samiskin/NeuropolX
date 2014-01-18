package gui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;

/** The "Screen" class
 * The frame that everything is ran and displayed on
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Screen extends JFrame
{	
	// The menu that is currently being used
	private Menu currentMenu;
	
	// The list of menus that the game can go through
	// They are initialized here in order to assign links
	private Menu mainMenu;
	private Menu nortonMenu;
	private Menu controlsMenu;
	private Menu ddosProfile;
	private Menu wormProfile;
	private Menu bruteProfile;
	private Menu rootProfile;
	private Menu databaseMenu;
	private Menu creditsMenu;
	private GameScreen gameScreen;
	private PauseMenu pauseMenu;
	

	private boolean sound;
	
	private Image soundON = Toolkit.getDefaultToolkit().getImage("Sprites/soundON.png");
	private Image soundOFF = Toolkit.getDefaultToolkit().getImage("Sprites/soundOFF.png");

	// The two background songs
	private AudioClip menuMusic = Applet.newAudioClip(getCompleteURL ("Sound/Concentrate.wav"));
	private AudioClip gameMusic = Applet.newAudioClip(getCompleteURL ("Sound/Focus.wav"));
	
	
	/** Initializes the screen, setting its size and adding the panel. 
	 * @param screenSize the size of the screen, ratios are always kept at 10:7
	 */
	public Screen (int screenSize)
	{
		super ("MainScreen");
		setSize (new Dimension (screenSize,(int)(screenSize*0.7)));
		setLocation (100, 100);
		setUndecorated(true);
		sound = true;
		
		initializeMenus();
		currentMenu = mainMenu;
		if (sound)
			menuMusic.loop();

		getContentPane ().add (new DrawingPanel (), BorderLayout.CENTER);		
		setVisible (true);
	}
	

    /** Gets the URL needed for newAudioClip (Code from Mr. Ridout)
     * @param fileName	The name of the file
     * @return	the URL of that file
     */
    public static URL getCompleteURL (String fileName)
    {
        try
        {
            return new URL ("file:" + System.getProperty ("user.dir") + "/" + fileName);
        }
        catch (MalformedURLException e)
        {
            System.err.println (e.getMessage ());
        }
        return null;
    }
	
	/** Initializes every menu object, and assigns their links.  
	 */
	private void initializeMenus ()
	{
		// Initialise all the menus
		mainMenu = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/MainMenu.jpg"), this);
		nortonMenu = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/Instructions - Norton.jpg"), this);
		controlsMenu = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/Instructions - Controls.jpg"), this);
		databaseMenu = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/Database.jpg"), this);
		ddosProfile = new Menu(Toolkit.getDefaultToolkit().getImage("Backgrounds/DDoS Description.jpg"),this);
		wormProfile = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/Worm Description.jpg"),this);
		bruteProfile = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/Brute Force Description.jpg"),this);
		rootProfile = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/Rootkit Description.jpg"),this);
		creditsMenu = new Menu (Toolkit.getDefaultToolkit().getImage("Backgrounds/Credits.jpg"),this);
		gameScreen = new GameScreen (this);
		pauseMenu = new PauseMenu (this, gameScreen, mainMenu);
		gameScreen.setPauseMenu(pauseMenu);
		
		// Adds links for the Main Menu
		mainMenu.addClickable(new Point (850,470), new Dimension (100,32), gameScreen);
		mainMenu.addClickable(new Point (695,505), new Dimension (255,32), nortonMenu);
		mainMenu.addClickable(new Point (610,540),new Dimension(340,32),databaseMenu);
		mainMenu.addClickable(new Point (790,575), new Dimension (160,32), creditsMenu);
		mainMenu.setExit(new Point (850,610), new Dimension (100,32));
		
		// Adds links to the Instructions screens
		nortonMenu.addClickable(new Point (807,587), new Dimension (125,34), controlsMenu);
		controlsMenu.addClickable(new Point (807,587), new Dimension (125,34), mainMenu);
		
		// Adds the link to each virus and to the main menu to the Database menu.
		databaseMenu.addClickable(new Point (176,57), new Dimension (323,234), ddosProfile);
		databaseMenu.addClickable(new Point (513,57), new Dimension (323,234), wormProfile);
		databaseMenu.addClickable(new Point (176,304), new Dimension (323,234), rootProfile);
		databaseMenu.addClickable(new Point (513,304), new Dimension (323,234), bruteProfile);
		databaseMenu.addClickable(new Point (800,620), new Dimension (130,25), mainMenu);
		
		// Adds the Menu and Database links to every virus profile
		ddosProfile.addClickable(new Point (0,640), new Dimension (313,60),mainMenu);
		ddosProfile.addClickable(new Point (715,640), new Dimension (285,60),databaseMenu);
		wormProfile.addClickable(new Point (0,640), new Dimension (313,60),mainMenu);
		wormProfile.addClickable(new Point (715,640), new Dimension (285,60),databaseMenu);
		rootProfile.addClickable(new Point (0,640), new Dimension (313,60),mainMenu);
		rootProfile.addClickable(new Point (715,640), new Dimension (285,60),databaseMenu);
		bruteProfile.addClickable(new Point (0,640), new Dimension (313,60),mainMenu);
		bruteProfile.addClickable(new Point (715,640), new Dimension (285,60),databaseMenu);
		
		// Add the main menu link to the credit menu
		creditsMenu.addClickable(new Point (394,552),new Dimension (212, 47), mainMenu);
		
		
	}
	
	
	/**	The main method that creates the screen
	 * @param args	the arguments
	 */
	public static void main (String [] args)
	{
		Screen mainScreen = new Screen(1000);
		mainScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	/** The "DrawingPanel" class
	 * The panel that everything runs on
	 * @author Shiranka Miskin
	 * @version January 2012
	 */
	private class DrawingPanel extends JPanel implements ActionListener
	{
		MouseHandler mouse;
		Timer timer;		

		/** Adds the listeners and starts the timer, which activates the run method
		 */
		public DrawingPanel ()
		{			
			this.setFocusable(true);
			
			mouse = new MouseHandler ();
			this.addMouseListener (mouse);
			this.addKeyListener (new KeyHandler());
			setBackground (Color.black);
			setResizable(false);
			// Controls if there is a border or not
			setUndecorated(true);
			
			// Unmute
			sound = true;
			
			// The game updates every time the timer finishes
			timer = new Timer (30,this);
			timer.start();
		}

		/** Draws everything to the panel
		 * @param g  the graphics context to draw on
		 */
		public void paintComponent (Graphics g)
		{
			super.paintComponent (g);
			// Draws whatever the current menu on the screen wants to draw
			currentMenu.draw(g);
			if (sound)
				g.drawImage(soundON,0,0,this);
			else
				g.drawImage (soundOFF,0,0,this);
		}
		
		/** Refreshes the game every time the timer activates
		 * @param e  the event that has occured
		 */
		public void actionPerformed (ActionEvent e)
		{
			// Runs everything the current menu wants to run
			currentMenu.run();
			// If the menu has completed, and it is time to move on to another,
			// switch the current menu to whatever the next menu is
			if (currentMenu.isComplete())
			{
				// If the game is starting, start the game music
				if (sound && currentMenu != pauseMenu && currentMenu.nextMenu == gameScreen)
				{
					menuMusic.stop();
					gameMusic.loop();
				}
				// If the game is ending, start the menu music
				else if (sound && ((currentMenu == gameScreen && currentMenu.nextMenu != pauseMenu) ||
						(currentMenu == pauseMenu && currentMenu.nextMenu != gameScreen)))
				{
					gameMusic.stop();
					menuMusic.loop();
				}
				currentMenu = currentMenu.nextMenu;
				currentMenu.reset();
			}
			// Draw everything once all necessary data has been changed
			this.repaint();
		}
	}

	/** Handles all mouse input
	 * @author Shiranka Miskin
	 * @version January 2012
	 */
	private class MouseHandler extends MouseAdapter
	{
		/** Runs whatever is supposed to run for the current 
		 *  menu when a mouse action is completed unless the
		 *  user clicked the sound button
		 * @param event  the key that has been pressed on the mouse
		 */
		public void mousePressed (MouseEvent event)
		{	
			// If the user has clicked the mute button
			if (event.getX() >= 9 && event.getX() <= 35 && event.getY() >= 9 && event.getY() <= 35)
			{
				// If you're on the game screens, stop the game music, and 
				// stop the menu music if you are not on the game screen
				if (currentMenu == gameScreen || currentMenu == pauseMenu)
				{
					if (sound)
						gameMusic.stop();
					else
						gameMusic.loop();
				}
				else
				{
					if (sound)
						menuMusic.stop();
					else
						menuMusic.loop();
				}
				sound = !sound;
				gameScreen.toggleSound();
					
			}
			// If the user hasn't clicked the mute button, run the proper
			// menu commands for the mouse event
			else
			{
				currentMenu.getMouseInput(event);
			}
			//System.out.println ("X:" + event.getX() + "  Y:" + event.getY());
		}
	}
	
	/** Handles all keyboard input
	 * @author Shiranka Miskin
	 * @version January 2012
	 */
	private class KeyHandler extends KeyAdapter
	{
		/** Activates any necessary keyboard input for the current menu
		 * @param event  the key that is pressed
		 */
		public void keyPressed (KeyEvent event)
		{
			currentMenu.getKeyInput(event);
		}
	}
	
}
