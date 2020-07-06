/**
 * @file GameInfoUpdate.java
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

import plangame.gwt.shared.state.GameInfo;


/**
 * Callbacks when the game info is updated
 * 
 * @author Joris Scharpff
 */
public interface GameInfoUpdateListener extends DataUpdateListener {
	/**
	 * Called when new game info becomes known
	 * 
	 * @param gameinfo The new game information
	 */
	public void onGameInfoSet( GameInfo gameinfo );
}
