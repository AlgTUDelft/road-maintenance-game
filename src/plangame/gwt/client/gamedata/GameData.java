/**
 * @file ClientGameDataCache.java
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

import java.util.List;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.game.score.PendingScore;
import plangame.game.score.TTLBandwidth;
import plangame.game.score.TTLScore;
import plangame.model.mechanism.PaymentMechanism;
import plangame.model.tasks.Task;
import plangame.model.time.TimePoint;
import plangame.model.time.TimeSpan;
import plangame.model.traffic.TTLDistribution;
import plangame.model.traffic.TTLModel;


/**
 * All game data is computed and stored in this class so that all widgets
 * displaying data can re-use previously computed values when possible.
 * 
 * @author Joris Scharpff
 */
public class GameData {
	/** The client game data */
	protected ClientGameData data;
	
	/** The game data cache */
	protected GameDataCache cache;
	
	/**
	 * Creates a new GameData class
	 * 
	 * @param clientdata The client game data class that contains all relevant
	 * game data
	 */
	public GameData( ClientGameData clientdata ) {
		this.data = clientdata;
		this.cache = new GameDataCache( );
	}
		
	/**
	 * Computes the total profits of the player
	 * 
	 * @param player The player
	 * @return The total profits of the player as a total score. The best case
	 * component contains the profits excluding risk, the worst case part
	 * including risk
	 */
	public TotalScore getProfits( Player player ) {
		data.checkReady( );

		// sum over the task profits
		final TotalScore profits = new TotalScore( );
		for( PlanTask pt : data.getJointPlan( ).getPlanned( player ) ) {
			final PendingScore taskscore = getProfits( pt.getTask( ) );
			
			if( pt.isDelayed( ) )
				profits.add( taskscore.getTotal( ), 0.0 );
			else if( pt.isPending( ) )
				profits.add( taskscore.getRegular( ), taskscore.getDelayed( ) );
			else
				profits.add( taskscore.getRegular( ), 0.0 );
		}
		
		// subtract penalties for not planned tasks
		for( Task t : player.getPortfolio( ).getTasks( ) )
			if( !data.getJointPlan( ).isPlanned( t ) ) profits.add( -t.getPenalty( ), 0 );
		
		// return best and worst case extremes (instead of best and delta)
		return new TotalScore( profits.getBestCase( ), profits.getBestCase( ) + profits.getWorstCase( ) ) ;
	}
	
	/**
	 * Computes the total profits for the given task
	 * 
	 * @param task The task
	 * @return The total profits as delay score, empty score if no method
	 * is planned for the task
	 */
	public PendingScore getProfits( Task task ) {
		data.checkReady( );
		
		// get the planned task
		final PlanTask pt = data.getJointPlan( ).getPlanned( task );
		if( pt == null ) return new PendingScore( );
				
		// get revenue
		final double revenue = pt.getRevenue( );
		
		// get maintenance costs
		final PendingScore costs = getMaintenanceCost( task );
		
		// and payments
		final TTLScore payments = getPayments( task );
		
		// return the total profits in best and worst case
		final double best = revenue - costs.getRegular( ) - (payments.getIndividual( ).getRegular( ) + payments.getNetworkRegular( ).getWorstRealised( ) );
		final double worst = revenue - costs.getTotal( ) - (payments.getIndividual( ).getTotal( ) + payments.getNetworkRegular( ).getWorstRealised( ) + payments.getNetworkDelayed( ).getWorstRealised( ));

		return new PendingScore( best, worst - best, pt.getDelayStatus( ) );			
	}
	
	/**
	 * Computes the profit ranking score of the player, highest best case profit
	 * is ranked number 1
	 * 
	 * @param player The player 
	 * @return The profit rank of the player
	 */
	public int getProfitRank( Player player ) {
		data.checkReady( );
		
		final double profit = getProfits( player ).getBestCase( );
		
		int rank = 1;
		for( Player p : data.getPlayers( ) ) {
			if( getProfits( p ).getBestCase( ) > profit )
				rank++;
		}
		
		return rank;
	}
	
	/**
	 * Computes the maintenance cost for the task using the method and start time
	 * that is known in the joint plan, returns empty score if no method is
	 * planned for the task
	 * 
	 * @param task The task
	 * @return The delay score containing the regular and delay part of the
	 * total maintenance costs
	 */
	public PendingScore getMaintenanceCost( Task task ) {
		data.checkReady( );
		
		// check if a method is planned
		final PlanTask pt = data.getJointPlan( ).getPlanned( task );
		if( pt == null ) return new PendingScore( );
		
		return pt.getCost( );
	}
	
	/**
	 * Computes the total individual TTL at the specified time point
	 * 
	 * @param time The time to compute it at
	 * @param relative Compute TTL relative to idle?
	 * @return The total TTLScore per score category
	 */
	public double getTTLIndividual( TimePoint time, boolean relative ) {
		data.checkReady( );
		
		// get the TTLModel
		final TTLModel ttlmodel = data.getGameInfo( ).getInfra( ).getTTLModel( );
		
		// get list of planned tasks, including delay time
		final List<PlanTask> planned = data.getJointPlan( ).getPlannedAt( time, true );
		
		// compute individual, network best and network worst TTL
		double indiv = 0;
		for( PlanTask pt : planned )
			indiv += ttlmodel.getIndividualTTL( pt.getMethod( ), time );
		return (relative ? rel( indiv, getTTLIdle( time )) : indiv);
	}
	
	/**
	 * Computes the total individual TTL for the player at the specified time point
	 * 
	 * @param player The player to get individual TTL for
	 * @param time The time to compute it at
	 * @param relative Compute TTL relative to idle?
	 * @return The total TTLScore per score category
	 */
	public double getTTLIndividual( Player player, TimePoint time, boolean relative ) {
		data.checkReady( );
		
		// get the TTLModel
		final TTLModel ttlmodel = data.getGameInfo( ).getInfra( ).getTTLModel( );
		
		// get list of planned tasks, including delay time
		final List<PlanTask> planned = data.getJointPlan( ).getPlannedAt( player, time, true );
		
		// compute individual, network best and network worst TTL
		double indiv = 0;
		for( PlanTask pt : planned )
			indiv += ttlmodel.getIndividualTTL( pt.getMethod( ), time );
		return (relative ? rel( indiv, getTTLIdle( time )) : indiv);
	}	
	
	/**
	 * Computes the total network TTL at the given time point
	 * 
	 * @param time the time
	 * @param relative Compute TTL scores relative to idle?
	 * @return A delay score containing best and worst case
	 */
	public TTLBandwidth getTTLNetwork( TimePoint time, boolean relative ) {
		data.checkReady( );
		
		final TTLBandwidth ttl = data.getJointPlan( ).getTTLNetwork( time, data.getGameInfo( ).getInfra( ).getTTLModel( ) );
		
		return (relative ? rel( ttl, getTTLIdle( time )) : ttl );
	}
	
	/**
	 * Computes the distribution of network TTL over the assets during the entire
	 * game period
	 * 
	 * @return The distribution of network TTL
	 */
	public TTLDistribution getTTLAvgNetworkDistribution( ) {
		data.checkReady( );
		
		return getTTLAvgNetworkDistribution( data.getGamePeriod( ) );
	}
	
	/**
	 * Computes the distribution of network TTL over the assets during the period
	 * of the planned task
	 * 
	 * @param ptask The planned task
	 * @return The distribution of network TTL
	 */
	public TTLDistribution getTTLAvgNetworkDistribution( PlanTask ptask ) {
		data.checkReady( );

		return getTTLAvgNetworkDistribution( ptask.getPeriod( true ) );
	}
	
	/**
	 * Computes the distribution of network TTL over the assets during the period
	 * 
	 * @param period The period
	 * @return The distribution of network TTL
	 */
	public TTLDistribution getTTLAvgNetworkDistribution( TimeSpan period ) {
		data.checkReady( );
		
		// get the TTL distribution in the best and worste case
		final TTLModel ttlmodel = data.getGameInfo( ).getInfra( ).getTTLModel( );
		
		// sum best and worst case over the period
		TTLDistribution avgdist = new TTLDistribution( );
		for( TimePoint time : period.toWeeks( ) ) {
			// reused cached info if possible
			TTLDistribution dist = cache.getAvgNetworkDist( time );
			if( dist == null ) {
				
				// get planned methods in best and worst case
				final List<PlanTask> pbest = data.getJointPlan( ).getPlannedAt( time, false );
				final List<PlanTask> pworst = data.getJointPlan( ).getPlannedAt( time, true );
				
				// sum TTL
				final TTLDistribution tbest = ttlmodel.getNetworkDistribution( pbest, time );
				final TTLDistribution tworst = ttlmodel.getNetworkDistribution( pworst, time );
				
				dist = TTLDistribution.average( tbest, tworst );
				cache.setAvgNetworkDist( time, dist );
			}

			avgdist = avgdist.add( dist );
		}
		
		// return the average of both distributions
		return avgdist; 
	}
	
	/**
	 * Computes the total TTL info for the player
	 * 
	 * @param player The player
	 * @param relative Compute TTL relative to idle?
	 * @return The TTLScore containing the total TTL for all of the planned
	 * methods of the player
	 */
	public TotalScore getTTL( Player player, boolean relative ) {
		data.checkReady( );
		
		// sum over all TTL scores
		final TotalScore total = new TotalScore( );
		for( PlanTask pt : data.getJointPlan( ).getPlanned( player ) ) {
			final TTLScore score = getTTL( pt.getTask( ), relative );
			
			final PendingScore indiv = score.getIndividual( );
			final double regbest = score.getNetworkRegular( ).getBestRealised( );
			final double regworst = score.getNetworkRegular( ).getWorstRealised( );
			final double delbest = score.getNetworkDelayed( ).getBestRealised( );
			final double delworst = score.getNetworkDelayed( ).getWorstRealised( );
			
			if( pt.isDelayed( ) ) {
				total.add( indiv.getTotal( ) + regbest + delbest, indiv.getTotal( ) + regworst + delworst );
			} else if( pt.isPending( ) ) {
				total.add( indiv.getRegular( ) + regworst, indiv.getTotal( ) + regworst + delworst );
			} else {
				total.add( indiv.getRegular( ) + regworst, indiv.getRegular( ) + regworst );
			}
		}
		
		return total;
	}
	
	/**
	 * Computes the TTL ranking score of the player, lowest best-case TTL is
	 * ranked number 1
	 * 
	 * @param player The player 
	 * @return The TTL rank of the player
	 */
	public int getTTLRank( Player player ) {
		data.checkReady( );
		
		final double ttl = getTTL( player, false ).getBestCase( );
		
		int rank = 1;
		for( Player p : data.getPlayers( ) ) {
			if( getTTL( p, false ).getBestCase( ) < ttl )
				rank++;
		}
		
		return rank;
	}	
	
	/**
	 * Computes TTL info for the task that is already planned, returns an empty
	 * score if the method is not planned
	 * 
	 * @param task The task
	 * @param relative Compute TTL relative to idle?
	 * @return The TTLScore containing the score components
	 */
	public TTLScore getTTL( Task task, boolean relative ) {
		data.checkReady( );
		
		// check if there is a method planned
		final PlanTask pt = data.getJointPlan( ).getPlanned( task );
		if( pt == null ) return new TTLScore( );
		
		// get the TTLModel
		final TTLModel ttlmodel = data.getGameInfo( ).getInfra( ).getTTLModel( );

		// get TTL
		final TTLScore ttl = data.getJointPlan( ).getTTL( pt, ttlmodel );
		return (relative ? rel( ttl, getTTLIdle( ) ) : ttl );
	}	
	
	/**
	 * Returns the total idle TTL over the entire game period
	 * @return The idle TTL
	 */
	public double getTTLIdle( ) {
		data.checkReady( );
		
		// check the cache
		Double idleTTLtotal = cache.getTTLIdleTotal( );
		if( idleTTLtotal == null ) {
			// invalid, re-compute
			idleTTLtotal = new Double( 0 );
			for( TimePoint week : data.getGamePeriod( ).toWeeks( ) )
				idleTTLtotal += getTTLIdle( week );
			
			// cache the result
			cache.setTTLIdleTotal( idleTTLtotal );
		}
		
		return idleTTLtotal;
	}
	
	/**
	 * Returns the idle TTL for the specified time
	 * 
	 * @param time The time
	 * @return The idle TTL at the given time
	 */
	public double getTTLIdle( TimePoint time ) {
		data.checkReady( );
		
		// check the cache
		Double idleTTL = cache.getTTLIdle( time );
		if( idleTTL == null ) {
			// re-compute
			idleTTL = new Double( data.getGameInfo( ).getInfra( ).getTTLModel( ).getIdle( time ) );
			
			// store in cache
			cache.setTTLIdle( time, idleTTL );
		}
		
		return idleTTL;
	}
	
	/**
	 * Retrieves the total joint plan TTL score, best and worst case
	 * 
	 * @param relative Compute relative to idle?
	 * @return The total TTL best and worst case of the current joint plan
	 */
	public TotalScore getTotalTTL( boolean relative ) {
		data.checkReady( );
		
		// sum player ttl scores
		final TotalScore total = new TotalScore( );
		for( Player p : data.getPlayers( ) ) {
			final TotalScore ttl = getTTL( p, relative );
			total.add( ttl.getBestCase( ), ttl.getWorstCase( ) );
		}
		
		return total;
	}
	
	/**
	 * Computes the payments for the task
	 * 
	 * @param task The task
	 * @return The TTLScore with in each component the payment or an empty
	 * container if no method is planned for the task
	 */
	public TTLScore getPayments( Task task ) {
		data.checkReady( );
		
		// get the method
		final PlanTask pt = data.getJointPlan( ).getPlanned( task );
		if( pt == null ) return new TTLScore( );
		
		// check the cache first
		TTLScore payments = cache.getTaskPayment( pt );
		if( payments == null ) {		
			// get method TTL
			final TTLScore ttl = getTTL( task, false );
			
			// get the mechanism
			final JointPlan jplan = data.getJointPlan( );
			final PaymentMechanism mechanism = data.getGameInfo( ).getMechanism( );
			
			final Player p = task.getPlayer( );
			
			// compute payment for each component
			// individual
			final PendingScore indiv = new PendingScore( mechanism.getPayment( p, jplan, ttl.getIndividual( ).getRegular( ) ),
					mechanism.getPayment( p, jplan, ttl.getIndividual( ).getDelayed( ) ), pt.getDelayStatus( ) );
			
			// network regular
			final TTLBandwidth networkreg = new TTLBandwidth( mechanism.getPayment( p, jplan, ttl.getNetworkRegular( ).getBestCase( ) ),
					mechanism.getPayment( p, jplan, ttl.getNetworkRegular( ).getWorstCase( ) ),
					mechanism.getPayment( p, jplan, ttl.getNetworkRegular( ).getBestRealised( ) ),
					mechanism.getPayment( p, jplan, ttl.getNetworkRegular( ).getWorstRealised( ) ) );
			
			// network delay
			final TTLBandwidth networkdel = new TTLBandwidth( mechanism.getPayment( p, jplan, ttl.getNetworkDelayed( ).getBestCase( ) ),
					mechanism.getPayment( p, jplan, ttl.getNetworkDelayed( ).getWorstCase( ) ),
					mechanism.getPayment( p, jplan, ttl.getNetworkDelayed( ).getBestRealised( ) ),
					mechanism.getPayment( p, jplan, ttl.getNetworkDelayed( ).getWorstRealised( ) ) );

			payments = new TTLScore( indiv, networkreg, networkdel );
			cache.setMethodPayment( pt, payments );
		}
			
		// return the TTLScore
		return payments;
	}
	
	/** Compute relative scores */
	private static double rel( double x, double y ) { return (x + y) / y - 1.0; }
	private static PendingScore rel( PendingScore ds, double y ) { return new PendingScore( rel( ds.getRegular( ), y ), rel( ds.getDelayed( ), y ), ds.getDelayStatus( ) ); }
	private static TTLBandwidth rel( TTLBandwidth ttl, double y ) { return new TTLBandwidth( rel( ttl.getBestCase( ), y), rel( ttl.getWorstCase( ), y), rel( ttl.getBestRealised( ), y), rel( ttl.getWorstRealised( ), y ) ); }
	private static TTLScore rel( TTLScore s, double y ) { return new TTLScore( rel( s.getIndividual( ), y ), rel( s.getNetworkRegular( ), y ), rel( s.getNetworkDelayed( ), y ) ); }
}
