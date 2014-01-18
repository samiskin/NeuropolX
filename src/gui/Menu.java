package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/** The "Menu" class
 * A menu with a background image and clickable areas that
 *  link to other menus
 * @author Shiranka Miskin
 * @version January 2012
 */
public class Menu {
	
	protected Image mainImage;
	protected static Container container;
	
	// A list of all the areas that 
	private ArrayList <ClickableArea>clickables;
	private ClickableArea exit;
	
	// Variables for if the menu is completed and what menu to proceed to afterwards
	protected boolean completed;
	protected Menu nextMenu;

	/** Initializes the Menu object
	 * @param img		The image to display as a background
	 * @param container	The container in which the menu exists
	 */
	public Menu (Image img, Container menuContainer)
	{
		mainImage = img;
		container = menuContainer;
		clickables = new ArrayList <ClickableArea>();
		completed = false;
	}
	
	/** Sets the background of the menu to a certain image
	 * @param bgImage	What image to switch to
	 */
	public void setBackground (Image bgImage)
	{
		mainImage = bgImage;
	}
	
	/** Draws the background image of the menu
	 * @param g  the graphics context to draw on
	 */
	public void draw(Graphics g)
	{
		g.drawImage (mainImage,0, 0, container);
		
		
		// Outlines every clickable area, used to aid in 
		// coding the coordinates and dimensions
		/*g.setColor(Color.green);
		if (exit != null)
			g.drawRect(exit.x, exit.y,exit.width, exit.height);
		for (clickableArea clickable : clickables)
		{
			g.drawRect (clickable.x,clickable.y,clickable.width,clickable.height);
		}*/
		
	}
	
	/** Handles mouse input for the menu
	 * @param event
	 */
	public void getMouseInput(MouseEvent event)
	{
		checkClickables (event.getPoint());
	}
	
	/** Handles keyboard input for the menu
	 * @param event  The KeyEvent to operate with
	 */
	public void getKeyInput (KeyEvent event)
	{
		
	}
	
	/** Sets a clickable area to exit the game
	 * @param position	the top left of the clickable area
	 * @param size		the size of the area
	 */
	public void setExit (Point position, Dimension size)
	{
		exit = new ClickableArea (position,size,null);
	}
	
	/** Resets the menu
	 */
	public void reset ()
	{
		completed = false;
	}
	
	/**  Parameter to find out if the menu is completed, and if the program
	 *   should proceed to the next menu
	 * @return  True if the menu is complete, false if not
	 */
	public boolean isComplete()
	{
		return completed;
	}
	
	/** Checks every clickable area to see if that link has been clicked.  
	 * @param location  The point on the screen to check
	 */
	public void checkClickables(Point location)
	{
		if (exit != null && exit.contains(location))
			System.exit(0);
		for (ClickableArea clickable : clickables)
		{
			// If the specific clickable link has been clicked,
			// set the next menu to whatever link has been clicked
			if (clickable.contains(location))
			{
				nextMenu = clickable.getLink();
				completed = true;
			}
		}
	}
	
	/** Runs anything needed for the specific menu
	 *  A generic menu has nothing to run, as it simply 
	 *  displays the background and allows the user 
	 *  to click on various locations on it
	 */
	public void run()
	{
		
	}
	
	/** Adds a clickable area in the menu, that links to another menu
	 * @param position 	The top left corner of the clickable area
	 * @param dimension The size of the area
	 * @param link		What menu the area links to
	 */
	public void addClickable (Point position, Dimension dimension, Menu link)
	{
		clickables.add(new ClickableArea(position,dimension,link));
	}

	/** The "clickableArea"  
	 * An area on a menu that links to another menu
	 * @author Shiranka Miskin
	 * @version January 2012
	 */
	private class ClickableArea extends Rectangle
	{
		// What the menu links to
		private Menu link;
		
		/** Creates a new clickable area
		 * @param position	The top left corner of the area
		 * @param dimension	The size of the area
		 * @param link		The menu the area sends the user to when activated
		 */
		public ClickableArea (Point position, Dimension dimension, Menu link)
		{
			// Top left corner is at position, because the object
			// rectangle works that way
			super (position,dimension);
			this.link = link;
		}
		
		/** Method to find out what the area links to
		 * @return the menu that corresponds with the area
		 */
		public Menu getLink ()
		{
			return link;
		}	
	}
}
