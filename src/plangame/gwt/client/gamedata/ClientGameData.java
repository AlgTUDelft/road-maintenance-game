/**
 * @file ClientData.java
 * @brief [brief description]
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         11 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gamedata;

import java.util.ArrayList;
import java.util.List;

import plangame.game.plans.DelayStatus;
import plangame.game.plans.JointPlan;
import plangame.game.plans.Plan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanException;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.gwt.shared.gameresponse.GameResults;
import plangame.gwt.shared.state.GameInfo;
import plangame.model.time.TimePoint;
import plangame.model.time.TimeSpan;


/**
 * Class that contains and updates all relevant data that is to be presented
 * in the client interface
 * 
 * @author Joris Scharpff
 */
public class ClientGameData {
	/** The current game info */
	protected GameInfo gameinfo;
	
	/** The current joint plan */
	protected JointPlan jointplan;
	
	/** The game data computations class */
	protected GameData gamedata;
	
	/** List of widgets that should be notified on changes */
	protected List<DataUpdateListener> updates;
	
	/**
	 * Creates new client game data object
	 */
	public ClientGameData( ) {		
		// initialise new game data object
		gamedata = new GameData( this );
		
		// setup update listening and add the game data first to the list, making
		// sure all computations are done before other objects are notified
		updates = new ArrayList<DataUpdateListener>( );		
		updates.add( gamedata.cache );
	}
	
	/**
	 * Creates a data-only copy of the client game data that can be used for
	 * future manipulation. The registered update listeners are not copied, hence
	 * existing listeners should be added to the copy as well if they should be
	 * updated on changes to the copy
	 * 
	 * @return A data-only copy of the client game data
	 */
	public ClientGameData dataCopy( ) {
		// create the copy
		final ClientGameData copy = new ClientGameData( );
		
		// copy game info and joint plan
		// FIXME the game info is now copied by reference
		copy.gameinfo = gameinfo;
		copy.jointplan = new JointPlan( jointplan );

		// copy the game data computations and cache
		copy.gamedata.cache.copyFrom( gamedata.cache );
		
		return copy;
	}
	
	/**
	 * Adds an update listener
	 * 
	 * @param updatelistener The update listener to add
	 */
	public void addUpdateListener( DataUpdateListener updatelistener ) {
		updates.add( updatelistener );
	}
	
	/**
	 * Called whenever the game information is updated
	 * 
	 * @param gameinfo The new game info
	 */
	public void setGameInfo( GameInfo gameinfo ) {
		this.gameinfo = gameinfo;
		
		// notify all listeners
		for( DataUpdateListener l : updates )
			if( l instanceof GameInfoUpdateListener )
				((GameInfoUpdateListener) l).onGameInfoSet( gameinfo );
	}
	
	/** @return The current known game info */
	public GameInfo getGameInfo( ) { return gameinfo; }
	
	/**
	 * Called whenever the joint plan changes so that the data can be updated
	 * 
	 * @param jplan The new joint plan
	 */
	public void setJointPlan( JointPlan jplan ) {
		if( jointplan == null )
			jointplan = new JointPlan( jplan );
		else
			jointplan.update( jplan );		
		
		// notify all listeners
		for( DataUpdateListener l : updates )
			if( l instanceof JointPlanUpdateListener )
				((JointPlanUpdateListener)l).onJointPlanSet( jointplan );
	}
	
	/**
	 * Sets one player's plan in the joint plan, calls onJointPlanSet after this
	 * 
	 * @param plan The plan to set in the joint plan
	 */
	public void setPlan( Plan plan ) {
		getJointPlan( ).setPlan( plan );
		
		// notify all listeners
		for( DataUpdateListener l : updates )
			if( l instanceof JointPlanUpdateListener )
				((JointPlanUpdateListener)l).onJointPlanSet( jointplan );
	}

	/** @return The current joint plan */
	public JointPlan getJointPlan( ) { return jointplan; }
	
	/**
	 * Called whenever the current plan is changed, listeners are only notified
	 * if the change was successful
	 * 
	 * @param change The plan change
	 * @param validate True if the plan change should be validated
	 * @return The planned task that results
	 * @throws PlanException if the change is validated and it is invalid
	 */
	public PlanTask changeJointPlan( PlanChange change, boolean validate ) throws PlanException {
//		System.out.println( change );
//		System.out.println( "BEFORE:");
//		System.out.println( jointplan.printSchedule( ) );

		final PlanTask ptask = jointplan.applyChange( change, validate );
//		
//		System.out.println( "AFTER:");
//		System.out.println( jointplan.printSchedule( ) );

		// notify all listeners
		for( DataUpdateListener l : updates )
			if( l instanceof JointPlanUpdateListener )
				((JointPlanUpdateListener)l).onJointPlanChange( change, validate );
		
		return ptask;
	}
	
	/**
	 * Notifies all listeners of the current game info and joint plan, without
	 * changing anything
	 */
	public void notifyListeners( ) {
		// notify all listeners of game info
		for( DataUpdateListener l : updates )
			if( l instanceof GameInfoUpdateListener )
				((GameInfoUpdateListener)l).onGameInfoSet( getGameInfo( ) );
		
		// notify all listeners of joint plan
		for( DataUpdateListener l : updates )
			if( l instanceof JointPlanUpdateListener )
				((JointPlanUpdateListener)l).onJointPlanSet( getJointPlan( ) );
		
	}
	
	/**
	 * Restores the game data after a reconnect
	 * 
	 * @param gameinfo The game info
	 * @param jplan The joint plan
	 */
	public void restore( GameInfo gameinfo, JointPlan jplan ) {
		setGameInfo( gameinfo );
		setJointPlan( jplan );
	}
	
	/**
	 * Updates the game data by processing the game results
	 * 
	 * @param results The results to process
	 */
	public void updateResults( GameResults results ) {
		// update game time
		getJointPlan( ).setGameTime( results.getGameTime( ) );
		
		// apply all updates
		for( PlanTask pt : results.getDelayed( ) )
			changeJointPlan( PlanChange.status( pt.getMethod( ), DelayStatus.Delayed ), false );
		for( PlanTask pt : results.getCompleted( ) )
			if( getJointPlan( ).getPlanned( pt.getTask( ) ).isPending( ) )
				changeJointPlan( PlanChange.status( pt.getMethod( ), DelayStatus.AsPlanned ), false );
	}
	
	/** @return The list of players in the game */
	public List<Player> getPlayers( ) { checkInfoReady( ); return getGameInfo( ).getPlayers( ); }
	
	/** @return The current game period */
	public TimeSpan getGamePeriod( ) { checkInfoReady( ); return getGameInfo( ).getGamePeriod( ); }
	
	/** @return The game start time */
	public TimePoint getStartTime( ) { checkReady( ); return getJointPlan( ).getPeriod( ).getStart( ); }

	/** @return The game end time */
	public TimePoint getEndTime( ) { checkReady( ); return getJointPlan( ).getPeriod( ).getEnd( ); }
	
	/** @return The current game time */
	public TimePoint getCurrentTime( ) { checkReady( ); return getJointPlan( ).getGameTime( ); }
	
	/** @return The game data object */
	public GameData getData( ) { return gamedata; }
	
	/**
	 * Asserts that the game info is ready for use
	 * 
	 * @throws RuntimeException if the game info or joint plan is null
	 */
	protected void checkInfoReady( ) throws RuntimeException {
		if( gameinfo == null )
			throw new RuntimeException( "Game info is not fully loaded" );
	}

	
	/**
	 * Asserts that the game data is ready for use, game data should not be used
	 * before both all game info is known and a joint plan has been set
	 * 
	 * @throws RuntimeException if the game info or joint plan is null
	 */
	protected void checkReady( ) throws RuntimeException {
		if( !isReady( ) )
			throw new RuntimeException( "Game data is not fully loaded" );
	}
	
	/**
	 * Checks if the client game data is ready for use, i.e. the game info is set
	 * and there is a joint plan
	 * 
	 * @return True if	game data is known and joint plan is set
	 */
	public boolean isReady( ) { 
		return !(gameinfo == null || jointplan == null);
	}
}
