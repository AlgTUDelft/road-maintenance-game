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
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.dialogs.Dialog;
import plangame.gwt.client.widgets.map.MapWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog that is shown at the start of a game
 *
 * @author Joris Scharpff
 */
public class StartGameDialog extends Dialog {
	protected interface StartGameDialogUIBinder extends UiBinder<Widget, StartGameDialog> { }

	/** The player */
	protected Player player;
	
	/** The client game data */
	protected ClientGameData gamedata;

	// UI Binder elements
	@UiField protected VerticalPanel pnlMain;
	@UiField protected HorizontalPanel pnlTitle;
	@UiField protected Label lblWelcomeTitle;
	@UiField protected Label lblColour;
	@UiField protected HTMLPanel pnlColour;
	@UiField protected HorizontalPanel pnlInfo;
	@UiField protected Label lblWelcomeText;
	@UiField protected MapWidget wdMap;
	@UiField protected Button btnStart;
	
	
	/**
	 * Creates a new Start Game Dialog for the player
	 * 
	 * @param player The player to display the dialog for
	 * @param gamedata The game data
	 */
	public StartGameDialog( Player player, ClientGameData gamedata ) {
		super( Lang.text.StartGameDialog_Title( ) );
		
		this.player = player;
		this.gamedata = gamedata.dataCopy( );
		
		this.gamedata.addUpdateListener( wdMap );
		wdMap.setGameData( this.gamedata );
		this.gamedata.notifyListeners( );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#initUI()
	 */
	@Override
	protected Widget initUI( ) { return ((StartGameDialogUIBinder)GWT.create( StartGameDialogUIBinder.class )).createAndBindUi( this ); }
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#init()
	 */
	@Override
	protected void init( ) {
		super.init( );
						
		// setup map
		wdMap.setSize( "280px", "280px" ); // FIXME move to CSS
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.Dialog#initShow()
	 */
	@Override
	protected void initShow( ) {
		super.initShow( );
		
		// set text
		lblWelcomeTitle.setText( Lang.text.StartGameDialog_WelcomeTitle( player.getDescription( ) ) );
		lblWelcomeText.setText( player.getPortfolio( ).getLongDescription( ) );
		
		// show the portfolio colour
		pnlColour.getElement( ).getStyle( ).setBackgroundColor( player.getPortfolio( ).getColour( ) );		
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
}
