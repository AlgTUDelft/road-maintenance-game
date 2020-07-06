/**
 * @file GameDataCache.java
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
 * @date         13 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gamedata;

import java.util.List;

import plangame.game.plans.DelayStatus;
import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanTask;
import plangame.game.score.TTLScore;
import plangame.gwt.client.ClientView;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.state.GameInfo;
import plangame.model.object.ObjectMap;
import plangame.model.tasks.Task;
import plangame.model.tasks.TaskMethod;
import plangame.model.time.TimePoint;
import plangame.model.time.TimeSpan;
import plangame.model.traffic.TTLDistribution;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 * Cache functionality for gae data computations
 *
 * @author Joris Scharpff
 */
public class GameDataCache implements GameInfoUpdateListener, JointPlanUpdateListener {
	/** The game time in weeks */
	private int weeks;
	
	/** Idle TTL per week */
	private Double[] idleTTL;
	
	/** Total idle TTL (cached because it is used a lot with relative display) */
	private Double idleTTLTotal;
	
	/** Payment for a method at each time */
	private ObjectMap<Task, TTLScore> task_payments;
	
	/** TTL distribution of the joint plan per week */
	private TTLDistribution[] netdist;
	
	/** Number of cache hits */
	private long stats_hits;
	
	/** Number of cache misses */
	private long stats_misses;
	
	/** The number of milliseconds between cache performance output */
	private static final int CACHE_STATS_DELAY = 10000;
	
	/**
	 * Creates a new game data cache
	 */
	public GameDataCache( ) {
		this( true );
	}
	
	/**
	 * Creates a new cache
	 * 
	 * @param maincache True if this is the client's main cache, not a copy
	 */
	private GameDataCache( boolean maincache ) {
		// initialise stats
		stats_hits = 0;
		stats_misses = 0;
		
		// if we are running cache performance profiler, output cache statistics 
		// once in a while
		if( maincache && DebugGlobals.cachePerformance( ) )
			Scheduler.get( ).scheduleFixedDelay( new RepeatingCommand( ) {
				@Override public boolean execute( ) {
					debugStats( );
					return true;
				}
			}, CACHE_STATS_DELAY );
	}
	
	/**
	 * Copies all information from the specified cache
	 * 
	 * @param cache The cache to copy values from
	 */
	protected void copyFrom( GameDataCache cache ) {
		// keep track of copy time
		final long copystart = System.currentTimeMillis( ); 			
		
		// copy the number of weeks
		weeks = cache.weeks;
		
		// copy all elements
		idleTTLTotal = cache.idleTTLTotal;
		idleTTL = new Double[ weeks ];
		netdist = new TTLDistribution[ weeks ];
		for( int i = 0; i < weeks; i++ ) {
			idleTTL[ i ] = new Double( cache.idleTTL[ i ] );
			netdist[ i ] = (cache.netdist[ i ] != null ? new TTLDistribution( cache.netdist[ i ] ) : null );
		}
		
		// and per-task data
		task_payments = new ObjectMap<Task, TTLScore>( );
		for( Task task : cache.task_payments.getKeys( ) ) {
			task_payments.put( task, cache.task_payments.get( task ) );
		}
		
		// output copy time if profiling
		if( DebugGlobals.cachePerformance( ) )
			ClientView.getInstance( ).debug( "Cache copied in " + (System.currentTimeMillis( ) - copystart) + " msec", false );
	}

	/**
	 * @see plangame.gwt.client.gamedata.GameInfoUpdateListener#onGameInfoSet(plangame.gwt.shared.state.GameInfo)
	 */
	@Override
	public void onGameInfoSet( GameInfo gameinfo ) {
		// get the period length
		weeks = gameinfo.getGamePeriod( ).getWeeks( );
		
		// clear the entire cache
		idleTTLTotal = null;
		idleTTL = new Double[ weeks ];
		task_payments = new ObjectMap<Task, TTLScore>( );
		netdist = new TTLDistribution[ weeks ];
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		// invalidate all parts of the cache that are affected by the new joint
		// plan
		
		// invalidate caches
		netdist = new TTLDistribution[ weeks ];
		task_payments = new ObjectMap<Task, TTLScore>( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		// invalidate selectively
		switch( change.getType( ) ) {
			case MethodAdded: invalidateAdd( change.getMethod( ), change.getTime( ) ); break;
			case MethodChanged: invalidateChange( change.getPrevious( ), change.getMethod( ), change.getTime( ) ); break;
			case MethodMoved: invalidateMove( change.getPrevious( ), change.getTime( ) ); break;
			case MethodRemoved: invalidateRemove( change.getPrevious( ) ); break;
			case MethodDelaySet: invalidateDelay( change.getPrevious( ), change.getStatus( ) ); break;
		}
	}
	
	/**
	 * Invalidates the cache parts affected by adding a method to the joint plan
	 * 
	 * @param method The method that was added
	 * @param time The time at which it was added
	 */
	private void invalidateAdd( TaskMethod method, TimePoint time ) {
		task_payments.remove( method.getTask( ) );
		
		// invalidate weeks that the method is planned in
		invalidateSpan( method.getRegularSpan( time ) );
	}
	
	/**
	 * Invalidates the cache parts affected by changing the method
	 * 
	 * @param prev The previously planned task
	 * @param newmethod The new method
	 * @param newtime The newly chosen time
	 */
	private void invalidateChange( PlanTask prev, TaskMethod newmethod, TimePoint newtime ) {
		task_payments.remove( newmethod.getTask( ) );

		// invalidate both old and new spans
		invalidateSpan( prev.getPeriod( true ) );		
		invalidateSpan( newmethod.getSpan( newtime, prev.getDelayStatus( ), true ) );
	}
	
	/**
	 * Invalidates the cache parts affected by moving the method
	 * 
	 * @param prev The previously planned method
	 * @param newtime The newly chosen time
	 */
	private void invalidateMove( PlanTask prev, TimePoint newtime ) {
		task_payments.remove( prev.getTask( ) );

		// invalidate both old and new spans
		invalidateSpan( prev.getPeriod( true ) );		
		invalidateSpan( prev.getMethod( ).getSpan( newtime, prev.getDelayStatus( ), true ) );
	}
	
	/**
	 * Invalidates cache parts affected by removing the method
	 * 
	 * @param prev The previously planned method
	 */
	private void invalidateRemove( PlanTask prev ) {
		task_payments.remove( prev.getTask( ) );
		
		// invalidate previous span
		invalidateSpan( prev.getPeriod( true ) );				
	}
	
	/**
	 * Invalidates the cache parts affected by changing the delay status of the method
	 * 
	 * @param prev The previously planned task
	 * @param newdelay The new delay status
	 */
	private void invalidateDelay( PlanTask prev, DelayStatus newdelay ) {
		task_payments.remove( prev.getTask( ) );

		// invalidate delay part
		invalidateSpan( prev.getPeriodDelayed( ) );			
	}
	
	/**
	 * Invalidates all cache parts in the specified time span
	 * 
	 * @param span The time span to invalidate
	 */
	private void invalidateSpan( TimeSpan span ) {
		final List<TimePoint> weeks = span.toWeeks( );

		for( TimePoint week : weeks ) {
			try {
				netdist[ week.getWeek( ) ] = null;
			} catch( IndexOutOfBoundsException io ) {
				// FIXME!!!
			}
		}
	}
	
	/** @return The total idle TTL, null if not in cache */
	protected Double getTTLIdleTotal( ) { hit( idleTTLTotal != null); return idleTTLTotal; }
	
	/** Sets the total idle TTL @param ttl The new idle TTL total */
	protected void setTTLIdleTotal( double ttl ) { idleTTLTotal = new Double( ttl ); }
	
	/** @return The idle TTL at time t, null if not in cache */
	protected Double getTTLIdle( TimePoint time ) { hit( idleTTL[ time.getWeek( ) ] != null); return idleTTL[ time.getWeek( ) ]; }
	
	/** Sets the total idle TTL @param ttl The new idle TTL total */
	protected void setTTLIdle( TimePoint time, double ttl ) { idleTTL[ time.getWeek( ) ] = new Double( ttl ); }
	
	/** @return The distribution of network TTL at the specified time */
	protected TTLDistribution getAvgNetworkDist( TimePoint time ) { hit( netdist[ time.getWeek( ) ] != null); return netdist[ time.getWeek( ) ]; }
	
	/** Sets the network TTL distribution @param time The time @param dist The distribution */
	protected void setAvgNetworkDist( TimePoint time, TTLDistribution dist ) { netdist[ time.getWeek( ) ] = new TTLDistribution( dist ); }

	/**
	 * Retrieve the payment for the specified planned task
	 * 
	 * @param planned The planned task
	 * @return The payments for the method at the given time
	 */
	public TTLScore getTaskPayment( PlanTask planned ) {
		final TTLScore payments = task_payments.get( planned.getTask( ) );
		hit( payments != null );
		return payments;
	}
	
	/**
	 * Sets the payment for the planned task
	 * 
	 * @param planned The planned task
	 * @param payment The TTLScore containing the payment
	 */
	public void setMethodPayment( PlanTask planned, TTLScore payment ) {
		task_payments.put( planned.getTask( ), payment );
	}
	
	/**
	 * Updates the statistics based on the boolean value
	 * 
	 * @param hit True if the cache contains a stored value
	 */
	private void hit( boolean hit ) {
		if( hit )
			stats_hits++;
		else
			stats_misses++;
	}
	
	/**
	 * Periodically called to output cache performance
	 */
	private void debugStats( ) {
		final double perc = (stats_hits + stats_misses > 0 ? ((double)stats_hits) / (double)(stats_hits + stats_misses) * 100 : 0);
		ClientView.getInstance( ).debug( "Cache stats: " + stats_hits + " hits, " + stats_misses + " misses (" + perc + " %)" , false );
	}
}
