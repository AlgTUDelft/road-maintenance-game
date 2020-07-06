/**
 * @file GameViewController.java
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

import java.util.ArrayList;
import java.util.List;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Abstract implementation of the game view controller. Each view should have
 * a custom control implementation
 *
 * @author Joris Scharpff
 */
public class ControlWidget extends BasicWidget {	
	/** The list of controls */
	protected List<ControlButton> controls;
	
	/** The HTML panel containing all control buttons */
	protected Panel pnlControls;
	
	/**
	 * Creates a new GameViewController
	 */
	public ControlWidget( ) {
		super( );
		
		controls = new ArrayList<ControlButton>( );
		init( );
	}
	
	/**
	 * Initialise the controller UI
	 */
	private void init( ) {
		// creates a panel to hold all command buttons
		pnlControls = new VerticalPanel( );
		pnlControls.setStyleName( "controller" );

		initWidget( pnlControls );
	}
	
	/**
	 * Shows the correct buttons for the current game state
	 * 
	 * @param state The current game state
	 */
	public void setState( GameState state ) {
		for( ControlButton c : controls )
			c.setVisible( state );
	}
	
	/**
	 * Shows the correct buttons for the current client state
	 * 
	 * @param state The current client state
	 */
	public void setState( ClientState state ) {
		for( ControlButton c : controls )
			c.setVisible( state );
	}
	
	
	/**
	 * Adds a control button to the controller
	 * 
	 * @param control The control button
	 */
	public void addControl( ControlButton control ) {
		controls.add( control );

		// add it to the panel
		pnlControls.add( control );
	}
	
	/**
	 * Adds the disconnect button to the controller
	 */
	public void addDisconnect( ) {
		// create the button
		final ControlButton disc = new ControlButton( Lang.text.Client_Disconnect( ) ) {
			
			@Override
			protected void onClick( ) {
				ClientView.getInstance( ).disconnect( );
			}
		};
		
		// set custom style name for this button
		disc.addStyleDependentName( "disconnect" );
		
		addControl( disc );
	}
}
