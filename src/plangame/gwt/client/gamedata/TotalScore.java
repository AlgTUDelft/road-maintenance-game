/**
 * @file TotalScore.java
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
 * @date         22 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gamedata;

import java.io.Serializable;

/**
 * Container for a total score
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class TotalScore implements Serializable {
	/** The best-case total */
	protected double bestcase;
	
	/** The worst-case total */
	protected double worstcase;
	
	/** 
	 * Creates a new empty total score
	 */
	public TotalScore( ) {
		this( 0, 0 );
	}
	
	/**
	 * Creates a new total score
	 * 
	 * @param best The best score
	 * @param worst The worst score
	 */
	public TotalScore( double best, double worst ) {
		this.bestcase = best;
		this.worstcase = worst;
	}
	
	/**
	 * Adds the best and worst to this score
	 * 
	 * @param dbest The best score to add
	 * @param dworst The worst case score to add
	 */
	public void add( double dbest, double dworst ) {
		bestcase += dbest;
		worstcase += dworst;
	}
	
	/** @return The best case total score */
	public double getBestCase( ) { return bestcase; }
	
	/** @return The worst case total score */
	public double getWorstCase( ) { return worstcase; }
	
	/** @return The difference between both cases */
	public double getDelta( ) { return worstcase - bestcase; }

}
