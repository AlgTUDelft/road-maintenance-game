/**
 * @file GameInfo.java
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
 * @date         3 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.state;

import java.io.Serializable;
import java.util.List;

import plangame.game.player.Player;
import plangame.model.infra.Infrastructure;
import plangame.model.mechanism.PaymentMechanism;
import plangame.model.object.BasicID;
import plangame.model.tasks.Portfolio;
import plangame.model.time.TimeSpan;

/**
 * Contains all information about the current game run
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class GameInfo implements Serializable {
	/** The game ID */
	protected BasicID gameID;
	
	/** The players of the game */
	protected List<Player> players;
		
	/** The portfolios */
	protected List<Portfolio> portfolios;
	
	/** The total game period */
	protected TimeSpan gameperiod;
	
	/** The infrastructure model it uses */
	protected Infrastructure infra;
	
	/** The payment mechanism used */
	protected PaymentMechanism mechanism;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected GameInfo( ) { }
	
	/**
	 * Creates a new game information object
	 * 
	 * @param gameID The game ID
	 * @param players The game players
	 * @param portfolios The list of portfolios known in the game
	 * @param gameperiod The game period
	 * @param infra The infrastructure
	 * @param mechanism The payment mechanism
	 */
	public GameInfo( BasicID gameID, List<Player> players, List<Portfolio> portfolios, TimeSpan gameperiod, Infrastructure infra, PaymentMechanism mechanism  ) {
		this.gameID = gameID;
		this.players = players;
		this.portfolios = portfolios;
		this.gameperiod = gameperiod;
		this.infra = infra;
		this.mechanism = mechanism;
	}
	
	/** @return The game ID */
	public BasicID getGameID( ) { return gameID; }
	
	/** @return The list of game players */
	public List<Player> getPlayers( ) { return players; }
	
	/** @return The game portfolios */
	public List<Portfolio> getPortfolios( ) { return portfolios; }
	
	/** @return The game period */
	public TimeSpan getGamePeriod( ) { return gameperiod; }
	
	/** @return The infrastructure it uses */
	public Infrastructure getInfra( ) { return infra; }
	
	/** @return The payment mechanism */
	public PaymentMechanism getMechanism( ) { return mechanism; }	
}
