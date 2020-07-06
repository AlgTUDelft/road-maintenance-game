/**
 * @file ProfileScores.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2015 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         9 jan. 2015
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.gametrace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.model.tasks.TaskMethod;

/**
 * Class to score team profiles based on chosen methods
 *
 * @author Joris Scharpff
 */
public class ProfileScores {
	/** Profit score for each of the methods */
	protected final static double[] Qp = { 1, 4, 2, 3 };
	
	/** TTL score for each of the methods */
	protected final static double[] Qt = { 4, 1, 2, 3 };	
	
	/** Risk score for each of the methods */
	protected final static double[] Qr = { 3, 1, 4, 2 };	
	
	/**
	 * Returns a map of profile scores per player for the given joint plan
	 * 
	 * @param jplan The joint plan
	 * @return The score map
	 */
	public Map<Player, ProfileScoreResult> getScore( JointPlan jplan ) {
		// compute the score per player
		final Map<Player, ProfileScoreResult> scores = new HashMap<Player, ProfileScoreResult>( jplan.getPlayers( ).size( ) );
		for( Player p : jplan.getPlayers( ) ) {
			scores.put( p, getScore( jplan, p ) );
		}
		return scores;
	}

	/**
	 * Computes the profile score of the player in the joint plan
	 * 
	 * @param jplan The joint plan
	 * @param p The player
	 * @return The player's profile score
	 */
	protected ProfileScoreResult getScore( JointPlan jplan, Player p ) {
		// score each of its methods on each component
		double profit = 0;
		double ttl = 0;
		double risk = 0;
		
		// get the planned methods
		final List<PlanTask> tasks = jplan.getPlanned( p );
		
		// compute score of each of it's methods
		for( PlanTask pt : tasks ) {
			final ProfileScoreResult ms = getMethodScore( pt.getMethod( ) );
			profit += ms.profit;
			ttl += ms.ttl;
			risk += ms.risk;
		}

		// compute maximum and minimum score to normalise
		final double min = tasks.size( );
		final double max = tasks.size( ) * 4;

		// return the normalised score
		return new ProfileScoreResult( (profit - min) / (max - min), (ttl - min) / (max - min), (risk - min) / (max - min) );
	}
	
	/**
	 * Computes the profile score for the given method
	 * 
	 * @param tm The method
	 * @return The profile score
	 */
	protected ProfileScoreResult getMethodScore( TaskMethod tm ) {
		// score based on method ID
		final String mID = tm.getID( ).toString( ).substring( tm.getID( ).toString( ).lastIndexOf( "." ) + 1 );

		try {
			final int i = Integer.parseInt( mID.substring( 1, 2 ) ) - 1;
			return new ProfileScoreResult( Qp[i], Qt[i], Qr[i] );
		} catch( Exception e ) {
			throw new RuntimeException( "Unknown method!" );			
		}
	}
}
