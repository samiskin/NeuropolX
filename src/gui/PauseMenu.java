package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/** The "PauseMenu" class
 * The menu that the game goes to when the game is paused or the game ends
 * @author Shiranka Miskin
 * @version January 2012
 */
public class PauseMenu extends Menu{
	
	private GameScreen gameScreen;
	private Menu mainMenu;
	
	private boolean gameOver;
	
	private Rectangle resumeBox = new Rectangle(new Point (394,293),new Dimension (212, 47));
	private Rectangle mainMenuBox = new Rectangle(new Point (394,355),new Dimension (212, 47));
	private Rectangle exitGameBox = new Rectangle(new Point (394,417),new Dimension (212, 47));
	
	
	/**	Creates a new pause menu
	 * @param container		the container the pause menu is drawn on
	 * @param gameScreen	the gameScreen it originates from
	 * @param mainMenu		the main menu it can send the user
	 */
	public PauseMenu (Container container, GameScreen gameScreen, Menu mainMenu)
	{
		super (Toolkit.getDefaultToolkit().getImage("Sprites/Options.png"), container);
		
		this.gameScreen = gameScreen;		
		this.mainMenu = mainMenu;
		gameOver = false;
	}
	
	/** Handles keyboard input for the menu
	 * @param event  The KeyEvent to operate with
	 */
	public void getKeyInput (KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			completed = true;
			nextMenu = gameScreen;
		}
	}
	
	/* (non-Javadoc)
	 * @see gui.Menu#getMouseInput(java.awt.event.MouseEvent)
	 */
	public void getMouseInput (MouseEvent event)
	{
		if (resumeBox.contains(event.getPoint()))
		{			
			if (gameOver)
				gameScreen.restart();
			completed = true;
			nextMenu = gameScreen;
		}
		else if (mainMenuBox.contains(event.getPoint()))
		{
			completed = true;
			gameScreen.restart();
			nextMenu = mainMenu;
		}
		else if (exitGameBox.contains(event.getPoint()))
		{
			System.exit(0);
		}	
		setGameOver(false);
				
	}
	
	/** Draws the background image of the menu
	 * @param g  the graphics context to draw on
	 */
	public void draw(Graphics g)
	{
		gameScreen.draw(g);
		g.drawImage (mainImage,0, 0, container);
		if (gameOver)
		{
			// Display the score
			g.setColor (Color.white);
			g.setFont(new Font ("Arial",Font.BOLD,25));
			g.drawString ("Score: " + gameScreen.getScore(), 410,255);
		}
	}

	/** Sets the pause menu to either being a pause screen or a game over screen
	 * @param over	true if the game is over, false if it is not (therefore simply paused)
	 */
	public void setGameOver(boolean over) {
		if (over)
		{
			setBackground (Toolkit.getDefaultToolkit().getImage("Sprites/GameOver.png"));
			gameOver = true;
		}
		else
		{
			setBackground (Toolkit.getDefaultToolkit().getImage("Sprites/Options.png"));
			gameOver = false;
		}
		
	}
	
}
