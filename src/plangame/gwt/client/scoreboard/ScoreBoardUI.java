/**
 * @file ScoreBoardUI.java
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
 * @date         15 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.scoreboard;

import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.gameview.GameViewUI;
import plangame.gwt.client.widgets.scoretable.ScoreTableWidget;
import plangame.gwt.client.widgets.scoretable.ScoreTableWidget.ScoreTableMode;
import plangame.gwt.shared.state.GameServerInfo;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Joris Scharpff
 */
public class ScoreBoardUI extends GameViewUI {
	protected interface ScoreBoardUIBinder extends UiBinder<Widget, ScoreBoardUI> {}
	
	/** The UI elements */
	@UiField(provided=true) protected ScoreTableWidget tblScores;
	
	/**
	 * Creates a new ScoreBoardUI
	 * 
	 * @param view The view associated with this UI 
	 */
	public ScoreBoardUI( ScoreBoard view ) {
		super( view );
	}
	
	/**
	 * @see plangame.gwt.client.ClientViewUI#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		// init the score table
		tblScores = new ScoreTableWidget( ScoreTableMode.Both );
		
		return ((ScoreBoardUIBinder) GWT.create( ScoreBoardUIBinder.class )).createAndBindUi( this );
	}	
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUI#registerDataWidgets(plangame.gwt.client.gamedata.ClientGameData)
	 */
	@Override
	protected void registerDataWidgets( ClientGameData gamedata ) {
		gamedata.addUpdateListener( tblScores );
		tblScores.setGameData( gamedata );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUI#updateServerInfo(plangame.gwt.shared.state.GameServerInfo)
	 */
	@Override
	protected void updateServerInfo( GameServerInfo serverinfo ) {
		// set the update live setting in the score table
		tblScores.setUpdateLive( serverinfo.getConfig( ).scoreUpdateLive( ) );
	}
	
	/**
	 * @return The score table
	 */
	protected ScoreTableWidget getScores( ) {
		return tblScores;
	}

	/**
	 * Updates the score table
	 */
	protected void updateScores( ) {
		tblScores.getData( ).refresh( );
	}
}
