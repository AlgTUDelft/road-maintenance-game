/**
 * @file ScoreBar.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         2 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.scorebar;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.player.Player;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.gamedata.TotalScore;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.client.widgets.listeners.TTLWidget;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Bar that can be used to display the player score so far
 *
 * @author Joris Scharpff
 */
public class ScoreBarWidget extends BasicWidget implements TTLWidget, JointPlanUpdateListener {
	/** The player to display the score bar for */
	protected Player player;
	
	/** The bar panel */
	protected HorizontalPanel pnlBar;
	
	/** The profits label */
	protected Label lblProfits;
	
	/** The TTL label */
	protected Label lblTTL;
	
	/** The TTL label */
	protected Label lblTTLMine;
	
	/** Display TTL absolute or relative? */
	protected boolean relative;
	
	/**
	 * Creates the widget
	 */
	public ScoreBarWidget( ) {
		super( );
		
		// create the bar
		pnlBar = new HorizontalPanel( );
		pnlBar.addStyleName( "scorebar" );
		
		// create and add the labels
		lblProfits = new Label( );
		lblProfits.addStyleName( "scorebar-profits" );
		pnlBar.add( lblProfits );
		lblTTL = new Label( );
		lblTTL.addStyleName( "scorebar-ttl" );
		pnlBar.add( lblTTL );
		lblTTLMine = new Label( );
		lblTTLMine.addStyleName( "scorebar-ttl" );
		pnlBar.add( lblTTLMine );
		
		// init the composite
		initWidget( pnlBar );
		
		// swt to relative mode
		setDisplayRelative( true );
	}
	
	/**
	 * Sets the player to display score of
	 * 
	 * @param player The player
	 */
	public void setPlayer( Player player ) {
		this.player = player;
		
		update( );
	}
	
	/**
	 * @return The player
	 */
	public Player getPlayer( ) { return player; }
	
	/**
	 * Sets the display of TTL to relative (percentages)
	 * 
	 * @param relative True to enable relative display
	 */
	@Override
	public void setDisplayRelative( boolean relative ) {
		this.relative = relative;
		
		// update widget (if possible)
		update( );
	}
	
	/**
	 * @return True if TTL should be displayed relative
	 */
	public boolean isDisplayRelative( ) { return relative; }
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		update( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		update( );
	}
	
	/**
	 * Updates the scores using the info in the joint plan
	 */
	public void update( ) {
		if( getPlayer( ) == null || !gameDataReady( ) ) return;
		
		// compute all totals
		final TotalScore prof = getGameData( ).getData( ).getProfits( getPlayer( ) );
		final TotalScore ttl = getGameData( ).getData( ).getTotalTTL( isDisplayRelative( ) );
		final TotalScore myttl = getGameData( ).getData( ).getTTL( getPlayer( ), isDisplayRelative( ) );
		
		// fill in the totals
		final Style s = (isDisplayRelative( ) ? Style.Percentage2 : Style.IntK);
		lblProfits.setText( Lang.text.W_Score_Profits( Format.f( prof.getBestCase( ), Style.CurrK ), Format.f( Math.abs( prof.getDelta( ) ), Style.CurrK ) ) );
		lblTTL.setText( Lang.text.W_Score_TTLOverall( Format.f( ttl.getBestCase( ), s ), Format.f( ttl.getDelta( ), s ) ) );
		lblTTLMine.setText( Lang.text.W_Score_TTLMine( Format.f( myttl.getBestCase( ), s ), Format.f( myttl.getDelta( ), s ) ) );
	}
}
