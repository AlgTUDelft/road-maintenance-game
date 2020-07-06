/**
 * @file ProfileScoreResult.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2015 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         9 jan. 2015
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.gametrace;

/**
 * Container for a single profile score
 *
 * @author Joris Scharpff
 */
public class ProfileScoreResult {
	/** Profit score */
	protected final double profit;
	
	/** TTL score */
	protected final double ttl;
	
	/** Risk score */
	protected final double risk;
	
	/**
	 * Creates a new profile score
	 * 
	 * @param profit The profit score
	 * @param ttl The TTL score
	 * @param risk The risk-averseness score
	 */
	public ProfileScoreResult( double profit, double ttl, double risk ) {
		this.profit = profit;
		this.ttl = ttl;
		this.risk = risk;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString( ) {
		return profit + " " + ttl + " " + risk;
	}
}
