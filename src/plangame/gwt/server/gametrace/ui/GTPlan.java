/**
 * @file GTPlan.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2014 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         23 dec. 2014
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.gametrace.ui;

import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import plangame.game.player.Player;
import plangame.model.object.BasicID;
import plangame.model.time.TimePoint;
import plangame.model.time.TimeSpan;

/**
 * Component to show the plan of a game trace
 *
 * @author Joris Scharpff
 */
public class GTPlan extends Container {
	/** The players in the plan grid */
	protected List<Player> players;
	
	/** The plan period */
	protected TimeSpan period;
	
	/**
	 * Creates a new GTPlan grid
	 */
	public GTPlan( ) {
		// create dummy grid
		players = new ArrayList<Player>( );
		players.add( new Player( BasicID.makeValidID( "(none)" ), "(none)" ) );
		period = new TimeSpan( new TimePoint( 0 ), new TimePoint( 1 ) );
		
		// init dummy grid
		initGrid( );
	}
	
	/**
	 * Creates a grid for the players and the period
	 */
	protected void initGrid( ) {
		// create new layout
		final GridLayout grid = new GridLayout( players.size( ) + 1, period.getWeeks( ) + 1 );
		this.setLayout( grid );
		
		// add player labels
		for( int i = 0; i < players.size( ); i++ ) {
			this.add( new JLabel( players.get( i ).toString( ) ) );
			for( int j = 0; j < period.getWeeks( ); j++ ) {
				this.add( new JLabel( "" ) );
			}
		}
		
		// add week labels below the grid
		this.add( new JLabel( "Week  ", SwingConstants.RIGHT ) );
		for( int j = 0; j < period.getWeeks( ); j++ ) {
			if( j % 5 == 0 )
				this.add( new JLabel( "" + j + 1 ) );
			else
				this.add( new JLabel( "" ) );
		}
	}
	
}
