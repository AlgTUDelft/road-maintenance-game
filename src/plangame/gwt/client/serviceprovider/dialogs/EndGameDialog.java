/**
 * @file StartGameDialog.java
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
 * @date         31 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.serviceprovider.dialogs;

import plangame.game.player.Player;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.dialogs.Dialog;
import plangame.gwt.client.widgets.scoretable.ScoreTableWidget;
import plangame.gwt.client.widgets.scoretable.ScoreTableWidget.ScoreTableMode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog that is shown at the start of a game
 *
 * @author Joris Scharpff
 */
public class EndGameDialog extends Dialog {
	protected interface EndGameDialogUIBinder extends UiBinder<Widget, EndGameDialog> { }

	/** The player */
	protected Player player;
	
	/** The client game data */
	protected ClientGameData gamedata;
	
	/** Display TTL relative? */
	protected boolean relative;

	// UI Binder elements
	@UiField(provided=true) protected ScoreTableWidget tblProfit;
	@UiField(provided=true) protected ScoreTableWidget tblTTL;
	@UiField protected Label lblNetworkTTL;
	
	/**
	 * Creates a new End Game Dialog for the player
	 * 
	 * @param player The player to display the dialog for
	 * @param gamedata The game data
	 * @param relative True to show TTL relative
	 */
	public EndGameDialog( Player player, ClientGameData gamedata, boolean relative ) {
		super( Lang.text.EndGameDialog_Title( ) );
		
		this.gamedata = gamedata;
		this.relative = relative;
		
		tblProfit.setGameData( gamedata );
		tblTTL.setGameData( gamedata );		
		tblTTL.setDisplayRelative( relative );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#initProvided()
	 */
	@Override
	protected void initProvided( ) {
		// setup tables
		tblProfit = new ScoreTableWidget( ScoreTableMode.Profits );
		tblTTL = new ScoreTableWidget( ScoreTableMode.TTL );
						
		// hide pagers
		tblProfit.getData( ).setShowPager( false );
		tblTTL.getData( ).setShowPager( false );
		
		// and order tables by rank
		tblProfit.setSortOnRank( true );
		tblTTL.setSortOnRank( true );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#initUI()
	 */
	@Override
	protected Widget initUI( ) { return ((EndGameDialogUIBinder)GWT.create( EndGameDialogUIBinder.class )).createAndBindUi( this ); }
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#initShow()
	 */
	@Override
	protected void initShow( ) {
		super.initShow( );
		
		// add players to tables
		for( Player p : getGameData( ).getPlayers( ) ) {
			tblProfit.getData( ).addItem( p );
			tblTTL.getData( ).addItem( p );
		}
		
		// set total network TTL
		final Style style = (relative ? Style.Percentage2 : Style.IntK);
		lblNetworkTTL.setText( Format.f( getGameData( ).getData( ).getTotalTTL( relative ).getBestCase( ), style ) );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#onShow()
	 */
	@Override
	protected void onShow( ) {
		// make sure the rows are sorted properly
		tblProfit.getData( ).sort( );
		tblTTL.getData( ).sort( );
	}
	
	/**
	 * Closes the dialog
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnStart")
	protected void close( ClickEvent e ) {
		hide( );
	}
	
	/**
	 * @return The game data
	 */
	private ClientGameData getGameData( ) {
		return gamedata;
	}
}
