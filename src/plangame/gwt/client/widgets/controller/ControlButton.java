/**
 * @file ControlButton.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         4 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controller;

import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * Buttons with a callback for events
 *
 * @author Joris Scharpff
 */
public abstract class ControlButton extends Button {
	/** The game state in which the button should be displayed */
	protected GameState gamestate;

	/** The client state in which the button should be displayed */
	protected ClientState clientstate;

	/**
	 * Creates a new ControlButton displayed in the specified states
	 * 
	 * @param caption The button text
	 * @param gamestate The game state to display it in (null for always)
	 * @param clientstate The client state to display it in (null for always)
	 */
	private ControlButton( String caption, GameState gamestate, ClientState clientstate ) {
		super( caption );
		
		// only one of these will be set because of the visible constructors
		this.gamestate = gamestate;
		this.clientstate = clientstate;
		
		// add handler for click events
		addClickHandler( new ClickHandler( ) {
			
			@Override
			public void onClick( ClickEvent event ) {
				ControlButton.this.onClick( );
			}
		} );
		
		// set style and hide the button
		setVisible( false );
		setStylePrimaryName( "controlbutton" );
	}
	
	/**
	 * Creates a new ControlButton that is always displayed
	 * 
	 * @param caption The button caption
	 */
	public ControlButton( String caption ) {
		this( caption, null, null );
	}
	
	/**
	 * Creates a new control button with the specified caption that is displayed
	 * only in the specified game state
	 * 
	 * @param caption The button caption
	 * @param state The game state in which to display the button or null for
	 * always
	 */
	public ControlButton( String caption, GameState state ) {
		this( caption, state, null );
	}
	
	/**
	 * Creates a new control button with the specified caption that is displayed
	 * only in the specified client state
	 * 
	 * @param caption The button caption
	 * @param state The client state in which to display the button or null for
	 * always
	 */
	public ControlButton( String caption, ClientState state ) {
		this( caption, null, state );
	}
	
	/**
	 * Sets the visibility of the button depending on the current game state
	 * 
	 * @param state The current game state
	 */
	protected void setVisible( GameState state ) {
		setVisible( (gamestate == null || gamestate.equals( state )) && shouldDisplay( ) );
	}
	
	/**
	 * Sets the visibility of the button depending on the current client state
	 * 
	 * @param state The current client state
	 */
	protected void setVisible( ClientState state ) {
		setVisible( (clientstate == null || clientstate.equals( state )) && shouldDisplay( ) );
	}	
	
	/**
	 * Function that can be overridden to add additional display conditions. This
	 * function is called whenever any state changes to determine whether or not
	 * the button should display. By default the function always returns true,
	 * i.e. the button is always displayed when the state is correct.
	 * 
	 * @return True if the button should display
	 */
	public boolean shouldDisplay( ) {
		return true;
	}
		
	/**
	 * Called when the button is clicked
	 */
	protected abstract void onClick( );
}
